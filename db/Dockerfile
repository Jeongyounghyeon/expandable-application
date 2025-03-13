FROM mysql:lts

ENV MYSQL_ROOT_PASSWORD=0000
ENV TZ Asia/Seoul

# Init DB
COPY ./script/init.sql /docker-entrypoint-initdb.d/

VOLUME /var/lib/mysql

EXPOSE 3306

CMD ["mysqld", "--mysql-native-password=ON"]