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
 * 客户端代理：把动态代理封装request对象
 *
 * '@AllArgsConstructor'：它是lombok中的注解。使用后添加一个构造函数，该构造函数含有所有已声明字段属性参数
 *                        （这也就是为什么ClientProxy明明没定义构造函数，但RPCClient还可以再创建ClientProxy时，
 *                        通过构造函数传参给 host 和 port。）
 * java动态代理机制中有两个重要的类和接口InvocationHandler（接口）和Proxy（类）：也是实现动态代理的核心
 * InvocationHandler接口：是proxy代理实例的调用处理程序实现的一个接口，每一个proxy代理实例都有一个关联的调用处理程序，
 *                        在代理实例调用方法时，方法调用被编码分派到调用处理程序的invoke方法。
 *                        每一个动态代理类的调用处理程序都必须实现InvocationHandler接口，并且每个代理类的实例都关联到了
 *                        实现该接口的动态代理类调用处理程序中，当我们通过动态代理对象调用一个方法时候，这个方法的调用
 *                        就会被转发到实现InvocationHandler接口类的invoke方法来调用
 * Proxy：该类用于动态生成代理类，只需传入目标接口、目标接口的类加载器以及InvocationHandler便可为目标接口生成代理类及代理对象
 * Proxy.newProxyInstance：该方法用于为指定类装载器、一组接口及调用处理器生成动态代理类实例
 */
@AllArgsConstructor
public class ClientProxy implements InvocationHandler {

    private String host;

    private int port;

    /**
     * 动态代理，每一次代理对象调用方法，会经过此方法增强（反射获取request对象，socket发送至客户端）
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 构建RPCRequest对象，初始化其中的四个重要参数，使用了lombok中的builder。
        // 初始化interfaceName。初始化methodName，初始化params，，初始化paramsTypes
        RPCRequest request = RPCRequest.builder()
                                       .interfaceName(method.getDeclaringClass().getName())
                                       .methodName(method.getName())
                                       .params(args)
                                       .paramsTypes(method.getParameterTypes())
                                       .build();

        // 调用IOClient，通过输入输出流进行request的数据传输，并返回服务器端传来的response
        RPCResponse response = IOClient.sendRequest(host, port, request);
        System.out.println("response: " + response);

        return response.getData();  // 获取RPCResponse中的目标数据（因为RPCResponse中除了目标数据，还有状态码和状态信息这些非目标数据）
    }

    /**
     * 传入Client需要的服务的class反射对象
     */
    <T> T getProxy(Class<T> clazz) {
        // 传入目标接口的类加载器，目标接口，和InvocationHandler（的实现类，也就是本类，this），生成动态代理类实例
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
