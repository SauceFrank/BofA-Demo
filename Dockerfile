FROM maven:3-jdk-8-alpine

RUN apk update && apk add curl bash && rm -rf /var/cache/apk/*

ARG basePath="/usr/build"

ARG SAUCE_USERNAME
ARG SAUCE_ACCESS_KEY
ARG SAUCE_REGION
ARG BUILD_TYPE

WORKDIR ${basePath}

COPY src ${basePath}/src/.
COPY pom.xml ${basePath}/pom.xml

RUN mvn dependency:resolve
RUN mvn test-compile
RUN mvn test; exit 0