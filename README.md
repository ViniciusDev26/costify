# Costify - Recipe Cost Calculator

Sistema de cálculo de custos de receitas baseado em ingredientes, desenvolvido como monorepo com backend Java e frontend React.

## Sobre o Projeto

Costify é uma plataforma que permite calcular o custo real de receitas culinárias com base nos preços dos ingredientes. Cada ingrediente possui quantidade, unidade de medida e preço, permitindo cálculos precisos do custo total de produção.

## Estrutura do Monorepo

```
costify/
├── api/          # Backend - Java 21 + Spring Boot (Clean Architecture)
├── web/          # Frontend - React 19 + TypeScript + Vite
├── docs/         # Documentação e diagramas (ER diagram)
├── Makefile      # Orquestração unificada dos módulos
└── docker-compose.yml  # Stack completo (postgres + api + web)
```

## Stack Tecnológica

### Backend (`api/`)
- **Java 21** (Eclipse Temurin)
- **Spring Boot 3.5.5**
- **PostgreSQL 16.9**
- **Maven** (gerenciamento de dependências)
- **Flyway** (migrações de banco de dados)
- **JUnit 5 + Testcontainers** (testes)

### Frontend (`web/`)
- **React 19** + **TypeScript**
- **Vite** (rolldown-vite)
- **TailwindCSS v4** + **shadcn/ui**
- **Zustand** (estado), **React Router v7** (rotas)
- **React Query** (integração com API)
- **Bun** (package manager)
- **Vitest** (testes)

## Pré-requisitos

**Este projeto roda exclusivamente via Docker.** Você precisa apenas de:

- [Docker](https://docs.docker.com/get-docker/) (versão 20.10+)
- [Docker Compose](https://docs.docker.com/compose/install/) (versão 2.x+)
- [Make](https://www.gnu.org/software/make/)

**Não é necessário instalar Java, Maven, Node.js ou Bun na sua máquina!**

## Início Rápido

```bash
# 1. Clone o repositório
git clone <repository-url>
cd costify

# 2. Inicie o stack completo (postgres + api + web)
make up

# 3. Verifique se está rodando
curl http://localhost:8080/api/actuator/health
# Resposta esperada: {"status":"UP"}
```

**Serviços disponíveis:**
- API: `http://localhost:8080/api`
- Web: `http://localhost:5173`
- PostgreSQL: `localhost:5432`

## Comandos Makefile

### Stack Completo

```bash
make up           # Inicia todos os serviços
make down         # Para e remove os containers
make deploy       # Rebuilda e inicia todos os serviços
make logs         # Acompanha logs de todos os serviços
```

### API (Backend)

```bash
make up-db        # Inicia apenas o PostgreSQL
make deploy-api   # Rebuilda e inicia a API
make logs-api     # Logs da API em tempo real
make test-api     # Roda todos os testes da API
make test-api-class CLASS=IngredientTest  # Roda teste específico
```

### Web (Frontend)

```bash
make dev-web      # Modo desenvolvimento com hot reload (docker compose watch)
make deploy-web   # Rebuilda e inicia o frontend
make logs-web     # Logs do frontend em tempo real
make test-web     # Roda todos os testes do frontend
make build-web    # Build de produção
```

### Testes

```bash
make test         # Roda os testes de API e Web em sequência
```

## Estrutura de Diretórios Detalhada

```
costify/
├── api/                                  # Backend (Java/Spring Boot)
│   ├── src/
│   │   ├── main/java/br/unifor/costify/
│   │   │   ├── application/              # Casos de uso, DTOs, factories
│   │   │   ├── domain/                   # Entidades e lógica de negócio
│   │   │   ├── infra/                    # Controllers, repositories, config
│   │   │   └── CostifyApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/             # Migrações Flyway (V1-V4)
│   ├── Dockerfile
│   ├── docker-compose.yml                # Stack local de desenvolvimento
│   ├── Makefile
│   └── pom.xml
├── web/                                  # Frontend (React/TypeScript)
│   ├── src/
│   │   ├── api/costify/                  # Cliente HTTP e React Query hooks
│   │   ├── pages/                        # Páginas (ingredients, recipes, home)
│   │   ├── components/ui/               # Componentes shadcn/ui
│   │   ├── stores/                       # Zustand stores (theme, etc.)
│   │   ├── routes/                       # Configuração React Router
│   │   └── hooks/
│   ├── Dockerfile
│   ├── package.json
│   └── vite.config.ts
├── docs/
│   ├── database-er-diagram.mmd           # Diagrama ER (gerado automaticamente)
│   └── README.md
├── .github/workflows/
│   └── build-and-test.yml               # CI: testa API + Web + gera ER diagram
├── Makefile                              # Orquestração raiz
└── docker-compose.yml                   # Stack completo
```

## API Endpoints

Todos os endpoints estão disponíveis em `http://localhost:8080/api`.

### Ingredientes

```bash
GET    /api/ingredients          # Listar todos
GET    /api/ingredients/{id}     # Buscar por ID
POST   /api/ingredients          # Criar novo
PUT    /api/ingredients/{id}     # Atualizar
```

**Criar ingrediente:**
```json
{
  "name": "Leite",
  "packageQuantity": 1.0,
  "packagePrice": 5.50,
  "packageUnit": "L"
}
```

### Receitas

```bash
GET    /api/recipes              # Listar todas
GET    /api/recipes/{id}         # Buscar por ID
POST   /api/recipes              # Criar nova
PUT    /api/recipes/{id}         # Atualizar
GET    /api/recipes/{id}/cost    # Calcular custo detalhado
```

### Unidades de Medida

```bash
GET    /api/units                # Listar unidades disponíveis
```

**Unidades disponíveis:** `ML`, `L`, `TBSP` (volume) | `G`, `KG`, `TBSP_BUTTER` (peso) | `UN` (unidade)

## Configuração

### Variáveis de Ambiente

```yaml
# API (docker-compose.yml)
DB_HOST: postgres
DB_PORT: 5432
DB_NAME: costify
DB_USER: postgres
DB_PASSWORD: postgres
SERVER_PORT: 8080
CONTEXT_PATH: /api

# Web
VITE_COSTIFY_API_URL: http://localhost:8080/api
```

## CI/CD

O projeto usa **GitHub Actions** com três jobs:

- **API Tests**: `make test-api` (Maven + Testcontainers via Docker)
- **Web Tests**: `make test-web` (Vitest via Bun Docker)
- **Web Build**: `make build-web` (build de produção)
- **ER Diagram**: geração automática do diagrama de banco ao fazer push em `main`

## Documentação Adicional

- **[api/CLAUDE.md](api/CLAUDE.md)**: Arquitetura detalhada do backend
- **[web/CLAUDE.md](web/CLAUDE.md)**: Arquitetura e convenções do frontend
- **[docs/README.md](docs/README.md)**: Diagrama ER do banco de dados

## Licença

Este projeto é um projeto acadêmico da Universidade de Fortaleza (UNIFOR).