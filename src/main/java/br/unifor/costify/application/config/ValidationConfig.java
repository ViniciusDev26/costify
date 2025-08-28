package br.unifor.costify.application.config;

import br.unifor.costify.application.validation.ValidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {
    
    @Bean
    public ValidationService validationService() {
        return new ValidationService();
    }
}