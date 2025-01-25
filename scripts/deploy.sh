#!/bin/bash

CURRENT_PID=$(docker container ls -q)
echo "> docker stop $CURRENT_PID"
sudo docker stop "$CURRENT_PID"

echo "> login to ECR"
# shellcheck disable=SC2046
sudo docker login -u AWS -p $(aws ecr get-login-password --region ap-northeast-2) 897729140973.dkr.ecr.ap-northeast-2.amazonaws.com

echo "> docker pull latest image"
sudo docker pull 897729140973.dkr.ecr.ap-northeast-2.amazonaws.com/dev/resumate-ecr:latest

echo "> docker run latest image"
sudo docker run -d -p 8080:8080 897729140973.dkr.ecr.ap-northeast-2.amazonaws.com/dev/resumate-ecr:latest
