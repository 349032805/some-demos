package kitt.core.domain;

import java.util.List;
import java.util.Map;

/**
 * Created by lich on 15/12/18.
 */
public class District {
    private String name;
    private int isdelete;
    private char mold;
    private String code;
    private int level;
    private String parent;
    private String sequence;
    private List<Map<String,Object>> regionList;

    public District() {
    }

    public District(String name, String code) {
        this.name = name;
        this.code = code;
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

    public List<Map<String, Object>> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<Map<String, Object>> regionList) {
        this.regionList = regionList;
    }
}
