FROM mysql:5.6

MAINTAINER Michael Seaton <mseaton@pih.org>

COPY my.cnf /etc/alternatives/my.cnf
RUN chmod 644 /etc/alternatives/my.cnf
COPY openmrs.sql /docker-entrypoint-initdb.d/setup.sql
RUN chown -R mysql:mysql /docker-entrypoint-initdb.d/

ENV MYSQL_ROOT_PASSWORD="test"
ENV MYSQL_DATABASE="openmrs"
