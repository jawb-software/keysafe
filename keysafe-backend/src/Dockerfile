FROM openjdk:17
WORKDIR /opt/app
ADD app/target/keysafe-backend.jar keysafe-backend.jar
ENTRYPOINT ["java", "-jar", "keysafe-backend.jar"]
EXPOSE 8080