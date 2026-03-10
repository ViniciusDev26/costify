# ─── Base ─────────────────────────────────────────────────────────────────────
FROM oven/bun:1-alpine AS base

WORKDIR /app
COPY package.json bun.lock ./

# ─── Dependencies ─────────────────────────────────────────────────────────────
FROM base AS deps
RUN bun install --frozen-lockfile

# ─── Development ──────────────────────────────────────────────────────────────
FROM deps AS development

COPY . .

EXPOSE 5173

CMD ["bun", "run", "dev", "--host", "0.0.0.0"]

# ─── Builder ──────────────────────────────────────────────────────────────────
FROM deps AS builder

ARG VITE_COSTIFY_API_URL
ENV VITE_COSTIFY_API_URL=$VITE_COSTIFY_API_URL

COPY . .
RUN bun run build

# ─── Production ───────────────────────────────────────────────────────────────
FROM nginx:alpine AS production

COPY --from=builder /app/dist /usr/share/nginx/html

RUN printf 'server {\n\
  listen 80;\n\
  root /usr/share/nginx/html;\n\
  index index.html;\n\
  location / {\n\
    try_files $uri $uri/ /index.html;\n\
  }\n\
}\n' > /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]