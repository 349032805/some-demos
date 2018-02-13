package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by jack on 15/1/25.
 */
public enum EnumOrder implements Serializable {
    WaitConfirmed("待确认"),           //订单状态：待确认
    WaitSignContract("待签合同"),      //订单状态：待签合同 -- 进行中
    WaitPayment("待付款"),             //订单状态：待付款 -- 进行中
    WaitVerify("审核中"),              //订单状态：待审核（审核中） -- 进行中  （现货搜索）
    MakeMatch("撮合中"),               //订单状态：撮合中 -- 进行中           （商城）
    VerifyPass("交割中"),            //订单状态：审核通过（交割中） -- 进行中
    VerifyNotPass("审核未通过"),       //订单状态：审核未通过 -- 进行中
    WaitBalancePayment("待付尾款"),    //订单状态：待付尾款 -- 进行中
    ReturnGoods("退货中"),             //订单状态：退货中 -- 退货中
    Canceled("已取消"),                //订单状态：已取消 -- 已取消
    Deleted("已删除"),                 //订单状态：已删除
    Completed("已完成"),               //订单状态：已完成 -- 已完成
    ReturnCompleted("退货完成"),       //订单状态：退货完成 -- 已完成

    NULL("空"),                       //订单状态：空，

    PayTheWhole("付全款"),             //付款类型：付全款
    PayCashDeposit("付10%保证金"),          //付款类型：付10%保证金

    MallOrder,               //订单类型：自营单，商城订单
    OtherOrder;         //订单类型：委托单，其它卖家订单

    public String value;

    EnumOrder(String value) {
        this.value=value;
    }
    EnumOrder(){}

    public String value() {
        return this.value;
    }

}
