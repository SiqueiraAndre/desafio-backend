# API de Contas a Pagar

Sistema para gerenciamento de contas a pagar desenvolvido em Spring Boot seguindo arquitetura Domain Driven Design (DDD).

## 🚀 Tecnologias

- Java 17
- Spring Boot 3.4.12
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Docker & Docker Compose
- Maven

## 📋 Pré-requisitos

- Java 17+
- Docker e Docker Compose
- Maven (opcional, pode usar ./mvnw)

## 🛠️ Configuração e Execução

### 1. Usando Docker Compose (Recomendado)

```bash
# Clonar o repositório
cd contas-api

# Executar com Docker Compose
docker-compose -f Docker/docker-compose.yml up -d

# Para parar os serviços
docker-compose -f Docker/docker-compose.yml down
```

### 2. Execução Local

```bash
# 1. Iniciar PostgreSQL local (ou usar Docker)
docker run -d --name postgres-contas \
  -e POSTGRES_USER=contas_user \
  -e POSTGRES_PASSWORD=contas_pass \
  -e POSTGRES_DB=contas_db \
  -p 5432:5432 postgres:15-alpine

# 2. Compilar e executar a aplicação
./mvnw clean package
./mvnw spring-boot:run

# Ou usando Java diretamente
java -jar target/contas-api-*.jar
```

## 🏗️ Arquitetura

O projeto segue os princípios do Domain Driven Design (DDD):

```
src/main/java/com/contasapagar/contasapi/
├── domain/              # Regras de negócio
│   ├── entity/         # Entidades do domínio
│   ├── repository/     # Interfaces dos repositórios
│   ├── valueobject/    # Value Objects
│   └── enums/          # Enumerações
├── application/         # Casos de uso
│   ├── service/        # Serviços de aplicação
│   ├── dto/            # DTOs
│   └── mapper/         # Mapeadores
├── infrastructure/      # Implementações técnicas
│   ├── repository/     # Implementações JPA
│   └── config/         # Configurações
└── presentation/        # Interface com o usuário
    ├── controller/     # Controllers REST
    └── exception/      # Tratamento de exceções
```

## 🔐 Autenticação

A API utiliza autenticação HTTP Basic. Usuários configurados:

- **Admin**: `admin / admin123` (role: ADMIN)
- **User**: `user / user123` (role: USER)

Exemplo de uso:
```bash
curl -u admin:admin123 http://localhost:8080/api/contas
```

## 📋 Endpoints da API

### Documentação Interativa
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

### Endpoints Principais

#### Contas
- `GET /api/contas` - Listar contas com filtros e paginação
- `POST /api/contas` - Cadastrar nova conta
- `GET /api/contas/{id}` - Buscar conta por ID
- `PUT /api/contas/{id}` - Atualizar conta
- `PATCH /api/contas/{id}/situacao` - Alterar situação da conta
- `DELETE /api/contas/{id}` - Remover conta (apenas ADMIN)

#### Relatórios
- `GET /api/contas/total-pago` - Valor total pago por período

#### Importação
- `POST /api/contas/importacao` - Importar contas via CSV
- `GET /api/contas/importacao/template` - Baixar template CSV
- `GET /api/contas/importacao/info` - Informações sobre formato

#### Utilitários
- `GET /api/health` - Health check (público)
- `GET /api/health/info` - Informações da aplicação (público)

### Exemplos de Uso

#### Cadastrar Conta
```bash
curl -u admin:admin123 -X POST \
  -H "Content-Type: application/json" \
  -d '{"dataVencimento":"31/12/2024","valor":150.50,"descricao":"Conta de luz"}' \
  http://localhost:8080/api/contas
```

#### Listar Contas com Filtros
```bash
curl -u admin:admin123 \
  "http://localhost:8080/api/contas?dataVencimentoInicio=01/01/2024&dataVencimentoFim=31/12/2024&size=10"
```

#### Alterar Situação para Pago
```bash
curl -u admin:admin123 -X PATCH \
  -H "Content-Type: application/json" \
  -d '{"situacao":"PAGO","dataPagamento":"01/12/2024"}' \
  http://localhost:8080/api/contas/1/situacao
```

## 📊 Banco de Dados

A estrutura do banco é gerenciada pelo Flyway. A tabela principal é:

```sql
CREATE TABLE contas (
                        id BIGSERIAL PRIMARY KEY,
                        data_vencimento DATE NOT NULL,
                        data_pagamento DATE,
                        valor DECIMAL(15,2) NOT NULL,
                        descricao VARCHAR(500) NOT NULL,
                        situacao VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔧 Configuração

### application.properties

As principais configurações estão em `src/main/resources/application.properties`:

- Banco de dados PostgreSQL
- JPA/Hibernate
- Flyway migrations
- Configurações de servidor
- Logs detalhados

### Perfis

- **default**: Desenvolvimento local
- **docker**: Execução em container

## 📝 Próximos Passos

1. ✅ **Fase 1: Configuração Base** - CONCLUÍDA
    - ✅ Configuração do banco PostgreSQL
    - ✅ Setup do Flyway
    - ✅ Migration inicial
    - ✅ Estrutura DDD
    - ✅ Docker Compose

2. ✅ **Fase 2: Domain Layer** - CONCLUÍDA
    - ✅ Criar entidade Conta
    - ✅ Implementar enums SituacaoEnum
    - ✅ Definir repository interfaces
    - ✅ Value Objects (Periodo)

3. ✅ **Fase 3: Infrastructure Layer** - CONCLUÍDA
    - ✅ Implementar repositories JPA
    - ✅ Configurar Spring Security
    - ✅ Configurações JPA e Transações
    - ✅ Serviço de tarefas agendadas

4. ✅ **Fase 4: Application Layer** - CONCLUÍDA
    - ✅ Criar services e DTOs
    - ✅ Implementar mappers

5. ✅ **Fase 5: Presentation Layer** - CONCLUÍDA
    - ✅ Desenvolver controllers REST
    - ✅ Implementar importação CSV
    - ✅ Documentação Swagger/OpenAPI
    - ✅ Tratamento global de exceções

6. **Fase 6: Testes**
    - Testes unitários
    - Testes de integração

## 🚦 Status do Projeto

🟢 **Configuração Base**: Completa
🟢 **Domain Layer**: Completa
🟢 **Infrastructure Layer**: Completa
🟢 **Application Layer**: Completa
🟢 **Presentation Layer**: Completa
⏳ **Testes**: Pendente
