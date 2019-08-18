#!/usr/bin/env bash
cd "$(dirname $0)"
BIN_DIR="$(pwd)"
. ${BIN_DIR}/env-conf.sh
. ${BIN_DIR}/comm-func.sh
PRJ_DIR="$(dirname ${BIN_DIR})"

prjNum=$(ps -ef|grep ${APP_NAME}|grep java|grep ${PRJ_DIR}|grep -v grep|wc -l)
if [ ${prjNum} -eq 0 ];then
  echo "this project has dead"
elif [ ${prjNum} -eq 1 ];then
  echo "this project is alived, info:$(ps -ef|grep ${APP_NAME}|grep java|grep -v grep|awk -F' ' '{print $2}'|xargs pwdx|grep ${PRJ_DIR})"
else
  echo "this project start more than one, please check:ps -ef|grep ${APP_NAME}|grep -v grep|awk -F' ' '{print $2}'|xargs pwdx|grep ${PRJ_DIR}"
fi
exit ${prjNum}
