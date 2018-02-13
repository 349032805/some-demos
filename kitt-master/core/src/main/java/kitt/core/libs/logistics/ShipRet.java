package kitt.core.libs.logistics;

/**
 * Created by zhangbolun on 16/1/15.
 */
public class ShipRet {
    public Object RetData =new Object(){public int GoodId; };
    public int RetCode;
    public String RetMsg;

    public Object getRetData() {
        return RetData;
    }

    public void setRetData(Object retData) {
        RetData = retData;
    }

    public int getRetCode() {
        return RetCode;
    }

    public void setRetCode(int retCode) {
        RetCode = retCode;
    }

    public String getRetMsg() {
        return RetMsg;
    }

    public void setRetMsg(String retMsg) {
        RetMsg = retMsg;
    }

    @Override
    public String toString() {
        return "CancelShipRet{" +
                "RetData=" + RetData +
                ", RetCode=" + RetCode +
                ", RetMsg='" + RetMsg + '\'' +
                '}';
    }
}
