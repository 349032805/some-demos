package kitt.core.libs.logistics;

/**
 * Created by zhangbolun on 16/1/15.
 */
public class CancelShip {
    private String No;        //易煤流水
    private String Remark;    //备注
    private String IP;        //IP
    private String EditDate;  //YYYY-MM-DD HH:MM:SS

    public CancelShip(){}

    public CancelShip(String no, String remark, String IP, String editDate) {
        No = no;
        Remark = remark;
        this.IP = IP;
        EditDate = editDate;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getEditDate() {
        return EditDate;
    }

    public void setEditDate(String editDate) {
        EditDate = editDate;
    }

    @Override
    public String toString() {
        return "CancelShip{" +
                "No='" + No + '\'' +
                ", Remark='" + Remark + '\'' +
                ", IP='" + IP + '\'' +
                ", EditDate='" + EditDate + '\'' +
                '}';
    }
}
