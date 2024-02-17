FROM openjdk:19


RUN apt update
RUN apt install default-jdk -y
COPY . .
RUN javac TeksiBotApplication.java

CMD ["java","TeksiBotApplication"]


