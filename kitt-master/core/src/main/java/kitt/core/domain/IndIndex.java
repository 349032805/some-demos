package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by jack on 6/8/15.
 */
public class IndIndex implements Serializable {
    private String indindexcfgid;      //行业指数id
    private String d1;                 //维度1
    private String d2;                 //维度2
    private String val;                //数值
    private boolean isdelete;          //是否删除

    public IndIndex(){}

    public IndIndex(String indindexcfgid, String d1) {
        this.indindexcfgid = indindexcfgid;
        this.d1 = d1;
    }

    public IndIndex(String indindexcfgid, String d1, String val) {
        this.indindexcfgid = indindexcfgid;
        this.d1 = d1;
        this.val = val;
    }

    public String getIndindexcfgid() {
        return indindexcfgid;
    }

    public void setIndindexcfgid(String indindexcfgid) {
        this.indindexcfgid = indindexcfgid;
    }

    public String getD1() {
        return d1;
    }

    public void setD1(String d1) {
        this.d1 = d1;
    }

    public String getD2() {
        return d2;
    }

    public void setD2(String d2) {
        this.d2 = d2;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public boolean isdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }
}
