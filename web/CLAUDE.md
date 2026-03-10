# CLAUDE.md — Costify Web

This file provides guidance to Claude Code when working with the `web/` module.

## Commands

All commands run via Docker from the **monorepo root** using `make`. Do not run bun/npm directly on the host.

### Development

```bash
# From monorepo root
make dev-web          # Start web dev server with hot reload (docker compose watch)
make deploy-web       # Build and start web container
make logs-web         # View web container logs
```

### Testing

```bash
make test-web         # Run all tests once (Vitest via Bun Docker)
```

### Build

```bash
make build-web                                    # Production build
make build-web VITE_COSTIFY_API_URL=<url>         # With custom API URL
```

### Linting (inside container or with bun locally)

```bash
bun run lint          # Biome check + fix
```

## Architecture

React 19 single-page application for recipe cost management.

### Tech Stack

- **Frontend**: React 19 + TypeScript 5.8
- **Build Tool**: Vite (rolldown-vite)
- **Styling**: TailwindCSS v4 + shadcn/ui (Radix UI primitives)
- **State Management**: Zustand v5 with localStorage persistence
- **Routing**: React Router v7
- **Data Fetching**: React Query (TanStack Query v5)
- **HTTP Client**: Axios
- **Forms**: React Hook Form + Zod validation
- **Testing**: Vitest + React Testing Library + jsdom
- **Linting**: Biome
- **Package Manager**: Bun

### Project Structure

```
web/src/
├── api/
│   ├── axios.ts                  # Axios instance configured with base URL
│   └── costify/
│       ├── client.ts             # API client config
│       └── queries/              # React Query hooks per feature
├── pages/
│   ├── home/                     # Home page
│   ├── ingredients/              # Ingredient management
│   │   ├── schemas/              # Zod validation schemas
│   │   ├── components/           # Feature-specific components
│   │   ├── list/                 # Ingredient list page
│   │   └── edit/                 # Ingredient create/edit page
│   ├── recipes/                  # Recipe management
│   │   ├── schemas/              # Zod validation schemas
│   │   ├── components/
│   │   └── edit/                 # Recipe create/edit page
│   └── NotFound.tsx
├── components/
│   ├── ui/                       # shadcn/ui primitives (button, dialog, select...)
│   └── Navigation.tsx
├── stores/
│   └── theme/                    # Theme store (light/dark) with Zustand + localStorage
│       ├── types.ts
│       ├── store.ts
│       └── ThemeEffects.tsx
├── routes/
│   └── index.tsx                 # React Router configuration
├── hooks/                        # Custom React hooks
├── lib/                          # Utility functions
└── __tests__/                    # Test files
```

### Key Patterns

- **Feature-based pages**: Each feature in `pages/` has its own schemas, components, and sub-pages
- **React Query**: All API calls go through hooks in `api/costify/queries/`. No direct axios calls in components
- **Zod schemas**: Form validation schemas co-located with features in `pages/<feature>/schemas/`
- **Theme**: Zustand store with localStorage persistence; `ThemeEffects` syncs class to `<html>` element
- **Path alias**: `@/*` maps to `./src/*`

### Environment Variables

| Variable              | Default                         | Description        |
|-----------------------|---------------------------------|--------------------|
| `VITE_COSTIFY_API_URL` | `http://localhost:8080/api`    | Backend API base URL|

Configure via `docker-compose.yml` or pass directly to `make build-web`.

### Docker

The `Dockerfile` has two targets:
- `development` (default in `docker-compose.yml`): runs `bun run dev` with hot reload
- `production`: runs `bun run build` and serves static files

### Testing

Test files use `.spec.ts` / `.spec.tsx` extension, co-located with the code they test or under `__tests__/`.

```bash
make test-web   # runs: bun install --frozen-lockfile && bun run test
```