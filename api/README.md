# Costify API

Backend do projeto Costify. REST API para gerenciamento de ingredientes e receitas, com cálculo de custos.

## Tech Stack

- **Java 21** (Eclipse Temurin)
- **Spring Boot 3.5.5**, Maven
- **PostgreSQL 16.9**, Flyway (migrations)
- **JUnit 5** + Testcontainers (testes)
- **Lombok**, Spring Security

## Arquitetura

Clean Architecture com três camadas:

- **`domain/`** — entidades, value objects, regras de negócio (sem dependências externas)
- **`application/`** — casos de uso, DTOs, contratos de repositório
- **`infra/`** — controllers REST, repositórios JPA, configurações Spring

## Comandos

### Via monorepo root (preferido)

```bash
make test-api                        # Rodar todos os testes
make test-api-class CLASS=FooTest    # Rodar teste específico
make deploy-api                      # Build e start da API
make logs-api                        # Ver logs
make up-db                           # Iniciar apenas o PostgreSQL
```

### Via Makefile local (dentro de api/)

```bash
make test                  # Rodar todos os testes
make test-class CLASS=Foo  # Rodar teste específico
make up                    # Iniciar postgres + app
make deploy                # Rebuild e iniciar app
make logs                  # Ver logs do app
make down                  # Parar containers
```

## Endpoints

Base URL: `http://localhost:8080/api`

| Método | Rota                        | Descrição                   |
|--------|-----------------------------|-----------------------------|
| GET    | `/ingredients`              | Listar ingredientes          |
| GET    | `/ingredients/{id}`         | Buscar ingrediente por ID   |
| POST   | `/ingredients`              | Criar ingrediente            |
| PUT    | `/ingredients/{id}`         | Atualizar ingrediente        |
| GET    | `/recipes`                  | Listar receitas              |
| GET    | `/recipes/{id}`             | Buscar receita por ID        |
| POST   | `/recipes`                  | Criar receita                |
| PUT    | `/recipes/{id}`             | Atualizar receita            |
| GET    | `/recipes/{id}/cost`        | Calcular custo da receita    |
| GET    | `/units`                    | Listar unidades disponíveis  |
| GET    | `/actuator/health`          | Health check                 |

## Unidades Disponíveis

| Nome         | Tipo   | Fator base |
|--------------|--------|------------|
| `ML`         | VOLUME | 1.0        |
| `L`          | VOLUME | 1000.0     |
| `TBSP`       | VOLUME | 15.0       |
| `G`          | WEIGHT | 1.0        |
| `KG`         | WEIGHT | 1000.0     |
| `TBSP_BUTTER`| WEIGHT | 14.0       |
| `UN`         | UNIT   | 1.0        |

## Variáveis de Ambiente

| Variável               | Padrão    |
|------------------------|-----------|
| `DB_HOST`              | postgres  |
| `DB_PORT`              | 5432      |
| `DB_NAME`              | costify   |
| `DB_USER`              | postgres  |
| `DB_PASSWORD`          | postgres  |
| `SERVER_PORT`          | 8080      |
| `CONTEXT_PATH`         | /api      |
| `DB_POOL_SIZE`         | 20        |
| `DB_POOL_MIN_IDLE`     | 5         |
| `SPRING_PROFILES_ACTIVE` | prod    |

## Migrations

Gerenciadas via Flyway em `src/main/resources/db/migration/`:

- `V1` — Criação das tabelas `ingredients` e `recipes`
- `V2` — Conversão de campos de unidade para enum
- `V3` — Remoção da coluna `unit_cost`
- `V4` — Adição da coluna `total_cost` em `recipes`

<!-- ER_DIAGRAM_START -->
## Database ER Diagram

```mermaid
erDiagram

    "measurement_unit (ENUM)" {
        ML string
        L string
        G string
        KG string
        UN string
        TBSP string
        TBSP_BUTTER string
    }

    flyway_schema_history {
        installed_rank integer PK
        version varchar(50)
        description varchar(200)
        type varchar(20)
        script varchar(1000)
        checksum integer
        installed_by varchar(100)
        installed_on timestamp_without_time_zone
        execution_time integer
        success boolean
        string INDEX_flyway_schema_history_s_idx_success
    }

    ingredients {
        id varchar(255) PK
        name varchar(255) UK
        package_quantity numeric(10_3)
        package_price numeric(10_2)
        created_at timestamp_without_time_zone
        updated_at timestamp_without_time_zone
        package_unit measurement_unit
        string INDEX_idx_ingredients_name_name
        string INDEX_idx_ingredients_package_unit_package_unit
        string UNIQUE-INDEX_ingredients_name_key_name
    }

    recipe_ingredients {
        id integer PK
        recipe_id varchar(255) FK
        ingredient_id varchar(255) FK
        quantity numeric(10_3)
        created_at timestamp_without_time_zone
        unit measurement_unit
        string INDEX_idx_recipe_ingredients_ingredient_id_ingredient_id
        string INDEX_idx_recipe_ingredients_recipe_id_recipe_id
        string INDEX_idx_recipe_ingredients_unit_unit
        string UNIQUE-INDEX_uk_recipe_ingredient_recipe_id_ingredient_id
    }

    recipes {
        id varchar(255) PK
        name varchar(255) UK
        created_at timestamp_without_time_zone
        updated_at timestamp_without_time_zone
        total_cost numeric(10_2)
        string INDEX_idx_recipes_name_name
        string UNIQUE-INDEX_recipes_name_key_name
    }

    ingredients ||--o{ recipe_ingredients : "has"
    recipes ||--o{ recipe_ingredients : "has"

    ingredients }o--|| "measurement_unit (ENUM)" : "uses"
    recipe_ingredients }o--|| "measurement_unit (ENUM)" : "uses"
```
<!-- ER_DIAGRAM_END -->
