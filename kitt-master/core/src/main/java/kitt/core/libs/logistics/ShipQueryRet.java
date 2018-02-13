package kitt.core.libs.logistics;

import java.util.Arrays;

/**
 * Created by zhangbolun on 16/1/9.
 */
public class ShipQueryRet {
    private int RetCode;                //调用是否成功（0 成功、1 失败
    private String RetMsg;                  //调用结果描述（成功、失败）
    private int TotalRecord;            //查询总记录条数;
    private Integer TotalPage;              //返回总页数;
    private ShipQueryRetData[] RetData; //业务数据

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

    public int getTotalRecord() {
        return TotalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        TotalRecord = totalRecord;
    }

    public Integer getTotalPage() {
        return TotalPage;
    }

    public void setTotalPage(Integer totalPage) {
        TotalPage = totalPage;
    }

    public ShipQueryRetData[] getRetData() {
        return RetData;
    }

    public void setRetData(ShipQueryRetData[] retData) {
        RetData = retData;
    }

    @Override
    public String toString() {
        return "ShipQueryRet{" +
                "RetCode=" + RetCode +
                ", RetMsg='" + RetMsg + '\'' +
                ", TotalRecord=" + TotalRecord +
                ", TotalPage=" + TotalPage +
                ", RetData=" + Arrays.toString(RetData) +
                '}';
    }
}
