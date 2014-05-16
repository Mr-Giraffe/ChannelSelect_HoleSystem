#!/bin/bash
echo "0913" | sudo iwconfig eth1 channel $1
echo "0913" | sudo iwconfig eth1 rate 1M
