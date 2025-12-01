package com.contasapagar.contasapi.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para verificações de saúde da aplicação.
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Endpoints para verificação de saúde da aplicação")
public class HealthController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    /**
     * Verifica se a aplicação está funcionando
     */
    @GetMapping
    @Operation(summary = "Health Check", description = "Verifica se a aplicação está funcionando corretamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aplicação funcionando normalmente")
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("port", serverPort);
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        health.put("version", "1.0.0");

        return ResponseEntity.ok(health);
    }

    /**
     * Informações da aplicação
     */
    @GetMapping("/info")
    @Operation(summary = "Informações da aplicação", description = "Retorna informações básicas sobre a aplicação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações retornadas com sucesso")
    })
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", applicationName);
        info.put("description", "API REST para gerenciamento de contas a pagar");
        info.put("version", "1.0.0");
        info.put("java.version", System.getProperty("java.version"));
        info.put("spring.boot.version", org.springframework.boot.SpringBootVersion.getVersion());

        Map<String, String> contact = new HashMap<>();
        contact.put("name", "André Siqueira");
        contact.put("email", "andre@siqueira.dev");
        info.put("contact", contact);

        return ResponseEntity.ok(info);
    }
}
