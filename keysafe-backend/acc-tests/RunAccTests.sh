#!/bin/bash

cd ../src || exit;

#
# Falls der script ohne Argumente gestartet wird, wird die Anwendung komplett neu gebaut
#
if [ $# -eq 0 ]
  then
    #
    # Baue springboot app
    #
    mvn clean install

    #
    # Erstelle docker image für diese springboot app
    #
    docker build -t keysafe-backend-app .

  else
    echo '[!] skipping maven und docker build'

fi

cd ../acc-tests || exit;

#
# Erstelle tests images und starte container
#   - 'force-recreate' : container werden immer neu erstellt
#   - 'abort-on-container-exit': alle test container werden beendet sobald einer (Acc-Test container) beendet ist
#
docker-compose down
docker-compose up --build --abort-on-container-exit

#
# Entferne anonyme images
#
docker image prune -f

