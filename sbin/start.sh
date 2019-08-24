#!/usr/bin/env bash
cd "$(dirname $0)"
BIN_DIR="$(pwd)"
. ${BIN_DIR}/env-conf.sh
. ${BIN_DIR}/comm-func.sh
PRJ_DIR="$(dirname ${BIN_DIR})"

# get all dependency jars, prepared to add into classpath
dependencyJarStr=$(getAllPartternFiles "${PRJ_DIR}/dependency/*.jar" ":")

# load hadoop config file list from env-conf.sh
#hadoopFileList=$(loadSpecifyFilesInDir "${HADOOP_CONF_DIR}" "${HADOOP_FILE_LIST}" ":")

# add project and hadoop config dir to classpath
if [ "${NATIVE_FS_FLAG}x" == "falsex" ];then
    cusClassPath=${CUS_FS_CONF_DIR}:${PRJ_DIR}/conf:${dependencyJarStr}
else
    cusClassPath=${PRJ_DIR}/conf:${dependencyJarStr}
fi


# get project jar file path, it is better have one *.jar file in ${PRJ_DIR}/lib path
prjJar=$(getFirstPartternFiles "${PRJ_DIR}/lib/*.jar")

# start java program
java -cp "${cusClassPath}:${prjJar}" -DAPP_NAME=${APP_NAME} -Dlog4j.configuration=file:"${PRJ_DIR}/conf/log4j.properties" "org.apache.contrib.ftp.FtpServerStartUp" ${CUS_FTPSERVER_XML}
