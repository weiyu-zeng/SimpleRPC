package com.rpc.client;


import com.rpc.common.Blog;
import com.rpc.common.User;
import com.rpc.service.BlogService;
import com.rpc.service.UserService;

/**
 * @author weiyu_zeng
 *
 * RPC客户端：调用服务器端的方法
 */
public class RPCClient {

    public static void main(String[] args) {

        ClientProxy rpcClientProxy = new ClientProxy ("127.0.0.1", 8899);  // 初始化host和port
        UserService proxy = rpcClientProxy .getProxy(UserService.class);

        // 服务的方法1
        User userByUserId = proxy.getUserByUserId(10);
        System.out.println("从服务器端得到的user为：" + userByUserId);

        // 服务的方法2
        User user = User.builder().userName("张三").id(100).sex(true).build();
        Integer integer = proxy.insertUserId(user);
        System.out.println("向服务器端插入数据" + integer);

        // 服务的方法3
        BlogService blogService = rpcClientProxy.getProxy(BlogService.class);
        Blog blogById = blogService.getBlogById(10000);
        System.out.println("从服务端得到的blog为：" + blogById);
    }
}
