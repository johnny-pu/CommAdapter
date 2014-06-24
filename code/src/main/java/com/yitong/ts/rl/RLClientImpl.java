package com.yitong.ts.rl;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


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


    /**
     * 调用服务端方法
     *
     * @param method   方法名
     * @param code     鉴权码
     * @param password 鉴权密码
     * @param params   参数列表
     * @return 执行结果
     * @throws Exception
     */
    public Object[] invoke(String method, String code, String password, Object... params) throws Exception {
        JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
        Client client = clientFactory.createClient(this.wsdlUrl);
        // 添加soap header
        client.getOutInterceptors().add(new BankSoapHeaderInterceptor(code, password));
        // 添加soap消息日志打印
        client.getOutInterceptors().add(new org.apache.cxf.interceptor.LoggingOutInterceptor());
        return client.invoke(method, params);
    }


    @Override
    public String getInvoiceNo(String code, String password) throws Exception {
        return String.valueOf(this.invoke("GetInvoiceNo", code, password)[0]);
    }

    @Override
    public String changePasswd(String code, String password, String nNewPasswd) throws Exception {
        return String.valueOf(this.invoke("ChangePasswd", code, password, nNewPasswd)[0]);
    }

    @Override
    public Object[] getCustInfo(String code, String password, String nCardId) throws Exception {
        return this.invoke("GetCustInfo", code, password, nCardId);
    }

    @Override
    public Object[] custChange(String code, String password, String nCustId, String nYear,
                               String nInvoiceCode, String nInvoiceName, double nMoney,
                               String nMethod, String nBankSerialNo)
            throws Exception {
        BigDecimal decimal = new BigDecimal(Double.toString(nMoney));
        return this.invoke("CustCharge", code, password, nCustId, nYear, nInvoiceCode, nInvoiceName, decimal, nMethod, nBankSerialNo);
    }

    @Override
    public String usedInvoiceCancel(String code, String password, String nInvoiceCode) throws Exception {
        return String.valueOf(this.invoke("UsedInvoiceCancel", code, password, nInvoiceCode)[0]);
    }

    @Override
    public String freeInvoiceCancel(String code, String password, String nInvoiceCode) throws Exception {
        return String.valueOf(this.invoke("FreeInvoiceCancel", code, password, nInvoiceCode)[0]);
    }

    @Override
    public String chargeCaclute(String code, String password, String beginDate, String endDate) throws Exception {
        return String.valueOf(this.invoke("ChargeCaclute", code, password, beginDate, endDate)[0]);
    }

    @Override
    public Object[] chargeDetailCaclute(String code, String password, String beginDate, String endDate) throws Exception {
        return this.invoke("ChargeCaclute", code, password, beginDate, endDate);
    }
}
