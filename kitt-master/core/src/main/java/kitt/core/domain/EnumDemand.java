package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by zhangbolun on 15/7/23.
 */
public enum EnumDemand  implements Serializable {
    CheckStatus_WaitConfirmed("待确认"),                //审核状态-待确认
    CheckStatus_WaitVerify("待审核"),                   //审核状态-待审核
    CheckStatus_VerifyPass("审核通过"),                 //审核状态-审核通过
    CheckStatus_VerifyNotPass("审核未通过"),            //审核状态-审核未通过

    TradeStatus_QuoteNotStart("未开始报价"),            //交易状态-未开始报价  quote
    TradeStatus_QuoteStart("开始报价"),                 //交易状态-开始报价   quote  比价中 报价中
    TradeStatus_QuoteFinished("报价结束"),              //交易状态-报价结束   quote  未中标 已中标

    MydemandStatus_WaitVerify("审核中"),                //我的需求-审核中
    MydemandStatus_VerifyNotPass("审核未通过"),          //我的需求-审核未通过
    MydemandStatus_Mapping("匹配中"),                   //我的需求-匹配中,报价结束以后
    MydemandStatus_QuoteStart("报价中");                //我的需求-报价中

    EnumDemand(String value) {
        this.value=value;
    }
    EnumDemand(){}

    public String value;
    public String value() {
        return this.value;
    }
}
