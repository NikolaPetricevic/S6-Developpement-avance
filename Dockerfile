# =====================================================================
# Stage 1 — Build
# On utilise une image Maven + JDK pour compiler et packager le projet
# =====================================================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copier d'abord uniquement le pom.xml pour exploiter le cache Docker des dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le reste des sources et construire le JAR (sans les tests)
COPY src ./src
RUN mvn package -DskipTests -B

# =====================================================================
# Stage 2 — Runtime
# Image minimale JRE pour exécuter le JAR produit
# =====================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copier uniquement le JAR depuis l'étape de build
COPY --from=build /app/target/ToDoList-1.0-SNAPSHOT.jar app.jar

# Créer le dossier logs et donner les droits à l'utilisateur non-root
RUN mkdir -p logs && chown -R appuser:appgroup /app

USER appuser

# Port exposé (doit correspondre à SERVER_PORT)
EXPOSE 8080

# Lancement de l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
