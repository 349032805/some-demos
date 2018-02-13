package kitt.core.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * Created by lich on 15/12/30.
 */
public class SendMessage {
    private int id;
    @NotBlank(message = "手机号码不能为空")
    @Length(max=11,message = "手机号码限制为11个字符")
    private String telephone;      //手机号码
    @NotBlank(message = "短信内容不能为空")
    @Length(max=195,message = "短信内容限制为195个字符")
    private String content;        //短信内容
    private int userid;            //操作人id
    private String operator;       //操作人名字
    private LocalDateTime createtime;   //创建时间
    private LocalDateTime lastupdatetime; //最后更新时间
    private int isdelete;                  //是否删除  0:未删除  1:已删除


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
