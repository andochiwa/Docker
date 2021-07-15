FROM openjdk:11.0.9.1-jdk
COPY ./spring-cloud-gateway-demo/target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar
COPY ./wait-for-it.sh /app
WORKDIR /app
CMD ["java", "-jar", "demo.jar"]
