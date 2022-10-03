#!/bin/bash

if [[ ! -f .env ]]
then
    echo ".env does not exist."
    exit
fi

#
# Baue springboot app
#
mvn clean install || exit

#
# Erstelle docker image für diese springboot app
#
docker build -t keysafe-backend-app . || exit

#
docker-compose down

#
# Entferne anonyme images
#
docker image prune -f

docker-compose build
docker-compose up


