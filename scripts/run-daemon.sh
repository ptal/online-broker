#!/usr/bin/env bash
DAEMON_DIR=/home/ubuntu/online-broker/daemon
echo $(date) >> $DAEMON_DIR/logs/cron-log.txt
java -cp $DAEMON_DIR/daemon-assembly-0.1-SNAPSHOT.jar fr.jussieu.Daemon >> $DAEMON_DIR/logs/cron-log.txt
