package com.rpc.client;


import com.rpc.common.RPCRequest;
import com.rpc.common.RPCResponse;

/**
 * @author weiyu_zeng
 *
 * RPC客户端：发送请求，获得response
 */
public interface RPCClient {
    RPCResponse sendRequest(RPCRequest request);
}
