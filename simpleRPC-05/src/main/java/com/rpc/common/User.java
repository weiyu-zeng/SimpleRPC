package com.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author weiyu_zeng
 *
 * 定义简单User信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    // 客户端和服务端共有的，模拟RPC中传输的信息
    private Integer id;
    private String userName;
    private Boolean sex;
}
