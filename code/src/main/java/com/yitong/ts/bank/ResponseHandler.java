package com.yitong.ts.bank;

import java.util.Hashtable;

/**
 * 响应处理
 *
 * @author
 */
public interface ResponseHandler {

    public static String ERROR_TPL = "";
    public static String GetInvoiceNo = "";
    public static String SUCCESS_TPL = "";
    public static String ChargeCaclute="";

    public String responseSuccess(String tpl, Hashtable<String, String> values);


    public String responseError(String message);
}
