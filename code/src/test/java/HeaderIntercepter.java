import org.apache.cxf.binding.soap.SoapHeader;
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



public class HeaderIntercepter extends AbstractSoapInterceptor {
    public HeaderIntercepter(String name,String password){
        super(Phase.WRITE);
        addAfter(SoapPreProtocolOutInterceptor.class.getName());
        this.name = name;
        this.password=password;
    }



    private String name;
    private String password;

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        QName qname = new QName("RequestSOAPHeader");
        Document doc = DOMUtils.createDocument();

        Element root = doc.createElementNS("http://tempuri.org/", "MySoapHeader");
        Element elName = doc.createElement("Name");
        elName.setTextContent(name);
        Element elPassword=doc.createElement("Password");
        elPassword.setTextContent(password);

        root.appendChild(elName);
        root.appendChild(elPassword);


        Header head = new Header(qname, root);
        message.getHeaders().add(head);
    }
}
