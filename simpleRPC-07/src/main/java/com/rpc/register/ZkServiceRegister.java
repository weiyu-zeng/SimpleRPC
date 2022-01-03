package com.rpc.register;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author weiyu_zeng
 *
 * Curator：是Zookeeper开源的客户端框架，封装了很多API，使用起来非常的方便
 * CuratorFramework：连接zookeeper服务的框架，客户端创建使用静态工厂方式CuratorFrameworkFactory进行创建
 * tickTime：zk的心跳间隔(heartbeat interval）,也是session timeout基本单位.单位为毫秒.
 * minSessionTimeout:最小超时时间,zk设置的默认值为2*tickTime.
 * maxSessionTimeout:最大超时时间,zk设置的默认值为20*tickTime.
 * retryPolicy()重连策略：
 * Curator 四种重连策略：
 *      1.RetryUntilElapsed(int maxElapsedTimeMs, int sleepMsBetweenRetries)
 *      以sleepMsBetweenRetries的间隔重连,直到超过maxElapsedTimeMs的时间设置
 *
 *      2.RetryNTimes(int n, int sleepMsBetweenRetries)
 *      指定重连次数
 *
 *      3.RetryOneTime(int sleepMsBetweenRetry)
 *      重连一次,简单粗暴
 *
 *      4.ExponentialBackoffRetry
 *      ExponentialBackoffRetry(int baseSleepTimeMs, int maxRetries)
 *      ExponentialBackoffRetry(int baseSleepTimeMs, int maxRetries, int maxSleepMs)
 *      时间间隔 = baseSleepTimeMs * Math.max(1, random.nextInt(1 << (retryCount + 1)))
 *
 * namespace(): 为了避免各个应用的zk patch冲突, Curator Framework内部会给每一个Curator Framework实例分配一个namespace(可选).
 *              这样你在create ZNode的时候都会自动加上这个namespace作为这个node path的root.
 * CuratorFramework.create()：开始创建操作，可以调用额外的方法(比如方式mode 或者后台执行background) 并在最后调用forPath()
 *                            指定要操作的ZNode
 * CuratorFramework.checkExists(): 开始检查ZNode是否存在的操作. 可以调用额外的方法(监控或者后台处理)并在最后调用forPath()
 *                                指定要操作的ZNode
 * CuratorFramework.start() / close()：启动和关闭客户端
 * CuratorFramework(client).create().withMode(CreateMode.EPHEMERAL)：这将使用给定的数据创建临时结点 EPHEMERAL ZNode
 * CuratorFramework.getChildren()：开始获得ZNode的子节点列表。 以调用额外的方法(监控、后台处理或者获取状态watch,
 *                                 background or get stat)并在最后调用forPath()指定要操作的ZNode
 *
 * InetSocketAddress：该类实现了可序列化接口，直接继承自java.net.SocketAddress类。实现 IP 套接字地址（IP 地址 + 端口号）。
 *                    它还可以是一个对（主机名 + 端口号），在此情况下，将尝试解析主机名。如果解析失败，则该地址将被视为未解析
 *                    地址，但是其在某些情形下仍然可以使用，比如通过代理连接。
 *                    构造方法：InetSocketAddress(InetAddress addr,  int port)  根据 IP 地址和端口号创建套接字地址。
 *                             InetSocketAddress(String hostname, int port) 根据主机名（IP地址指代）和端口号创建套接字地址。
 * InetSocketAddress.getHostName()：获取 hostname。即地址的主机名部分。
 * InetSocketAddress.getPort()  获取端口号。
 */
public class ZkServiceRegister implements ServiceRegister {

    // curator 提供的zookeeper客户端
    private CuratorFramework client;

    // zookeeper根路径结点
    private static final String ROOT_PATH = "MyRPC";

    // 构造方法
    // 这里负责zookeeper客户端的初始化，并与zookeeper服务端建立连接。
    // 初始化包括指定重连策略，指定连接zookeeper的端口，指定超时时间，指定命名空间
    // 初始化完成之后start()开启zookeeper客户端。
    public ZkServiceRegister() {

        // 重连策略：指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);

        // zookeeper的地址固定，不管是服务提供者还是消费者，都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                                             .sessionTimeoutMs(40000)
                                             .retryPolicy(policy)
                                             .namespace(ROOT_PATH)
                                             .build();
        this.client.start();
        System.out.println("zookeeper 连接成功");
    }

    // 注册：传入服务方法名(String)，传入主机名和端口号的套接字地址(InetSocketAddress)
    @Override
    public void register(String serviceName, InetSocketAddress serverAddress) {
        try {
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            Stat stat = client.checkExists().forPath("/" + serviceName);
            if (stat == null) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath("/" + serviceName);
            }
            // 路径地址，一个/代表一个节点
            String path = "/" + serviceName + "/" + getServiceAddress(serverAddress);
            // 临时节点，服务器下线就删除节点
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            System.out.println("此服务已存在");
        }
    }

    // 根据服务名返回地址
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            List<String> strings = client.getChildren().forPath("/" + serviceName);
            // 这里默认用的第一个，后面加负载均衡
            String string = strings.get(0);
            return parseAddress(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 地址 -> XXX.XXX.XXX.XXX:port 字符串
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }

    // 字符串解析为地址：按照":"切分开，前半是host(String)，后半是port(int)
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
