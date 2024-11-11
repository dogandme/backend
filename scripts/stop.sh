#!/bin/bash

PROJECT_ROOT="/home/ec2-user/deploy"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"
TIME_NOW=$(date '+%Y-%m-%d %H:%M:%S')

# 현재 구동 중인 애플리케이션 PID 확인 (프로세스 이름에 'mungwithme' 포함)
CURRENT_PID=$(pgrep -f 'mungwithme')

if [ -z "$CURRENT_PID" ]; then
  echo "$TIME_NOW > 현재 실행 중인 애플리케이션이 없습니다." >> $DEPLOY_LOG
else
  echo "$TIME_NOW > 실행 중인 애플리케이션 PID: $CURRENT_PID. 애플리케이션 종료 중..." >> $DEPLOY_LOG
  kill -15 "$CURRENT_PID"

  # 종료 대기 (최대 10초)
  for i in {1..10}; do
    if ps -p "$CURRENT_PID" > /dev/null; then
      echo "$TIME_NOW > 종료 대기 중..." >> $DEPLOY_LOG
      sleep 1
    else
      echo "$TIME_NOW > 애플리케이션 종료 완료." >> $DEPLOY_LOG
      break
    fi

    # 프로세스가 종료되지 않으면 강제 종료
    if [ "$i" -eq 10 ]; then
      echo "$TIME_NOW > 애플리케이션이 정상 종료되지 않아 강제 종료를 시도합니다." >> $DEPLOY_LOG
      kill -9 "$CURRENT_PID"
    fi
  done
fi
