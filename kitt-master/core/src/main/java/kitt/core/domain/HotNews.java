package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by liuxinjie on 15/9/5.
 */
public class HotNews implements Serializable {
    private int id;            //id
    private int aid;           //对应article  id
    private boolean isshow;    //是否在前台显示
    private boolean isdelete;  //是否删除
    private int level;         //文章类别
    private int sequence;      //次序

    public HotNews() {
    }

    public HotNews(int aid) {
        this.aid = aid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public boolean isshow() {
        return isshow;
    }

    public void setIsshow(boolean isshow) {
        this.isshow = isshow;
    }

    public boolean isdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
