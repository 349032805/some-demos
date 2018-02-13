package kitt.core.entity;

import java.io.Serializable;

/**
 * Created by lich on 15-09-02.
 */
public class Dictionary extends BaseEntity implements Serializable {
    private int id;
    private String code;
    private String name;

    public Dictionary(){

    }

    public Dictionary(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Dictionary(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
