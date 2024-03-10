FROM 10.1.66.33:8082/openjdk:11
WORKDIR /opt/app
EXPOSE 8080
COPY target/*.jar /opt/app/
RUN chgrp -R 0 ./ && chmod -R g=u ./ 
CMD ["java", "-jar", "ibs-corp-cross-exchange-0.0.1-SNAPSHOT.jar"]
