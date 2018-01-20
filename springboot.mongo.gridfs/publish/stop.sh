#!/bin/sh
pidFile=tpid

read -r pid < $pidFile

if [  $pid ]; then
  echo 'processId in file:'${pid}
  if ps -p $pid > /dev/null
  then
     echo "$pid is running, and will kill with 15"
     # Do something knowing the pid exists, i.e. the process with $PID is running
     echo "kill -15 $pid 。。。"
     kill -15 $pid   	
  fi
  sleep 5
  if ps -p $pid > /dev/null
  then
     echo "$pid is running, and will kill with 15"
     # Do something knowing the pid exists, i.e. the process with $PID is running
     echo "kill -9 $pid 。。。"
     kill -9 $pid
  else 
     echo "has stopped by kill -15"
  fi
  >$pidFile
else
  echo "pid is null when trying to stop"
fi
