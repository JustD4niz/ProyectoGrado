FROM tomcat:9.0-jdk17-temurin AS build
WORKDIR /build
COPY WEB-INF/lib ./lib
COPY src ./src
RUN find src -name "*.java" > sources.txt && \
    javac --release 17 -encoding UTF-8 \
      -classpath "lib/*:/usr/local/tomcat/lib/*" \
      -d classes @sources.txt

FROM tomcat:9.0-jre17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY WEB-INF /usr/local/tomcat/webapps/ROOT/WEB-INF
COPY --from=build /build/classes /usr/local/tomcat/webapps/ROOT/WEB-INF/classes
COPY META-INF /usr/local/tomcat/webapps/ROOT/META-INF
COPY *.jsp /usr/local/tomcat/webapps/ROOT/
COPY acciones /usr/local/tomcat/webapps/ROOT/acciones
COPY vistas /usr/local/tomcat/webapps/ROOT/vistas
COPY css /usr/local/tomcat/webapps/ROOT/css
COPY js /usr/local/tomcat/webapps/ROOT/js
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN sed -i 's/\r$//' /docker-entrypoint.sh && chmod +x /docker-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["catalina.sh", "run"]
