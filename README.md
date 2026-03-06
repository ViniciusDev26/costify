# Costify - Recipe Cost Calculator

Sistema de cálculo de custos de receitas baseado em ingredientes, desenvolvido com Clean Architecture e Domain-Driven Design.

## Sobre o Projeto

Costify é uma plataforma que permite calcular o custo real de receitas culinárias com base nos preços dos ingredientes. Cada ingrediente possui quantidade, unidade de medida e preço, permitindo cálculos precisos do custo total de produção.

### Arquitetura

O projeto segue os princípios de **Clean Architecture** e **Domain-Driven Design**:

- **Domain Layer**: Lógica de negócio pura (entidades, value objects, regras de domínio)
- **Application Layer**: Casos de uso e orquestração da lógica de negócio
- **Infrastructure Layer**: Implementações técnicas (banco de dados, controllers REST, configurações)

Para mais detalhes arquiteturais, consulte [CLAUDE.md](CLAUDE.md).

## Stack Tecnológica

- **Java 21** (Eclipse Temurin)
- **Spring Boot 3.5.5**
- **PostgreSQL 16.9**
- **Maven** (gerenciamento de dependências)
- **Flyway** (migrações de banco de dados)
- **Docker & Docker Compose** (containerização)
- **JUnit 5 + Testcontainers** (testes)

## Pré-requisitos

**Este projeto roda exclusivamente via Docker.** Você precisa apenas de:

- [Docker](https://docs.docker.com/get-docker/) (versão 20.10+)
- [Docker Compose](https://docs.docker.com/compose/install/) (versão 1.29+)

**Não é necessário instalar Java, Maven ou PostgreSQL na sua máquina!**

## Início Rápido

```bash
# 1. Clone o repositório
git clone <repository-url>
cd costify

# 2. Inicie a aplicação
docker-compose up -d

# 3. Acompanhe os logs (opcional)
docker-compose logs -f app

# 4. Verifique se está rodando
curl http://localhost:8080/api/actuator/health
# Resposta esperada: {"status":"UP"}
```

**A aplicação estará disponível em:** `http://localhost:8080/api`

## Comandos Úteis

### Gerenciar Aplicação

```bash
# Ver status dos containers
docker-compose ps

# Parar a aplicação
docker-compose stop

# Iniciar novamente
docker-compose start

# Reiniciar a aplicação
docker-compose restart app

# Ver logs em tempo real
docker-compose logs -f app

# Parar e remover containers (mantém dados)
docker-compose down

# Parar e remover TUDO incluindo dados
docker-compose down -v
```

### Rebuild da Aplicação

Após fazer mudanças no código:

```bash
# Rebuild e restart
docker-compose up -d --build app

# Rebuild sem cache (força nova build)
docker-compose build --no-cache app
docker-compose up -d
```

### Acessar Containers

```bash
# Shell na aplicação
docker exec -it costify-app sh

# Shell no PostgreSQL
docker exec -it costify-postgres sh

# Acessar banco de dados
docker exec -it costify-postgres psql -U postgres -d costify
```

### Testar API

```bash
# Script de teste automatizado
chmod +x test-api.sh
./test-api.sh

# Health check
curl http://localhost:8080/api/actuator/health

# Listar unidades disponíveis
curl http://localhost:8080/api/units

# Listar ingredientes
curl http://localhost:8080/api/ingredients
```

## Estrutura de Diretórios

```
costify/
├── src/
│   ├── main/
│   │   ├── java/br/unifor/costify/
│   │   │   ├── application/          # Casos de uso e DTOs
│   │   │   ├── domain/               # Entidades e lógica de negócio
│   │   │   ├── infra/                # Controllers, repositories, config
│   │   │   └── CostifyApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── db/migration/         # Migrações Flyway
│   └── test/                         # Testes unitários e de integração
├── docker-compose.yml                # Orquestração de containers
├── Dockerfile                        # Build da aplicação
├── test-api.sh                       # Script de teste da API
├── DOCKER.md                         # Guia completo Docker
├── CLAUDE.md                         # Documentação de arquitetura
├── pom.xml                          # Dependências Maven
└── README.md                        # Este arquivo
```

## API Endpoints

Todos os endpoints estão disponíveis em `http://localhost:8080/api`.

### Ingredientes

#### Listar todos os ingredientes
```bash
GET /api/ingredients
```

**Resposta:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Leite",
    "packageQuantity": 1.0,
    "packagePrice": 5.50,
    "packageUnit": "L",
    "unitCost": 0.0055
  }
]
```

#### Buscar ingrediente por ID
```bash
GET /api/ingredients/{id}
```

#### Criar novo ingrediente
```bash
POST /api/ingredients
Content-Type: application/json

{
  "name": "Leite",
  "packageQuantity": 1.0,
  "packagePrice": 5.50,
  "packageUnit": "L"
}
```

#### Atualizar ingrediente
```bash
PUT /api/ingredients/{id}
Content-Type: application/json

{
  "name": "Leite Integral",
  "packageQuantity": 1.0,
  "packagePrice": 6.00,
  "packageUnit": "L"
}
```

### Receitas

#### Listar todas as receitas
```bash
GET /api/recipes
```

#### Buscar receita por ID
```bash
GET /api/recipes/{id}
```

#### Criar nova receita
```bash
POST /api/recipes
Content-Type: application/json

{
  "name": "Bolo de Chocolate",
  "ingredients": [
    {
      "ingredientId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 500.0,
      "unit": "ML"
    }
  ]
}
```

#### Atualizar receita
```bash
PUT /api/recipes/{id}
Content-Type: application/json

{
  "name": "Bolo de Chocolate Deluxe",
  "ingredients": [
    {
      "ingredientId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 600.0,
      "unit": "ML"
    }
  ]
}
```

#### Calcular custo da receita
```bash
GET /api/recipes/{id}/cost
```

**Resposta:**
```json
{
  "recipeName": "Bolo de Chocolate",
  "totalCost": 2.75,
  "ingredientCosts": [
    {
      "ingredientName": "Leite",
      "quantity": 500.0,
      "unit": "ML",
      "cost": 2.75
    }
  ]
}
```

### Unidades de Medida

#### Listar unidades disponíveis
```bash
GET /api/units
```

**Resposta:**
```json
[
  {
    "name": "ML",
    "type": "VOLUME",
    "factorToBase": 1.0
  },
  {
    "name": "L",
    "type": "VOLUME",
    "factorToBase": 1000.0
  },
  {
    "name": "G",
    "type": "WEIGHT",
    "factorToBase": 1.0
  },
  {
    "name": "KG",
    "type": "WEIGHT",
    "factorToBase": 1000.0
  },
  {
    "name": "TBSP",
    "type": "VOLUME",
    "factorToBase": 15.0
  },
  {
    "name": "TBSP_BUTTER",
    "type": "WEIGHT",
    "factorToBase": 14.0
  },
  {
    "name": "UN",
    "type": "UNIT",
    "factorToBase": 1.0
  }
]
```

## Configuração

### Variáveis de Ambiente

As configurações são gerenciadas via `docker-compose.yml`. Para alterar, edite diretamente o arquivo:

```yaml
services:
  app:
    environment:
      # Database
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: costify
      DB_USER: postgres
      DB_PASSWORD: postgres

      # Application
      SERVER_PORT: 8080
      CONTEXT_PATH: /api

      # Connection Pool
      DB_POOL_SIZE: 20
      DB_POOL_MIN_IDLE: 5
```

### Alterar Porta da Aplicação

Edite `docker-compose.yml`:

```yaml
services:
  app:
    ports:
      - "8081:8080"  # Mapeie 8081 do host para 8080 do container
```

### Perfis do Spring

O projeto possui 3 perfis:

- **dev**: Desenvolvimento (logs verbosos, SQL visível)
- **prod**: Produção (logs otimizados, sem SQL)
- **test**: Testes (usado pelo Testcontainers)

Por padrão, o Docker usa **prod**. Para alterar:

```yaml
services:
  app:
    environment:
      SPRING_PROFILES_ACTIVE: dev  # ou prod
```

## Gerenciamento de Dados

### Backup do Banco

```bash
# Criar backup
docker exec costify-postgres pg_dump -U postgres costify > backup.sql

# Restaurar backup
docker exec -i costify-postgres psql -U postgres costify < backup.sql
```

### Consultar Dados

```bash
# Listar tabelas
docker exec -it costify-postgres psql -U postgres -d costify -c "\dt"

# Ver ingredientes
docker exec -it costify-postgres psql -U postgres -d costify -c "SELECT * FROM ingredients;"

# Ver receitas
docker exec -it costify-postgres psql -U postgres -d costify -c "SELECT * FROM recipes;"

# Ver histórico de migrações Flyway
docker exec -it costify-postgres psql -U postgres -d costify -c "SELECT * FROM flyway_schema_history ORDER BY installed_on;"
```

### Resetar Dados

```bash
# Parar containers e remover volumes (apaga TODOS os dados)
docker-compose down -v

# Reiniciar do zero
docker-compose up -d
```

## Troubleshooting

### Porta 8080 já está em uso

```bash
# Opção 1: Parar processo que usa a porta
sudo lsof -ti:8080 | xargs kill -9

# Opção 2: Alterar porta no docker-compose.yml
# ports: ["8081:8080"]
```

### Container não inicia

```bash
# Ver logs detalhados
docker-compose logs app

# Verificar health check
docker inspect costify-app | grep -A 10 Health

# Reconstruir sem cache
docker-compose build --no-cache app
docker-compose up -d
```

### Erro de conexão com banco

```bash
# Verificar se PostgreSQL está saudável
docker-compose ps postgres

# Ver logs do PostgreSQL
docker-compose logs postgres

# Reiniciar PostgreSQL
docker-compose restart postgres
```

### Aplicação reinicia constantemente

```bash
# Ver últimos 100 logs
docker-compose logs --tail=100 app

# Verificar memória disponível
docker stats costify-app

# Aumentar timeout do health check (docker-compose.yml)
# healthcheck:
#   start_period: 120s
```

## Desenvolvimento

### Rodar Testes

Os testes requerem acesso ao código-fonte e ao Maven dentro do container:

```bash
# Rodar todos os testes
docker-compose exec app ./mvnw test -DargLine="-ea"

# Rodar teste específico
docker-compose exec app ./mvnw test -Dtest=IngredientControllerTest

# Ver relatório de testes
docker-compose exec app cat target/surefire-reports/*.txt
```

### Hot Reload (Desenvolvimento)

Para desenvolvimento com reload automático, você pode montar o código-fonte:

1. Edite `docker-compose.yml` e adicione:
```yaml
services:
  app:
    volumes:
      - ./src:/app/src
    command: ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

2. Rebuild e restart:
```bash
docker-compose up -d --build app
```

Agora mudanças no código serão refletidas automaticamente (pode demorar ~10s).

### Verificar Logs de Performance

```bash
# CPU e Memória em tempo real
docker stats costify-app

# Logs com timestamp
docker-compose logs -f -t app

# Logs das últimas 24h
docker-compose logs --since 24h app
```

## Health Checks

A aplicação expõe endpoints de monitoramento:

```bash
# Health básico
curl http://localhost:8080/api/actuator/health

# Liveness (aplicação está viva)
curl http://localhost:8080/api/actuator/health/liveness

# Readiness (aplicação está pronta para tráfego)
curl http://localhost:8080/api/actuator/health/readiness
```

## Documentação Adicional

- **[DOCKER.md](DOCKER.md)**: Guia completo de comandos Docker
- **[CLAUDE.md](CLAUDE.md)**: Arquitetura detalhada e decisões de design
- **[test-api.sh](test-api.sh)**: Script de teste da API

## Licença

Este projeto é um projeto acadêmico da Universidade de Fortaleza (UNIFOR).

## Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Contato

Para dúvidas ou sugestões, abra uma issue no repositório.
