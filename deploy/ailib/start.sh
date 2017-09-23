#!/bin/bash

#app=org.voltdb.compiler.VoltCompiler
voltdb=org.voltdb.VoltDB
VOLTDB_VOLTDB=$VOLTDB_PATH/voltdb
VOLTDB_LIB=$VOLTDB_PATH/lib

APPCLASSPATH=$CLASSPATH:$({ \
    \ls -1 "$VOLTDB_VOLTDB"/voltdb-*.jar; \
    \ls -1 "$VOLTDB_LIB"/*.jar; \
    \ls -1 "$VOLTDB_LIB"/extension/*.jar; \
} 2> /dev/null | paste -sd ':' - )

optType=$1

host=$2

temp=`echo $0 | awk -F '/' '{print $NF}'`

echo $0 > .temp

log4jPath=`sed "s/$temp//g" .temp`

cd $log4jPath

rm -rf .temp

if [[ ${optType}X = X || ${host}X = X ]]; then
	#statements	 
	 echo "Usage: ./$temp create hostip"
else
	#echo "./$temp $optType $host"
	if [ $optType = "rejoin" ]; then
	    export paramter="leader $host"
	elif [ $optType = "recover" ]; then
		export paramter="deployment deployment.xml leader $host"
	else
	    export paramter="catalog ai-voltdb-server.jar deployment deployment.xml leader $host"
	fi

$JAVA_HOME/bin/java -classpath $APPCLASSPATH -Dlog4j.configuration=log4j.properties -Djava.library.path=$VOLTDB_VOLTDB $voltdb $optType $paramter
fi
