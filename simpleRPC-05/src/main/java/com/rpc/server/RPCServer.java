package com.rpc.server;

/**
 * @author weiyu_zeng
 *
 * RPC服务器端：接受，解析request，封装，发送response
 *
 */
public interface RPCServer {
    void start(int port);
    void stop();
}