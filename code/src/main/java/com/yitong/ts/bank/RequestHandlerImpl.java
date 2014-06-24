package com.yitong.ts.bank;

import com.yitong.ts.rl.RLClient;
import org.apache.commons.lang.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

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

    @Autowired
    private RLClient rlClient;

    @Override
    public String handle(String message) {
        try {
            SAXReader reader = new SAXReader();
            ByteArrayInputStream is = new ByteArrayInputStream(message.getBytes());
            Document doc = reader.read(is);
            String root = "/WEBSERVICE/BODY";
            Node functionNameNode = doc.selectSingleNode(root + "/FunctionName");
            String functionName = functionNameNode != null ? functionNameNode.getText() : "";
            if (StringUtils.isBlank(functionName))
                throw new Exception("客户端发送的报文未正确设置'FunctionName'");
            //匹配操作
            if (fun_getinvoiceNo.equals(functionName)) {//获取发票号
                return this.getInvoiceNo();
            } else if (fun_usedInvoiceCancel.equals(functionName)) {//已使用发票作废
                Node nInvocieCodeNode = doc.selectSingleNode(root + "/nInvocieCode");
                String nInvocieCode = nInvocieCodeNode != null ? nInvocieCodeNode.getText() : "";
                if (StringUtils.isBlank(nInvocieCode))
                    throw new Exception("[UsedInvoiceCancel]未设置待作废发票号。");
                return this.usedInvoiceCancel(nInvocieCode);
            } else if (fun_freeInvoiceCancel.equals(functionName)) {//未使用发票作废
                Node nInvocieCodeNode = doc.selectSingleNode(root + "/nInvocieCode");
                String nInvocieCode = nInvocieCodeNode != null ? nInvocieCodeNode.getText() : "";
                if (StringUtils.isBlank(nInvocieCode))
                    throw new Exception("[FreeInvoiceCancel]未设置待作废发票号。");
                return this.freeInvoiceCancel(nInvocieCode);
            } else if (fun_chargeCaclute.equals(functionName)) {//收费员收费金额统计
                Node beginDateNode = doc.selectSingleNode(root + "/begDate");
                String beginDate = beginDateNode != null ? beginDateNode.getText() : "";
                Node endDateNode = doc.selectSingleNode(root + "/endDate");
                String endDate = endDateNode != null ? endDateNode.getText() : "";
                return this.chargeCaclute(beginDate, endDate);
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
            return this.errorHandler(e);
        }
    }

    //获取发票号
    private String getInvoiceNo() throws Exception {
        String retMsg = this.rlClient.getInvoiceNo();
        String[] temp = this.validResult(retMsg, 4, "GetInvoiceNo");
        String resultDesc = temp[1];
        String invoiceNo = temp[2]; //当前发票号
        int remainInvoice = Integer.parseInt(temp[3]);//剩余发票数
        return ResponseTemplate.GetInvoiceNo.replace("${ResultAbout}", resultDesc)
                .replace("${CurrInvoiceNo}", invoiceNo).replace("${SurInvoiceNo}", remainInvoice + "");
    }

    //修改密码
    private String changePasswd(String nNewPasswd) throws Exception {
        String retMsg = this.rlClient.changePasswd(nNewPasswd);
        String[] temp = this.validResult(retMsg, 2, "ChangePasswd");
        String resultDesc = temp[1];
        //TODO 修改密码
        return ResponseTemplate.ChangePasswd.replace("${ResultAbout}", resultDesc);
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
    private String usedInvoiceCancel(String nInvoiceCode) throws Exception {
        String retMsg = this.rlClient.usedInvoiceCancel(nInvoiceCode);
        String[] temp = this.validResult(retMsg, 2, "UsedInvoiceCancel");
        String resultDes = temp[1];
        return ResponseTemplate.UsedInvoiceCancel.replace("${ResultAbout}", resultDes);
    }

    //未使用发票作废
    private String freeInvoiceCancel(String nInvoiceCode) throws Exception {
        String retMsg = this.rlClient.freeInvoiceCancel(nInvoiceCode);
        String[] temp = this.validResult(retMsg, 2, "FreeInvoiceCancel");
        String resultDes = temp[1];
        return ResponseTemplate.FreeInvoiceCancel.replace("${ResultAbout}", resultDes);
    }

    //收费员收款金额统计
    private String chargeCaclute(String beginDate, String endDate) throws Exception {
        String retMsg = this.rlClient.chargeCaclute(beginDate, endDate);
        String[] temp = this.validResult(retMsg, 5, "ChargeCaclute");
        String resultDesc = temp[1];
        String chargeMoney = temp[2];
        String eInvoiceNo = temp[3];
        String cInvoiceNo = temp[4];
        return ResponseTemplate.ChargeCaclute.replace("${ResultAbout}", resultDesc)
                .replace("${ChargeMoney}", chargeMoney)
                .replace("${EInvoiceNo}", eInvoiceNo)
                .replace("${CInvoiceNo}", cInvoiceNo);
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

    //校验带out参数webservice返回结果
    private String[] validResult(String[] ret, String method) throws Exception {
        //TODO 校验带out参数webservice返回结果
        return null;
    }

    //错误处理
    private String errorHandler(Exception e) {
        logger.error("客户端请求处理发生异常。", e);
        //生成返回结果(xml)
        Document doc = DocumentFactory.getInstance().createDocument();
        Element body = doc.addElement("BODY");
        Element resultAbout = body.addElement("ResultAbout");
        resultAbout.setText(e.getMessage());
        return doc.asXML();
    }

}
