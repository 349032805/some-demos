package kitt.core.domain;

/**
 * Created by xiangyang on 16/1/17.
 */
public class MySupplyer {

    private int userId;
    private int supplyerId;
    //0 黑名单 1 白名单
    private int status;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSupplyerId() {
        return supplyerId;
    }

    public void setSupplyerId(int supplyerId) {
        this.supplyerId = supplyerId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public MySupplyer(int userId, int supplyerId, int status) {
        this.userId = userId;
        this.supplyerId = supplyerId;
        this.status = status;
    }

    public MySupplyer() {
    }
}
