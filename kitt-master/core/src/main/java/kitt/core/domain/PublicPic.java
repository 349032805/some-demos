package kitt.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by fanjun on 15-5-7.
 */
public class PublicPic implements Serializable {

    private int id;
    private String name;
    private String path;
    private String comment;
    private LocalDateTime createtime;


    public PublicPic(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }
}
