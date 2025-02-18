# 💰 API de Contas a Pagar

[![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-green?logo=spring)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-24.0+-blue?logo=docker)](https://www.docker.com/)

API para gerenciamento de contas a pagar com autenticação, filtros avançados e importação via CSV.

## 📋 Índice
- [Tecnologias](#-tecnologias)
- [Instalação](#-instalação)
- [Documentação da API](#-documentação-da-api)
- [Autenticação](#-autenticação)
- [Exemplos Práticos](#-exemplos-práticos)
- [Importar no Postman](#-importar-no-postman)

## 🛠 Tecnologias
- **Java 17+** - Linguagem principal
- **Spring Boot 3.2** - Framework backend
- **PostgreSQL 14** - Banco de dados
- **Docker** - Containerização
- **Flyway** - Migrações de banco

## ⚙️ Instalação

### Pré-requisitos
- Docker 24.0+
- Docker Compose 2.20+


```bash
# 1. Clone o repositório
git clone https://github.com/SiqueiraAndre/desafio-backend

# 2. Inicie os containers
docker compose -f docker/docker-compose.yml up --build -d
```

## 🔑 Autenticação
Todas as requisições precisam incluir no cabeçalho:
```
Authorization: ApiKey #32373c
```


## 📚 Documentação da API

### Endpoints Principais

| Método | Endpoint                 | Descrição               |
| -------- |--------------------------|-------------------------|
| POST        | <code>/api/conta         | Cria nova conta         |
| GET        | <code>/api/contas	             | Lista contas com filtros |
| GET        | <code>/api/contas/{id}	        | Obtém conta por ID      |
| PUT        | <code>/api/contas/{id}	        | Atualizar conta |
| PATCH        | <code>/api/contas/{id}/status	 | Alterar a situação da conta |
| GET        | <code>/api/contas/total-pago	  | Calcula total pago por período |
| GET        | <code>/api/contas/importar	    | Importa contas via CSV |



## 🚀 Exemplos Práticos

### 1. Criar Nova Conta

```http
POST /api/contas
Content-Type: application/json

{
  "dataVencimento": "2024-12-31",
  "descricao": "Aluguel",
  "valor": 1500.50
}
```

#### Resposta:

```json
{
  "id": 1,
  "dataVencimento": "2024-12-31",
  "dataPagamento": null,
  "valor": 1500.50,
  "descricao": "Aluguel",
  "situacao": "PENDENTE"
}
```

### 2. Filtrar Contas

```http
GET /api/contas?dataVencimento=2024-12-31&size=5&sort=valor,desc
```

#### Resposta Paginada:

```json
{
  "content": [
    {
      "id": 1,
      "descricao": "Aluguel",
      "valor": 1500.50,
      "dataVencimento": "2024-12-31",
      "situacao": "PENDENTE"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "page": 0,
  "size": 5
}
```

### 3. Importar CSV

```bash
curl -X POST -F "arquivo=@contas.csv" http://localhost:8081/api/contas/importar
```


### Estrutura CSV:

```csv
data_vencimento,data_pagamento,valor,descricao
15/08/2024,10/08/2024,1500.50,Aluguel
20/08/2024,,750.00,Manutenção
```

## 📌 Importar no Postman

### 1. Baixe a coleção: [Contas-a-Pagar.postman_collection.json](Contas-a-Pagar.postman_collection.json)
### 2. Importe no Postman
### 3. Configure environment variables:
* base_url: http://localhost:8081
* api_key: #32373c
### 4. Arquivo CSV
* Para utilizar o end-point <code>/api/contas/importar</code> baixe o [arquivo.csv](arquivo.csv)

## 🛠 Configuração Avançada

```env
# application.properties

spring.application.name=contas-a-pagar
spring.security.user.name=admin
spring.security.user.password=password

spring.datasource.url=jdbc:postgresql://localhost:5432/contas
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.show_sql=true

spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

server.port= 8081
```