# Corely Gym Membership and Workout Tracker

## Run the application with Docker

1. Copy `.env.example` to `.env` and set both database passwords to strong, different values.
2. For an HTTPS deployment, set `SESSION_COOKIE_SECURE=true` and update `APP_CORS_ALLOWED_ORIGINS` to the frontend's exact public origin.
3. Start the stack from the repository root:

   ```sh
   docker compose up --build -d
   ```

4. Open `http://localhost:8081` (or the configured `FRONTEND_PORT`). Stop the stack with `docker compose down`; the named `mysql_data` volume preserves data. To intentionally remove the database and all member activity, use `docker compose down -v`.

The frontend container serves the static site and proxies `/api` to Spring Boot. MySQL is not published to the host. Flyway applies the schema migration on backend startup, and Hibernate validates the resulting schema. Do not use the example/local fallback passwords in a public deployment. Configure secrets through the deployment platform rather than committing `.env`.

## Local development

- Start a MySQL 8 instance and create a database/user matching `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`. The application defaults are for local development only.
- Start the backend with `cd backend/gym-tracker-backend && bash mvnw spring-boot:run`.
- Serve the repository root from a local static server on port `5500` and open `/frontend/pages/login.html`. The default CORS origins permit `localhost:5500` and `127.0.0.1:5500`.

Member registration/login and profile/onboarding use the backend. A successful login creates an HTTP-only session cookie; member endpoints require that session and restrict requests to the signed-in member. Attendance, workout sessions, and dated weight measurements are persisted in MySQL. Attendance allows one record per member/day, and weight measurements update the existing entry for the same member/date and update the member's profile weight.

Membership tier selection, term rules, pricing (Starter, Pro, and Elite), and activation are fully configured and persisted in MySQL. The member dashboard automatically aggregates real database metrics for attendance streaks, completed workouts, weight trends, and membership expiry.

## Administrator Management Console

A dedicated Administrator perspective is available at `/frontend/pages/admin/login.html` (or linked directly from the landing page and login page).

- **Default Administrator Credentials:**
  - **Email:** `admin@corelygym.com`
  - **Password:** `AdminPass123!`
- **Capabilities:**
  - **Live Overview:** Real-time KPI counters for total members, active paid plans, revenue, today's check-ins, and completed workouts.
  - **Member Management:** Search and filter members, add new members, edit details, assign/upgrade/cancel membership tiers, and delete members.
  - **Attendance Management:** Live check-in log, manual check-in for any member, undo/delete check-in records.
  - **Workout Oversight:** Real-time stream of member workouts with exercise lists and timestamps.

## Deployment Options

See [`DEPLOYMENT.md`](./DEPLOYMENT.md) for full deployment instructions:
- **Vercel:** Ready with [`vercel.json`](./vercel.json) (`npx vercel`)
- **Netlify:** Ready with [`netlify.toml`](./netlify.toml) (`npx netlify deploy`)
- **AWS:** S3 + CloudFront (Frontend) and AWS App Runner / Elastic Beanstalk (Spring Boot Backend) + RDS MySQL
- **Docker Compose:** Full stack deployment via `docker compose up --build -d`

## Verification

```sh
cd backend/gym-tracker-backend && bash mvnw test
cd frontend && npm ci && npm test
```

Backend tests use an isolated H2 database. The login browser test mocks the API so it does not require a running MySQL server.
