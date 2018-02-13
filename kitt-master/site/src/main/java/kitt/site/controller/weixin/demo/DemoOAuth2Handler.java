package kitt.site.controller.weixin.demo;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by hongpf on 15/1/22.
 */
public class DemoOAuth2Handler implements WxMpMessageHandler {
    protected final Logger log = LoggerFactory.getLogger(DemoOAuth2Handler.class);

    @Override
  public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
      WxMpService wxMpService, WxSessionManager sessionManager) {
    //String href = "<a href=\"" + wxMpService.oauth2buildAuthorizationUrl(WxConsts.OAUTH2_SCOPE_BASE,null)
     //+ "\">测试oauth2</a>";
        String href = "<a href=\"" + "https://open.weixin.qq.com/connect/oauth2/authorize?appid=Wx6abf1fd4bc589d0a&redirect_uri=http%3A%2F%2Fhongpf.ngrok.io%2Fm%2Flogin&response_type=code&scope=snsapi_base#wechat_redirect"
           + "\">测试oauth2</a>";
        log.info("",href);
        System.out.println(href);
    return WxMpXmlOutMessage
        .TEXT()
        .content(href)
        .fromUser(wxMessage.getToUserName())
        .toUser(wxMessage.getFromUserName()).build();
  }
}
