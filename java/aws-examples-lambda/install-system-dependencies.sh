#!/bin/sh

cd `dirname ${0}`

mvn install:install-file \
  -Dfile=./talend/A_Basic_Job_0.1/lib/systemRoutines.jar \
  -DgroupId=com.github.tachesimazzoca.aws.examples.lambda \
  -DartifactId=talend-system-routines \
  -Dversion=0.0.0 \
  -Dpackaging=jar \
  -DgeneratePom=true

mvn install:install-file \
  -Dfile=./talend/A_Basic_Job_0.1/lib/userRoutines.jar \
  -DgroupId=com.github.tachesimazzoca.aws.examples.lambda \
  -DartifactId=talend-user-routines \
  -Dversion=0.0.0 \
  -Dpackaging=jar \
  -DgeneratePom=true

mvn install:install-file \
  -Dfile=./talend/A_Basic_Job_0.1/A_Basic_Job/a_basic_job_0_1.jar \
  -DgroupId=com.github.tachesimazzoca.aws.examples.lambda \
  -DartifactId=talend-job \
  -Dversion=0.0.0 \
  -Dpackaging=jar \
  -DgeneratePom=true

