call mvn clean package -DskipTests
call argumentsFormat.bat
call java -jar target/jdebug-1.0-SNAPSHOT-jar-with-dependencies.jar %arguments%
pause