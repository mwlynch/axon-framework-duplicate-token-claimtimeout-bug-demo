#!/usr/bin/env bash

cd "$(dirname "$0")"

#COMPILE
mvn clean compile

#RUN DATABASES
docker-compose -p axon-framework-duplicate-token-claimtimeout-bug-demo -f docker-compose.yml down
docker-compose -p axon-framework-duplicate-token-claimtimeout-bug-demo -f docker-compose.yml up -d

#WAIT
echo "PRESS ANY KEY TO RUN GRACEFUL TEST (NO DUPLICATE TOKENS CREATED)"
read

mvn exec:java -Pgraceful
mvn exec:java -Pgraceful
mvn exec:java -Pgraceful

#WAIT
echo "PRESS ANY KEY TO RUN CRASH TEST (DUPLICATE TOKENS CREATED)"
read

#RUN
mvn exec:java -Pcrash
mvn exec:java -Pcrash
mvn exec:java -Pcrash


#WAIT
echo "PRESS ANY KEY TO EXIT AND REMOVE DATABASES"
read

#STOP DATABASES
docker-compose -p axon-framework-duplicate-token-claimtimeout-bug-demo -f docker-compose.yml down

#CLEAN MAVEN
mvn clean
