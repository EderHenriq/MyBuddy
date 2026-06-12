FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copia os arquivos de dependência primeiro (otimiza cache do Docker)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Baixa as dependências sem compilar o código
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Agora copia o código-fonte
COPY src ./src

# Compila e gera o .jar 
RUN ./mvnw package -DskipTests -B


FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Usuário sem root por segurança
RUN addgroup -S mybuddy && adduser -S mybuddy -G mybuddy

# Copia só o .jar do stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Pasta de uploads de imagens
RUN mkdir -p /app/uploads && chown -R mybuddy:mybuddy /app

USER mybuddy

EXPOSE 8081

ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]