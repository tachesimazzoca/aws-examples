#!/bin/sh
cd `dirname $0`
 ROOT_PATH=`pwd`
 java -Xms256M -Xmx1024M -cp $ROOT_PATH:$ROOT_PATH/../lib/systemRoutines.jar:$ROOT_PATH/../lib/userRoutines.jar::.:$ROOT_PATH/a_basic_job_0_1.jar:$ROOT_PATH/../lib/dom4j-1.6.1.jar:$ROOT_PATH/../lib/log4j-1.2.15.jar: sandbox.a_basic_job_0_1.A_Basic_Job --context=Default "$@" 
