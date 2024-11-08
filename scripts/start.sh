#!/bin/bash

PROJECT_ROOT="/home/ec2-user/deploy"
JAR_FILE="$PROJECT_ROOT/mungwithme-0.0.1-SNAPSHOT.jar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +"%Y-%m-%d %H:%M:%S")

# 1. 이전 애플리케이션 종료
CURRENT_PID=$(pgrep -f $JAR_FILE)

if [ -n "$CURRENT_PID" ]; then
    echo "$TIME_NOW > 이전 프로세스 종료: PID $CURRENT_PID" >> $DEPLOY_LOG
    sudo kill -15 "$CURRENT_PID"
    sleep 5
fi

# 2. 최신 JAR 파일 복사
echo "$TIME_NOW > JAR 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE

# 3. 환경 변수 로드
echo "$TIME_NOW > 환경 변수 로드" >> $DEPLOY_LOG
source ~/.bashrc

# 4. JAR 파일 실행
echo "$TIME_NOW > JAR 파일 실행: $JAR_FILE" >> $DEPLOY_LOG
nohup java -jar -Dspring.profiles.active=prod $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

# 5. 실행된 프로세스 아이디 출력
NEW_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디: $NEW_PID" >> $DEPLOY_LOG
