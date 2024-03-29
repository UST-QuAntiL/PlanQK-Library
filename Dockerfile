FROM gradle:7-jdk17 as builder
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle assemble --no-daemon

FROM tomcat:10.0-jdk17
LABEL maintainer = "Lukas Harzenetter <lharzenetter@gmx.de>"

ENV LIBRARY_WORKSPACE /var/planqk-library

RUN mkdir /var/planqk-library \
    && touch /var/planqk-library/PlanQK.bib \
    && sed -i 's/port="8080"/port="2903"/g' ${CATALINA_HOME}/conf/server.xml

COPY --from=builder /home/gradle/src/build/libs/PlanQK-Library*.war ${CATALINA_HOME}/webapps/ROOT.war

EXPOSE 2903

CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
