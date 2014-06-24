package com.yitong.ts.bank;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Socket服务端
 *
 * @author puxiwu
 */
@Component
public class SocketServer extends IoHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(SocketServer.class);
    @Value("${bank.socket.port}")
    private Integer port = 9989; //socket接收端端口
    @Value("${bank.socket.encoding}")
    private String encoding = "UTF-8";//socket报文编码
    @Value("${bank.socket.idle}")
    private Integer idle = 15; //socket连接最大闲置时长(秒)

    @Autowired
    private RequestHandler requestHandler;

    /**
     * 初始化，启动Socket服务
     */
    @PostConstruct
    public void init() {
        try {
            IoAcceptor acceptor = new NioSocketAcceptor();
            //(设置编码器会引起非mina客户端通信故障)acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName(this.encoding))));
            acceptor.setHandler(this);
            acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, this.idle);
            acceptor.bind(new InetSocketAddress(this.port));
            logger.info("Socket服务端正在监听端口:" + this.port);
        } catch (Exception e) {
            logger.error("Socket服务端启动异常，服务强制退出。", e);
            System.exit(0);
        }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
        logger.info("与客户端({})创建连接。", remoteAddress.getAddress().getHostAddress());
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
        logger.error("与客户端(" + remoteAddress.getAddress().getHostAddress() + ")会话发生异常。", cause);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
        logger.info("正在关闭闲置的客户端连接:{}。", remoteAddress.getAddress().getHostAddress());
        session.close(true);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
        logger.info("已关闭客户端连接:{}。", remoteAddress.getAddress().getHostAddress());
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (message == null) return;
        IoBuffer buffer = (IoBuffer) message;
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        String resMsg = new String(bytes, Charset.forName(this.encoding));
        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
        String address = remoteAddress.getAddress().getHostAddress();
        logger.info("接受到客户端({})发送的数据。", address);
        String responseMsg = this.requestHandler.handle((String) message);
        logger.info("响应客户端({})请求，{}", address, responseMsg);
        session.write(responseMsg);
    }
}
