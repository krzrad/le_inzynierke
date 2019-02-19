#!/bin/bash

if [ ${1##*.} = ${2##*.} ] && [ ${1##*.} = "cql" ] ; then
	if [ $# -eq 3 ] && ([ $3 = '-d' ] || [ $3 = '--diff' ]); then
		java -jar CassandraAnalyser.jar $1 $2 '-d'
	fi
	if [ $# -eq 2 ]; then
		java -jar CassandraAnalyser.jar $1 $2
	fi
	
else
	echo "Nie podano plików CQL..."
fi
