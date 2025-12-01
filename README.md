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

3. **Fase 3: Infrastructure Layer**
    - Implementar repositories JPA
    - Configurar Spring Security

4. **Fase 4: Application Layer**
    - Criar services e DTOs
    - Implementar mappers

5. **Fase 5: Presentation Layer**
    - Desenvolver controllers REST
    - Implementar importação CSV

6. **Fase 6: Testes**
    - Testes unitários
    - Testes de integração

## 🚦 Status do Projeto

🟢 **Configuração Base**: Completa
🟢 **Domain Layer**: Completa
⏳ **Infrastructure Layer**: Pendente
⏳ **Application Layer**: Pendente
⏳ **Presentation Layer**: Pendente
⏳ **Testes**: Pendente
