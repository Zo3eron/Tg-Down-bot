FROM openjdk:19
ADD build/libs/Teksi-bot0.0.1-SNAPSHOT.jar app.jar
VOLUME /simple.app
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"]



