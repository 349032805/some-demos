package kitt.core.domain;

/**
 * Created by xiangyang on 15-6-11.
 */
public class OrderByType {

    private String field;
    private String direction;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public OrderByType asc(String field) {
        this.field = field;
        this.direction="asc";
        return this;
    }

    public OrderByType desc(String field) {
        this.field=field;
        this.direction = "desc";
        return this;
    }
}
