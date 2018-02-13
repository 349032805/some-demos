package kitt.admin.config.weixin;

import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@PropertySource("classpath:/weixin.properties")
public class KittInMemoryConfigStorage extends WxMpInMemoryConfigStorage {

    @Autowired
    StringRedisTemplate stringRedisTemplate ;

    @Value("${prd.appId}")
    @Override
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    @Value("${prd.token}")
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    @Value("${prd.appSecret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    @Value("${prd.aesKey}")
    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }


    @Override
    @Value("${prd.oauth2redirectUri}")
    public void setOauth2redirectUri(String oauth2redirectUri) {
        this.oauth2redirectUri = oauth2redirectUri;
    }


    public String getAccessToken() {
        return stringRedisTemplate.opsForValue().get("weixin_access_token");
    }

    public boolean isAccessTokenExpired()
    {
        String s =  stringRedisTemplate.opsForValue().get("weixin_access_expiresTime");
        if(s==null){
            return false ;
        }else{
            return System.currentTimeMillis() >Long.parseLong(s);
        }
    }

    public synchronized void updateAccessToken(WxAccessToken accessToken) {
        updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
    }


    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        stringRedisTemplate.opsForValue().set("weixin_access_token", accessToken, 7100, TimeUnit.SECONDS);
        this.accessToken = accessToken;
        this.expiresTime = (System.currentTimeMillis() + (expiresInSeconds - 200) * 1000l) ;
        stringRedisTemplate.opsForValue().set("weixin_access_expiresTime", expiresTime+"", 7100, TimeUnit.SECONDS);
    }

    public void expireAccessToken() {
        this.expiresTime =0 ;
        stringRedisTemplate.opsForValue().set("weixin_access_expiresTime", "0", 7100, TimeUnit.SECONDS);
    }

    @Override
    public String toString() {
        return "SimpleWxConfigProvider [appId=" + appId + ", secret=" + secret + ", accessToken=" + accessToken
                + ", expiresTime=" + expiresTime + ", token=" + token + ", aesKey=" + aesKey + "]";
    }

//    public static KittInMemoryConfigStorage fromXml(InputStream is) {
//        XStream xstream = XStreamInitializer.getInstance();
//        xstream.processAnnotations(KittInMemoryConfigStorage.class);
//        return (KittInMemoryConfigStorage) xstream.fromXML(is);
//    }
}
