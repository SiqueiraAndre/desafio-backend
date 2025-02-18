# Desafio Backend

## Documentação da API de Contas a Pagar
 
Todas as requisições precisam incluir no cabeçalho:
```
Authorization: ApiKey #32373c
```
- Cadastrar conta;
- Atualizar conta;
- Alterar a situação da conta;
- Obter a lista de contas a pagar, com filtro de data de vencimento e descrição;
- Obter conta filtrando o id;
- Obter valor total pago por período.

## Tecnologias Utilizadas
- Java 17+
- Docker
- Postgresql 14
- Maven
- Spring Boot 3.2.0

## Instruções de instalação
#### 1. Faça o clone ou baixe o projeto e extraia para o local desejado
#### 2. Execute o docker:
```bash
docker compose -f docker/docker-compose.yml up --build
```
### Importar Postman
* Acesse o arquivo em formato JSON que está salvo nesse repositório [Contas-a-Pagar.postman_collection.json](Contas-a-Pagar.postman_collection.json)

### Arquivo CSV
* Para utilizar o end-point importar utilize o arquivo [arquivo.csv](arquivo.csv)


---

## Endpoints

### 1. Cadastrar uma nova conta

**Endpoint:**
```
POST /api/contas
```

**Parâmetros:**
- `dataVencimento` (Query, LocalDate): Data de Vencimento da conta.
- `descricao` (Query, String): Descrição da conta.
- `situacao` (Query, String): situação da conta.
- `valor` (Query, BigDecimal): Valor da conta.

**Resposta:**
- `200 OK`: Retorna os dados da conta.

**Exemplo de requisição:**
```
POST /api/contas
Authorization: ApiKey #32373c
```
```json
{
  "dataVencimento": "2025-02-20",
  "descricao": "Energia",
  "situacao": "PENDENTE",
  "valor": 110.99
}
```

---

### 2. Atualizar uma conta

**Endpoint:**
```
POST /api/contas/{id}
```

**Parâmetros:**
- `id` (Path Variable, Long): ID da conta a pagar.
- `dataPagamento` (Query, LocalDate): Data de Pagamento da conta.
- `dataVencimento` (Query, LocalDate): Data de Vencimento da conta.
- `descricao` (Query, String): Descrição da conta.
- `situacao` (Query, String): situação da conta.
- `valor` (Query, BigDecimal): Valor da conta.

**Resposta:**
- `200 OK`: Retorna os dados da conta.

**Exemplo de requisição:**
```
POST /api/contas/5
Authorization: ApiKey #32373c
```
```json
{
  "dataPagamento": null,
  "dataVencimento": "2025-12-31",
  "descricao": "Aluguel",
  "situacao": "PENDENTE",
  "valor": 1000.50
}
```

---

### 3. Alterar a situação da conta

**Endpoint:**
```
PATCH /api/contas/{id}/situacao
```

**Parâmetros:**
- `id` (Path Variable, Long): ID da conta a pagar.
- `situacao` (Query, String): situação da conta.

**Resposta:**
- `200 OK`: Retorna os detalhes da conta a pagar.

**Exemplo de requisição:**
```
PATCH /api/contas/10/situacao
Authorization: ApiKey #32373c
```
**Exemplo de resposta:**
```json
{
  "id": 10,
  "descricao": "Energia",
  "valor": 100.99,
  "dataVencimento": "2025-02-20",
  "dataPagamento": "2025-02-17",
  "situacao": "PAGA"
}
```

---

### 4. Obter a lista de contas a pagar, com filtro de data de vencimento e descrição (com Paginação)

**Endpoint:**
```
GET /api/contas
```

**Parâmetros:**
- `dataVencimento` (Query, LocalDate) - Opcional: Filtra pelo vencimento.
- `descricao` (Query, String) - Opcional: Filtra pela descrição.
- `page` (Query, Integer) - Opcional: Número da página (começando em 0). Valor padrão: `0`.
- `size` (Query, Integer) - Opcional: Quantidade de registros por página. Valor padrão: `10`.
- `sort` (Query, String) - Opcional: Campo para ordenação (exemplo: `dueDate,desc`).

**Resposta:**
- `200 OK`: Retorna uma página de contas a pagar no formato paginado.

**Exemplo de requisição:**
```
GET /api/contas?page=0&size=10&sort=dueDate,desc
Authorization: ApiKey #32373c
```

**Exemplo de resposta:**
```json
{
  "content": [
    {
      "id": 12,
      "descricao": "Material de escritório",
      "valor": 325.75,
      "dataVencimento": "2024-09-05",
      "dataPagamento": "2024-09-01",
      "situacao": "PAGA"
    },
    {
      "id": 13,
      "descricao": "Serviços de consultoria",
      "valor": 890.00,
      "dataVencimento": "2024-08-10",
      "dataPagamento": "2024-08-12",
      "situacao": "PAGA"
    },
    {
      "id": 14,
      "descricao": "Licença de softwarea",
      "valor": 1200.00,
      "dataVencimento": "2024-08-25",
      "dataPagamento": null,
      "situacao": "PENDENTE"
    },
    {
      "id": 15,
      "descricao": "Energia",
      "valor": 110.99,
      "dataVencimento": "2025-02-20",
      "dataPagamento": null,
      "situacao": "PENDENTE"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 13,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "numberOfElements": 13,
  "first": true,
  "empty": false
}
```

---

### 5. Obter Conta a Pagar por ID

**Endpoint:**
```
PATCH /api/contas/{id}
```

**Parâmetros:**
- `id` (Path Variable, Long): ID da conta a pagar.

**Resposta:**
- `200 OK`: Retorna os detalhes da conta a pagar.

**Exemplo de requisição:**
```
GET /api/contas/1
Authorization: ApiKey #32373c
```

---

### 6. Obter Total Pago por período.

**Endpoint:**
```
GET /api/contas/total-pago
```

**Parâmetros:**
- `dataInicial` (Query, LocalDate): Data inicial.
- `dataFinal` (Query, LocalDate): Data final.

**Resposta:**
- `200 OK`: Retorna o total pago no período.

**Exemplo de requisição:**
```
GET /contas/total-pago?dataInicial=2023-01-01&dataFinal=2023-12-31
Authorization: ApiKey #32373c
```