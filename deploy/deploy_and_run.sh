#!/bin/bash

# 변수 설정
JAR_FILE=../build/libs/*.jar
REMOTE_USER=gwon
REMOTE_HOST=bottlemoon.me
REMOTE_DIR=/home/gwon/optimization
DOCKER_COMPOSE_FILE=docker-compose.yml
DOCKERFILE=Dockerfile

# JAR 파일 빌드
mvn clean package

# JAR 파일과 Dockerfile, docker-compose.yml 전송
scp $JAR_FILE $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR
scp $DOCKER_COMPOSE_FILE $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR
scp $DOCKERFILE $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR

# 원격 서버에서 Docker Compose 실행
ssh $REMOTE_USER@$REMOTE_HOST << 'ENDSSH'
cd /home/gwon/optimization
docker compose down
docker compose up --build -d
ENDSSH