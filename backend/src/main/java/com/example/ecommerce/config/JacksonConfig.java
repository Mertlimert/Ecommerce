package com.example.ecommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        // Configure Hibernate module to handle lazy loading properly
        hibernateModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, false);
        
        return new Jackson2ObjectMapperBuilder()
                .modules(hibernateModule, new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
}