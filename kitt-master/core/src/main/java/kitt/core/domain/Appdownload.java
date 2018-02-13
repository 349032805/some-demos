package kitt.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by fanjun on 15-5-28.
 */
public class Appdownload implements Serializable {

    private int id;
    private String type;    //设备类型
    private String version; //下载版本
    private String useragent;
    private String ip;
    private LocalDateTime createtime;

    public Appdownload(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }
}
