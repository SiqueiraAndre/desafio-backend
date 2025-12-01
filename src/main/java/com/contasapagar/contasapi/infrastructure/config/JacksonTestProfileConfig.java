package com.contasapagar.contasapi.infrastructure.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.format.DateTimeFormatter;

@Configuration
@Profile("test")
public class JacksonTestProfileConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localDateCustomizer() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return builder -> builder.serializers(new LocalDateSerializer(dateFormatter));
    }
}

