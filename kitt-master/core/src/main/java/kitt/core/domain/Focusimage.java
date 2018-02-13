package kitt.core.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * Created by lich on 16/2/16.
 */
public class Focusimage {
    private int id;
    @Length(max=100,message = "图片名称长度限制为100")
    @NotBlank(message = "图片名称不能为空")
    private String pictitle;
    @Length(max=100,message = "文章标题长度限制为100")
    @NotBlank(message = "文章标题不能为空")
    private String articletitle;
    @Length(max=100,message = "文章链接长度限制为100")
    @NotBlank(message = "文章链接不能为空")
    private String articlelink;
    @Length(max=100,message = "图片地址长度限制为100")
    @NotBlank(message = "图片地址不能为空")
    private String picaddress;
    @Length(max=500,message = "摘要长度限制为500")
    private String summary;
    private LocalDateTime createtime;
    private LocalDateTime lastupdatetime;
    private int isdelete;
    private String lasteditman;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPictitle() {
        return pictitle;
    }

    public void setPictitle(String pictitle) {
        this.pictitle = pictitle;
    }

    public String getArticletitle() {
        return articletitle;
    }

    public void setArticletitle(String articletitle) {
        this.articletitle = articletitle;
    }

    public String getArticlelink() {
        return articlelink;
    }

    public void setArticlelink(String articlelink) {
        this.articlelink = articlelink;
    }

    public String getPicaddress() {
        return picaddress;
    }

    public void setPicaddress(String picaddress) {
        this.picaddress = picaddress;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }

    public String getLasteditman() {
        return lasteditman;
    }

    public void setLasteditman(String lasteditman) {
        this.lasteditman = lasteditman;
    }
}
