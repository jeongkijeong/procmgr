#!/bin/sh

PROC_HOME=$HOME/app/procmgr
PROC_NAME=procmgr.jar

PROC_LIST=`ps -ef | grep $PROC_HOME | grep $PROC_NAME | grep -v grep | awk '{print $2 }'`

for PID in $PROC_LIST
do
    kill -9 $PID
    echo $PROC_HOME/$PROC_NAME 'shutdown succeeded.('$PID')'
done
