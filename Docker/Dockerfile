# Estágio de build otimizado para cache de dependências
FROM maven:3.8.6-eclipse-temurin-17 AS build

WORKDIR /app

# Primeiro copia apenas o POM para cachear dependências
COPY pom.xml .

# Baixa dependências (cacheado se o POM não mudar)
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests -Dfinal.name=contas-a-pagar

# Estágio de produção minimalista
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia apenas o JAR buildado
COPY --from=build /app/target/*.jar app.jar

# Garante usuário não-root
RUN adduser -D nonroot && chown -R nonroot:nonroot /app
USER nonroot

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]