#!/bin/sh
echo "try to stop the process"
./stop.sh

echo "try to start the process"
nohup java -Xmx500m -Xss256k -jar springboot.mongo.gridfs-0.0.1-SNAPSHOT.jar   2>&1 &


echo "current process id is "$$
echo $! > tpid

