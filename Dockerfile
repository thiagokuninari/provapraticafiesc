FROM openjdk:11.0.5-jdk-slim
VOLUME /tmp

RUN apt update && apt install curl -y
RUN apt install wget -y

#Configura o timezone do container
ENV TZ=America/Sao_Paulo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN wget -O apm-agent.jar https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.35.0/elastic-apm-agent-1.35.0.jar

ENTRYPOINT exec java -javaagent:apm-agent.jar \
                   $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
