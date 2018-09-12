#!/bin/bash

mv `ls -t packages/*.jar|head -n 1` Edison-client.jar -f

sudo java -jar Edison-client.jar -n EXTEL-01 -u 192.168.30.133
