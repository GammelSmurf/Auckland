FROM adoptopenjdk/openjdk11:latest

RUN mkdir -p /software
ADD /backend/target/backend-*.war /software/auck.war
CMD java -Dserver.port=$PORT $JAVA_OPTS -jar /software/auck.war