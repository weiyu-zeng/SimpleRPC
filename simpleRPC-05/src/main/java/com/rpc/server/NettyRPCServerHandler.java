package com.rpc.server;

import com.rpc.common.RPCRequest;
import com.rpc.common.RPCResponse;
import com.rpc.service.ServiceProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zwy
 *
 * ChannelHandlerContext.writeAndFlush：方法会将数据写到ChannelPipeline中当前ChannelHandler的下一个ChannelHandler开始处理。
 */
@AllArgsConstructor
public class NettyRPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    private ServiceProvider serviceProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequest msg) throws Exception {
        // System.out.println(msg);
        RPCResponse response = getResponse(msg);
        ctx.writeAndFlush(response);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    // 这里和WorkThread里的getResponse差不多
    RPCResponse getResponse(RPCRequest request) {
        // 得到服务名
        String interfaceName = request.getInterfaceName();
        // 得到服务器相应类
        Object service = serviceProvider.getService(interfaceName);
        // 反射调用方法
        Method method = null;
        try {
            method = service.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
            Object invoke = method.invoke(service, request.getParams());
            return RPCResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RPCResponse.fail();
        }
    }
}
