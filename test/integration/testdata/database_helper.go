package testdata

import (
	"context"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"time"

	"github.com/testcontainers/testcontainers-go"
	"github.com/testcontainers/testcontainers-go/modules/postgres"
	"github.com/testcontainers/testcontainers-go/wait"
	"github.com/vini/costify-go/internal/infrastructure/config"
	"github.com/vini/costify-go/internal/infrastructure/database"
	"gorm.io/gorm"
)

type DatabaseTestHelper struct {
	container testcontainers.Container
	DB        *gorm.DB
}

func NewDatabaseTestHelper(ctx context.Context) (*DatabaseTestHelper, error) {
	// Create postgres container
	postgresContainer, err := postgres.RunContainer(ctx,
		testcontainers.WithImage("postgres:16.9-alpine"),
		postgres.WithDatabase("testdb"),
		postgres.WithUsername("testuser"),
		postgres.WithPassword("testpass"),
		testcontainers.WithWaitStrategy(
			wait.ForLog("database system is ready to accept connections").
				WithOccurrence(2).
				WithStartupTimeout(5*time.Minute)),
	)
	if err != nil {
		return nil, fmt.Errorf("failed to start container: %w", err)
	}

	// Get connection details
	host, err := postgresContainer.Host(ctx)
	if err != nil {
		return nil, fmt.Errorf("failed to get container host: %w", err)
	}

	mappedPort, err := postgresContainer.MappedPort(ctx, "5432")
	if err != nil {
		return nil, fmt.Errorf("failed to get container port: %w", err)
	}

	// Create database connection
	cfg := &config.DatabaseConfig{
		Host:     host,
		Port:     mappedPort.Port(),
		User:     "testuser",
		Password: "testpass",
		DBName:   "testdb",
		SSLMode:  "disable",
	}

	db, err := database.NewGormConnection(cfg)
	if err != nil {
		return nil, fmt.Errorf("failed to connect to test database: %w", err)
	}

	// Create SQL connection for migrations
	sqlDB, err := database.NewSQLConnection(cfg)
	if err != nil {
		return nil, fmt.Errorf("failed to create SQL connection: %w", err)
	}

	// Get working directory to build relative path to migrations
	wd, err := os.Getwd()
	if err != nil {
		return nil, fmt.Errorf("failed to get working directory: %w", err)
	}
	
	// Build migrations path relative to project root
	migrationsPath := filepath.Join(wd, "..", "..", "..", "migrations")
	
	// Run migrations
	if err := database.RunMigrations(sqlDB, migrationsPath); err != nil {
		return nil, fmt.Errorf("failed to run migrations: %w", err)
	}

	return &DatabaseTestHelper{
		container: postgresContainer,
		DB:        db,
	}, nil
}

func (h *DatabaseTestHelper) Cleanup(ctx context.Context) error {
	if h.container != nil {
		if err := h.container.Terminate(ctx); err != nil {
			log.Printf("failed to terminate container: %v", err)
			return err
		}
	}
	return nil
}

func (h *DatabaseTestHelper) CleanDatabase() error {
	// Clean all tables in reverse order to respect foreign key constraints
	tables := []string{"recipe_ingredients", "recipes", "ingredients"}
	
	for _, table := range tables {
		if err := h.DB.Exec(fmt.Sprintf("DELETE FROM %s", table)).Error; err != nil {
			return fmt.Errorf("failed to clean table %s: %w", table, err)
		}
	}
	
	return nil
}