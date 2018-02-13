package kitt.core.service;

import kitt.core.libs.SMSClient;
import kitt.ext.WithLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * Created by lxj on 14/11/6.
 */
@Service
@PropertySource("classpath:/sms.properties")
public class SMS implements WithLogger {
	@Value("${sms.sn}")
	private String sn;
	@Value("${sms.password}")
	private String password;
	@Value("${sms.server}")
	private String server;

	/**
	 * 发送短信方法,此方法禁止随便调用,
	 * 如需要, 请优先考虑使用MessageNotice.java 里面的 CommonMessage,
	 * 使用详细请查看MessageNotice.java 文件
     */
	public void send(String phone, String content) throws Exception {
		SMSClient smsClient = new SMSClient(server, sn, password);
		String hellowords = "";
		smsClient.sendSMS(phone, content, hellowords, ConfigConsts.YiMeiSMSSignature);
		//SMSResponse smsResponse = smsClient.sendSMS(phone, content, hellowords, signature);


		//System.out.println("***************************************");
		//System.out.println(smsResponse.getCode() + "--------------");
		//System.out.println(smsResponse.getCode());
		//System.out.println("***************************************");
		logger().info("CODE---------" + phone + "-------" + content);
	}

	public void send(String phone, String content, String hellowords, String signature) throws Exception {
		SMSClient smsClient = new SMSClient(server, sn, password);
		//smsClient.sendSMS(phone, content, hellowords, signature);
		//SMSResponse smsResponse = smsClient.sendSMS(phone, content, hellowords, signature);

		//System.out.println("***************************************");
		//System.out.println(smsResponse.getCode() + "--------------");
		//System.out.println(smsResponse.getCode());
		//System.out.println("***************************************");
		logger().info("CODE---------" + phone + "-------" + content);
	}

}
