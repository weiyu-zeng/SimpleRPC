package com.rpc.server;

/**
 * @author zwy
 */
public interface RPCServer {
    void start(int port);
    void stop();
}