package com.yitong.app.communicate.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.yitong.app.model.ResultEntry;
import com.yitong.app.servlet.Log4jInit;
import com.yitong.app.wrap.XmlUtil;
import com.yitong.commons.model.Consts;
import com.yitong.commons.util.StringUtil;
import com.yitong.commons.model.Properties;

/**
 * 上海银行手机银行开发通信Client实现
 * 
 * @author iven
 * 
 */
public class HttpMsgClient implements MbankMsgClient {

	private Logger logger = Logger.getLogger(this.getClass());

	private Pattern pattern = Pattern.compile((new StringBuilder("</ *"))
			.append(" *>").toString());

	private int connTimeout = 6000;
	private int readTimeout = 60000;
	private String wapServerURL = "-1";
	private String wapip;
	private int port;
	private String SocialInsIp;
	private int SocialInsport;
	
	/**
	 * HttpClient 初始化方法
	 */
	public void init() {
		// 初始化网银url地址		
		String urlkey = Consts.WAP_URL_KEY.replace("${weblogic.Name}",System.getProperty("weblogic.Name", ""));
		
		System.out.println("Consts.WAP_URL_KEY:"+Consts.WAP_URL_KEY);
		System.out.println("服务名称 weblogic.Name:" + System.getProperty("weblogic.Name", ""));
		System.out.println("对应交易url的键值 urlkey:" + urlkey);
		
//		connTimeout = Integer.parseInt(Log4jInit.getCommString(Consts.HttpClient_connTimeout));
//		readTimeout = Integer.parseInt(Log4jInit.getCommString(Consts.HttpClient_readTimeout));
		 
		// 判断是否取到交易url
		if (null != Log4jInit.getCommString(urlkey)) {
			// wap 接口 url 解析部分
			wapServerURL = Log4jInit.getCommString(urlkey);
			
			System.out.println("交易URL 静态保存值 wap url:" + wapServerURL);
			
			String tempstr = wapServerURL.substring(wapServerURL.indexOf("//") + 2);
			wapip = tempstr.substring(0, tempstr.indexOf(":"));
			String portstr = tempstr.substring(tempstr.indexOf(":") + 1);
			port = StringUtil.string2Int(portstr);
			SocialInsIp = Log4jInit.getCommString(Properties.getString("Social_Ins_WapIp"));
			SocialInsport = Integer.parseInt(Log4jInit.getCommString(Properties.getString("Social_Ins_Port")));
		}else{
			System.out.println("Log4jInit.getCommString 没有取到值");
		}		
	}

	public void requestData(String sendData, ResultEntry rst) throws Exception {
		String transName = rst.getTRANS_NO();
		ByteBuffer writebuf = null;
		ByteBuffer readbuf = null;
		String responseStr;
		int nBytes = 0;
		if (false) {
			// 本地测试访问 start
			xmlInstances xi = xmlInstances.getInstance();
			Document docTest;
			if (xi.hasXml(transName)) {
				docTest = xi.getDocByKey(transName);
			} else {
				String rootpath = this.getClass().getResource("/").getPath();
				rootpath = rootpath.replaceAll("classes/", "");
				docTest = XmlUtil.readFile(rootpath + "XML/" + transName
						+ ".xml");
				xi.putDoc(transName, docTest);
			}
			rst.setXmlData(docTest.asXML());
			rst.setMessage("操作成功!");
			rst.setStatus(1);
			// 本地测试访问 end
		} else {
			Socket socket = new Socket();
			socket.setSoLinger(true, 2);
			socket.setSoTimeout(readTimeout);
			// 关闭Nagle算法.立即发包
			socket.setTcpNoDelay(true);
			// 连接服务器
			if(transName.equals("lzb_690270")||transName.equals("lzb_690280")||transName.equals("lzb_760310")||transName.equals("lzb_760300")||transName.equals("lzb_690670")||transName.equals("lzb_690680")){
				socket.connect(new InetSocketAddress(SocialInsIp, SocialInsport));	
			}else{
				socket.connect(new InetSocketAddress(wapip, port));
			}

			// 获取输出输入流
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();

			// 记录流水日志报文
			rst.setTRANS_DATA(sendData);
			try {
				String falge = transName.substring(0,transName.length()-3);
				if(falge.indexOf("lzb_712") != -1){
					
					String dataLengh = sendData;
					int length = dataLengh.replace("  ", "").replace("\n", "").length();
					sendData = sendData.replace("  ", "").replace("\n", "");
					dataLengh = ""+length;
					if(dataLengh.length() < 6){
			        	dataLengh = "000000"+dataLengh;
			        	dataLengh = dataLengh.substring(dataLengh.length()-6,dataLengh.length());
			        }
					sendData = dataLengh + sendData;
				}
				
				if(transName.equals("lzb_711070") || transName.equals("lzb_711080")){
					byte[] sendbyte = sendData.getBytes("GBK");
					writebuf = ByteBuffer.allocate(sendbyte.length);
					writebuf = ByteBuffer.wrap(sendbyte);
					out.write(sendbyte);
				} else{
					byte[] sendbyte = sendData.getBytes("UTF-8");
					writebuf = ByteBuffer.allocate(sendbyte.length);
					writebuf = ByteBuffer.wrap(sendbyte);
					out.write(sendbyte);
				}
				
				// out.write((new byte[] { 13 }));
				out.flush();
				logger.debug("send str=" + sendData);
				if(transName.equals("lzb_711070") || transName.equals("lzb_711080")){
					responseStr = new String((byte[]) readStream(in), "GBK");
					logger.debug("recvString=" + responseStr);
				}else{
					responseStr = new String((byte[]) readStream(in), "UTF-8");
					logger.debug("recvString=" + responseStr);
				}
				// 记录流水日志报文
				rst.setTRANS_DATA(responseStr);
				if (null != responseStr && !"".equals(responseStr.toString())) {
					rst.setXmlData(responseStr.toString());
					rst.setMessage("操作成功!");
					rst.setStatus(1);
				} else {
					rst.setStatus(0);
					logger.info("wap interface response time:timeout!");
				}
			} catch (SocketTimeoutException ste) {
				logger.error("Conn Wap TCP/IP Timeout!", ste);
				rst.setMessage("Conn Wap TCP/IP Timeout!" + transName);
				rst.setStatus(0);
				throw ste;
			}finally{
				out.close();
				in.close();
				socket.close();
			}
		}
	}

	protected Object readStream(InputStream input) throws IOException {
		BufferedReader lineReader = new BufferedReader(new InputStreamReader(
				input, "8859_1"));
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Matcher m;
		do {
			String line = lineReader.readLine();
			if (line == null)
				break;
			bout.write(line.getBytes("8859_1"));
			bout.write("\r\n".getBytes());
			m = pattern.matcher(line);
		} while (!m.find());
		return bout.toByteArray();
	}

	public int getConnTimeout() {
		return connTimeout;
	}

	public void setConnTimeout(int connTimeout) {
		this.connTimeout = connTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
}
