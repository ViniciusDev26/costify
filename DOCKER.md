# Guia Docker - Costify

Referência rápida de comandos Docker e Docker Compose para gerenciar a aplicação Costify.

## Comandos Essenciais

### Iniciar Aplicação

```bash
# Construir e iniciar todos os serviços
docker-compose up -d

# Acompanhar logs em tempo real
docker-compose logs -f

# Ver apenas logs da aplicação
docker-compose logs -f app

# Ver apenas logs do PostgreSQL
docker-compose logs -f postgres
```

### Parar Aplicação

```bash
# Parar todos os serviços (mantém dados)
docker-compose stop

# Parar e remover containers (mantém dados)
docker-compose down

# Parar, remover containers e volumes (APAGA TODOS OS DADOS)
docker-compose down -v
```

### Verificar Status

```bash
# Listar containers em execução
docker-compose ps

# Verificar health checks
curl http://localhost:8080/api/actuator/health

# Ver uso de recursos
docker stats costify-app costify-postgres
```

### Rebuild da Aplicação

```bash
# Rebuild apenas a aplicação (após mudanças no código)
docker-compose build app

# Rebuild sem cache (força download de dependências)
docker-compose build --no-cache app

# Rebuild e reiniciar
docker-compose up -d --build app
```

### Acessar Containers

```bash
# Shell na aplicação
docker exec -it costify-app sh

# Shell no PostgreSQL
docker exec -it costify-postgres sh

# Acessar PostgreSQL diretamente
docker exec -it costify-postgres psql -U postgres -d costify
```

## Gerenciamento de Dados

### Backup do Banco de Dados

```bash
# Criar backup do banco
docker exec costify-postgres pg_dump -U postgres costify > backup.sql

# Restaurar backup
docker exec -i costify-postgres psql -U postgres costify < backup.sql
```

### Limpar Dados

```bash
# Parar containers e remover volumes (apaga todos os dados)
docker-compose down -v

# Reiniciar do zero
docker-compose up -d
```

### Ver Dados no Banco

```bash
# Listar tabelas
docker exec -it costify-postgres psql -U postgres -d costify -c "\dt"

# Ver ingredientes
docker exec -it costify-postgres psql -U postgres -d costify -c "SELECT * FROM ingredients;"

# Ver receitas
docker exec -it costify-postgres psql -U postgres -d costify -c "SELECT * FROM recipes;"

# Ver migrations aplicadas (Flyway)
docker exec -it costify-postgres psql -U postgres -d costify -c "SELECT * FROM flyway_schema_history;"
```

## Troubleshooting

### Aplicação não inicia

```bash
# Ver logs detalhados
docker-compose logs app

# Verificar se PostgreSQL está saudável
docker-compose ps postgres

# Reiniciar containers
docker-compose restart app
```

### Porta 8080 já está em uso

```bash
# Opção 1: Parar o processo que está usando a porta
sudo lsof -ti:8080 | xargs kill -9

# Opção 2: Alterar porta no docker-compose.yml
# Edite a linha: "8081:8080" (mapeia 8081 do host para 8080 do container)
```

### Erro de conexão com banco de dados

```bash
# Verificar se PostgreSQL aceitou conexões
docker-compose logs postgres | grep "ready to accept connections"

# Testar conexão manualmente
docker exec -it costify-postgres pg_isready -U postgres

# Reiniciar PostgreSQL
docker-compose restart postgres
```

### Container reinicia constantemente

```bash
# Ver últimos 100 logs
docker-compose logs --tail=100 app

# Verificar health check
docker inspect costify-app | grep -A 10 Health

# Desabilitar health check temporariamente
# Comente a seção healthcheck no docker-compose.yml
```

### Limpar cache do Docker

```bash
# Remover imagens não utilizadas
docker image prune -a

# Remover volumes não utilizados
docker volume prune

# Limpar todo o sistema Docker (cuidado!)
docker system prune -a --volumes
```

## Desenvolvimento

### Rebuild Rápido (Hot Reload)

Para desenvolvimento rápido, você pode montar o código fonte como volume:

```yaml
# Adicione no service 'app' do docker-compose.yml
volumes:
  - ./src:/app/src
```

Depois:
```bash
docker-compose up -d
docker-compose exec app ./mvnw spring-boot:run
```

### Rodar Testes dentro do Container

```bash
# Rodar todos os testes
docker-compose exec app ./mvnw test -DargLine="-ea"

# Rodar teste específico
docker-compose exec app ./mvnw test -Dtest=IngredientControllerTest
```

### Ver Variáveis de Ambiente

```bash
# Ver todas as variáveis de ambiente da aplicação
docker exec costify-app env | grep -E "(DB_|SPRING_|SERVER_)"
```

## Configurações de Produção

### Usar Arquivo .env

Crie um arquivo `.env` na raiz do projeto:

```bash
# Copiar exemplo
cp .env.example .env

# Editar com suas configurações
nano .env
```

Docker Compose vai carregar automaticamente as variáveis do `.env`.

### Alterar Profile do Spring

```bash
# Via .env
SPRING_PROFILES_ACTIVE=prod

# Via linha de comando
docker-compose run -e SPRING_PROFILES_ACTIVE=dev app
```

### Configurar Pool de Conexões

```bash
# Para aplicações com alto tráfego
DB_POOL_SIZE=50
DB_POOL_MIN_IDLE=10
```

### Limitar Recursos do Container

Edite `docker-compose.yml`:

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 2G
        reservations:
          cpus: '0.5'
          memory: 512M
```

## Monitoramento

### Logs Estruturados

```bash
# Logs com timestamp
docker-compose logs -f -t app

# Últimas 50 linhas de log
docker-compose logs --tail=50 app

# Logs desde ontem
docker-compose logs --since 24h app
```

### Métricas de Performance

```bash
# Ver uso de CPU/Memória em tempo real
docker stats costify-app

# Exportar métricas
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}"
```

### Health Check Contínuo

```bash
# Verificar a cada 5 segundos
watch -n 5 'curl -s http://localhost:8080/api/actuator/health'
```

## Multi-Ambiente

### Desenvolvimento

```bash
# Use perfil dev
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

### Staging/Produção

```bash
# Use perfil prod (padrão)
docker-compose up -d

# Ou explicitamente
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## Referências

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
- [Eclipse Temurin Docker Hub](https://hub.docker.com/_/eclipse-temurin)
