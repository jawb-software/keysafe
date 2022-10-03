#### Create .env file in src/ folder with content:

    POSTGRES_DB             = <database-name>
    POSTGRES_USER           = <username>
    POSTGRES_PASSWORD       = <password>
    SPRING_PROFILES_ACTIVE  = prod

###Build & Start

####1. Required software and tools
* Java 17+
* Maven
* Docker
####2. Build and start the app:

    mvn clean package 
    docker build -t keysafe-backend-app .

    docker-compose down
    docker-compose build
    docker-compose up

####Alternatively

    ./Start.sh


####Swagger

http://localhost:8080/swagger.html