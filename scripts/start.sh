#!/bin/bash

# 변수 정의
PROJECT_ROOT="/home/ec2-user"
DEPLOY_DIR="$PROJECT_ROOT/deploy"
JAR_FILE="$PROJECT_ROOT/mungwithme-0.0.1-SNAPSHOT.jar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

S3_BUCKET="mungwithme-github-action-s3"
S3_KEY="BE_Server/latest.zip"
ZIP_FILE="$DEPLOY_DIR/latest.zip"

TIME_NOW=$(date +"%Y-%m-%d %H:%M:%S")

# 로그 시작
echo "$TIME_NOW > 배포 스크립트 시작" >> $DEPLOY_LOG

# S3에서 최신 zip 파일 다운로드
echo "$TIME_NOW > S3에서 최신 zip 파일 다운로드: s3://$S3_BUCKET/$S3_KEY" >> $DEPLOY_LOG
aws s3 cp s3://$S3_BUCKET/$S3_KEY $ZIP_FILE

if [ $? -ne 0 ]; then
  echo "$TIME_NOW > S3에서 zip 파일 다운로드 실패" >> $DEPLOY_LOG
  exit 1
fi

# 다운로드한 zip 파일 압축 해제
echo "$TIME_NOW > 압축 해제 시작" >> $DEPLOY_LOG
unzip -o $ZIP_FILE -d $DEPLOY_DIR

if [ $? -ne 0 ]; then
  echo "$TIME_NOW > 압축 해제 실패" >> $DEPLOY_LOG
  exit 1
fi

# 현재 실행 중인 애플리케이션 종료
echo "$TIME_NOW > 현재 실행 중인 애플리케이션 종료 시도" >> $DEPLOY_LOG
CURRENT_PID=$(pgrep -f $JAR_FILE)

if [ -n "$CURRENT_PID" ]; then
  echo "$TIME_NOW > 실행 중인 애플리케이션(PID: $CURRENT_PID) 종료" >> $DEPLOY_LOG
  kill -15 "$CURRENT_PID"
  sleep 5
else
  echo "$TIME_NOW > 실행 중인 애플리케이션이 없습니다" >> $DEPLOY_LOG
fi

# JAR 파일 복사
JAR_PATH=$(ls $DEPLOY_DIR/build/libs/mungwithme-0.0.1-SNAPSHOT.jar 2> /dev/null)

if [ -n "$JAR_PATH" ]; then
  cp -f $JAR_PATH $JAR_FILE
  echo "$TIME_NOW > JAR 파일 복사 완료: $JAR_PATH" >> $DEPLOY_LOG
else
  echo "$TIME_NOW > JAR 파일이 존재하지 않습니다. 복사 실패." >> $DEPLOY_LOG
  exit 1
fi

# JAR 파일 실행
echo "$TIME_NOW > JAR 파일 실행 시작: $JAR_FILE" >> $DEPLOY_LOG
nohup java -jar $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

# 실행된 애플리케이션 PID 확인
NEW_PID=$(pgrep -f $JAR_FILE)
if [ -n "$NEW_PID" ]; then
  echo "$TIME_NOW > 새로운 애플리케이션이 실행되었습니다. PID: $NEW_PID" >> $DEPLOY_LOG
else
  echo "$TIME_NOW > 애플리케이션 실행에 실패했습니다." >> $DEPLOY_LOG
  exit 1
fi
