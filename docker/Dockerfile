FROM java:8
MAINTAINER lianyanjie
VOLUME /tmp
COPY *.jar /app.jar
CMD ["--spring.profiles.active=prod"]
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]