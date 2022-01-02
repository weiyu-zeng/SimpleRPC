package com.rpc.client;

import com.rpc.common.Blog;
import com.rpc.common.User;
import com.rpc.service.BlogService;
import com.rpc.service.UserService;


/**
 * @author zwy
 *
 * 用例测试的类，继承了很大一部分RPCClient 的main函数。
 * 但也新增了新的使用方法。
 */
public class TestClient {
    public static void main(String[] args) {
        // 构建一个使用java socket或者netty的客户端
        RPCClient rpcClient = new NettyRPCClient("127.0.0.1", 8899);
        // 把这个客户端传入代理客户端
        RPCClientProxy rpcClientProxy = new RPCClientProxy(rpcClient);
        // 代理客户端根据不同的服务，获得一个代理类， 并且这个代理类的方法以或者增强（封装数据，发送请求）
        UserService userService = rpcClientProxy.getProxy(UserService.class);

        // 服务的方法1
        User userByUserId = userService.getUserByUserId(10);
        System.out.println("从服务器端得到的user为：" + userByUserId);

        // 服务的方法2
        User user = User.builder().userName("张三").id(100).sex(true).build();
        Integer integer = userService.insertUserId(user);
        System.out.println("向服务器端插入数据" + integer);

        // 服务的方法3
        BlogService blogService = rpcClientProxy.getProxy(BlogService.class);
        Blog blogById = blogService.getBlogById(10000);
        System.out.println("从服务端得到的blog为：" + blogById);
    }
}
