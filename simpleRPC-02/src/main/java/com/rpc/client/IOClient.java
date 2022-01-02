package com.rpc.client;


import com.rpc.common.RPCRequest;
import com.rpc.common.RPCResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * @author zwy
 *
 * IO Client：底层的通信
 * 通过Socket和输出流把 RPCRequest 传给服务器端，接收到服务器端传来的 RPCResponse，返回这个 RPCResponse
 *
 * 这里负责底层与服务器端的通信，发送的Request，接受的是Response对象
 * 客户端发起一次请求调用，Socket建立连接，发起请求Request，得到相应Response
 * 这里的request是封装好的（上层进行封装），不同的service需要进行不同的封装，客户端只知道Service接口，
 * 需要一层动态代理根据反射封装不同的Service
 */
public class IOClient {

    public static RPCResponse sendRequest(String host, int port, RPCRequest request) throws IOException, ClassNotFoundException {

        // 老样子，创建Socket对象，定义host和port
        Socket socket = new Socket(host, port);

        // 定义输入输出流对象
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        System.out.println("request: " + request);

        // 输出流写入request对对象，刷新输出流
        objectOutputStream.writeObject(request);
        objectOutputStream.flush();

        // 通过输入流的readObject方法，得到服务器端传来的RPCResponse，并返回RPCResponse对象
        RPCResponse response = (RPCResponse) objectInputStream.readObject();

        return response;

    }
}