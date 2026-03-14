# ☁️ Cloud Deployment Guide: PharmaMap

This guide will show you exactly how to deploy your PharmaMap application to the cloud for free (or very cheap) using **GitHub, Railway, Render, and Vercel**. 

Here is the architecture we are building:
1. **GitHub**: Stores your code online.
2. **Railway**: Hosts your MySQL Database.
3. **Render**: Hosts your Spring Boot (Java) Backend.
4. **Vercel**: Hosts your React (Vite) Frontend.

Follow these instructions step-by-step. Don't worry if you are a beginner!

---

## 🛠️ Step 1: Push Your Code to GitHub

First, your code needs to live on GitHub so that the cloud platforms can pull it automatically whenever you make changes.

1. Go to [GitHub](https://github.com/) and create an account if you don't have one.
2. Click the **"+"** button at the top right and select **New repository**.
3. Name it `pharmamap` (leave it Public or Private, your choice) and click **Create repository**.
4. Open a terminal (Command Prompt or PowerShell) on your computer, navigate to your project folder (`d:\Projects\Web\PharmaMap`), and run the following commands:
   ```bash
   git init
   git add .
   git commit -m "Initial commit for PharmaMap"
   git branch -M main
   # Replace this URL with your actual GitHub repository URL!
   git remote add origin https://github.com/YOUR_USERNAME/pharmamap.git
   git push -u origin main
   ```
*Great! Your code is now safely on GitHub.*

---

## 🗄️ Step 2: Deploy MySQL Database on Railway

Railway is the easiest place to host a MySQL database.

1. Go to [Railway](https://railway.app/) and sign in with your GitHub account.
2. Click **New Project**.
3. Select **Provision PostgreSQL** (Wait, we need MySQL!). Select **Provision MySQL** instead.
4. Click on your newly created MySQL box in the Railway dashboard.
5. Click on the **Connect** tab.
6. Note down the following connection details. You will need them for Render:
   * **Host** (e.g., `viaduct.proxy.rlwy.net`)
   * **Port** (e.g., `12345`)
   * **User** (e.g., `root`)
   * **Password** (e.g., `your_long_password`)
   * **Database Name** (e.g., `railway`)

---

## ⚙️ Step 3: Deploy Spring Boot Backend on Render

Render is a modern cloud provider perfect for running Java/Spring Boot APIs.

1. Go to [Render](https://render.com/) and sign in with GitHub.
2. Click **New +** and select **Web Service**.
3. Connect your GitHub account and select your `pharmamap` repository.
4. **Configuration details:**
   * **Name**: `pharmamap-backend`
   * **Root Directory**: `backend` (⚠️ THIS IS VERY IMPORTANT!)
   * **Environment**: `Docker`
   * **Region**: Choose the one closest to you.
   * **Instance Type**: Free or Starter.
5. Scroll down to **Advanced** and click **Add Environment Variable**. Add these matching your Railway details:
   * `DB_HOST` = (Your Railway Host)
   * `DB_PORT` = (Your Railway Port)
   * `DB_NAME` = (Your Railway Database Name, usually `railway`)
   * `DB_USER` = (Your Railway User)
   * `DB_PASSWORD` = (Your Railway Password)
   * `JWT_SECRET` = `type_any_long_random_secret_here_for_security_1234567890qwertyuiopasdfghjkl`
6. Click **Create Web Service**.
7. Wait ~5-10 minutes for it to build and deploy. Once it says **Live**, copy your backend URL at the top left (it will look like `https://pharmamap-backend.onrender.com`).

*Test it! Visit `https://pharmamap-backend.onrender.com/api/search` in your browser. You might get an empty list, but it shouldn't be an error page!*

---

## 🌐 Step 4: Deploy React Frontend on Vercel

Vercel is the ultimate hosting platform for React and Vite applications.

1. Go to [Vercel](https://vercel.com/) and sign in with GitHub.
2. Click **Add New...** and select **Project**.
3. Import your `pharmamap` GitHub repository.
4. **Configuration details:**
   * **Project Name**: `pharmamap`
   * **Framework Preset**: Vite
   * **Root Directory**: Click the "Edit" button and select `frontend`. (⚠️ THIS IS VERY IMPORTANT!)
5. Expand the **Environment Variables** section. Add the following:
   * **Name**: `VITE_API_BASE_URL`
   * **Value**: `https://pharmamap-backend.onrender.com/api` *(Make sure you replace this with your actual Render URL + `/api`!)*
6. Click **Deploy**.
7. Wait ~2 minutes. Once you see confetti 🎉, your app is live!

---

## 🎉 Congratulations!

Your application is now fully live on the internet! 

* **To make changes**: Simply edit code on your computer, run `git add .`, `git commit -m "Update"`, and `git push`. Render and Vercel will completely rebuild and update your live sites automatically!
