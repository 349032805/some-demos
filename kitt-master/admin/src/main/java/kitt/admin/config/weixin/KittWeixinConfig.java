package kitt.admin.config.weixin;

import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
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


}
