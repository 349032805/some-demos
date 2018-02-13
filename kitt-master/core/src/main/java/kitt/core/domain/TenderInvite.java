package kitt.core.domain;

/**
 * Created by xiangyang on 16/1/15.
 */
public class TenderInvite {

    private int id;
    private int userId;
    private int supplyerId;
    private int tenderDeclarationId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTenderDeclarationId() {
        return tenderDeclarationId;
    }

    public void setTenderDeclarationId(int tenderDeclarationId) {
        this.tenderDeclarationId = tenderDeclarationId;
    }



    public int getSupplyerId() {
        return supplyerId;
    }

    public void setSupplyerId(int supplyerId) {
        this.supplyerId = supplyerId;
    }
}
