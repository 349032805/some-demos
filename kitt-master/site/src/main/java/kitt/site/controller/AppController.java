package kitt.site.controller;

import kitt.core.domain.Appdownload;
import kitt.core.domain.Dictionary;
import kitt.core.persistence.AppdownloadMapper;
import kitt.core.persistence.DictionaryMapper;
import kitt.ext.WithLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;

/**
 * Created by fanjun on 15-5-20.
 */
@Controller
public class AppController implements WithLogger {

    @Autowired
    protected DictionaryMapper dictionaryMapper;

    @Autowired
    protected AppdownloadMapper appdownloadMapper;

    //app通过请求下载
    @RequestMapping("/yimeiwangApp")
    public void yimeiwangApp(HttpServletRequest request,HttpServletResponse response) throws Exception{

        Appdownload appdownload = new Appdownload();
        String userAgent = request.getHeader("user-agent");
        appdownload.setUseragent(userAgent);
        String type = null;
        if (userAgent.toUpperCase().contains("Android".toUpperCase())) {
            response.sendRedirect("/files/app/androidApp/yimeiwang.apk");
            type = "Android";
        } else if (userAgent.toUpperCase().contains("iPhone".toUpperCase())
                || userAgent.toUpperCase().contains("ios".toUpperCase())
                || userAgent.toUpperCase().contains("ipad".toUpperCase())) {
            response.sendRedirect("http://www.apple.com/itunes");  //https://itunes.apple.com
            type = "ios";
        } else {
            response.sendRedirect("/files/app/androidApp/yimeiwang.apk");
            type = "Android";
        }
        appdownload.setType(type);
        appdownload.setIp(getIp(request));
        Dictionary d = dictionaryMapper.getAPKVersion();
        appdownload.setVersion(d.getName());
        appdownload.setCreatetime(LocalDateTime.now());
        appdownloadMapper.addAppdownload(appdownload);
    }

    //调用腾讯的页面数据获取ip所在的省份和城市
    public String getAddressByIP(String ip) throws Exception{
        String strIP = ip;
        URL url = new URL( "http://ip.qq.com/cgi-bin/searchip?searchip1=" + strIP);
        URLConnection conn = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GBK"));
        String line = null;
        StringBuffer result = new StringBuffer();
        while((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();
        strIP = result.substring(result.indexOf("该IP所在地为："));
        strIP = strIP.substring(strIP.indexOf("<span>"),strIP.indexOf("</span>"));
        strIP = strIP.substring(strIP.indexOf(">"),strIP.indexOf("&")).replace(">","");
        return strIP;
        }

    public String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(ip != null && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(ip != null && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }
}
