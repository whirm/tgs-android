# Released under GNU LGPL 2.1
# See LICENSE.txt for more information

#import android
import os
import random
import socket
import sys
import time
import threading
import logging
import android
#import dht

#sys.stderr = open('/sdcard/dht', 'w')


#my_path = os.path.dirname(dht.__file__)
#sys.path.append(my_path)
#print >>sys.stderr, my_path
#print >>sys.stderr, sys.path


droid = android.Android()

from pymdht import Pymdht
from identifier import Id, RandomId
from node import Node
import routing_nice_rtt as routing_m_mod
import lookup_a4 as lookup_m_mod
import exp_plugin_template as experimental_m_mod

#import ptime as time
#import identifier


HANDSHAKE = 0x00
DATA = 0x01
ACK = 0x02
HAVE = 0x03
HASH = 0x04
PEX_RES = 0x05
PEX_REQ = 0x06
SIGNED_HASH = 0x07
HINT = 0x08
MSGTYPE_RCVD = 0x09
VERSION = 0x10

CHANNEL_SIZE = 4
BIN_SIZE = 4
HASH_SIZE = 20
TS_SIZE = 4
PEER_SIZE = 6
VERSION_SIZE = 1

CHANNEL_ZERO = '\0' * CHANNEL_SIZE

MIN_BT_PORT = 1024
MAX_BT_PORT = 2**16


stop_server = False

my_node = Node(('127.0.0.1', 7000), RandomId())

dht = Pymdht(my_node, '/sdcard/swift/',
             routing_m_mod,
             lookup_m_mod,
             experimental_m_mod,
             None,
             logging.DEBUG,
             False)

#droid = android.Android()


class SwiftTraker(object):

    def __init__(self, port):
#        Raul, 2012-03-09: Do not create a thread
#        threading.Thread.__init__(self)
        self.rand_num = random.randint(0, 999)
        self.dht = dht
        self.port = port
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.socket.settimeout(10) # This is to show that the thread is running
        try:
            self.socket.bind(('', port))
        except (socket.error):
            droid.MakeToast('EXCEP: port in use')
            raise
        self.channel_m = ChannelManager()
        
    def start(self):#run(self):
        while 1:
            try:
                data, addr = self.socket.recvfrom(1024)
            except (socket.timeout):
                droid.makeToast('DHT alive %d' % self.rand_num)
            except:
                droid.makeToast('EXCEPTION in recvfrom')
            else:
                self.handle(data, addr)

    def _on_peers_found(self, channel, peers, node):
        #current_time = time.time()
#        if not channel.open: 
#            print 'Got peers but channel is CLOSED'
#            return
#        if not peers:
#            droid.makeToast("DHT: end of lookup")  
#            print "end of lookup"
#            self.channel_m.remove(channel)
#            self.wfile.write('%d CLOSE\r\n' % (channel.send))
#            return
        new_peers = []
        if peers is None:
            droid.makeToast('End of lookup')
            return
        for peer in peers:
            if peer not in channel.peers:
                channel.peers.add(peer)
                new_peers.append(peer)
        droid.makeToast('DHT got %d peers' % len(channel.peers))
        print 'got %d peer' % len(peers)
#        for peer in new_peers:
#            msg = '%d PEER %s:%d\r\n' % (channel.send,
#                                         peer[0], peer[1])
#            self.wfile.write(msg)
#        return

    def handle(self, data , addr):
        data_len = len(data)
        i = 0
        print 'in: ',
        remote_cid = data[:CHANNEL_SIZE]
        i += CHANNEL_SIZE
        print '%r' % remote_cid,
        channel = self.channel_m.get(remote_cid, addr)
        if not channel:
            print 'Invalid channel id'
            return
        while i < data_len:
            msg_type = ord(data[i])
            i += 1
            if msg_type == HANDSHAKE:
                print 'HAND',
                channel.remote_cid = data[i:i+CHANNEL_SIZE]
                i += CHANNEL_SIZE
            elif msg_type == DATA:
                print 'DATA',
                i = data_len # DATA always ends a datagram
            elif msg_type == ACK:
                print 'ACK',
                i += TS_SIZE + BIN_SIZE
            elif msg_type == HAVE:
                print 'HAVE',
                i += BIN_SIZE
            elif msg_type == HASH:
                print 'HASH',
                i += BIN_SIZE
                channel.rhash = Id(data[i:i+HASH_SIZE])
                i += HASH_SIZE
            elif msg_type == PEX_RES:
                print 'PRES',
                i += 0 #no arguments
            elif msg_type == PEX_REQ:
                print 'PREQ',
                i += PEER_SIZE
            elif msg_type == SIGNED_HASH:
                print 'SHASH',
                print `data`
                raise NotImplemented
            elif msg_type == HINT:
                print 'HINT',
                i += BIN_SIZE
            elif msg_type == MSGTYPE_RCVD:
                print 'MSGTYPE > not implemented',
                print `data`
                raise NotImplemented
            elif msg_type == VERSION:
                print 'VERSION',
                i += VERSION_SIZE
            else:
                print 'UNKNOWN: ', msg_type,
                print `data`
                raise NotImplemented
        print
        if remote_cid == CHANNEL_ZERO and channel.rhash:
            droid.makeToast(">>>>>>> DHT: got HANDSHAKE from swift <<<<<<<")  
            self.dht.get_peers(channel, channel.rhash, self._on_peers_found, 0)
            # need to complete handshake
            reply = ''.join((channel.remote_cid,
                             chr(HANDSHAKE),
                             channel.local_cid,
                             ))
            self.socket.sendto(reply, addr)
            droid.makeToast('>>>>>>>>>>>>> GETTING PEERS <<<<<<<<<<<<<<')
            reply = ''.join((channel.remote_cid,
                             chr(PEX_RES),
                             socket.inet_aton('130.161.211.194'), #Delft
                             chr(20050>>8),
                             chr(20050%256),
                             chr(PEX_RES),
                             socket.inet_aton('192.16.125.242'), #KTH
                             chr(20050>>8),
                             chr(20050%256),
                             ))
            self.socket.sendto(reply, addr)
            # time.sleep(.1) #CLOSE
            # reply = ''.join((channel.remote_cid,
            #                  chr(HANDSHAKE),
            #                  CHANNEL_ZERO,
            #                  ))
            # self.socket.sendto(reply, addr)
            
                
class Channel(object):

    def __init__(self, remote_addr):
        self.remote_addr = remote_addr
        self.local_cid = ''.join(
            [chr(random.randint(0, 0xff)) for i in range(CHANNEL_SIZE)])
        self.remote_cid  = None
        self.peers = set()
        self.rhash = None

        
class ChannelManager(object):

    def __init__(self):
        self.channels = []

    def get(self, local_cid, remote_addr):
        channel = None
        if local_cid == CHANNEL_ZERO:
            channel = Channel(remote_addr)
            self.channels.append(channel)
        else:
            for c in self.channels:
                if c.local_cid == local_cid:
                    channel = c
        return channel

    def remove(self, channel):
        for i in range(len(self.channels)):
            if self.channels[i].local_cid == local_cid:
                del self.channels[i]

                
if __name__ == '__main__':
    st = SwiftTraker(9999).start()
    time.sleep(2222)
