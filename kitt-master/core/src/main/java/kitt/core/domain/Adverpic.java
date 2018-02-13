package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by lich on 15/9/18.
 */
public class Adverpic implements Serializable {

  private Integer id;
  private String picurl;                   //图片地址
  private int sequence;                  //图片次序
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createtime;       //创建时间
  private boolean isdelete;               //是否删除
  private boolean isshow;               //是否显示
  private String linkurl;                //链接文章路径
  private String comment;           //描述信息

  private LocalDateTime lastupdatetime;        //最后一次变化时间
  private LocalDateTime lastedittime;          //最后一次更改时间
  private String lasteditman;


  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public boolean isshow() {
    return isshow;
  }

  public void setIsshow(boolean isshow) {
    this.isshow = isshow;
  }

  public String getLinkurl() {
    return linkurl;
  }

  public void setLinkurl(String linkurl) {
    this.linkurl = linkurl;
  }

  public int getSequence() {
    return sequence;
  }

  public void setSequence(int sequence) {
    this.sequence = sequence;
  }

  public LocalDateTime getCreatetime() {
    return createtime;
  }

  public void setCreatetime(LocalDateTime createtime) {
    this.createtime = createtime;
  }

  public boolean isdelete() {
    return isdelete;
  }

  public void setIsdelete(boolean isdelete) {
    this.isdelete = isdelete;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getPicurl() {
    return picurl;
  }

  public void setPicurl(String picurl) {
    this.picurl = picurl;
  }

  public LocalDateTime getLastupdatetime() {
    return lastupdatetime;
  }

  public void setLastupdatetime(LocalDateTime lastupdatetime) {
    this.lastupdatetime = lastupdatetime;
  }

  public LocalDateTime getLastedittime() {
    return lastedittime;
  }

  public void setLastedittime(LocalDateTime lastedittime) {
    this.lastedittime = lastedittime;
  }

  public String getLasteditman() {
    return lasteditman;
  }

  public void setLasteditman(String lasteditman) {
    this.lasteditman = lasteditman;
  }

  @Override
  public String toString() {
    return "adverpic{" +
      "id=" + id +
      ", picurl='" + picurl + '\'' +
      ", sequence=" + sequence +
      ", createtime=" + createtime +
      ", isdelete=" + isdelete +
      ", isshow=" + isshow +
      ", linkurl='" + linkurl + '\'' +
      ", comment='" + comment + '\'' +
      '}';
  }

  public Adverpic() {
  }

  public Adverpic(String picurl, LocalDateTime createtime, String linkurl, String comment) {
    this.picurl = picurl;
    this.createtime = createtime;
    this.linkurl = linkurl;
    this.comment = comment;
  }
}
