#!/bin/sh
if [ $# != 2 ]
then
    echo "Usage: ./run.sh <filename.xml>"
    exit 0
fi
echo "Loading..."
java -jar lib/jython.jar launcher.py --input $1 --input2 $2
