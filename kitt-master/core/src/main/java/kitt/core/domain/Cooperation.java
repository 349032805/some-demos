package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 15/9/28.
 * 我要合作实体类
 */
public class Cooperation implements Serializable {
    private int id;
    private String code;                       //我要合作申请单编号
    private String companyname;                //客户公司名称
    private String linkmanname;                //客户联系人姓名
    private String linkmanphone;               //客户联系人电话
    private String remarks;                    //客户填写的备注
    private int status;                        //此条信息的状态，分为待处理和已处理两种
    private String statusname;                 //此条信息的状态文本
    private int kind;                          //企业大的分类，使用数字1，2，3
    private String kindname;                   //kind文本， 供应商，金融机构等等，和kind一一对应
    private int type;                          //企业细的分类，使用数字1，2，3
    private String typename;                   //type文本，煤炭企业，电厂，钢厂等，和type一一对应
    private boolean isdelete;                  //是否删除
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;          //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;      //最后一次更改时间，MySQL自动更新，该字段不需要维护
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime solvedtime;          //管理员处理此条信息时间
    private int solvedmanid;                   //处理此条信息管理员id
    private String solvedmanusername;          //处理此条信息管理员账号
    private String solvedremarks;              //处理此条信息时，管理员填写的备注
    private int clienttype;                    //创建此条信息，客户使用的客户端类型
    private boolean login;                     //客户是否已登录
    private int userid;                        //已登陆客户的id
    private String userphone;                  //已登陆客户的 登陆手机号

    public Cooperation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getLinkmanname() {
        return linkmanname;
    }

    public void setLinkmanname(String linkmanname) {
        this.linkmanname = linkmanname;
    }

    public String getLinkmanphone() {
        return linkmanphone;
    }

    public void setLinkmanphone(String linkmanphone) {
        this.linkmanphone = linkmanphone;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusname() {
        return statusname;
    }

    public void setStatusname(String statusname) {
        this.statusname = statusname;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getKindname() {
        return kindname;
    }

    public void setKindname(String kindname) {
        this.kindname = kindname;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public boolean isdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
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

    public LocalDateTime getSolvedtime() {
        return solvedtime;
    }

    public void setSolvedtime(LocalDateTime solvedtime) {
        this.solvedtime = solvedtime;
    }

    public int getSolvedmanid() {
        return solvedmanid;
    }

    public void setSolvedmanid(int solvedmanid) {
        this.solvedmanid = solvedmanid;
    }

    public String getSolvedmanusername() {
        return solvedmanusername;
    }

    public void setSolvedmanusername(String solvedmanusername) {
        this.solvedmanusername = solvedmanusername;
    }

    public String getSolvedremarks() {
        return solvedremarks;
    }

    public void setSolvedremarks(String solvedremarks) {
        this.solvedremarks = solvedremarks;
    }

    public int getClienttype() {
        return clienttype;
    }

    public void setClienttype(int clienttype) {
        this.clienttype = clienttype;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }
}
