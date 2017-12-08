#!/bin/sh
java -server -Xmx1024M -Xms1024M -XX:PermSize=128M -XX:MaxPermSize=128M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=../logs -Djava.ext.dirs=../lib -cp ../lib/smsSender-0.0.1-SNAPSHOT.jar com.chelsea.smsSender.Main >> ../logs/out 2>&1 &
echo $! > pid