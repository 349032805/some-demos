package kitt.core.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yimei on 15/7/16.
 */
public class DataObject implements Serializable {
    private String parentname;
    private String name;
    private List<IndIndex> indIndexList;

    public DataObject(){}

    public DataObject(String parentname, String name, List<IndIndex> indIndexList) {
        this.parentname = parentname;
        this.name = name;
        this.indIndexList = indIndexList;
    }

    public String getParentname() {
        return parentname;
    }

    public void setParentname(String parentname) {
        this.parentname = parentname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IndIndex> getIndIndexList() {
        return indIndexList;
    }

    public void setIndIndexList(List<IndIndex> indIndexList) {
        this.indIndexList = indIndexList;
    }
}
