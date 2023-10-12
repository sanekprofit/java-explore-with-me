FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar stat_service.jar
ENTRYPOINT ["java","-jar","/stat_service.jar"]