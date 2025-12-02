# 💰 API de Contas a Pagar

[![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?logo=spring)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-24.0+-blue?logo=docker)](https://www.docker.com/)

API para gerenciamento de contas a pagar com autenticação, filtros e importação via CSV.

## 📋 Índice
- [Tecnologias](#-tecnologias)
- [Instalação](#-instalação)
- [Autenticação](#-autenticação)
- [Documentação da API](#-documentação-da-api)
- [Endpoints](#-endpoints)
- [Exemplos Práticos](#-exemplos-práticos)
- [Importar no Postman](#-importar-no-postman)
- [Configuração](#-configuração)

## 🛠 Tecnologias
- Java 17+
- Spring Boot 3.2
- PostgreSQL 14
- Docker e Docker Compose
- Flyway (migrações de banco)
- JPA/Hibernate

## ⚙️ Instalação

### Pré-requisitos
- Docker 24.0+
- Docker Compose 2.20+

```bash
# 1. Clone o repositório
git clone https://github.com/SiqueiraAndre/desafio-backend

# 2. Suba os serviços
docker compose -f Docker/docker-compose.yml up --build -d

# 3. Logs (opcional)
docker compose -f Docker/docker-compose.yml logs -f app postgres
```

- Aplicação: http://localhost:8080/api
- Banco de Dados: localhost:5432 (container postgres)

## 🔑 Autenticação
Autenticação HTTP Basic. Utilize um dos usuários em memória:

- admin / admin123 (ROLE_ADMIN)
- user / user123 (ROLE_USER)

Exemplo com cURL:
```bash
curl -u user:user123 http://localhost:8080/api/health
```

- Regras de acesso:
  - GET/POST/PUT/PATCH em /contas: USER ou ADMIN
  - DELETE em /contas/{id}: apenas ADMIN

## 📚 Documentação da API
Swagger UI:
```
http://localhost:8080/api/swagger-ui.html
```
OpenAPI JSON:
```
http://localhost:8080/api/v3/api-docs
```

## 📌 Endpoints

| Método | Endpoint                          | Descrição                                  |
|--------|-----------------------------------|--------------------------------------------|
| POST   | /api/contas                       | Cria nova conta                            |
| GET    | /api/contas                       | Lista contas com filtros e paginação        |
| GET    | /api/contas/{id}                  | Obtém conta por ID                         |
| PUT    | /api/contas/{id}                  | Atualiza conta (restrição: não PAGO)       |
| PATCH  | /api/contas/{id}/situacao         | Altera a situação da conta                 |
| DELETE | /api/contas/{id}                  | Remove conta (apenas ADMIN)                |
| GET    | /api/contas/total-pago            | Total pago por período                     |
| POST   | /api/contas/importacao            | Importa contas via CSV                     |
| GET    | /api/contas/importacao/template   | Baixa template CSV                         |
| GET    | /api/contas/importacao/info       | Informações sobre o formato de importação  |
| GET    | /api/health                       | Health check                               |
| GET    | /api/health/info                  | Informações da aplicação                   |

Observações de paginação/ordenção no GET /api/contas:
- Parâmetros: `page` (0..N), `size` (1..100), `sort` (ex.: `dataVencimento,asc` ou `valor,desc`)
- Filtros: `descricao`, `situacao` (PENDENTE, PAGO, VENCIDO, CANCELADO),
  `dataVencimentoInicioStr` e `dataVencimentoFimStr` (formato dd/MM/yyyy)

## 🚀 Exemplos Práticos

### 1) Criar Nova Conta
```http
POST /api/contas
Content-Type: application/json
Authorization: Basic <base64(user:user123)>

{
  "dataVencimento": "31/12/2024",
  "descricao": "Aluguel",
  "valor": 1500.50
}
```
Resposta (200/201):
```json
{
  "id": 1,
  "dataVencimento": "31/12/2024",
  "dataPagamento": null,
  "valor": 1500.50,
  "descricao": "Aluguel",
  "situacao": "PENDENTE"
}
```

### 2) Filtrar Contas
```http
GET /api/contas?descricao=Aluguel&dataVencimentoInicioStr=01/01/2024&dataVencimentoFimStr=31/12/2024&page=0&size=5&sort=valor,desc
Authorization: Basic <base64(user:user123)>
```
Resposta (paginada, exemplo simplificado):
```json
{
  "content": [
    {
      "id": 1,
      "descricao": "Aluguel",
      "valor": 1500.50,
      "dataVencimento": "31/12/2024",
      "situacao": "PENDENTE"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 5
}
```

### 3) Alterar Situação (Pagar Conta)
```http
PATCH /api/contas/1/situacao
Content-Type: application/json
Authorization: Basic <base64(user:user123)>

{
  "situacao": "PAGO",
  "dataPagamento": "15/12/2024"
}
```

### 4) Total Pago por Período
```http
GET /api/contas/total-pago?dataInicio=01/12/2024&dataFim=31/12/2024
Authorization: Basic <base64(user:user123)>
```

### 5) Importar CSV
```bash
curl -u admin:admin123 \
  -X POST -F "arquivo=@contas.csv" \
  http://localhost:8080/api/contas/importacao
```
- Formato esperado (separador ponto e vírgula):
```csv
data_vencimento;valor;descricao
15/08/2024;1500.50;Aluguel
20/08/2024;750.00;Manutenção
```
- Datas aceitas: `dd/MM/yyyy`, `yyyy-MM-dd`, `dd-MM-yyyy`

## 📌 Importar no Postman
1. Baixe a coleção: `Contas-a-Pagar.postman_collection.json` (na raiz do projeto)
2. Baixe a environment: `Contas-a-Pagar.postman_environment.json` (na raiz do projeto)
3. Importe ambos no Postman
4. Selecione a environment "Contas a Pagar - Local"
5. Para `/api/contas/importacao`, use o arquivo `template_importacao_contas.csv`

## 🛠 Configuração

application.properties (perfil padrão/local):
```properties
spring.application.name=contas-api

# Banco de dados local
spring.datasource.url=jdbc:postgresql://localhost:5432/contas_db
spring.datasource.username=contas_user
spring.datasource.password=contas_pass
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# Datas (JSON)
spring.jackson.date-format=dd/MM/yyyy
spring.jackson.serialization.write-dates-as-timestamps=false

# Server
server.port=8080
server.servlet.context-path=/api
```

Docker Compose (já configura variáveis para apontar para o container Postgres):
- App: http://localhost:8080/api
- DB: jdbc:postgresql://contas-postgres:5432/contas_db (interno aos containers)

---

Notas:
- Todas as consultas são paginadas (parâmetros `page`, `size`, `sort`).
- Para ordenação múltipla, repita `sort` (ex.: `sort=valor,desc&sort=dataVencimento,asc`).
- DELETE exige ROLE_ADMIN.
- CSV: primeira linha de cabeçalho é opcional e pode ser ignorada automaticamente.
