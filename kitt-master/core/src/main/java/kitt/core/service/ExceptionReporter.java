package kitt.core.service;

import kitt.ext.WithLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.Properties;

/**
 * Created by joe on 2/26/15.
 */
@Service
@PropertySource("classpath:/mail.properties")
public class ExceptionReporter implements WithLogger {
    // http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mail.html
    protected JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    @Autowired
    protected MailMessage mm;


    @Value("${mail.mailMethod}")
    private String mailMethod = "";

    @Value("${mail.to}")
    private String to;

    public void handle(Exception ex, String requestUrl, String formData, String requestHeader, Object user) {
        if ("sendcloud".equalsIgnoreCase(mailMethod)) {
            logger().info("正在尝试使用sendcloud发送邮件");
            // mm.setText("requestUrl:\t" + requestUrl + "\n fromData={" + formData + "}\n" + ex.getMessage() + " " + getStackTrace(ex));
            // mm.setSubject(ZonedDateTime.now().toString() +" "+ ex.getClass().getName());
            boolean success = false;
            try {

                success = mm.send(
                        "requestHeader:\t" + requestHeader + "\nrequestUrl:\t" + requestUrl + "\n formData=" + formData + "\ncurrentUser:\t" + user + "\n" + ex.getMessage() + " " + getStackTrace(ex),
                        ZonedDateTime.now().toString() + " " + ex.getClass().getName());
            } catch (Exception e) {
                logger().error("发送报警邮件报错" + e.getStackTrace().toString());
            }
            if (!success) {
                logger().warn("sendCloud发送邮件失败，正在尝试使用SimpleMailMessage发送...");
                sendSimpleMail(ex, requestUrl, formData, requestHeader, user);
            }
        } else { //if ("simplemail".equalsIgnoreCase(mailMethod)) {
            logger().info("正在尝试使用simpleMail发送邮件");
            sendSimpleMail(ex, requestUrl, formData, requestHeader, user);
        }
    }

    private void sendSimpleMail(Exception ex, String requestUrl, String formData, String requestHeader, Object user) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("warning@server.yimei180.com");
        msg.setTo(to.split(";"));
        msg.setSubject(ZonedDateTime.now().toString() + " " + ex.getClass().getName());
        msg.setText("requestHeader:\t" + requestHeader + "\nrequestUrl:\t" + requestUrl + "\nformData:\t" + formData + "\ncurrentUser:\t" + user + "\n" + ex.getMessage() + " " + getStackTrace(ex));
        try {
            javaMailSender.send(msg);
        } catch (Exception x) {
            logger().error("SimpleMailMessage发送报警邮件失败：", x);
        }
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    /**
     * @param from        本地开发调试，默认请使用QQ邮箱
     * @param mailPssword 邮箱密码
     * @param to
     * @param content     邮件内容
     */
    @Async
    public void sendSimpleMail(String from, String mailPssword, String to, String content) {
        logger().info("************开始发送邮件了*********");
        SimpleMailMessage msg = new SimpleMailMessage();
        //设置发送邮件服务信息
        javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPort(25);
        javaMailSender.setUsername(from);
        javaMailSender.setPassword(mailPssword);
        javaMailSender.setJavaMailProperties(new Properties() {{
            put("mail.transport.protocol", "smtp");
            put("mail.smtp.auth", true);
            put("mail.smtp.starttls.enable", true);
            put("mail.debug", true);
        }});
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("验证邮箱找回密码");
        msg.setText(content);
        try {
            javaMailSender.send(msg);
            logger().info("************邮件发送成功了*********");
        } catch (Exception x) {
            logger().error("找回密码邮件发送失败：", x);
        }
    }
}
