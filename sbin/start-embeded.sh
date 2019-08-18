#!/usr/bin/env bash
cd "$(dirname $0)"
BIN_DIR="$(pwd)"
cd "$(dirname ${BIN_DIR})"

function getAllPartternFile(){
  funcRelativePath=$1
  funcPattern=$2
jarsArr=($(ls ${funcRelativePath}/*.jar))
jarsArrLen=${#jarsArr[@]}
jarsStr="conf/:/etc/hadoop/conf/"
for((i = 0;i < jarsArrLen;i ++))
do
    jarsStr=${jarsStr}":"${jarsArr[i]}
done
}

echo ${BIN_DIR}
jarsArr=($(ls dependencies/*.jar))
jarsArrLen=${#jarsArr[@]}
jarsStr="conf/:/etc/hadoop/conf/"
for((i = 0;i < jarsArrLen;i ++))
do
    jarsStr=${jarsStr}":"${jarsArr[i]}
done
#jarsStr=${jarsStr#*:}

java -cp "${jarsStr}:lib/abitmiss-ftpserver-0.1.jar" "FtpServerStartUp"
