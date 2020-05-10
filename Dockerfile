FROM maven:3-openjdk-11
COPY . /app
WORKDIR /app
RUN mvn package
ENTRYPOINT ["java","-jar","/app/target/gateway-latest.jar"]
