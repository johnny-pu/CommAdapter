package com.yitong.ts.bank;

import com.yitong.ts.rl.RLClient;
import org.apache.commons.lang.StringUtils;
import org.dom4j.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import java.io.ByteArrayInputStream;
import java.util.Hashtable;

/**
 * 客户端请求处理接口实现
 *
 * @author puxiwu
 */
@Component
public class RequestHandlerImpl implements RequestHandler {
    private Logger logger = LoggerFactory.getLogger(RequestHandlerImpl.class);

    private final static String separator = "\\|";//分隔符
    private final static String fun_getinvoiceNo = "GetInvoiceNo"; //获取发票号
    private final static String fun_usedInvoiceCancel = "UsedInvoiceCancel";//已使用发票作废
    private final static String fun_freeInvoiceCancel = "FreeInvoiceCancel";//未使用发票作废
    private final static String fun_chargeCaclute = "ChargeCaclute";//收费员收款金额统计
    private final static String fun_custCharge = "CustCharge"; //客户交费
    private final static String fun_changePasswd = "ChangePasswd"; //修改密码

    @Value("${request.msg.codeNode}")
    private String codeNodePath;
    @Value("${request.msg.passwdNode}")
    private String passwdNodePath;

    @Autowired
    private RLClient rlClient;
    @Autowired
    private ResponseHandler responseHandler;

    @Override
    public String handle(String message) {
        try {
            SAXReader reader = new SAXReader();
            ByteArrayInputStream is = new ByteArrayInputStream(message.getBytes());
            Document doc = reader.read(is);
            String root = "/WEBSERVICE/BODY";
            Node codeNode = doc.selectSingleNode(this.codeNodePath);
            String code = codeNode != null ? codeNode.getText() : "";
            Node passwdNode = doc.selectSingleNode(this.passwdNodePath);
            String passwd = passwdNode != null ? passwdNode.getText() : "";
            Node functionNameNode = doc.selectSingleNode(root + "/FunctionName");
            String functionName = functionNameNode != null ? functionNameNode.getText() : "";
            //匹配操作
            if (fun_getinvoiceNo.equals(functionName)) {//获取发票号
                return this.getInvoiceNo(code, passwd);
            } else if (fun_changePasswd.equals(functionName)) {
                Node newPaswdNode = doc.selectSingleNode(root + "/nNwePaswd");
                String newPasswd = newPaswdNode != null ? newPaswdNode.getText() : "";
                return this.changePasswd(code, passwd, newPasswd);
            } else if (fun_usedInvoiceCancel.equals(functionName)) {//已使用发票作废
                Node nInvocieCodeNode = doc.selectSingleNode(root + "/nInvocieCode");
                String nInvocieCode = nInvocieCodeNode != null ? nInvocieCodeNode.getText() : "";
                if (StringUtils.isBlank(nInvocieCode))
                    throw new Exception("[UsedInvoiceCancel]未设置待作废发票号。");
                return this.usedInvoiceCancel(code, passwd, nInvocieCode);
            } else if (fun_freeInvoiceCancel.equals(functionName)) {//未使用发票作废
                Node nInvocieCodeNode = doc.selectSingleNode(root + "/nInvocieCode");
                String nInvocieCode = nInvocieCodeNode != null ? nInvocieCodeNode.getText() : "";
                if (StringUtils.isBlank(nInvocieCode))
                    throw new Exception("[FreeInvoiceCancel]未设置待作废发票号。");
                return this.freeInvoiceCancel(code, passwd, nInvocieCode);
            } else if (fun_chargeCaclute.equals(functionName)) {//收费员收费金额统计
                Node beginDateNode = doc.selectSingleNode(root + "/begDate");
                String beginDate = beginDateNode != null ? beginDateNode.getText() : "";
                Node endDateNode = doc.selectSingleNode(root + "/endDate");
                String endDate = endDateNode != null ? endDateNode.getText() : "";
                return this.chargeCaclute(code, passwd, beginDate, endDate);
            } else if (fun_custCharge.equals(functionName)) {//客户交费
                Node nCustIdNode = doc.selectSingleNode(root + "/nCustID");
                String nCustId = nCustIdNode != null ? nCustIdNode.getText() : "";
                Node nYearNode = doc.selectSingleNode(root + "/nYear");
                String nYear = nYearNode != null ? nYearNode.getText() : "";
                Node nInvoiceCodeNode = doc.selectSingleNode(root + "/nInvoiceCode");
                String nInvoiceCode = nInvoiceCodeNode != null ? nInvoiceCodeNode.getText() : "";
                Node nInvoiceNameNode = doc.selectSingleNode(root + "/nInvoiceName");
                String nInvoiceName = nInvoiceNameNode != null ? nInvoiceNameNode.getText() : "";
                Node nMoneyNode = doc.selectSingleNode(root + "/nMoney");
                String nMoney = nMoneyNode != null ? nMoneyNode.getText() : "";
                Node nMethodNode = doc.selectSingleNode(root + "/nMethod");
                String nMethod = nMethodNode != null ? nMethodNode.getText() : "";
                Node nBankSerialNoNode = doc.selectSingleNode(root + "/nBankSerialNo");
                String nBankSerialNo = nBankSerialNoNode != null ? nBankSerialNoNode.getText() : "";
                return this.custCharge(nCustId, nYear, nInvoiceCode, nInvoiceName, Double.parseDouble(nYear), nMethod, nBankSerialNo);
            } else
                throw new Exception("无法识别的操作:" + functionName);
        } catch (Exception e) {
            logger.error("请求报文处理发生异常。", e);
            return this.responseHandler.responseError(e.getMessage());
        }
    }

    //获取发票号
    private String getInvoiceNo(String code, String password) throws Exception {
        String retMsg = this.rlClient.getInvoiceNo(code, password);
        String[] temp = retMsg.split(separator);
        if (!"1".equals(temp[0]))
            return this.responseHandler.responseError(temp[1]);
        Hashtable<String, String> values = new Hashtable<String, String>();
        values.put("ResultAbout", temp[1]);
        values.put("CurrInvoiceNo", temp[2]);
        values.put("SurInvoiceNo", temp[3]);
        return this.responseHandler.responseSuccess(ResponseHandler.GetInvoiceNo, values);
    }

    //修改密码
    private String changePasswd(String code, String passwd, String nNewPasswd) throws Exception {
        String retMsg = this.rlClient.changePasswd(code, passwd, nNewPasswd);
        String[] temp = retMsg.split(separator);
        if (!"1".equals(temp[0]))
            return this.responseHandler.responseError(temp[1]);
        Hashtable<String, String> values = new Hashtable<String, String>();
        values.put("ResultAbout", temp[1]);
        values.put("Content", "");
        return this.responseHandler.responseSuccess(ResponseHandler.SUCCESS_TPL, values);
    }

    //查询客户信息及欠费
    private String getCustInfo(String nCardId) {

        //TODO 查询客户信息及欠费
        return null;
    }

    //客户缴费
    private String custCharge(String nCustId, String nYear, String nInvoiceCode,
                              String nInvoiceName, double nMoney, String nMethod,
                              String nBankSerialNo) throws Exception {
//        String[] ret = this.rlClient.custChange(nCustId, nYear, nInvoiceCode, nInvoiceName, nMoney, nMethod, nBankSerialNo);
//        String retContent[] = this.validResult(ret, "CustCharge");
//        SAXReader reader = new SAXReader();
//        ByteArrayInputStream is = new ByteArrayInputStream(retContent.getBytes());
//        Document doc = reader.read(is);
//        Node root = doc.selectSingleNode("/NewDataSet");
//        String rspMsg = root.asXML().replace("<NewDataSer>", "").replace("</NewDataSet>", "");
        //TODO 客户交费
        return null;
    }

    //已使用发票作废
    private String usedInvoiceCancel(String code, String passwd, String nInvoiceCode) throws Exception {
        String retMsg = this.rlClient.usedInvoiceCancel(code, passwd, nInvoiceCode);
        String[] temp = retMsg.split(separator);
        if (!"1".equals(temp[0]))
            return this.responseHandler.responseError(temp[1]);
        String resultDes = temp[1];
        Hashtable<String, String> values = new Hashtable<String, String>();
        values.put("ResultAbout", temp[1]);
        values.put("Content", "");
        return this.responseHandler.responseSuccess(ResponseHandler.SUCCESS_TPL, values);
    }

    //未使用发票作废
    private String freeInvoiceCancel(String code, String passwd, String nInvoiceCode) throws Exception {
        String retMsg = this.rlClient.freeInvoiceCancel(code, passwd, nInvoiceCode);
        String[] temp = retMsg.split(separator);
        if (!"1".equals(temp[0]))
            return this.responseHandler.responseError(temp[1]);
        String resultDes = temp[1];
        Hashtable<String, String> values = new Hashtable<String, String>();
        values.put("ResultAbout", temp[1]);
        values.put("Content", "");
        return this.responseHandler.responseSuccess(ResponseHandler.SUCCESS_TPL, values);
    }

    //收费员收款金额统计
    private String chargeCaclute(String code, String passwd, String beginDate, String endDate) throws Exception {
        String retMsg = this.rlClient.chargeCaclute(code, passwd, beginDate, endDate);
        String[] temp = retMsg.split(separator);
        if (!"1".equals(temp[0]))
            return this.responseHandler.responseError(temp[1]);
        Hashtable<String, String> values = new Hashtable<String, String>();
        values.put("ResultAbout", temp[1]);
        values.put("ChargeMoney", temp[2]);
        values.put("EInvoiceNo", temp[3]);
        values.put("CInvoiceNo", temp[4]);
        return this.responseHandler.responseSuccess(ResponseHandler.ChargeCaclute, values);
    }

    //收费员收款明细统计
    private String chargeDetailCaclute(String beginDate, String endDate) throws Exception {

        //TODO 收费员收款明细统计
        return null;
    }

    //结果校验
    private String[] validResult(String regMsg, int length, String method) throws Exception {
        method = StringUtils.isBlank(method) ? "" : "[" + method + "]";
        if (StringUtils.isBlank(regMsg))
            throw new Exception(String.format("%s调用WebService发成错误，返回空值。", method));
        String[] temp = regMsg.split(separator);
        if (temp.length != length)
            throw new Exception(String.format("%s成功调用WebService,但返回无法识别的报文:{%s}。", method, regMsg));
        String resultCode = temp[0];
        String resultDesc = temp[1];
        if ("0".equals(resultCode))
            throw new Exception(String.format("%s成功调用WebService但返回失败结果。%s",
                    method, StringUtils.isBlank(resultDesc) ? "" : ("服务端反馈信息: " + resultDesc)));
        if ("2".equals(resultCode))
            throw new Exception(String.format("%s成功调用WebService但返回异常结果。%s",
                    method, StringUtils.isBlank(resultDesc) ? "" : ("服务端反馈信息: " + resultDesc)));
        return temp;
    }

}
