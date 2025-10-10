package br.unifor.costify.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable()) // desabilita CSRF (importante para APIs REST)
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // habilita CORS
        .authorizeHttpRequests(
            auth -> auth.anyRequest().permitAll() // libera todas as rotas
            )
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*")); // permite todas as origens
    configuration.setAllowedMethods(List.of("*")); // permite todos os métodos (GET, POST, PUT, DELETE, etc)
    configuration.setAllowedHeaders(List.of("*")); // permite todos os headers
    configuration.setAllowCredentials(true); // permite credenciais
    configuration.setExposedHeaders(List.of("*")); // expõe todos os headers na resposta

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // aplica a configuração para todas as rotas
    return source;
  }
}
