#!/bin/bash
#JAVA_HOME=/cad2/ece419s/java/jdk1.6.0/
JAVA_HOME=/usr/

# arguments to Mazewar
# $1 = hostname of where mazewar client is located
# $2 = listening port

${JAVA_HOME}/bin/java Mazewar $1 $2
