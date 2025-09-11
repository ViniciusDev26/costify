package database

import (
	"database/sql"
	"fmt"

	"github.com/vini/costify-go/internal/infrastructure/config"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

// NewGormConnection creates a new GORM database connection
func NewGormConnection(cfg *config.DatabaseConfig) (*gorm.DB, error) {
	dsn := cfg.GetDSN()
	
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{
		Logger: logger.Default.LogMode(logger.Info),
	})
	if err != nil {
		return nil, fmt.Errorf("failed to connect to database: %w", err)
	}

	return db, nil
}

// NewSQLConnection creates a new SQL database connection for migrations
func NewSQLConnection(cfg *config.DatabaseConfig) (*sql.DB, error) {
	dsn := cfg.GetDSN()
	
	db, err := sql.Open("postgres", dsn)
	if err != nil {
		return nil, fmt.Errorf("failed to connect to database: %w", err)
	}

	if err := db.Ping(); err != nil {
		return nil, fmt.Errorf("failed to ping database: %w", err)
	}

	return db, nil
}