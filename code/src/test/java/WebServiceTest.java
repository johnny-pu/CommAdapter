import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.junit.Test;

import java.math.BigDecimal;

public class WebServiceTest {

    @Test
    public void TestSoapHeader() throws Exception {
        JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
        Client client = clientFactory.createClient("http://localhost:13971/WebService.asmx?wsdl");

        // 添加soap header
        client.getOutInterceptors().add(new HeaderIntercepter("puxiwu", "puxiwu"));
        // 添加soap消息日志打印
        client.getOutInterceptors().add(new org.apache.cxf.interceptor.LoggingOutInterceptor());


        Object[] ret = client.invoke("CheckHeader");
        for(Object obj:ret)
            System.out.println(obj);
    }

    @Test
    public void TestOutParam() throws Exception {
        JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
        Client client = clientFactory.createClient("http://localhost:13971/WebService.asmx?wsdl");

        // 添加soap header
        client.getOutInterceptors().add(new HeaderIntercepter("puxiwu", "puxiwu"));
        // 添加soap消息日志打印
        client.getOutInterceptors().add(new org.apache.cxf.interceptor.LoggingOutInterceptor());


        Object[] ret = client.invoke("TestOutParam","this is in param");
        for(Object obj:ret)
            System.out.println(obj);
    }

    @Test
    public void testDouble() throws Exception{
        JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
        Client client = clientFactory.createClient("http://localhost:13971/WebService.asmx?wsdl");
        BigDecimal value = new BigDecimal(Double.toString(0.12));
        System.out.println(value);
        Object[] ret = client.invoke("TestDouble",value);
        for(Object obj:ret)
            System.out.println(obj);
    }
}
