package com.contasapagar.contasapi.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuração da documentação OpenAPI/Swagger da aplicação.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addServersItem(localServer())
                .addServersItem(dockerServer())
                .addSecurityItem(securityRequirement())
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("basicAuth", securityScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("API de Contas a Pagar")
                .description("""
                        API REST para gerenciamento de contas a pagar desenvolvida com Spring Boot seguindo princípios DDD.
                        
                        ## Funcionalidades Principais
                        
                        - **CRUD completo** de contas a pagar
                        - **Alteração de situação** das contas (Pendente, Pago, Vencido, Cancelado)
                        - **Filtros avançados** por data de vencimento, descrição e situação
                        - **Relatórios** de valor total pago por período
                        - **Importação em lote** via arquivo CSV
                        - **Paginação** em todas as consultas
                        - **Autenticação** HTTP Basic
                        - **Controle de acesso** baseado em roles
                        
                        ## Autenticação
                        
                        A API utiliza autenticação HTTP Basic com os seguintes usuários:
                        
                        - **admin / admin123** (role: ADMIN) - Acesso total
                        - **user / user123** (role: USER) - Acesso limitado
                        
                        ## Formato de Datas
                        
                        Todas as datas devem ser informadas no formato brasileiro: **dd/MM/yyyy**
                        
                        ## Importação CSV
                        
                        O arquivo CSV deve seguir o formato: `data_vencimento;valor;descricao`
                        
                        Exemplo: `15/01/2024;150.50;Conta de luz`
                        """)
                .version("1.0.0");
//                .contact(apiContact())
//                .license(apiLicense());
    }

//    private Contact apiContact() {
//        return new Contact()
//                .name("André Siqueira")
//                .email("andre@siqueira.dev")
//                .url("https://github.com/andreSiqueira/contas-api");
//    }
//
//    private License apiLicense() {
//        return new License()
//                .name("MIT License")
//                .url("https://opensource.org/licenses/MIT");
//    }

    private Server localServer() {
        return new Server()
                .url("http://localhost:" + serverPort + "/api")
                .description("Servidor de Desenvolvimento Local");
    }

    private Server dockerServer() {
        return new Server()
                .url("http://localhost:8080/api")
                .description("Servidor Docker");
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("basicAuth");
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic")
                .name("basicAuth")
                .description("Autenticação HTTP Basic. Use: admin/admin123 ou user/user123");
    }
}
