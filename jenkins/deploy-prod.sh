
#Obtem a versão da aplicação
POM_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\[');

APP_PORT="8096";
HOST_PORT="8073";
CONTAINER_IP="172.10.10.73";
NAME_APP="autenticacao-api";
VARPATHSOURCE="/var/jenkins_home/.m2/repository/br/com/xbrain/"$NAME_APP"/"$POM_VERSION;
VARPATHDESTINY="/home/xbrain/prod/autenticacao";
JAR_FILE=$NAME_APP"-"$POM_VERSION".jar";
DOCKERFILE="$VARPATHDESTINY/Dockerfile";
CONTAINER="autenticacao-prod";
IMAGE_NAME=$CONTAINER;
PROFILE="producao";
JAVA_OPTS="-Xms2g -Xmx8g -Dspring.profiles.active=$PROFILE \
                          -Delastic.apm.service_name=$CONTAINER-api \
                          -Delastic.apm.server_url=http://192.168.2.23:8200 \
                          -Delastic.apm.application_packages=br.com.xbrain";

SSHSERVER="root@192.168.2.32";
SSH_PORT="43256";
REMOTE_CMD="ssh -p$SSH_PORT $SSHSERVER";

scp -P $SSH_PORT Dockerfile ${SSHSERVER}:${VARPATHDESTINY}
scp -P $SSH_PORT $VARPATHSOURCE/$JAR_FILE ${SSHSERVER}:${VARPATHDESTINY}

# Cria uma cópia do jar com um nome padrão para o container acessar
${REMOTE_CMD} cp $VARPATHDESTINY/$JAR_FILE $VARPATHDESTINY/app.jar

# Remove jars antigos do servidor
${REMOTE_CMD} rm -rf $VARPATHDESTINY/$NAME_APP*.jar


# verifica se é preciso apagar a imagem ou o container
if [ "$APAGAR_IMAGEM" = true ] || [ "$APAGAR_CONTAINER" = true ]; then
	echo "Apagando container"
    ${REMOTE_CMD} docker stop "$CONTAINER"
    ${REMOTE_CMD} docker rm "$CONTAINER"

    # verifica se é pra apagar a imagem
    if [ "$APAGAR_IMAGEM" = true ]; then
		echo "Apagando imagem"
    	${REMOTE_CMD} docker rmi "$IMAGE_NAME"
    fi
fi


# verificar se a imagem existe, se nao cria
if [ ! "$($REMOTE_CMD docker images -q $IMAGE_NAME)" ]; then
	echo "Criando imagem..."
	${REMOTE_CMD} docker build \
    	-f $DOCKERFILE \
        -t $IMAGE_NAME \
        $VARPATHDESTINY
fi

# se o container estiver rodando
if [ "$($REMOTE_CMD docker ps -q -f name=^/$CONTAINER$)" ]; then
	echo "Reiniciando container..."
	${REMOTE_CMD} docker stop "$CONTAINER"
	${REMOTE_CMD} docker start "$CONTAINER"

else
	# se o container existir, mas está parado -> START else RUN
	if [ "$($REMOTE_CMD docker ps -aq -f status=exited -f name=$CONTAINER)"  ] || [ "$($REMOTE_CMD docker ps -aq -f status=created -f name=$CONTAINER)" ]; then
		echo "Iniciando container..."
		${REMOTE_CMD} docker start "$CONTAINER"
	else
		echo "Criando container..."
		${REMOTE_CMD} docker run \
			--name=$CONTAINER \
            --restart=unless-stopped \
			--mount type=bind,source=$VARPATHDESTINY/app.jar,target=/app.jar \
			-p $HOST_PORT:$APP_PORT \
			-d \
			-e JAVA_OPTS="'$JAVA_OPTS'" \
      -e MINIO_TOKEN="'$MINIO_TOKEN'" \
      -e MINIO_SECRET="'$MINIO_SECRET'" \
			--net network_xbrain \
			--ip $CONTAINER_IP \
			$IMAGE_NAME
	fi
fi