FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/ehCache-0.0.1-SNAPSHOT.jar /app/ehCache.jar

EXPOSE 9090

CMD ["java" ,"-jar", "ehCache.jar"]

