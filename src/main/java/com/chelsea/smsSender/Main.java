package com.chelsea.smsSender;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.databinding.types.URI;
import org.apache.commons.lang3.StringUtils;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.SendSms;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.SendSmsE;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.SendSmsResponse;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.SendSmsResponseE;

public class Main {

	private static ConfigurationContext configurationContext;
	private static Properties prop = new Properties();
	static {
		try {
			prop.load(Main.class.getResourceAsStream("/properties/sms.properties"));
			configurationContext = ConfigurationContextFactory
					.createConfigurationContextFromFileSystem(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			String tel = "18780251861";
			String sms = "本次短信验证码：123456（如非本人操作，请忽略本短信）【中国移动】";
			String smsEndPoint = prop.getProperty("smsEndPoint");
			if (StringUtils.isBlank(smsEndPoint)) {
				System.out.println("警告, 短信接口URL未配置");
				return;
			}
			System.out.println("从" + smsEndPoint + "端口向" + tel + "发送短信:" + sms);
			SendSmsServiceStub sendSmsService = new SendSmsServiceStub(
					configurationContext, smsEndPoint);
			addHeaders(sendSmsService, tel);
			SendSmsE sendSms = new SendSmsE();
			SendSms param = new SendSms();
			param.setAddresses(new URI[] { new URI(tel.startsWith("tel:") ? tel
					: ("tel:" + tel)) });
			param.setMessage(sms);
			param.setSenderName(prop.getProperty("senderName"));
			sendSms.setSendSms(param);

			SendSmsResponseE sendSmsResponse = sendSmsService.sendSms(sendSms);
			SendSmsResponse response = sendSmsResponse.getSendSmsResponse();
			String result = response.getResult();
			System.out.println("短信下发结果:" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addHeaders(SendSmsServiceStub sendSmsService, String tel) {
		ServiceClient client = sendSmsService._getServiceClient();
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNS = omFactory.createOMNamespace(
				"http://www.huawei.com/schema/osg/common/v2_1", "ns1");
		OMElement head = omFactory.createOMElement("RequestSOAPHeader", omNS);

		// 通用head
		String[] headers = new String[] { "spId", "serviceId" };
		for (String headName : headers) {
			String headValue = prop.getProperty(headName);
			if (!StringUtils.isEmpty(headValue)) {
				OMElement headNode = omFactory.createOMElement(headName, omNS);
				headNode.addChild(omFactory.createOMText(headNode, headValue));
				head.addChild(headNode);
			} else {
				System.out.println(headName + "未配置，不加入header");
			}
		}
		// FA,OA填写接收手机号码
		headers = new String[] { "OA", "FA" };
		for (String headName : headers) {
			OMElement headNode = omFactory.createOMElement(headName, omNS);
			headNode.addChild(omFactory.createOMText(headNode, tel));
			head.addChild(headNode);
		}
		// 时间戳 "spPassword",
		String timestampString = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		OMElement timeStamp = omFactory.createOMElement("timeStamp", omNS);
		timeStamp.addChild(omFactory.createOMText(timeStamp, timestampString));
		head.addChild(timeStamp);
		String md5Password = md5_32(prop.getProperty("spId")
				+ prop.getProperty("spPassword") + timestampString);
		OMElement spPassword = omFactory.createOMElement("spPassword", omNS);
		spPassword.addChild(omFactory.createOMText(spPassword, md5Password));
		head.addChild(spPassword);
		System.out.println("短信接口header信息:" + head.toString());
		client.addHeader(head);
	}

	private static String md5_32(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("当前环境不支持MD5加密");
		}
		md.update(str.getBytes());
		byte b[] = md.digest();
		int i;
		StringBuffer buf = new StringBuffer("");
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		return buf.toString();
	}

}
