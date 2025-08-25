package br.unifor.costify.integration.flyway;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class FlywayMigrationIntegrationTest {
  @Autowired org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

  @Container
  static PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:16.9-alpine")
          .withDatabaseName("costify")
          .withUsername("postgres")
          .withPassword("postgres");

  // Injeta as propriedades do container no Spring Boot
  @DynamicPropertySource
  static void setDatasourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
    registry.add("spring.flyway.enabled", () -> true);
    registry.add("spring.flyway.locations", () -> "classpath:db/migration");
  }

  @Test
  void flywayMigrationsApplied() throws SQLException {
    Integer count =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'",
            Integer.class);

    assert count != null && count > 0;
  }
}
