javac -cp ../../lib/*;../../lib/tomcat/*;WEB-INF/classes;WEB-INF/lib/*; src/model/* src/controller/* src/api/* src/utils/* src/exception/* src/dao/* -d WEB-INF/classes
cd WEB-INF/classes 
jar -cf ../lib/mailtraffic.jar model/* controller/* api/* utils/* exception/* dao/*
cd ../../../../bin
app_ctl run