package com.yitong.ts.rl;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

/**
 * 银行鉴权拦截器
 *
 * @author puxiwu
 */
public class BankSoapHeaderInterceptor extends AbstractSoapInterceptor {

    public BankSoapHeaderInterceptor(String code, String passwd) {
        super(Phase.WRITE);
        addAfter(SoapPreProtocolOutInterceptor.class.getName());
        this.code = code;
        this.passwd = passwd;
    }

    //TODO 确认常量
    private static final String namespaceURL="";
    private static final String rootName="";
    private static final String elCodeName="";
    private static final String elPasswdName="";

    private String code;
    private String passwd;

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        Document doc = DOMUtils.createDocument();
        Element root = doc.createElementNS(namespaceURL,rootName);
        Element elCode = doc.createElement(elCodeName);
        elCode.setTextContent(this.code);
        Element elPasswd = doc.createElement(elPasswdName);
        elPasswd.setTextContent(this.passwd);
        root.appendChild(elCode);
        root.appendChild(elPasswd);
        QName qName = new QName("RequestSOAPHeader");
        Header header = new Header(qName,root);
        message.getHeaders().add(header);
    }
}
