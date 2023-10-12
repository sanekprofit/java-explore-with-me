FROM amazoncorretto:11-alpine-jdk
ADD target/stat_service.jar stat_serv.jar
ENTRYPOINT ["java","-jar","stat_serv.jar"]