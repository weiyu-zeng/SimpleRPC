package com.rpc.client;

import com.rpc.common.RPCRequest;
import com.rpc.common.RPCResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zwy
 *
 * 客户端代理：把动态代理封装request对象（这里和simpleRPC-02的ClientProxy函数一样，保留了动态代理的设计）
 */
@AllArgsConstructor
public class RPCClientProxy implements InvocationHandler {
    private RPCClient client;

    // jdk动态代理，每一次代理对象调用方法，会经过此方法增强（反射获取request对象，socket发送至客户端）
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // request的构建，使用了lombok中的builder，更加简洁
        RPCRequest request = RPCRequest.builder().interfaceName(method.getDeclaringClass().getName())
                                       .methodName(method.getName())
                                       .params(args)
                                       .paramsTypes(method.getParameterTypes())
                                       .build();
        // 数据传输
        RPCResponse response = client.sendRequest(request);
//        System.out.println(response);
        return response.getData();
    }

    <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
