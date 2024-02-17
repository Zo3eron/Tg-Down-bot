FROM openjdk:19

RUN javac TeksiBotApplication.java

CMD ["java","TeksiBotApplication"]


