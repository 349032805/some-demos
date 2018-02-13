package kitt.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by fanjun on 15-5-13.
 */
public class AppVersion implements Serializable {

    private int id;
    private String type;
    private String name;
    private String path;
    private String version;
    private boolean status;
    private LocalDateTime createtime;
    private LocalDateTime usetime;  //启用时间
    private String comment;         //版本描述
    private int versionnum;

    public AppVersion(){

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public LocalDateTime getUsetime() {
        return usetime;
    }

    public void setUsetime(LocalDateTime usetime) {
        this.usetime = usetime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getVersionnum() {
        return versionnum;
    }

    public void setVersionnum(int versionnum) {
        this.versionnum = versionnum;
    }
}
