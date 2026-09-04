# REVIVE MESH - DEPLOYMENT GUIDE

## Quick Deploy Options

### Option 1: Railway (Recommended - Simplest)
```bash
# Install Railway CLI
npm install -g @railway/cli

# Login
railway login

# Create new project
railway init

# Add PostgreSQL
railway add --service postgresql

# Add Redis
railway add --service redis

# Deploy backend
railway up --service backend -d backend/Dockerfile

# Deploy intelligence
railway up --service intelligence -d intelligence/Dockerfile

# Deploy frontend
railway up --service frontend -d frontend/Dockerfile

# Set environment variables via Railway dashboard
```

Required Railway Environment Variables:
- Backend: DATABASE_URL, REDIS_HOST, REDIS_PORT, KAFKA_BOOTSTRAP_SERVERS
- Frontend: NEXT_PUBLIC_API_URL
- Intelligence: (none required)

### Option 2: Render
1. Import repository from GitHub: https://github.com/sahanasenthilrajan/revive-mesh.git
2. Render will auto-detect render.yaml
3. Add PostgreSQL database (managed)
4. Add Redis instance (managed)
5. Configure environment variables
6. Deploy

### Option 3: Fly.io
```bash
fly launch --config fly.toml
fly postgres create --name revive-postgres
fly redis create --name revive-redis
fly deploy
```

## Kafka Limitation
Most free-tier platforms don't provide managed Kafka. Options:
1. Deploy without Kafka (events stored in database, polled)
2. Use Upstash Kafka free tier (1GB/month)
3. CloudKarafka free plan (25MB storage)
4. Run Kafka in same container as backend (not recommended for production)

## Manual Deployment Steps

### 1. Create Managed Services
- PostgreSQL database
- Redis cache
- Kafka (Upstash/CloudKarafka) OR use database polling

### 2. Build and Push Images
```bash
# Backend
docker build -t revive-backend ./backend
docker tag revive-backend registry.your-platform.com/revive-backend
docker push registry.your-platform.com/revive-backend

# Intelligence
docker build -t revive-intelligence ./intelligence
docker tag revive-intelligence registry.your-platform.com/revive-intelligence
docker push registry.your-platform.com/revive-intelligence

# Frontend
docker build -t revive-frontend ./frontend
docker tag revive-frontend registry.your-platform.com/revive-frontend
docker push registry.your-platform.com/revive-frontend
```

### 3. Set Environment Variables
Backend:
```
DATABASE_URL=postgresql://user:pass@host:5432/revive_mesh
REDIS_HOST=your-redis-host
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=your-kafka:9092
SPRING_PROFILES_ACTIVE=prod
```

Frontend:
```
NEXT_PUBLIC_API_URL=https://your-backend-url.com
```

### 4. Deploy Services
Deploy in order:
1. PostgreSQL (managed)
2. Redis (managed)
3. Kafka (managed/optional)
4. Intelligence service
5. Backend service
6. Frontend service

### 5. Run Database Migration
```bash
# Backend will auto-run Flyway migrations on startup
# Or manually: docker exec backend java -jar app.jar --flyway.migrate
```

## Production URLs
After deployment, you will have:
- Frontend: https://revive-frontend.your-platform.com
- Backend: https://revive-backend.your-platform.com
- Intelligence: https://revive-intelligence.your-platform.com

## Health Checks
- Backend: /actuator/health
- Intelligence: /health
- Frontend: / (root loads War Room)

## Demo Flow
1. Open frontend URL
2. Click "Failure Swarm" scenario button
3. Wait 2-3 seconds
4. Observe metrics update in War Room
5. Check backend logs for recovery cases

## Limitations
- Free tiers have compute/memory limits
- Kafka may require paid tier or alternative
- Database connections limited on free plans
- No auto-scaling on free tiers

## Cost
- Railway: $5/month trial credit, then pay-as-you-go
- Render: Free tier available with limits
- Fly.io: Free tier with resource limits
- Managed Kafka: $9-15/month minimum

## Troubleshooting
If deployment fails:
1. Check platform logs
2. Verify environment variables set
3. Check database connection
4. Verify Docker builds locally first
5. Test health endpoints

For Kafka issues:
- Use Upstash Kafka free tier
- Or implement database-backed event queue
- Or accept limited event processing on free tier
