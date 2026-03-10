M2_CACHE       := $(HOME)/.m2
API_DIR        := $(shell pwd)/api
WEB_DIR        := $(shell pwd)/web
TC_HOST        ?= host.docker.internal
DOCKER_NETWORK ?=

# ─── Full stack ───────────────────────────────────────────────────────────────

up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

deploy:
	docker compose up -d --build

# ─── API ──────────────────────────────────────────────────────────────────────

up-db:
	docker compose up -d postgres

test-api:
	docker run --rm \
		$(DOCKER_NETWORK) \
		-v /var/run/docker.sock:/var/run/docker.sock \
		-v $(API_DIR):/app \
		-v $(M2_CACHE):/root/.m2 \
		-w /app \
		-e TESTCONTAINERS_HOST_OVERRIDE=$(TC_HOST) \
		maven:3.9-eclipse-temurin-21 \
		./mvnw test -DargLine="-ea"

test-api-class:
	docker run --rm \
		$(DOCKER_NETWORK) \
		-v /var/run/docker.sock:/var/run/docker.sock \
		-v $(API_DIR):/app \
		-v $(M2_CACHE):/root/.m2 \
		-w /app \
		-e TESTCONTAINERS_HOST_OVERRIDE=$(TC_HOST) \
		maven:3.9-eclipse-temurin-21 \
		./mvnw test -DargLine="-ea" -Dtest="$(CLASS)"

deploy-api:
	docker compose up -d --build api

logs-api:
	docker compose logs -f api

# ─── Web ──────────────────────────────────────────────────────────────────────

dev-web:
	docker compose watch web

test-web:
	docker run --rm \
		-v $(WEB_DIR):/app \
		-w /app \
		oven/bun:1-alpine \
		sh -c "bun install --frozen-lockfile && bun run test"

build-web:
	docker run --rm \
		-v $(WEB_DIR):/app \
		-w /app \
		-e VITE_COSTIFY_API_URL=$(VITE_COSTIFY_API_URL) \
		oven/bun:1-alpine \
		sh -c "bun install --frozen-lockfile && bun run build"

deploy-web:
	docker compose up -d --build web

logs-web:
	docker compose logs -f web

# ─── Tests ────────────────────────────────────────────────────────────────────

test: test-api test-web

.PHONY: up down logs deploy up-db \
        test-api test-api-class deploy-api logs-api \
        dev-web test-web build-web deploy-web logs-web \
        test