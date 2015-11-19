#!/bin/bash

export CLASSPATH=./src/:./libs/*:./out/production/Refugees/

if [ "$(ls -A out/production/Refugees/)" ];then 
rm out/production/Refugees/*; 
echo "Deleting .class files"
fi

javac -d out/production/Refugees/ src/*.java ; optirun java -ea Main
