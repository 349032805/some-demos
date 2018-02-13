package kitt.core.domain;

/**
 * Created by lich on 15/12/14.
 */
public enum LogisticsStatus {
    TREATED_NOT("未处理"),          //未处理
    MATCH_ING("匹配中"),            //匹配中
    MATCH_COMPLETED("匹配完成"),    //匹配完成,已匹配
    TRANSPORT_ING("运送中"),        //运送中
    COMPLETED_ALREADY("已完成"),    //运输已完成
    NOT_TRADING("未交易"),          //未交易，已取消

    DELEGATE_TREATED_NOT("未处理"), //委托单未处理
    DELEGATE_TREATED("已处理");     //委托单已处理
    LogisticsStatus(){};
    public  String value;
    LogisticsStatus(String value){this.value=value;}

}
