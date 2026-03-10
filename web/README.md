# Costify Web

Frontend React do projeto Costify. Para orquestração completa, use os comandos `make` na raiz do monorepo.

## Tech Stack

- **React 19** + TypeScript 5.8
- **Vite** (rolldown-vite) — build tool
- **TailwindCSS v4** + **shadcn/ui** — UI e estilos
- **Zustand v5** — estado global com persistência
- **React Router v7** — roteamento
- **React Query (TanStack)** — data fetching e cache
- **React Hook Form** + **Zod** — formulários e validação
- **Bun** — package manager
- **Vitest** + React Testing Library — testes
- **Biome** — linter

## Comandos (via monorepo root)

```bash
make dev-web        # Desenvolvimento com hot reload
make deploy-web     # Build e start do container
make test-web       # Rodar testes
make build-web      # Build de produção
make logs-web       # Ver logs
```

## Estrutura do Projeto

```
src/
├── api/
│   ├── axios.ts              # Instância Axios configurada
│   └── costify/
│       ├── client.ts
│       └── queries/          # React Query hooks por feature
├── pages/
│   ├── home/
│   ├── ingredients/          # Listagem e edição de ingredientes
│   │   ├── schemas/          # Schemas Zod
│   │   ├── components/
│   │   ├── list/
│   │   └── edit/
│   ├── recipes/              # Listagem e edição de receitas
│   │   ├── schemas/
│   │   ├── components/
│   │   └── edit/
│   └── NotFound.tsx
├── components/
│   ├── ui/                   # Primitivos shadcn/ui
│   └── Navigation.tsx
├── stores/
│   └── theme/                # Tema claro/escuro com persistência
├── routes/
│   └── index.tsx
├── hooks/
└── lib/
```

## Variáveis de Ambiente

| Variável               | Padrão                       |
|------------------------|------------------------------|
| `VITE_COSTIFY_API_URL` | `http://localhost:8080/api`  |

Copie `.env.example` para `.env` para desenvolvimento local.