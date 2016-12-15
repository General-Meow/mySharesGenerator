# run docker run -p <PORT>:<PORT> -d generalmeow/mysharesgenerator:1.0 <COUNTRY>
FROM anapsix/alpine-java:8
MAINTAINER Paul Hoang 2016-12-12
RUN ["mkdir", "-p", "/home/javaapp"]
COPY ./build/libs/sharesGenerator.jar /home/javaapp/sharesGenerator.jar
WORKDIR /home/javaapp
EXPOSE 5551
EXPOSE 5552
EXPOSE 5553
EXPOSE 5554
ENTRYPOINT ["java", "-jar", "sharesGenerator.jar"]
