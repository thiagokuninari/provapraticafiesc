Autenticação - REST API
================================

API do serviço de autenticação das aplicações utilizando OAUTH2.

Instalação
----------
É necessário o maven instalado (apt-get install maven)

1. `mvn install` para instalar as dependências
2. `mvn spring-boot:run` para rodar a aplicação
3. Acesse a url: [http://localhost:8080](http://localhost:8080)


Tecnologias
-----------
Java8, Spring-Boot, Jpa/Hibernate, Oracle Database


Code Style
-----------
São utilizados os plugins checkStyle e PMD para checagem de estilo de código.
Utilize o seguinte comando para rodar a checagem:

1. `mvn process-classes` para rodar a checagem


Testes 
------
1. `mvn test` para executar os testes


GIT Workflow
-----------
A organização de branches e padrão de commits deve ser seguido conforme o documento:

[Git WorkFlow X-Brain](https://docs.google.com/document/d/1oVzpbnLO7V-Nl-5cegE3nMF6ZYErsns9rQsKy2MFINs/pub#h.ytnw7m7yf7nk)


Integração Continua
-------------------
Os commits realizados na branch DEV, são automaticamentes baixados pelo Jenkins, onde são 
executados os testes. É verificado checkstyle, PMD, e se a cobertura de testes atende
o percentual mínimo exigido. 


IDE
-----------
Por ser tratar um de um projeto MAVEN, pode ser utilizada qualquer IDE para desenvolvimento.
Sendo assim, não deve ser comitado nenhum arquivo específico da IDE. (nb-config, .idea, etc). 


Deploy
-----------
1. `mvn clean install -Dmaven.test.skip=true` para compilar o .JAR na pasta target
2. `./sendwar` autenticacao.JAR` para fazer o upload do jar para o servidor
3. `/opt/jdk/jdk1.8.0_111_64bits/bin/java -jar -Xmx8192m -Xms512m autenticacao.jar --spring.profiles.active=producao` para start


Parar a Aplicação
-----------
1. `lsof -i:8091` para descobrir o numero do processo pela porta
2. `kill -9 {numero_processo}`


Profiles
-----------
1. `mvn spring-boot:run -Dspring.profiles.active=homologacao` para setar o profile em desenvolvimento


Logs
-----------
Os logs estão configurados para serem armazenados na pasta: /var/log/autenticacao

1. `tail -f application.log` para visualizar o log em tempo real
