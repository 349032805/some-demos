package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by liuxinjie on 15/10/8.
 * StatusBook 主要处理后台list分类使用：例如 待处理，已处理等。
 */
public class StatusBook implements Serializable {
    private String type;                  //状态类型
    private int sequence;                 //状态编号，顺序
    private String name;                  //状态名称

    public StatusBook() {
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
