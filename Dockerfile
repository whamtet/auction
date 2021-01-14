FROM openjdk:8-alpine

COPY target/uberjar/auction.jar /auction/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/auction/app.jar"]
