package com.onep1us;

import annotation.RpcServiceScan;
import server.net.NettyServer;
import server.net.RpcServer;
import server.register.NacosServiceRegistry;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author wanjiahao
 */
@RpcServiceScan
public class Main {
    public static void main(String[] args) {
        RpcServer rpcServer;
        try {
            rpcServer = new NettyServer(InetAddress.getLocalHost().getHostAddress(), 8002, new NacosServiceRegistry("123.60.148.100:8848"));
            rpcServer.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
