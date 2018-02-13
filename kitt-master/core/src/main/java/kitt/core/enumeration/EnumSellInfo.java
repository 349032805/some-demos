package kitt.core.enumeration;

import java.io.Serializable;

/**
 * Created by lich on 15/8/28.
 */
public enum EnumSellInfo implements Serializable {
    WaitConfirmed("待确认"),                //待确认
    WaitVerify("审核中"),                   //待审核
    VerifyPass("审核通过"),                 //审核通过
    VerifyNotPass("审核未通过"),            //审核未通过
    Canceled("已取消"),                    //已取消
    Deleted("已删除"),                     //已删除
    SellOut("匹配完成"),                   //匹配完成
    OutOfDate("已过期"),                   //已过期
    OutOfStack("已下架"),                  //已下架

    Recommend("推荐"),                    //产品类型  producttype  推荐
    Common("正常");                       //产品类型  正常

    public String value;

    EnumSellInfo(String value) {
        this.value=value;
    }
    EnumSellInfo(){}

    public String value() {
        return this.value;
    }
}