# PharmaMap Deployment Guide

This guide details the steps to deploy the PharmaMap application using Docker. This architecture encapsulates the Spring Boot backend, the React frontend, and the MySQL database into isolated containers that communicate via a shared network.

## Prerequisites

Before deploying, ensure the host machine has the following installed:
- **Docker**: The container runtime.
- **Docker Compose**: The multi-container orchestration tool.
- **Git**: To clone the repository onto the server.

> [!TIP]
> Ensure ports `80` (Frontend HTTP), `8080` (Backend API), and `3306` (MySQL) are open and not engaged by other host services.

---

## Step 1: Clone the Repository

Clone your PharmaMap project repository onto the deployment server and navigate into the root directory.

```bash
git clone <your-repository-url>
cd PharmaMap
```

---

## Step 2: Configure Environment Variables

The orchestration relies on Docker Compose to inject environment variables. Ensure the `docker-compose.yml` file in the root directory contains your desired parameters. 

**Default Configuration:**
```yaml
# docker-compose.yml (Snippet)
services:
  database:
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: pharmamap
  backend:
    environment:
      - DB_HOST=database
      - DB_PORT=3306
      - DB_NAME=pharmamap
      - DB_USER=root
      - DB_PASSWORD=root
      - JWT_SECRET=YOUR_SECURE_RANDOMLY_GENERATED_SECRET_KEY
```

> [!IMPORTANT]
> Change the `JWT_SECRET` prior to production deployments for cryptographic security!
> Change the `MYSQL_ROOT_PASSWORD` and `DB_PASSWORD` to matching, secure values.

---

## Step 3: Verify the Multi-Stage Builds

The platform uses Multi-Stage Dockerfiles to keep the image sizes small and secure. 

**Backend (`backend/Dockerfile`):**
Uses Temurin JDK 21 and Maven to build the application into a `.jar`, then copies only the executable `.jar` into a pristine Java 21 JRE image.

**Frontend (`frontend/Dockerfile`):**
Uses Node.js to install dependencies and run the Vite production build (`npm run build`). It then copies the resulting static assets into an Nginx Alpine container. It embeds an `nginx.conf` that performs Reverse Proxying, eliminating CORS issues by securely tunneling `/api` requests directly to the backend container over the internal Docker network.

---

## Step 4: Build and Deploy

At the root of the repository (where `docker-compose.yml` is located), execute the following command to build the images and start the services in detached mode:

```bash
docker-compose up --build -d
```

### What happens during this process?
1. **Network Creation:** Docker sets up an internal bridge network linking the 3 services.
2. **Database Initialization:** MySQL spins up and dynamically provisions the `pharmamap` schema based on the volume mapping.
3. **Backend Compilation:** Maven downloads dependencies and packages the Spring Boot app. The resulting `backend` container waits for the `database` service before fully initializing JDBC connections.
4. **Frontend Rollout:** Vite transpiles the React code. Nginx mounts it and begins listening on Port 80.

---

## Step 5: Verify the Deployment

Once the command finishes, verify the system is running:

1. **Check Container Status:**
   ```bash
   docker-compose ps
   ```
   *Expected output: 3 containers running (`pharmamap-backend`, `pharmamap-frontend`, `pharmamap-db`).*

2. **Check Backend Logs (Optional but recommended):**
   ```bash
   docker-compose logs -f backend
   ```
   *Wait until you see `Started PharmaMapApplication in X seconds`.*

3. **Access the Application:**
   Open a web browser and navigate to the server's IP address or domain on port `80`:
   `http://<SERVER_IP>/`
   
   The Nginx proxy will automatically route API calls to `http://<SERVER_IP>/api/*` straight to the Spring Boot backend instance!

---

## Maintenance Commands

*   **Stop the application (keeps data):** `docker-compose stop`
*   **Bring down the application and remove containers:** `docker-compose down`
*   **Wipe Database Data completely (Caution!):** `docker-compose down -v`
*   **Rebuild after pulling new code changes:** `docker-compose up --build -d`
