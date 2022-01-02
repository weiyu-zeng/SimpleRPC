package com.rpc.server;

import com.rpc.service.*;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        BlogService blogService = new BlogServiceImpl();

//        Map<String, Object> serviceProvide = new HashMap<>();
//        serviceProvide.put("com.ganghuan.myRPCVersion2.service.UserService",userService);
//        serviceProvide.put("com.ganghuan.myRPCVersion2.service.BlogService",blogService);
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);  // 把userService存入 serviceProvider
        serviceProvider.provideServiceInterface(blogService);  // 把blogService存入 serviceProvider

//        RPCServer RPCServer = new ThreadPoolRPCRPCServer(serviceProvider);
        RPCServer RPCServer = new NettyRPCServer(serviceProvider);
        RPCServer.start(8899);
    }
}
