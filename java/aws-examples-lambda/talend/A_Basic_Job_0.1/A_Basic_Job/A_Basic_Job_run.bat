%~d0
 cd %~dp0
 java -Xms256M -Xmx1024M -cp ../lib/systemRoutines.jar;../lib/userRoutines.jar;.;a_basic_job_0_1.jar;../lib/dom4j-1.6.1.jar;../lib/log4j-1.2.15.jar; sandbox.a_basic_job_0_1.A_Basic_Job --context=Default %* 