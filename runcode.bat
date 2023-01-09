javac -cp ../../lib/*;../../lib/tomcat/*;WEB-INF/classes;WEB-INF/lib/*; src/model/* src/controller/* src/api/* src/utils/* src/exception/* src/dao/* src/singleton/* -d WEB-INF/classes
cd WEB-INF/classes 
jar -cf ../lib/mailtraffic.jar model/* controller/* api/* utils/* exception/* singleton/* dao/*
cd ../../../../bin
app_ctl run