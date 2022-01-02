package com.rpc.service;


import java.util.HashMap;
import java.util.Map;

/**
 * @author zwy
 *
 * ServiceProvider 存放服务接口名与服务端对应的实现类（本质是hashmap），服务启动时要暴露其相关的实现类
 * 根据request中的interface调用服务端中相关实现类。
 */
public class ServiceProvider {
    /**
     * 一个实现类可能实现多个接口
     */
    private Map<String, Object> interfaceProvider;

    // 构造函数，初始化一个空的 hashmap 赋给 Map<String, Object> interfaceProvider
    public ServiceProvider() {
        this.interfaceProvider = new HashMap<>();
    }

    public void provideServiceInterface(Object service) {
        // 反射，.getClass().getInterfaces()得到class的interface，按照interfaces name(key)和object(value)存入map
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class clazz : interfaces) {
            interfaceProvider.put(clazz.getName(), service);
        }
    }

    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);  // 通过interface name得到object
    }
}
