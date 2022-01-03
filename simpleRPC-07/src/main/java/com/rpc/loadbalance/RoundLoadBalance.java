package com.rpc.loadbalance;

import java.util.List;

/**
 * 轮询负载均衡
 */
public class RoundLoadBalance implements LoadBalance{
    private int choose = -1;

    @Override
    public String balance(List<String> addressList) {
        choose++;
        choose = choose % addressList.size();  // 索引
        System.out.println("负载均衡选择了" + choose + "服务器");
        return addressList.get(choose);  //
    }
}
