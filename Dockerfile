FROM maven:3.5-jdk-8 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests
WORKDIR usr/src/app/target
RUN ls -la

FROM gcr.io/distroless/java
ARG JAR_FILE=null
COPY --from=build /usr/src/app/target/${JAR_FILE} /usr/app/app.jar

ENTRYPOINT ["java","-jar","/usr/app/app.jar"]