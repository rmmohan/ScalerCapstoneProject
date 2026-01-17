# ---------- Build Stage ----------
FROM node:20-alpine AS builder

WORKDIR /app

# Copy dependency files first for caching
COPY package.json package-lock.json ./

# Clean, deterministic install
RUN npm ci

# Copy source code
COPY . .

# Build optimized production bundle
RUN npm run build


# ---------- Production Stage ----------
FROM nginx:alpine

# Copy custom nginx config
COPY nginx.conf /etc/nginx/nginx.conf
COPY certs /etc/nginx/certs

# Copy build output
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

# Run nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
