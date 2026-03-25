# Etapa 1: Build con Node/Angular CLI
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production

# Etapa 2: Servir con nginx
FROM nginx:alpine
COPY --from=build /app/dist/browser-interface/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
