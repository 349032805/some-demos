package kitt.site.controller.weixin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kitt.core.domain.User;
import kitt.site.service.Session;
import kitt.site.service.mobile.UserService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hongpf on 15/7/7.
 */
@Controller
@RequestMapping("/weixinroot")
public class WeixinRootController {

    private Logger log = Logger.getLogger(WeixinRootController.class);

    @Autowired
    private Session session;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper om;

    @Autowired
    protected WxMpConfigStorage wxMpConfigStorage;

    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected WxMpMessageRouter wxMpMessageRouter;


    @RequestMapping(value = "/getToken", method = RequestMethod.GET)
    public
    @ResponseBody
    Object checkCompanyStatus() throws WxErrorException {
        return wxMpService.getAccessToken(true);
        // wxMpConfigStorage.expireAccessToken();
        // return null  ;
    }


    @RequestMapping(method = RequestMethod.POST)
    public void mrootG(HttpServletRequest request, HttpServletResponse response) throws IOException {

        log.info(getHeadersInfo(request));
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
                "raw" :
                request.getParameter("encrypt_type");

        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());

            //  loadSessionIfBind(inMessage.getFromUserName());

            WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
            if (outMessage != null) {
                response.getWriter().write(outMessage.toXml());
            }
            return;
        }

        if ("aes".equals(encryptType)) {
            // 是aes加密的消息
            String msgSignature = request.getParameter("msg_signature");
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), wxMpConfigStorage, timestamp, nonce, msgSignature);

            //loadSessionIfBind(inMessage.getFromUserName());
            WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
            response.getWriter().write(outMessage.toEncryptedXml(wxMpConfigStorage));
            return;
        }

        response.getWriter().println("不可识别的加密类型");
        return;
    }

    //判断是否绑定账户，如果绑定账户，就把账户信息加载到session
    private void loadSessionIfBind(String wxopenid) {

        User u = userService.getUserByWxopenId(wxopenid);
        if (u != null) {
            session.login(u);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    String mrootp(@RequestParam("signature") String signature,
                  @RequestParam("timestamp") String timestamp,
                  @RequestParam("nonce") String nonce,
                  @RequestParam("echostr") String echostr) throws IOException {
        /**
         * 加密/校验流程如下：
         1. 将token、timestamp、nonce三个参数进行字典序排序
         2. 将三个参数字符串拼接成一个字符串进行sha1加密
         3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
         *
         */
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
         return null;

    }


    //获取header对象
    private String getHeadersInfo(HttpServletRequest request) throws JsonProcessingException {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        map.put("requestURL", request.getRequestURL().toString());
        return om.writeValueAsString(map);
    }
}
