package kitt.core.domain;

/**
 * Created by fanjun on 15-7-15.
 */
public class YaxisIndex {
    //记录数据在Y轴的哪边
    private String unitname;
    private int index;

    public YaxisIndex(){

    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
