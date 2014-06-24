package com.yitong.ts.rl;

/**
 * 热力WebService客户端接口
 *
 * @author puxiwu
 */
public interface RLClient {
    /**
     * 获取发票号
     *
     * @return String
     * @throws Exception
     */
    public String getInvoiceNo() throws Exception;

    /**
     * 修改鉴权密码
     *
     * @param nNewPasswd 新密码
     * @return String
     * @throws Exception
     */
    public String changePasswd(String nNewPasswd) throws Exception;


    /**
     * 获取客户信息及欠费信息
     *
     * @param nCardId 客户卡号
     * @return Object
     * @throws Exception
     */
    public String[] getCustInfo(String nCardId) throws Exception;


    /**
     * 客户缴费
     *
     * @param nCustId       客户卡号
     * @param nYear         缴费年度
     * @param nInvoiceCode  当前发票号
     * @param nInvoiceName  发票上的客户名称
     * @param nMoney        缴费金额
     * @param nMethod       缴费方式(20-现金，22-刷卡)
     * @param nBankSerialNo 银行交易流水号
     * @return Object
     * @throws Exception
     */
    public String[] custChange(String nCustId, String nYear, String nInvoiceCode,
                               String nInvoiceName, double nMoney, String nMethod,
                               String nBankSerialNo)
            throws Exception;

    /**
     * 已使用发票作废
     *
     * @param nInvoiceCode 发票号
     * @return String
     * @throws Exception
     */
    public String usedInvoiceCancel(String nInvoiceCode) throws Exception;

    /**
     * 未使用发票作废
     *
     * @param nInvoiceCode 发票号
     * @return String
     * @throws Exception
     */
    public String freeInvoiceCancel(String nInvoiceCode) throws Exception;

    /**
     * 收费员收款金额统计
     *
     * @param beginDate 统计开始日期(YYYY-MM-DD 00:00:00)
     * @param endDate   统计结束日期(YYYY-MM-DD +1 00:00:00)
     * @return String
     * @throws Exception
     */
    public String chargeCaclute(String beginDate, String endDate) throws Exception;

    /**
     * 收费员收款明细统计
     *
     * @param beginDate 统计开始日期(YYYY-MM-DD 00:00:00)
     * @param endDate   统计结束日期(YYYY-MM-DD +1 00:00:00)
     * @return Object
     * @throws Exception
     */
    public Object chargeDetailCaclute(String beginDate, String endDate) throws Exception;
}
