package com.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author zwy
 *
 * 定义简单User信息,要使用lombok，IDEA必须也安装lombok插件，否则用不了。
 *
 * '@Builder' 创建者模式又叫建造者模式。简单来说，就是一步步创建一个对象，它对用户屏蔽了里面构建的细节，但却可以精细地控制对象的构造过程。
 * '@NoArgsConstructor' 生成一个无参构造方法
 * '@AllArgsConstructor' 使用后添加一个构造函数，该构造函数含有所有已声明字段属性参数
 * '@Data' 相当于 @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode这5个注解的合集。
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    // 客户端和服务端共有的
    private Integer id;
    private String userName;
    private Boolean sex;
}
