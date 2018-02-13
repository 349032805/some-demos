package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by jack on 6/10/15.
 */
public class DataBook implements Serializable {
    private int id;                    // id
    private String type;               // 类型
    private int sequence;              // 编码（英文名）
    private String name;               // 名称（中文名）

    public DataBook(){}

    public DataBook(int sequence, String name) {
        this.sequence = sequence;
        this.name = name;
    }

    public DataBook(int sequence, String name,String type) {
        this.sequence = sequence;
        this.name = name;
        this.type=type;
    }

    public DataBook(String type, int sequence, String name) {
        this.type = type;
        this.sequence = sequence;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
