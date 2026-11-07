# Deployment Guide: Corely Gym Management & Tracker

This project is structured for easy multi-cloud deployment across **Vercel**, **Netlify**, **Render**, **Railway**, **AWS**, or **Docker Compose**.

---

## 1. Cloud Bridge Architecture (Hybrid Local/Cloud)

When the frontend is deployed to cloud providers like Vercel or Netlify while developing locally, use the automated Cloud Bridge script:

```bash
# Automatically checks MySQL, backend health, and connects the HTTPS tunnel
./scripts/start-cloud-bridge.sh
```

- Tunnel Endpoint: `https://long-seas-wait.loca.lt` (or generated tunnel URL)
- Frontend calls `/api/*` which transparently proxies to your Spring Boot instance on port 8080.

---

## 2. Deploy Frontend to Vercel

The project includes a ready-to-deploy [`vercel.json`](./vercel.json).

### Quick CLI Deploy:
```sh
# Deploy preview
npx vercel

# Deploy to production
npx vercel --prod
```

### Git-Linked Deploy (Recommended):
1. Push your repository to GitHub.
2. Go to [vercel.com](https://vercel.com) and click **"Add New Project"**.
3. Import your `gym-membership-workout-tracker` repository.
4. Set root directory or leave as repository root (the `vercel.json` automatically serves `/frontend` and sets up route rewrites).
5. Click **Deploy**.

---

## 3. 1-Click Deploy to Render (Backend + Database)

The repository includes a pre-configured [`render.yaml`](./render.yaml) blueprint:

1. Create a free account on [render.com](https://render.com).
2. Click **Blueprints** → **New Blueprint Instance**.
3. Connect repository `https://github.com/elaa0947/gym-membership-workout-tracker.git`.
4. Render automatically spins up:
   - Managed MySQL database (`gym_tracker`)
   - Spring Boot web service in Docker container
   - Injects `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`
5. Copy the generated Render URL (e.g., `https://gym-tracker-backend.onrender.com`) and update `vercel.json`:
   ```json
   {
     "src": "/api/(.*)",
     "dest": "https://gym-tracker-backend.onrender.com/api/$1"
   }
   ```

---

## 4. Deploy to Railway

1. Install Railway CLI or go to [railway.app](https://railway.app).
2. Create **New Project** → **Provision MySQL**.
3. Add **GitHub Repo** → select `gym-membership-workout-tracker`.
4. Railway uses [`railway.json`](./railway.json) and builds from `backend/gym-tracker-backend/Dockerfile`.
5. Connect database variables:
   - `SPRING_DATASOURCE_URL`: `${{MySQL.MYSQL_URL}}`
   - `PORT`: `8080`

---

## 5. Deploy Full Stack to AWS

### Option A: AWS Elastic Beanstalk / App Runner (Backend + Database)
1. **Database:** Create an **Amazon RDS MySQL** instance (`db.t3.micro` or `db.t4g.micro` for free tier) and import initial data from [`gym_tracker_schema_and_data.sql`](./gym_tracker_schema_and_data.sql).
2. **Backend:** 
   - Deploy `backend/gym-tracker-backend/target/gym-tracker-backend-0.0.1-SNAPSHOT.jar` to **AWS App Runner** or **AWS Elastic Beanstalk (Java SE)**.
   - Configure Environment Variables:
     - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<your-rds-endpoint>:3306/gym_tracker?useSSL=false&allowPublicKeyRetrieval=true`
     - `SPRING_DATASOURCE_USERNAME`: `<your-db-username>`
     - `SPRING_DATASOURCE_PASSWORD`: `<your-db-password>`
     - `APP_CORS_ALLOWED_ORIGINS`: `https://your-frontend-domain.com`
     - `PORT`: `8080`

### Option B: AWS S3 + CloudFront (Static Frontend)
1. Upload contents of `./frontend/` to an Amazon S3 bucket configured for Static Website Hosting.
2. Create an Amazon CloudFront distribution pointing to the S3 bucket with an `/api/*` behavior routing to the App Runner / ALB backend endpoint.

---

## 6. Deploy with Docker Compose (Any VPS / DigitalOcean / EC2)

```sh
# 1. Prepare environment variables
cp .env.example .env
# Edit .env with secure production passwords

# 2. Build and launch containers
docker compose up --build -d

# Stack is available at http://localhost:8081 (or your server IP)
```
