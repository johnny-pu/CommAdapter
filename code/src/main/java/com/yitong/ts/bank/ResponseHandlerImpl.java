package com.yitong.ts.bank;

import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResponseHandlerImpl implements ResponseHandler {

    @Override
    public String responseSuccess(String tpl, Hashtable<String, String> values) {
        String xml = new String(tpl);
        for(String key:values.keySet()){
            xml = xml.replace("${"+key+"}",values.get(key));
        }
        return this.compressXmlString(xml);
    }

    @Override
    public String responseError(String message) {
        String xml = ERROR_TPL.replace("${ResultAbout}", message);
        return this.compressXmlString(xml);
    }

    private String compressXmlString(String xml) {
        Pattern p = Pattern.compile(">\\s*<");
        Matcher m = p.matcher(xml.replace("\n", ""));
        return m.replaceAll("><");
    }
}
