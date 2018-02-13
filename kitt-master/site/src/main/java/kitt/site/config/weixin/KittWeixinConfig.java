package kitt.site.config.weixin;

import kitt.site.controller.weixin.demo.*;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hongpf on 15/4/22.
 */
@Configuration
public class KittWeixinConfig {

    @Bean
    public WxMpService wxMpService (WxMpConfigStorage wxMpConfigStorage){
        WxMpService service =  new KittWxMpServiceImpl();
        service.setWxMpConfigStorage(wxMpConfigStorage);
        return service  ;
    }

    @Bean
    public WxMpMessageRouter wxMpMessageRouter( WxMpService wxMpService ){

        WxMpMessageHandler logHandler = new DemoLogHandler();
        WxMpMessageHandler textHandler = new DemoTextHandler();
        WxMpMessageHandler imageHandler = new DemoImageHandler();
        WxMpMessageHandler oauth2handler = new DemoOAuth2Handler();
        DemoGuessNumberHandler guessNumberHandler = new DemoGuessNumberHandler();


        WxMpMessageRouter wxMpMessageRouter = new WxMpMessageRouter(wxMpService);
        wxMpMessageRouter
            .rule().handler(logHandler).next()
            .rule().msgType(WxConsts.XML_MSG_TEXT).matcher(guessNumberHandler).handler(guessNumberHandler).end()
            .rule().async(false).content("哈哈").handler(textHandler).end()
            .rule().async(false).content("图片").handler(imageHandler).end()
            .rule().async(false).content("o").handler(oauth2handler).end();
        return  wxMpMessageRouter;
    }
}
