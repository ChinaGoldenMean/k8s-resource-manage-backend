FROM openjdk:8-jdk-alpine
VOLUME /tmp

RUN apk --no-cache add tzdata ttf-dejavu && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone
    
ARG WAR_FILE

COPY ${WAR_FILE} app.war

ENV JAVA_OPTS=""

EXPOSE 9000

ENTRYPOINT java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.war
