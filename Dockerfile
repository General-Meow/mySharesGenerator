FROM openjdk:8
MAINTAINER Paul Hoang 2016-10-25
ENV PROCESS_PORT ${PROCESS_PORT}
RUN ["mkdir", "-p", "/home/javaapp"]
RUN ["echo", "$PROCESS_PORT"]
COPY ./build/libs/sharesGenerator.jar /home/javaapp/sharesGenerator.jar
WORKDIR /home/javaapp
EXPOSE ${PROCESS_PORT}
CMD ["java", "-jar", "sharesGenerator.jar"]