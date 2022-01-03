package com.rpc.client;


import com.rpc.common.RPCRequest;
import com.rpc.common.RPCResponse;

/**
 * @author zwy
 *
 * RPC客户端：发送请求，获得response
 */
public interface RPCClient {
    RPCResponse sendRequest(RPCRequest request);
}
