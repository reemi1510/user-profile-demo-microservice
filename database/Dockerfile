FROM postgres:9.6

COPY ./init-db.sh /docker-entrypoint-initdb.d/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD psql -d projectdb -U projectdb

EXPOSE 5432