FROM openjdk:17-oracle
CMD ["./mvnw", "clean", "package"]
ARG JAR_FILE=./*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]