FROM openjdk:11.0.2-jdk-slim
VOLUME /tmp

#Configura o timezone do container
ENV TZ=America/Sao_Paulo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Xmx1028m","-Xms518m","/app.jar"]