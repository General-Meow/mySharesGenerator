FROM openjdk:8
MAINTAINER Paul Hoang 2016-10-25
RUN ["mkdir", "-p", "/home/javaapp"]
COPY ./build/libs/sharesGenerator.jar /home/javaapp/sharesGenerator.jar
WORKDIR /home/javaapp
EXPOSE 5551
ENTRYPOINT ["java", "-jar", "sharesGenerator.jar"]
CMD ["uk"]