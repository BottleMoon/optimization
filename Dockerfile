FROM openjdk:17-oracle
CMD ["./mvnw", "clean", "package"]
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=local", "-jar", "app.jar"]