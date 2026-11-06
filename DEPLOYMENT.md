# Deployment Guide: Corely Gym Management & Tracker

This project is structured for easy multi-cloud deployment across **Vercel**, **Netlify**, **AWS**, or **Docker Compose**.

---

## 1. Deploy Frontend to Vercel

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

## 2. Deploy Frontend to Netlify

The project includes a pre-configured [`netlify.toml`](./netlify.toml).

### Quick CLI Deploy:
```sh
# Deploy preview
npx netlify deploy --dir=frontend

# Deploy to production
npx netlify deploy --dir=frontend --prod
```

### Git-Linked Deploy:
1. Connect repository on [netlify.com](https://netlify.com).
2. Set **Publish directory** to `frontend`.
3. Netlify automatically picks up `netlify.toml` for redirects and routing.

---

## 3. Deploy Full Stack to AWS

### Option A: AWS Elastic Beanstalk / App Runner (Backend + Database)
1. **Database:** Create an **Amazon RDS MySQL** instance (`db.t3.micro` or `db.t4g.micro` for free tier) and name the database `gym_tracker`.
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

## 4. Deploy with Docker Compose (Any VPS / DigitalOcean / EC2)

```sh
# 1. Prepare environment variables
cp .env.example .env
# Edit .env with secure production passwords

# 2. Build and launch containers
docker compose up --build -d

# Stack is available at http://localhost:8081 (or your server IP)
```
