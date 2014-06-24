package com.yitong.ts.bank;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 响应报文模板
 *
 * @author puxiwu
 */
@Component
public class ResponseTemplate {
    private Logger logger = LoggerFactory.getLogger(ResponseTemplate.class);
    @Value("${bank.socket.reponse.template.dir}")
    private String templatesDir = "WEB-INF/templates";
    public static String ChangePasswd;
    public static String ChargeCaclute;
    public static String FreeInvoiceCancel;
    public static String GetInvoiceNo;
    public static String UsedInvoiceCancel;

    @PostConstruct
    public void loadTemplates() {
        try {
            ChangePasswd = this.loadXml(this.templatesDir + "/ChangePasswd.xml");
            ChargeCaclute = this.loadXml(this.templatesDir + "/ChargeCaclute.xml");
            FreeInvoiceCancel = this.loadXml(this.templatesDir + "/FreeInvoiceCancel.xml");
            GetInvoiceNo = this.loadXml(this.templatesDir + "/GetInvoiceNo.xml");
            UsedInvoiceCancel = this.loadXml(this.templatesDir + "/UsedInvoiceCancel.xml");
            System.out.println(ChargeCaclute);

        } catch (Exception e) {
            logger.error("读取响应报文模板发生异常，程序强制退出。", e);
            System.exit(0);
        }
    }

    private String loadXml(String file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(new File(file));

        return compressXmlString(doc.asXML());
    }

    public static String compressXmlString(String xml) {
        Pattern p = Pattern.compile(">\\s*<");
        Matcher m = p.matcher(xml.replace("\n", ""));
        return m.replaceAll("><");
    }


}
