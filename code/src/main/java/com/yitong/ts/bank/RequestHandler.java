package com.yitong.ts.bank;


/**
 * 客户端请求处理接口
 *
 * @author puxiwu
 */
public interface RequestHandler {

    /**
     * 处理请求
     *
     * @param message 客户端请求报文
     * @return 响应报文
     */
    public String handle(String message);
}
