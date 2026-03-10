# Costify - Production Deployment Guide

Este guia fornece instruções completas para implantar o Costify em produção usando Docker.

## Pré-requisitos

- Docker Engine 20.10+
- Docker Compose 2.0+
- Mínimo 2GB de RAM disponível
- Portas 8080 disponível (ou configurar outra porta)

## Build da Imagem

### 1. Build Local

```bash
# Build da imagem de produção
docker build -t costify:latest .

# Verificar imagem criada
docker images | grep costify
```

### 2. Build com Tag Versionada

```bash
# Build com versão específica
docker build -t costify:0.0.1 -t costify:latest .
```

## Configuração de Produção

### 1. Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto (ou use variáveis de ambiente do sistema):

```env
# Database Configuration
DB_PASSWORD=your_secure_password_here

# Optional: Custom Configuration
# DB_HOST=postgres
# DB_PORT=5432
# DB_NAME=costify
# DB_USER=postgres
# DB_POOL_SIZE=20
# SERVER_PORT=8080
# CONTEXT_PATH=/api
```

**IMPORTANTE**:
- Nunca commite o arquivo `.env` com senhas reais
- Use senhas fortes e únicas para produção
- Considere usar gerenciadores de secrets (AWS Secrets Manager, HashiCorp Vault, etc)

### 2. Customização do docker-compose.prod.yml

Revise e ajuste o arquivo `docker-compose.prod.yml` conforme suas necessidades:

- **Recursos de CPU/Memória**: Ajuste os limites em `deploy.resources`
- **Portas**: Altere o mapeamento de portas se necessário
- **Volumes**: Descomente volumes para persistir logs
- **Network**: Configure rede customizada se necessário

## Deploy

### 1. Deploy com Docker Compose (Recomendado)

```bash
# Iniciar serviços em produção
docker compose -f docker-compose.prod.yml up -d

# Verificar status dos containers
docker compose -f docker-compose.prod.yml ps

# Ver logs
docker compose -f docker-compose.prod.yml logs -f

# Ver logs apenas da aplicação
docker compose -f docker-compose.prod.yml logs -f costify-app
```

### 2. Deploy Manual com Docker Run

```bash
# 1. Criar network
docker network create costify-network

# 2. Iniciar PostgreSQL
docker run -d \
  --name costify-postgres-prod \
  --network costify-network \
  -e POSTGRES_DB=costify \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=your_secure_password \
  -v postgres-data:/var/lib/postgresql/data \
  postgres:17-alpine

# 3. Aguardar PostgreSQL inicializar (10-15 segundos)
sleep 15

# 4. Iniciar Costify
docker run -d \
  --name costify-app-prod \
  --network costify-network \
  -p 8080:8080 \
  -e DB_HOST=costify-postgres-prod \
  -e DB_PORT=5432 \
  -e DB_NAME=costify \
  -e DB_USER=postgres \
  -e DB_PASSWORD=your_secure_password \
  -e SPRING_PROFILES_ACTIVE=prod \
  costify:latest
```

## Verificação de Saúde

### 1. Health Check da Aplicação

```bash
# Via curl
curl http://localhost:8080/api/actuator/health

# Resposta esperada:
# {"status":"UP"}

# Health check detalhado (requer autenticação)
curl http://localhost:8080/api/actuator/health/liveness
curl http://localhost:8080/api/actuator/health/readiness
```

### 2. Verificar Logs

```bash
# Logs da aplicação
docker logs costify-app-prod -f

# Logs do PostgreSQL
docker logs costify-postgres-prod -f

# Com Docker Compose
docker compose -f docker-compose.prod.yml logs -f
```

## Monitoramento

### 1. Métricas Prometheus

A aplicação expõe métricas no formato Prometheus:

```bash
curl http://localhost:8080/api/actuator/prometheus
```

### 2. Endpoints de Monitoramento

```bash
# Informações da aplicação
curl http://localhost:8080/api/actuator/info

# Métricas gerais
curl http://localhost:8080/api/actuator/metrics
```

## Manutenção

### 1. Atualização da Aplicação

```bash
# 1. Build nova versão
docker build -t costify:latest .

# 2. Parar containers
docker compose -f docker-compose.prod.yml down

# 3. Iniciar com nova versão
docker compose -f docker-compose.prod.yml up -d

# 4. Verificar logs
docker compose -f docker-compose.prod.yml logs -f costify-app
```

### 2. Backup do Banco de Dados

```bash
# Criar backup
docker exec costify-postgres-prod pg_dump -U postgres costify > backup_$(date +%Y%m%d_%H%M%S).sql

# Restaurar backup
docker exec -i costify-postgres-prod psql -U postgres costify < backup_20250118_120000.sql
```

### 3. Limpeza de Recursos

```bash
# Parar e remover containers
docker compose -f docker-compose.prod.yml down

# Remover containers e volumes (CUIDADO: perde dados!)
docker compose -f docker-compose.prod.yml down -v

# Remover imagens não utilizadas
docker image prune -a
```

## Troubleshooting

### 1. Aplicação não inicia

```bash
# Verificar logs
docker logs costify-app-prod

# Verificar conectividade com banco
docker exec costify-app-prod ping postgres

# Verificar variáveis de ambiente
docker exec costify-app-prod env | grep DB_
```

### 2. Problemas de Conexão com Banco

```bash
# Verificar se PostgreSQL está rodando
docker ps | grep postgres

# Verificar health do PostgreSQL
docker exec costify-postgres-prod pg_isready -U postgres

# Conectar manualmente ao banco
docker exec -it costify-postgres-prod psql -U postgres -d costify
```

### 3. Problemas de Memória

```bash
# Verificar uso de recursos
docker stats

# Ajustar limites de memória no docker-compose.prod.yml
# ou nas variáveis JAVA_OPTS
```

## Segurança em Produção

### Checklist de Segurança

- [ ] Usar senhas fortes e únicas
- [ ] Não expor porta do PostgreSQL (5432)
- [ ] Configurar SSL/TLS para banco de dados
- [ ] Usar HTTPS com reverse proxy (Nginx, Traefik)
- [ ] Limitar acesso à rede Docker
- [ ] Implementar rate limiting
- [ ] Configurar logs de auditoria
- [ ] Usar secrets management
- [ ] Manter imagens atualizadas
- [ ] Implementar backup automatizado

### Exemplo com Nginx Reverse Proxy

```nginx
server {
    listen 80;
    server_name costify.example.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Performance Tuning

### 1. JVM Tuning

Ajustar `JAVA_OPTS` no `docker-compose.prod.yml`:

```yaml
JAVA_OPTS: >-
  -XX:+UseContainerSupport
  -XX:MaxRAMPercentage=75.0
  -XX:InitialRAMPercentage=50.0
  -XX:+UseG1GC
  -XX:MaxGCPauseMillis=200
  -XX:+UseStringDeduplication
  -XX:+OptimizeStringConcat
```

### 2. Connection Pool

Ajustar tamanho do pool de conexões:

```yaml
DB_POOL_SIZE: 50          # Aumentar para alta concorrência
DB_POOL_MIN_IDLE: 10      # Manter conexões mínimas disponíveis
```

### 3. PostgreSQL Tuning

```sql
-- Conectar ao PostgreSQL
docker exec -it costify-postgres-prod psql -U postgres -d costify

-- Verificar configurações
SHOW shared_buffers;
SHOW max_connections;
SHOW work_mem;
```

## Ambientes de Deploy

### 1. Cloud Providers

#### AWS (ECS/Fargate)
- Use Amazon RDS para PostgreSQL
- Configure Auto Scaling
- Use Application Load Balancer

#### Google Cloud (Cloud Run)
- Use Cloud SQL para PostgreSQL
- Configure scaling automático
- Use Cloud Load Balancing

#### Azure (Container Instances)
- Use Azure Database for PostgreSQL
- Configure Azure Container Registry
- Use Azure Load Balancer

### 2. Kubernetes

```yaml
# Exemplo básico de deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: costify
spec:
  replicas: 3
  selector:
    matchLabels:
      app: costify
  template:
    metadata:
      labels:
        app: costify
    spec:
      containers:
      - name: costify
        image: costify:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_HOST
          value: "postgres-service"
```

## Suporte

Para problemas ou dúvidas:

1. Verifique os logs: `docker compose logs -f`
2. Consulte a documentação: `CLAUDE.md` e `README.md`
3. Reporte issues no repositório do projeto
