# Etapa 1: Build con Node/Angular CLI (all locales)
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production --localize

# Etapa 2: Servir con nginx (es, en, pl, ja under /usr/share/nginx/html/)
FROM nginx:alpine
COPY --from=build /app/dist/browser-interface/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
