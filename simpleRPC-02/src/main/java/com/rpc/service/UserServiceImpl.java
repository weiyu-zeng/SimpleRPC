package com.rpc.service;


import com.rpc.common.User;

import java.util.Random;
import java.util.UUID;


/**
 * @author zwy
 *
 * 服务器端提供服务的方法
 * 1.getUserByUserId方法：接收一个id，返回一个User对象，提供属于这个ID（Integer）的User，
 * User中包含他的ID（Integer），名字Name（String）和性别sex（Boolean）。
 * 2.insertUserId：打印成功插入数据的信息（模拟数据库插入数据的情况）
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUserByUserId(Integer id) {
        System.out.println("客户端查询了"+id+"的用户");

        // 模拟从数据库中取用户的行为
        Random random = new Random();
        User user = User.builder()
                .userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean()).build();
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("插入数据成功: " + user);
        return 1;
    }
}
