package kitt.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by xiajing on 15-9-10.
 */
public class QuickTrade implements Serializable {

  private int id;

  private String userphone;      //客户联系电话

  private String content;      //客户填写的内容

  private int status;          //状态，1(待处理），2(已处理）

  private String statusname;   //分为待处理，已处理

  private int isdelete;        //是否删除

  private LocalDateTime createtime;      //创建时间

  private LocalDateTime lastupdatetime;      //最后一次更新时间

  private LocalDateTime solvedtime;      //处理单子时间

  private int solvedmanid;        //处理单子管理员id

  private String solvedmanusername;      //处理单子管理员账号

  private String solvedremarks;       //处理单子人填写的备注

  private Integer clientType;

  private String code;


  public QuickTrade() {
  }


  public QuickTrade(String userphone, String content, LocalDateTime createtime, String code, Integer clientType) {
    this.userphone = userphone;
    this.content = content;
    this.createtime = createtime;
    this.code = code;
    this.clientType = clientType;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUserphone() {
    return userphone;
  }

  public void setUserphone(String userphone) {
    this.userphone = userphone;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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

  public int getIsdelete() {
    return isdelete;
  }

  public void setIsdelete(int isdelete) {
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

  public Integer getClientType() {
    return clientType;
  }

  public void setClientType(Integer clientType) {
    this.clientType = clientType;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
