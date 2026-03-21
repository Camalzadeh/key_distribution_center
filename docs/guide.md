# 🚀 Deployment and Setup Guide

This guide will help you set up and deploy the **WeCrypt** application.

## 📦 Local Setup with Docker

If you have Docker and Docker Compose installed, you can start everything (App + PostgreSQL) with a single command:

```bash
docker-compose up --build
```

- **App:** `http://localhost:8080`
- **PostgreSQL:** Port `5432`

## 🌍 Deployment to Render

To deploy this application to [Render](https://render.com), follow these steps:

### 1. Create a Managed PostgreSQL Database
1. Go to your Render Dashboard.
2. Click **New** -> **PostgreSQL**.
3. Name your database (e.g., `kdc`).
4. Once created, copy the **Internal Database URL**.

### 2. Create a Web Service
1. Click **New** -> **Web Service**.
2. Connect your GitHub repository.
3. Choose **Docker** as the Runtime.
4. Name your service (e.g., `wecrypt-messenger`).
5. Click **Advanced** and add the following **Environment Variables**:
   - `SPRING_DATASOURCE_URL`: (Paste the Internal Database URL from Step 1)
   - `SPRING_DATASOURCE_USERNAME`: (Find this in your Postgres credentials)
   - `SPRING_DATASOURCE_PASSWORD`: (Find this in your Postgres credentials)
   - `SPRING_JPA_HIBERNATE_DDL_AUTO`: `update`
6. Click **Deploy Web Service**.

## 🛠 Project Structure

- `src/main/resources/templates/`: UI templates (Thymeleaf).
- `src/main/resources/static/`: CSS and JS files.
- `Dockerfile`: Multi-stage build for efficient production image.
- `docker-compose.yml`: Local development environment.

## 🛡 Features

- **KDC (Key Distribution Center):** Handles key distribution between users.
- **Messenger:** Secure end-to-end communication.
- **Encryption/Decryption:** Integrated tools for message security.
