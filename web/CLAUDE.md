# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Development
- `npm run dev` - Start development server with Vite
- `npm run build` - Build for production (runs TypeScript compiler first, then Vite build)
- `npm run preview` - Preview production build locally

### Testing
- `npm test` or `npm run test` - Run all tests once with Vitest
- `npm run test:watch` - Run tests in watch mode

### Code Quality
- `npm run lint` - Run ESLint for code linting

## Architecture

This is a React web application for cost tracking ("Costify") built with modern tooling:

### Tech Stack
- **Frontend**: React 19 with TypeScript
- **Build Tool**: Vite (using rolldown-vite for performance)
- **Styling**: TailwindCSS v4 with shadcn/ui components
- **State Management**: Zustand with persistence
- **Routing**: React Router v7
- **Testing**: Vitest with jsdom and React Testing Library
- **Linting**: ESLint with TypeScript and React plugins

### Project Structure
- `src/components/ui/` - Reusable UI components (Button, Switch) based on shadcn/ui
- `src/stores/` - Zustand state stores organized by domain
  - `src/stores/theme/` - Theme management with types, store, and effect component
- Path alias `@/*` maps to `./src/*`

### Key Patterns
- **Theme System**: Uses Zustand store with localStorage persistence for light/dark theme toggle
- **Component Architecture**: Follows shadcn/ui patterns with Radix UI primitives
- **State Management**: Domain-organized stores in `/stores` with TypeScript types
- **Testing**: Co-located spec files (`.spec.ts/.tsx`) alongside components/stores

### Dependencies
- UI: @radix-ui components, lucide-react icons
- Styling: tailwindcss, clsx, tailwind-merge, class-variance-authority
- State: zustand
- Testing: vitest, @testing-library/react, jsdom