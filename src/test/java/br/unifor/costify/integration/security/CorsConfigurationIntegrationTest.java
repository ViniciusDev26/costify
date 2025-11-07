package br.unifor.costify.integration.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CorsConfigurationIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Container
  static PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:16.9-alpine")
          .withDatabaseName("costify")
          .withUsername("postgres")
          .withPassword("postgres");

  @DynamicPropertySource
  static void setDatasourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
  }

  @Test
  void shouldAllowCorsPreflightRequest() throws Exception {
    mockMvc
        .perform(
            options("/api/units")
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type"))
        .andExpect(status().isOk())
        .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
        .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))
        .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS))
        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
  }

  @Test
  void shouldAllowCorsFromDifferentOrigins() throws Exception {
    String[] origins = {
      "http://localhost:3000",
      "http://localhost:4200",
      "https://example.com",
      "https://app.example.com"
    };

    for (String origin : origins) {
      mockMvc
          .perform(
              options("/api/units")
                  .header(HttpHeaders.ORIGIN, origin)
                  .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
          .andExpect(status().isOk())
          .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
  }

  @Test
  void shouldAllowCorsForPostRequests() throws Exception {
    String requestBody =
        """
        {
          "name": "Test Ingredient",
          "packageQuantity": 1.0,
          "packagePrice": 10.0,
          "packageUnit": "KG"
        }
        """;

    mockMvc
        .perform(
            post("/api/ingredients")
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
  }

  @Test
  void shouldExposeHeadersInCorsResponse() throws Exception {
    mockMvc
        .perform(
            options("/api/units")
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
        .andExpect(status().isOk())
        .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
        .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS));
  }

  @Test
  void shouldAllowAllHttpMethods() throws Exception {
    mockMvc
        .perform(
            options("/api/ingredients")
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type"))
        .andExpect(status().isOk())
        .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS));
  }
}
