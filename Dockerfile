FROM maven:3-openjdk-11
COPY . /app
WORKDIR /app
RUN mvn clean install
RUN mvn package
ENTRYPOINT ["java","-jar","/app/target/gateway-latest.jar"]
