#!/usr/bin/env python
import socket
import sys
import random
import numpy

# HOST = 'coding.debuntu.org'
# GET = '/rss.xml'

# PORT = 80

HOST = 'localhost'
PORT = 4242;

try:
  sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
except socket.error, msg:
  sys.stderr.write("[ERROR] %s\n" % msg[1])
  sys.exit(1)

try:
  sock.connect((HOST, PORT))

except socket.error, msg:
  sys.stderr.write("[ERROR] %s\n" % msg[1])
  sys.exit(2)

message = "Hello, Mario! I am PyBrain! \r\n"
buf = "01000\r\n"

sock.send(message)
sock.send(buf)
sock.send(message + buf)

size = 4096
data = sock.recv(size)
print data
string = ""
#while len(data):
#    print len(data), data
#    data = sock.recv(size)

buf = "01000\r\n"
jump = "01010\r\n"
unjump = "01010\r\n"
actions = [buf, jump,unjump]
print "obs:"
sock.settimeout(3)

def parse(data):
    ret = numpy.empty(shape = (22,22), dtype = numpy.int)
    k = 0
    for i in range(22):
        for j in range(22):
#            if data[k+2] == '0false':
#                print data[k+2], "EXplausion! at ", k
#                break
            ret[i,j] = int(data[k + 2])
            k += 1
    return ret

while True:
#    print i, "len(data) = ", len(data)
#    while len(data):
#        print i, "len(data) = ", len(data)
#        string = string + data
#        data = sock.recv(size)
#        print string
#    print "received: " + data
#    print "sending: " + buf
    data = sock.recv(size).split(' ')
    if len(data) != 486:
        print "Network critical error. len(data) = ", len(data), ". \nConnection dropped"
        break
#    print "data:", data
#    print parse(data)
    sock.send(actions[random.randint(0, 2)])


sock.close()

print string
sys.exit(0)