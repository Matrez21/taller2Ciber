# Usa una imagen base con JDK
FROM openjdk:23-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR del proyecto al contenedor
COPY target/taller2-0.0.1-SNAPSHOT.jar app.jar
RUN ls -la /app


# Expón el puerto en el que la aplicación de Spring Boot estará escuchando
EXPOSE 8080

# Comando para ejecutar la aplicación cuando el contenedor se inicie
ENTRYPOINT ["java", "-jar", "app.jar"]
