FROM nginx:stable-perl

COPY ./conf/nginx.conf /etc/nginx/nginx.conf

ENV TZ Asia/Seoul

ENTRYPOINT [ "nginx", "-g", "daemon off;" ]
