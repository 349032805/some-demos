package kitt.site.service;

import kitt.site.ext.freemarker.Java8ObjectWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;



/**
 * Created by joe on 12/31/14.
 */

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Request {
    @Autowired
    protected HttpServletRequest request;

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public HttpServletRequest getRequest() {
      return request;
    }

  //add by xj  begin
    public String getHttpPath(){
        String path = request.getScheme()+"://"+request.getServerName();
        if(request.getServerPort() != 80){
           path += ":"+request.getServerPort();
        }
        return path;
    }

    public String GetTime(){
        return LocalDateTime.now().toLocalDate().toString();
    }


    public String GetYear(){
        return String.valueOf(LocalDateTime.now().getYear());
    }
    //add by xj  end
}
