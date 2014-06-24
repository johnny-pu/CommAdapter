package com.yitong.ts.rl;

import org.apache.cxf.endpoint.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Webservice客户端<br/>
 * create:2014-06-18
 *
 * @author puxiwu
 */
@Component
public class RLClientImpl implements RLClient {
    private Logger logger = LoggerFactory.getLogger(RLClientImpl.class);

    @Value("${rl.ws.url}")
    private String wsdlUrl;
    @Value("${rl.ws.qname}")
    private String qName;
    @Value("${rl.ws.header.code.attr}")
    private String codeAttributeName;
    @Value("${rl.ws.header.password.attr}")
    private String passwordAttributeName;
    @Value("${rl.ws.header.code.value}")
    private String code;       //鉴权Code
    @Value("${rl.ws.header.password.value}")
    private String password;   //鉴权密码

    private Client client;


    public void setCode(String code) {
        this.code = code;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 根据指定的url创建客户端
     *
     * @param wsdlUrl wsdl地址
     * @return 客户端
     */
    public void init() {
        logger.info("初始化创建webserver客户端(url:{})", this.wsdlUrl);
        RLClientImpl rlClient = new RLClientImpl();
//        DynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
//        Client client = clientFactory.createClient(wsdlUrl);
//        Hashtable<String, String> attributes = new Hashtable<String, String>();
//        attributes.put(this.codeAttributeName, this.code);
//        attributes.put(this.passwordAttributeName, this.password);
//        client.getOutInterceptors().add(new BankSoapHeader(this.qName, attributes));
//        rlClient.client = client;
    }

    /**
     * 调用服务端方法
     *
     * @param method 方法名
     * @param params 参数列表
     * @return 执行结果
     * @throws Exception
     */
    public Object[] invoke(String method, Object... params) throws Exception {
        return this.client.invoke(method, params);
    }


    @Override
    public String getInvoiceNo() throws Exception {
        return null;
    }

    @Override
    public String changePasswd(String nNewPasswd) throws Exception {
        return null;
    }

    @Override
    public String[] getCustInfo(String nCardId) throws Exception {
        return null;
    }

    @Override
    public String[] custChange(String nCustId, String nYear, String nInvoiceCode,
                             String nInvoiceName, double nMoney, String nMethod,
                             String nBankSerialNo)
            throws Exception {
        return null;
    }

    @Override
    public String usedInvoiceCancel(String nInvoiceCode) throws Exception {
        return null;
    }

    @Override
    public String freeInvoiceCancel(String nInvoiceCode) throws Exception {
        return null;
    }

    @Override
    public String chargeCaclute(String beginDate, String endDate) throws Exception {
        return null;
    }

    @Override
    public Object chargeDetailCaclute(String beginDate, String endDate) throws Exception {
        return null;
    }
}
