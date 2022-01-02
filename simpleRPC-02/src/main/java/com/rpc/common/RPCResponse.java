package com.rpc.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 定义了服务器端给客户端回应的抽象 RPCResponse，包含两个部分：
 * 1.状态信息：状态码int code，状态信息String message
 * 2.具体数据：Object data
 * 此外还包含success方法：将RPCResponse对象的code状态码初始化为200，data初始化为传入的data，后返回RPCResponse
 * 还包含fail方法，将RPCResponse对象的code初始化为500，将状态信息message初始化为"服务器发生错误"，后返回RPCResponse对象
 *
 * 上个例子中response传输的是User对象，显然在一个应用中我们不可能只传输一种类型的数据
 * 由此我们将传输对象抽象成为Object
 * RPC需要经过网络传输，有可能失败，类似于http，引入状态码和状态信息表示服务调用成功还是失败
 */
@Data
@Builder
public class RPCResponse implements Serializable {

    // 状态信息
    private int code;

    private String message;
    // 具体数据
    private Object data;

    public static RPCResponse success(Object data) {
        return RPCResponse.builder().code(200).data(data).build();
    }

    public static RPCResponse fail() {
        return RPCResponse.builder().code(500).message("服务器发生错误").build();
    }
}
