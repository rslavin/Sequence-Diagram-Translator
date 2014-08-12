#!/bin/sh
if [ $# != 2 ]
then
    echo "Usage: ./run.sh <policy_filename.uml> <regulation_filename.uml"
    exit 0
fi
echo "Loading..."
java -jar lib/jython.jar launcher.py --policy $1 --regulation $2
