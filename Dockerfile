FROM openjdk:11.0.5-jdk-slim
VOLUME /tmp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

#Configura o timezone do container
ENV TZ=America/Sao_Paulo

RUN \
  apt-get update && \
  apt-get install wget -y && \
  apt-get install curl -y && \
  apt-get clean && \
  ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
  wget -O apm-agent.jar https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.35.0/elastic-apm-agent-1.35.0.jar

ENTRYPOINT exec java -javaagent:apm-agent.jar \
                   $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /application.jar

HEALTHCHECK --interval=25s --timeout=3s --retries=5 CMD curl -f http://localhost:${ACTUATOR_PORT}/actuator/health || exit 1