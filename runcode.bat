javac -cp ../../lib/*;../../lib/tomcat/*;WEB-INF/classes;../../lib/mailtraffic/*; src/model/* src/controller/* src/api/* src/utils/* src/exception/* src/dao/* src/task/* src/auth/* -d WEB-INF/classes
cd WEB-INF/classes 
jar -cf ../../../../lib/mailtraffic.jar model/* controller/* api/* utils/* exception/* dao/* task/* auth/*
cd ../../../../bin
app_ctl run