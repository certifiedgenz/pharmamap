# Stage 1: Build the React Application
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# We embed the API route handling directly down back into Nginx relative paths for robust proxying
RUN npm run build

# Stage 2: Serve the application with Nginx
FROM nginx:alpine
# Copy the built assets to Nginx default public path
COPY --from=builder /app/dist /usr/share/nginx/html
# Copy custom Nginx configuration mapping `/api` directly to backend
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
