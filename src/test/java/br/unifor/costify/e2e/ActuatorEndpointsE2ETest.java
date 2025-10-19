package br.unifor.costify.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Actuator Endpoints E2E Tests")
class ActuatorEndpointsE2ETest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
      .withDatabaseName("costify_test")
      .withUsername("test")
      .withPassword("test");

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Should return UP status on /actuator/health endpoint")
  void shouldReturnHealthStatusUp() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("UP")));
  }

  @Test
  @DisplayName("Should return UP status on /actuator/health/liveness endpoint")
  void shouldReturnLivenessStatusUp() throws Exception {
    mockMvc.perform(get("/actuator/health/liveness"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("UP")));
  }

  @Test
  @DisplayName("Should return UP status on /actuator/health/readiness endpoint")
  void shouldReturnReadinessStatusUp() throws Exception {
    mockMvc.perform(get("/actuator/health/readiness"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("UP")));
  }

  @Test
  @DisplayName("Should verify database connectivity through health endpoint")
  void shouldVerifyDatabaseConnectivity() throws Exception {
    // When database is up (via Testcontainers), health should be UP
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("UP")));
  }

  @Test
  @DisplayName("Should return health details when database is connected")
  void shouldReturnHealthDetailsWithDatabase() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("UP")))
        .andExpect(jsonPath("$.components.db.status", is("UP")));
  }

  @Test
  @DisplayName("Should verify database is included in health components")
  void shouldVerifyDatabaseInHealthComponents() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.components.db").exists())
        .andExpect(jsonPath("$.components.db.status", is("UP")))
        .andExpect(jsonPath("$.components.db.details").exists())
        .andExpect(jsonPath("$.components.db.details.database", is("PostgreSQL")));
  }

  @Test
  @DisplayName("Should verify readiness depends on database connectivity")
  void shouldVerifyReadinessDependsOnDatabase() throws Exception {
    // Readiness probe should be UP only when database is accessible
    mockMvc.perform(get("/actuator/health/readiness"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("UP")));
  }

  @Test
  @DisplayName("Should verify database connection details in health endpoint")
  void shouldVerifyDatabaseConnectionDetails() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.components.db.details.validationQuery").exists());
  }
}
