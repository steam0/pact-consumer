#Download OS
FROM    openjdk:8-jdk-alpine

# Add volume
VOLUME  /tmp

# Add file to image
ADD     build/libs/jwt-demo-0.0.1-SNAPSHOT.jar /app.jar

# Open file mod.
RUN     sh -c 'touch /app.jar'

# Run app
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
