# Build Stage
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install --legacy-peer-deps
COPY . .
ARG PROJECT=shell
RUN npx ng build ${PROJECT} --configuration production

# Serve Stage
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
ARG PROJECT=shell
COPY --from=build /app/dist/${PROJECT}/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
