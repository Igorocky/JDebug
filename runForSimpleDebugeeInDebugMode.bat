call arguments.bat
call java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=2044 -jar target/jdebug-1.0-SNAPSHOT-jar-with-dependencies.jar %arguments%
pause