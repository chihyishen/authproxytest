FROM openjdk:17-jdk-slim AS builder

WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV PORT=8080

EXPOSE 8080

# 移除 production profile，簡化啟動
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]