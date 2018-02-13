package kitt.core.service;

import kitt.ext.WithLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by fanjun on 15-6-1.
 */
@Service
public class MailService implements WithLogger {

    private final static String USERNAME = "auto_server@yimei180.com";
    private final static String PASSWORD = "Aa1234567890";
    @Autowired
    private Freemarker freemarker;
    protected JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        //设置发送邮件服务信息
        javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPort(25);
        javaMailSender.setUsername(USERNAME);
        javaMailSender.setPassword(PASSWORD);
        javaMailSender.setJavaMailProperties(new Properties() {{
            put("mail.transport.protocol", "smtp");
            put("mail.smtp.auth", true);
            put("mail.smtp.starttls.enable", true);
            put("mail.debug", true);
        }});
        msg.setFrom(USERNAME);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        try {
            javaMailSender.send(msg);
            logger().info(logPrint(to,"验证码邮件",content));
        } catch (Exception x) {
            logger().error("邮件发送失败：", x);
        }
    }

    public void sendHtmlMail(final String to, final String uuid) throws Exception{
        //设置发送邮件服务信息
        javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPort(25);
        javaMailSender.setUsername(USERNAME);
        javaMailSender.setPassword(PASSWORD);
        javaMailSender.setJavaMailProperties(new Properties() {{
            put("mail.transport.protocol", "smtp");
            put("mail.smtp.auth", true);
            put("mail.smtp.starttls.enable", true);
            put("mail.debug", true);
        }});

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true,"utf-8");
        helper.setFrom(USERNAME);
        helper.setTo(to);
        helper.setSubject("来自易煤网的激活邮件");
        final String date = getFormatDate();
        String text = freemarker.render("/email/email", new HashMap<String, Object>() {{
            put("email", to);
            put("date", date);
            put("uuid", uuid);
        }});
        helper.setText(text, true);
        try {
            javaMailSender.send(msg);
            logger().info(logPrint(to,"激活邮件","激活码:"+uuid));
        } catch (Exception x) {
            logger().error("邮件发送失败：", x);
        }

    }

    public String getFormatDate(){
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonth().getValue();
        int day = LocalDate.now().getDayOfMonth();
        return year+"年"+month+"月"+day+"日";
    }

    public String logPrint(String email,String mailType,String content){
        String sentence = "************";
        sentence += LocalDateTime.now()+":系统向用户邮箱"+email+"发送"+mailType+"成功!";
        sentence += "内容:"+content;
        return sentence;
    }
}
