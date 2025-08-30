# Costify Web App 🎨

> **Angular 20 Frontend** - Modern single-page application for professional recipe cost management

[![Angular](https://img.shields.io/badge/Angular-20-red.svg)](https://angular.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9-blue.svg)](https://www.typescriptlang.org/)
[![Node.js](https://img.shields.io/badge/Node.js-24-green.svg)](https://nodejs.org/)
[![RxJS](https://img.shields.io/badge/RxJS-7.8-purple.svg)](https://rxjs.dev/)

## 📖 Overview

The Costify Web App is a modern Angular application that provides an intuitive interface for recipe cost calculation and management. Built with the latest Angular 20 features including **signals**, **standalone components**, and **modern control flow**.

## ✨ Key Features

### 🎯 Current Features (Foundation)
- **Modern Angular 20** - Built with latest framework features
- **Standalone Components** - No NgModule required, better performance
- **TypeScript 5.9** - Full type safety and modern language features
- **Responsive Design** - Mobile-first approach for kitchen environments
- **Hot Reload** - Instant development feedback
- **Production Build** - Optimized bundle for deployment

### 🚀 Planned Features (Coming Soon)
- **Recipe Cost Calculator** - Real-time cost calculation interface
- **Ingredient Manager** - Comprehensive ingredient database management
- **Recipe Builder** - Drag-and-drop recipe creation tool
- **Cost Analytics Dashboard** - Visual cost breakdowns and trends
- **User Authentication** - Secure login and user management
- **Offline Support** - PWA capabilities for kitchen use

## 🏗️ Architecture

### Component-Based Architecture

```
src/app/
├── 📱 app.ts                    # Root application component
├── ⚙️ app.config.ts             # Application configuration
├── 🛣️ app.routes.ts             # Routing configuration
├── 🎨 app.html                  # Root template
├── 💅 app.css                   # Root styles
└── 🧪 app.spec.ts              # Root component tests
```

### Modern Angular Features

- **Standalone Components**: Self-contained components without NgModules
- **Signals**: Reactive state management with built-in change detection
- **Control Flow**: Modern `@if`, `@for`, `@switch` syntax
- **Dependency Injection**: Constructor-based and `inject()` function patterns
- **Reactive Forms**: Type-safe form handling with validation

## 🛠️ Technology Stack

### Core Framework
- **Angular 20** - Latest version with modern features
- **Angular CLI** - Development tooling and build system
- **Angular Router** - Single-page application routing
- **Angular Forms** - Reactive forms with validation

### Language & Types
- **TypeScript 5.9** - Static typing and modern JavaScript features
- **RxJS 7.8** - Reactive programming for async operations
- **Zone.js 0.15** - Change detection and async operation handling

### Development Tools
- **Angular DevTools** - Browser extension for debugging
- **Hot Module Replacement** - Fast development reloads
- **Source Maps** - Debugging support in development
- **Tree Shaking** - Optimized production bundles

### Testing Framework
- **Jasmine** - Behavior-driven testing framework
- **Karma** - Test runner for unit tests
- **Angular Testing Utilities** - Component and service testing tools

### Build & Optimization
- **Vite** - Fast build tool and development server
- **esbuild** - JavaScript/TypeScript bundler
- **CSS Optimization** - Minification and bundling
- **Code Splitting** - Lazy loading for performance

## 🚀 Quick Start

### Prerequisites

- **Node.js 24+** - [Download Node.js](https://nodejs.org/)
- **npm** - Included with Node.js

### Development Setup

```bash
# Navigate to web app directory
cd apps/web

# Install dependencies
npm install

# Start development server
npm start

# Open in browser
# http://localhost:4200
```

### Using Make Commands (Recommended)

```bash
# From project root
make web-dev        # Start frontend dev server
make web-install    # Install dependencies
make web-build      # Build for production
make web-test       # Run unit tests
```

## 💻 Development Commands

### Essential Commands

| Command | Description |
|---------|-------------|
| `npm start` | Start development server (port 4200) |
| `npm run build` | Build for production |
| `npm test` | Run unit tests |
| `npm run test:watch` | Run tests in watch mode |

### Angular CLI Commands

```bash
# Generate new component
ng generate component features/recipe-list

# Generate service
ng generate service services/recipe

# Generate interface
ng generate interface models/ingredient

# Build for production
ng build --configuration production

# Analyze bundle size
ng build --stats-json
npx webpack-bundle-analyzer dist/web/stats.json
```

## 📁 Project Structure

```
src/
├── 📱 app/                      # Main application code
│   ├── app.ts                  # Root component
│   ├── app.config.ts           # App configuration
│   ├── app.routes.ts           # Route definitions
│   ├── app.html                # Root template
│   ├── app.css                 # Root styles
│   └── app.spec.ts             # Root component tests
├── 🌍 main.ts                   # Application bootstrap
├── 📄 index.html                # Entry HTML template
├── 🎨 styles.css                # Global styles
└── 🏠 public/                   # Static assets
    └── favicon.ico
```

### Future Structure (Planned)

```
src/app/
├── 🏗️ core/                    # Core application services
├── 📱 features/                # Feature modules
│   ├── recipes/               # Recipe management
│   ├── ingredients/           # Ingredient management
│   └── dashboard/             # Analytics dashboard
├── 🔗 shared/                  # Shared components and utilities
├── 🎨 styles/                  # Styling system
└── 🧪 testing/                 # Test utilities and mocks
```

## 🎨 Styling & UI

### Current Styling Approach
- **CSS Custom Properties** - Modern CSS variables for theming
- **Responsive Design** - Mobile-first media queries
- **Modern CSS** - Flexbox and Grid layouts
- **Component Styles** - Scoped styling per component

### Planned UI Enhancements
- **Angular Material** - Comprehensive UI component library
- **Dark/Light Theme** - Toggle between themes
- **Kitchen Mode** - High-contrast mode for kitchen environments
- **Print Styles** - Optimized recipe printouts

## 🧪 Testing

### Testing Structure

```bash
# Run all tests
npm test

# Run tests in watch mode (development)
npm run test:watch

# Run tests with coverage
npm run test:coverage

# Run specific test file
ng test --include='**/recipe.component.spec.ts'
```

### Testing Patterns

```typescript
// Example component test
describe('RecipeComponent', () => {
  let component: RecipeComponent;
  let fixture: ComponentFixture<RecipeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeComponent] // Standalone component
    }).compileComponents();
    
    fixture = TestBed.createComponent(RecipeComponent);
    component = fixture.componentInstance;
  });

  it('should calculate recipe cost correctly', () => {
    // Test implementation
  });
});
```

## 🔧 Configuration

### Angular Configuration

Key configuration files:
- **angular.json** - Angular CLI workspace configuration
- **tsconfig.json** - TypeScript compiler options
- **tsconfig.app.json** - Application TypeScript config
- **tsconfig.spec.json** - Test TypeScript config

### Environment Configuration

```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  appVersion: '1.0.0'
};

// src/environments/environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.costify.com',
  appVersion: '1.0.0'
};
```

### Build Configurations

| Configuration | Use Case | Optimization |
|---------------|----------|--------------|
| `development` | Local dev | Source maps, no minification |
| `production` | Deployment | Minified, tree-shaken |

## 🌐 API Integration

### HTTP Client Setup

```typescript
// Example service for API communication
@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  getRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(`${this.apiUrl}/recipes`);
  }

  calculateCost(recipeId: string): Observable<RecipeCost> {
    return this.http.post<RecipeCost>(
      `${this.apiUrl}/recipes/${recipeId}/calculate-cost`,
      {}
    );
  }
}
```

### Error Handling

```typescript
// Global error interceptor
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle API errors globally
        console.error('API Error:', error);
        return throwError(() => error);
      })
    );
  }
}
```

## 📱 Progressive Web App (PWA)

### PWA Features (Planned)
- **Service Worker** - Offline functionality
- **App Manifest** - Installable on devices
- **Push Notifications** - Recipe reminders
- **Background Sync** - Sync when connectivity returns

### PWA Setup (Future)

```bash
# Add PWA support
ng add @angular/pwa

# Build with PWA features
ng build --prod --service-worker
```

## 🚀 Build & Deployment

### Production Build

```bash
# Build for production
npm run build

# Build output location
dist/web/
├── index.html              # Entry point
├── main.[hash].js          # Application bundle
├── polyfills.[hash].js     # Browser polyfills  
├── styles.[hash].css       # Compiled styles
└── assets/                 # Static assets
```

### Build Optimization

- **Tree Shaking** - Remove unused code
- **Minification** - Compress JavaScript and CSS
- **Bundle Splitting** - Separate vendor and app code
- **Lazy Loading** - Load routes on demand
- **Ahead-of-Time (AOT)** - Compile templates during build

### Deployment Options

```bash
# Static hosting (Netlify, Vercel)
npm run build
# Deploy dist/web/ directory

# Docker deployment
FROM nginx:alpine
COPY dist/web /usr/share/nginx/html
EXPOSE 80

# CDN deployment
# Upload dist/web/ to AWS S3, Azure Blob, etc.
```

## 📊 Performance

### Performance Features
- **OnPush Change Detection** - Optimized change detection
- **Lazy Loading** - Load features on demand
- **Code Splitting** - Smaller initial bundle
- **Tree Shaking** - Remove unused code

### Performance Metrics
- **First Contentful Paint** - <1.5s
- **Largest Contentful Paint** - <2.5s
- **Bundle Size** - <250KB (gzipped)
- **Lighthouse Score** - 90+ (target)

### Performance Monitoring

```bash
# Analyze bundle size
ng build --stats-json
npx webpack-bundle-analyzer dist/web/stats.json

# Performance audit
ng build --prod
npx lighthouse http://localhost:4200 --view
```

## 🔧 Development Tools

### VS Code Extensions (Recommended)
- **Angular Language Service** - IntelliSense for Angular
- **Angular Snippets** - Code snippets for Angular
- **TypeScript Hero** - TypeScript refactoring tools
- **Prettier** - Code formatting
- **ESLint** - Code linting

### Chrome Extensions
- **Angular DevTools** - Debug Angular applications
- **Redux DevTools** - State management debugging (if using NgRx)

## 🎯 Future Roadmap

### Version 1.1 (Q2 2025)
- **Recipe Management UI** - Complete CRUD interface
- **Ingredient Database** - Searchable ingredient library
- **Cost Calculator** - Real-time cost calculation display

### Version 1.2 (Q3 2025)
- **Angular Material Integration** - Professional UI components
- **Progressive Web App** - Offline functionality
- **User Authentication** - Login and user management

### Version 2.0 (Q4 2025)
- **Advanced Analytics** - Cost trend visualization
- **Recipe Sharing** - Social features for recipe exchange
- **Mobile Apps** - Native iOS and Android applications

## 🤝 Contributing

### Development Guidelines
1. Use **standalone components** for all new components
2. Implement **OnPush change detection** for performance
3. Write **comprehensive tests** for components and services
4. Follow **Angular style guide** conventions
5. Use **reactive forms** for form handling

### Code Standards

```bash
# Format code
npm run format

# Lint code
npm run lint

# Type check
npm run type-check
```

## 📚 Resources

- **[Angular Documentation](https://angular.dev/)**
- **[Angular CLI Reference](https://angular.dev/tools/cli)**
- **[TypeScript Handbook](https://www.typescriptlang.org/docs/)**
- **[RxJS Documentation](https://rxjs.dev/guide/overview)**
- **[Angular Style Guide](https://angular.dev/style-guide)**

---

**🎨 Built with Angular 20 for modern web experiences**

*Signals • Standalone Components • Modern Architecture*
