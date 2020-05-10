FROM maven:3-openjdk-11
COPY . /app
WORKDIR /app
RUN ./mvnw compile
ENTRYPOINT ["java","-jar","/app/build/libs/gateway-latest.jar"]
