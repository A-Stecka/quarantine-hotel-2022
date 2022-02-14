FROM eclipse-mosquitto
COPY ./mosquitto.conf /mosquitto/config/mosquitto.conf

COPY ./ca.crt /mosquitto/config/ca.crt
COPY ./broker.crt /mosquitto/config/broker.crt
COPY ./broker.key /mosquitto/config/broker.key

COPY ./passwords.pwd /mosquitto/config/passwords.pwd

RUN cat /mosquitto/config/ca.crt >> /etc/ssl/certs/ca-certificates.crt