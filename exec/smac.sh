#!/bin/bash

SMAC=`ps ax | grep -v grep | grep Smac.jar`
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

usage() {
    echo "Usage: $0 [start|stop]"
    echo "start: Starts the Smac daemon if an instance is not already running"
    echo "stop : Stops the Smac daemon if an instance is already running"
}

start() {
    DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
    if [ 0 -eq ${#SMAC} ]
    then
        cd "${DIR}"
        java -jar Smac.jar &
        sleep 1
        clear
        echo "The Smac daemon has started"
    else
        echo "The Smac daemon is already running"
    fi
}

stop() {
    STOPPED=0
    for child in $(ps -o pid,cmd ax | grep -v grep | grep Smac.jar )
    do
        if [[ $child =~ ^[0-9]+$ ]]
        then
            kill -9 $child
            STOPPED=1
        fi
    done
    if [ 1 -eq ${STOPPED} ]
    then
        echo "The Smac daemon has been stopped"
    else
        echo "The Smac daemon is not running"
    fi
}

case $1 in
    "start")
        echo "Starting the Smac daemon"
        start
        ;;
    "stop")
        echo "Stopping the Smac daemon"
        stop
        ;;
    *)
        usage
        ;;
esac
