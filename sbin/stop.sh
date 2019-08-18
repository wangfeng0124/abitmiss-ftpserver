#!/usr/bin/env bash
cd "$(dirname $0)"
BIN_DIR="$(pwd)"
. ${BIN_DIR}/env-conf.sh
. ${BIN_DIR}/comm-func.sh
PRJ_DIR="$(dirname ${BIN_DIR})"

function checkAppAlive(){
    sh ${BIN_DIR}/status.sh
    prjNum=$?
    if [ ${prjNum} -eq 0 ];then
        echo "there has no program running..."
        exit 1
    elif [ ${prjNum} -gt 1 ];then
      echo "this project start more than one, please check:ps -ef|grep ${APP_NAME}|grep -v grep|awk -F' ' '{print $2}'|xargs pwdx|grep ${BIN_DIR}"
      exit 2
    fi
}

checkAppAlive
# kill process at dir: ${BIN_DIR}
echo "kill process at dir: ${BIN_DIR}"
while read line
do
  pidArr=(${line//:/})
  killSpecialPid ${pidArr[0]}
done <<EOF
$(ps -ef|grep "${APP_NAME}"|grep -v "grep"|awk -F' ' '{print $2}'|xargs pwdx|grep ${BIN_DIR})
EOF
