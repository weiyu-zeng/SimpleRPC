package com.rpc.client;


import com.rpc.common.User;
import com.rpc.service.UserService;

/**
 * @author zwy
 *
 * RPC客户端：调用服务器端的方法
 */
public class RPCClient {

    public static void main(String[] args) {
        // 初始化主机名ip和端口号port
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 8899);
        UserService proxy = clientProxy.getProxy(UserService.class);  // 反射获得代理

        // 服务的方法1：通过id获取User
        User userByUserId = proxy.getUserByUserId(10);
        System.out.println("从服务器端得到的user为：" + userByUserId);
        System.out.println();

        // 服务的方法2：（假装）插入一个User数据
        User user = User.builder().userName("张三").id(100).sex(true).build();
        Integer integer = proxy.insertUserId(user);
        System.out.println("向服务器端插入数据" + integer);
    }
}
