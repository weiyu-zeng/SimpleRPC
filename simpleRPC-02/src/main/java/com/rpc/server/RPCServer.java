package com.rpc.server;


import com.rpc.common.RPCRequest;
import com.rpc.common.RPCResponse;
import com.rpc.service.UserServiceImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * @author zwy
 *
 * RPC server：接受/解析request，封装，发送response
 *
 * getClass方法：返回Object的运行时类
 * Class.getMethod(String name, Class<?>... parameterTypes)：返回Method对象，方法的作用是获得对象所声明的公开方法
 *                 该方法的第一个参数name是要获得方法的名字，第二个参数parameterTypes是按声明顺序标识该方法形参类型。
 *  java.lang.reflect.Method.invoke(Object receiver, Object... args)：返回Object对象，方法来反射调用一个方法，
 *                  当然一般只用于正常情况下无法直接访问的方法（比如：private 的方法，或者无法或者该类的对象）。
 *                  方法第一个参数是方法属于的对象（如果是静态方法，则可以直接传 null），第二个可变参数是该方法的参数
 */
public class RPCServer {

    public static void main(String[] args) throws IOException {

        // 初始化（客户端Client）需要的服务:UserServiceImpl
        UserServiceImpl userService = new UserServiceImpl();

        // 创建ServerSocket对象，端口号要和Client一致
        ServerSocket serverSocket = new ServerSocket(8899);
        System.out.println("服务器启动！");

        // BIO的方式监听Socket，监听到之后返回Socket对象
        while (true) {
            Socket socket = serverSocket.accept();

            // 监听到连接之后，开启一个线程来处理
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // socket对象的获取输入输出流作为targat，初始化输入输出流
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                        // 读取客户端传过来的request
                        RPCRequest request = (RPCRequest) ois.readObject();

                        // 反射调用方法
                        Method method = userService.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
                        Object invoke = method.invoke(userService, request.getParams());

                        // 把得到的invoke对象写入response的success方法中，写入输出流（传给客户端），刷新输出流
                        oos.writeObject(RPCResponse.success(invoke));
                        oos.flush();
                    } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
