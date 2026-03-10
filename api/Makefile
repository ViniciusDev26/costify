M2_CACHE := $(HOME)/.m2
PROJECT_DIR := $(shell pwd)

# ─── Testes ──────────────────────────────────────────────────────────────────

test:
	docker run --rm \
		-v /var/run/docker.sock:/var/run/docker.sock \
		-v $(PROJECT_DIR):/app \
		-v $(M2_CACHE):/root/.m2 \
		-w /app \
		-e TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal \
		maven:3.9-eclipse-temurin-21 \
		./mvnw test -DargLine="-ea"

test-class:
	docker run --rm \
		-v /var/run/docker.sock:/var/run/docker.sock \
		-v $(PROJECT_DIR):/app \
		-v $(M2_CACHE):/root/.m2 \
		-w /app \
		-e TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal \
		maven:3.9-eclipse-temurin-21 \
		./mvnw test -DargLine="-ea" -Dtest="$(CLASS)"

# ─── Aplicação local ─────────────────────────────────────────────────────────

up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f app

restart:
	docker compose restart app

# ─── Build ───────────────────────────────────────────────────────────────────

build:
	docker compose build app

rebuild:
	docker compose build --no-cache app

deploy:
	docker compose up -d --build app

.PHONY: test test-class up down logs restart build rebuild deploy