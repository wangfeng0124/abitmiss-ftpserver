#!/usr/bin/env bash


#################################################################
# get all files which pattern matched, this function need two parameters
# param 01: a file path
# param 02: split char
#
# how to use? for example:
# abc=$(getAllPartternFiles "../src/main/resources/*.properties" ":")
# echo $abc
#################################################################
function getAllPartternFiles(){
  funcPath=$1
  funcSplitChar=$2
  tmpLsFileArr=($(ls ${funcPath}))
  tmpLsFileArrLen=${#tmpLsFileArr[@]}
  funcRsStr=""
  for((i = 0;i < tmpLsFileArrLen;i ++))
  do
    if [ $i -gt 0 ];then
      funcRsStr=${funcRsStr}${funcSplitChar}
    fi
    funcRsStr=${funcRsStr}${tmpLsFileArr[i]}
  done
  echo ${funcRsStr}
}

#################################################################
# get the first file which pattern matched, this function need two parameters
# param 01: a file path
#
# how to use? for example:
# abc=$(getFirstPartternFiles "../src/main/resources/*.properties")
# echo $abc
#################################################################
function getFirstPartternFiles(){
  funcPath=$1
  tmpLsFileArr=($(ls ${funcPath}))
  tmpLsFileArrLen=${#tmpLsFileArr[@]}
  funcRsStr=""
  if [ ${tmpLsFileArrLen} -gt 0 ];then
    funcRsStr=${tmpLsFileArr[0]}
  fi
  echo "${funcRsStr}"
}

#################################################################
# load sepecify files from a directory,
# param 01: a file path
# param 02: file list, seperated by comma
# param 03: seperator char, for output string
#
# how to use? for example:
# abc=$(loadSpecifyFilesInDir "/etc/hadoop/conf" "hdfs-site.xml,core-site.xml" ":")
# echo ${abc}
#################################################################
function loadSpecifyFilesInDir(){
  funcDir=$1
  funcFilesStr=$2
  funcSplitChar=$3
  tmpFileArr=(${funcFilesStr//,/ })
  tmpFileArrLen=${#tmpFileArr[@]}
  funcRsStr=""
  for((i = 0;i < tmpFileArrLen;i ++))
  do
    if [ $i -gt 0 ];then
      funcRsStr=${funcRsStr}${funcSplitChar}
    fi
    funcRsStr=${funcRsStr}${funcDir}/${tmpFileArr[i]}
  done
  echo "${funcRsStr}"
}

#################################################################
# kill a special process id
# param 01: pid
#
# how to use? for example:
# lkillSpecialPid 7012
# echo $?
#################################################################
function killSpecialPid(){
  funcPid=$1
  kill -9 ${funcPid}
  if [ $? -ne 0 ];then
    kill -9 ${funcPid}
    if [ $? -ne 0 ];then
      unset ${funcPid}
      echo "the ${funcPid} has failed kill, please check:ps -ef|grep ${funcPid}"
      return 1
    fi
  fi
  echo "the ${funcPid} has killed successfully"
  unset funcPid
  return 0
}
