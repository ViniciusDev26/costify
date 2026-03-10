# Costify Web

A modern React web application for cost tracking and management, built with the latest web technologies.

## Features

- ✨ Modern React 19 with TypeScript
- 🎨 Beautiful UI with Tailwind CSS and shadcn/ui components
- 🌙 Dark/Light theme toggle with persistence
- 📱 Responsive design
- ⚡ Fast development with Vite
- 🧪 Testing setup with Vitest and React Testing Library
- 🗂️ State management with Zustand

## Tech Stack

- **Frontend**: React 19, TypeScript
- **Build Tool**: Vite (rolldown-vite)
- **Styling**: Tailwind CSS v4
- **UI Components**: shadcn/ui, Radix UI
- **State Management**: Zustand
- **Routing**: React Router v7
- **Testing**: Vitest, React Testing Library, jsdom
- **Linting**: ESLint

## Getting Started

### Prerequisites

- Node.js (recommended: latest LTS)
- npm

### Installation

1. Clone the repository
```bash
git clone <repository-url>
cd costify-web
```

2. Install dependencies
```bash
npm install
```

3. Start the development server
```bash
npm run dev
```

The application will be available at `http://localhost:5173`

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm test` - Run tests
- `npm run test:watch` - Run tests in watch mode
- `npm run lint` - Run ESLint

## Project Structure

```
src/
├── components/
│   └── ui/           # Reusable UI components (shadcn/ui)
├── stores/
│   └── theme/        # Theme state management
├── lib/              # Utilities and helpers
├── App.tsx           # Main application component
└── main.tsx          # Application entry point
```

## Key Libraries

- [shadcn/ui](https://ui.shadcn.com/) - Beautiful and accessible UI components
- [React Router](https://reactrouter.com) - Declarative routing for React
- [Zustand](https://zustand-demo.pmnd.rs/) - Simple state management
- [Tailwind CSS](https://tailwindcss.com/) - Utility-first CSS framework
- [Radix UI](https://www.radix-ui.com/) - Low-level UI primitives

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
