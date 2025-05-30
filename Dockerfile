FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 安裝 Cloud SQL Proxy 和診斷工具
RUN apk add --no-cache wget curl netcat-openbsd && \
    wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O cloud_sql_proxy && \
    chmod +x cloud_sql_proxy

# 複製應用程式和啟動腳本
COPY --from=builder /app/target/*.jar app.jar
COPY start.sh .
RUN chmod +x start.sh

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV PORT=8080

EXPOSE 8080
CMD ["./start.sh"]