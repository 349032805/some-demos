package kitt.core.entity;

import java.io.Serializable;

/**
 * Created by liuxinjie on 16/2/29.
 */
public class RegionYM extends BaseEntity implements Serializable {
    private String name;                              //名称
    private int isdelete;                             //是否删除
    private char mold;                                //首字母
    private String code;                              //代码
    private int level;                                //级别,1.省份, 2.市, 3.县
    private String parent;                            //父code
    private String sequence;                          //顺序

    public RegionYM() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }

    public char getMold() {
        return mold;
    }

    public void setMold(char mold) {
        this.mold = mold;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
