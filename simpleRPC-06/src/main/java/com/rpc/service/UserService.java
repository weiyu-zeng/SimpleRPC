package com.rpc.service;

import com.rpc.common.User;

/**
 * @author zwy
 */
public interface UserService {

    // 客户端通过这个接口调用服务端的实现类
    User getUserByUserId(Integer id);

    // 给这个服务增加一个功能
    Integer insertUserId(User user);
}
