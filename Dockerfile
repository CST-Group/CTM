FROM openjdk:11-alpine

RUN apk add --no-cache ping
RUN apk add --no-cache wget
