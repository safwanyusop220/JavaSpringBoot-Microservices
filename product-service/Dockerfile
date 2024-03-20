FROM openjdk:21
WORKDIR /app
COPY ./target/product-service-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/product-service-0.0.1-SNAPSHOT.jar", "--spring.config.location=/tmp/config/application.properties"]

