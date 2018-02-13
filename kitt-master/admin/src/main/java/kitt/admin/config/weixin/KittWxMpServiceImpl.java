package kitt.admin.config.weixin;

import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.redisson.Redisson;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by hongpf on 15/7/10.
 */

public class KittWxMpServiceImpl extends WxMpServiceImpl {

    @Autowired
    protected  Redisson redisson;

    public String getAccessToken(boolean forceRefresh) throws WxErrorException {
        if (forceRefresh) {
            wxMpConfigStorage.expireAccessToken();
        }
        if (wxMpConfigStorage.isAccessTokenExpired()) {
           RLock lock =  redisson.getLock("weixin_access_lock") ;
           lock.lock(10, TimeUnit.SECONDS);
            try{
                if (wxMpConfigStorage.isAccessTokenExpired()) {
                    String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
                            + "&appid=" + wxMpConfigStorage.getAppId()
                            + "&secret=" + wxMpConfigStorage.getSecret()
                            ;
                    try {
                        HttpGet httpGet = new HttpGet(url);
                        if (httpProxy != null) {
                            RequestConfig config = RequestConfig.custom().setProxy(httpProxy).build();
                            httpGet.setConfig(config);
                        }
                        CloseableHttpClient httpclient = getHttpclient();
                        CloseableHttpResponse response = httpclient.execute(httpGet);
                        String resultContent = new BasicResponseHandler().handleResponse(response);
                        WxError error = WxError.fromJson(resultContent);
                        if (error.getErrorCode() != 0) {
                            throw new WxErrorException(error);
                        }
                        WxAccessToken accessToken = WxAccessToken.fromJson(resultContent);
                        wxMpConfigStorage.updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
                    } catch (ClientProtocolException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }finally {
                lock.unlock();
            }
        }
        return wxMpConfigStorage.getAccessToken();
    }
}
