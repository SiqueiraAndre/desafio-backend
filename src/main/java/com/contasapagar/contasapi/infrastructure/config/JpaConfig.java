package com.contasapagar.contasapi.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuração JPA para o projeto.
 * Habilita auditoria JPA, repositórios JPA e gerenciamento de transações.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.contasapagar.contasapi.infrastructure.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {

    // Esta classe serve principalmente para centralizar as configurações JPA
    // As anotações fazem todo o trabalho necessário
}
