FROM openjdk:8-alpine

ENV APP template-api

WORKDIR /usrl/local/$APP

COPY target/$APP.jar $APP.jar

CMD java -Dfile.encoding=UTF-8 -jar $APP.jar
