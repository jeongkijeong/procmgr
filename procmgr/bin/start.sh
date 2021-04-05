#!/bin/sh

export PROC_HOME=$HOME/app/procmgr
export PROC_NAME=procmgr.jar

if [ ! -z "`ps -eaf | grep java | grep $PROC_HOME/$PROC_NAME`" ]; then
    echo "$PROC_NAME already started."
    exit
fi

$JAVA_HOME/java -server -Xms1G -Xmx1G -XX:+UseG1GC -Duser.timezone=GMT+09:00 -jar $PROC_HOME/$PROC_NAME $PROC_HOME/conf/server.properties $PROC_HOME/conf/logback.xml &

