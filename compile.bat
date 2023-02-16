javac -cp ../../lib/*;../../lib/tomcat/*;WEB-INF/classes;../../lib/mailtraffic/*; src/model/* src/controller/* src/api/* src/utils/* src/dao/* src/task/* src/auth/* src/messagetrace/* -d WEB-INF/classes
cd WEB-INF/classes 
jar -cf ../../../../lib/mailtraffic.jar model/* controller/* api/* utils/* dao/* task/* auth/* messagetrace/*
