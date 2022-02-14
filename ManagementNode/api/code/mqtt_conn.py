#!/usr/bin/env python3

import ssl
import paho.mqtt.client as mqtt
from app.mgmt import Mgmt
from paho.mqtt.client import MQTTv311

OPEN = "open"
ALARM = "alarm"
CLOSE = "close"

client = mqtt.Client("mgmt", protocol=MQTTv311)
mn = Mgmt()

def process_message(client, userdata, message):
    msg_decoded = (str(message.payload.decode("utf-8")))
    if msg_decoded:
        result = mn.check_rfid(msg_decoded)
        if len(result) == 2:
            if result[1] == OPEN:
                client.publish(message.topic + "/listen", OPEN +"/"+result[0], 1)
            else:
                client.publish(message.topic + "/listen", ALARM + "/" + result[0], 1)
        else:
            client.publish(message.topic + "/listen", CLOSE, 1)

def connect_to_broker(broker):
    client.tls_set(ca_certs="/code/app/ca.crt", certfile="/code/app/client.crt", keyfile="/code/app/client.key",
                   tls_version=ssl.PROTOCOL_SSLv23, ciphers=None, cert_reqs=ssl.CERT_NONE)
    client.username_pw_set("mgmt", "1234")
    client.connect(broker, 8883, 600)

    client.on_message = process_message
    client.loop_start()
    client.subscribe("room/+", qos=1)

def disconnect_from_broker():
    client.loop_stop()
    client.disconnect()

