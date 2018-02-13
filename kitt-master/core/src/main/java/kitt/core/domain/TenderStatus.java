package kitt.core.domain;

/**
 * Created by zhangbolun on 15/11/11.
 */
public enum TenderStatus {
    //招标状态
    TENDER_EDIT("暂缓发布") ,          //招标编辑状态
    TENDER_GIVEUP("放弃招标"),         //招标放弃状态
    TENDER_APPLY("申请招标"),          //招标申请状态
    TENDER_VERIFY_FAIL("审批失败"),    //招标审批失败
    TENDER_VERIFY_PASS("已发布"),      //招标审批通过，招标预告，未开始投标
    TENDER_START("投标中"),            //投标开始,投标中
    TENDER_CHOOSE_CONFIRM("待选标"),   //选标阶段
    TENDER_RELEASE_RESULT("已完成"),   //公布中标结果，已完成
    TENDER_CANCEL("已作废"),           //投标时无人竞标,发布招标已作废
    //招标关注状态
    TENDER_FOCUS_TRUE("已关注"),       //招标已关注
    TENDER_FOCUS_CANCEL("取消关注"),    //招标取消关注
    //投标状态
    MYTENDER_EDIT("暂缓投标"),          //投标暂缓,编辑投标
    MYTENDER_MISSING("错过投标"),       //投标已截止,错过投标
    MYTENDER_TENDERED_FREE("已投标"),   //已投标,(不需要上传支付凭证)
    MYTENDER_TENDERED("已投标"),        //已投标,但未上传支付凭证，或支付凭证未确认,(需要支付凭证)
    MYTENDER_TENDERED_CONFIRM("已投标"),//已投标,并进行过支付确认(需要支付凭证)
    MYTENDER_CHOOSE_FREE("待选标"),     //待选标(不需要上传支付凭证)
    MYTENDER_WAITING_CHOOSE("待选标"),  //待选标,但未上传支付凭证，或支付凭证未确认(需要支付凭证)
    MYTENDER_CHOOSE("待选标"),          //待选标,并进行过支付确认(需要支付凭证)
    MYTENDER_FAIL("未中标"),            //未中标
    MYTENDER_SUCCEED("已中标"),         //已中标
    MYTENDER_GIVEUP("放弃投标"),        //放弃投标
    MYTENDER_CANCEL("已作废"),          //已投标但未上传支付凭证过投标时间后作废出处理
    //投标支付凭证
    waitVerify("待审核"),               //投标支付凭证待审核
    paidUp("已缴纳"),                   //投标支付凭证已审核
    unPaidUp("未缴纳");                 //投标支付凭证未缴纳

    public  String value;
    TenderStatus(String value){this.value=value;}
    TenderStatus(){}


}
