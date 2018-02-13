package kitt.core.persistence;

import org.apache.ibatis.annotations.Update;


/**
 * Created by fanjun on 15-1-27.
 */
public interface TimeTaskMapper {

    /* *******************系统定时任务sql ********************/
    //每天0点检查修改昨天报价截止的需求记录改为报价结束
    //@Update("update demands set tradestatus='报价结束' where date_add(quoteenddate,interval 1 day) = date_format(now(),'%Y-%m-%d')")
    //public void modifyTradestatusTask();

    //报价截止日 有报价 的需求状态更改  ”匹配中“
    @Update("update demands t  set t.tradestatus='匹配中' where date_add(quoteenddate,interval 1 day) <= date_format(now(),'%Y-%m-%d') and  exists  (select 1 from quotes q  where q.demandcode = t.demandcode )  and  t.tradestatus = '开始报价'")
    public void modifyTradestatusTask();

    //报价截止日 没有报价 的需求状态更改为  “已过期”
    @Update("update demands t  set t.tradestatus='已过期' where date_add(quoteenddate,interval 1 day) <= date_format(now(),'%Y-%m-%d') and  not  exists  (select 1 from quotes q  where   q.demandcode = t.demandcode )  and  t.tradestatus = '开始报价'")
    public void modifyTradestatusTask1();

    //将报价中的，过了提货截止日期的产品状态改为已过期
    @Update("update demands t  set tradestatus='已过期' where (case when deliverydate is null then  deliverydatestart else deliverydate end) <= date_format(now(),'%Y-%m-%d') and t.tradestatus = '匹配中'")
    public void modifyTradestatusTask2();

    //将超过7天的记录状态改为删除
    @Update("update demands set isdelete=1 where noshowdate <= date_format(now(),'%Y-%m-%d')")
    public void modifyIsdeleteTask();

    //过了报价截止时间后,交易状态改为匹配中（有报价）
    @Update("update mydemands t set status='匹配中' where date_add(quoteenddate,interval 1 day) <= date_format(now(),'%Y-%m-%d') and  exists (select 1 from  quotes q where q.demandcode = t.demandcode ) ")
    public void modifyStatusToMatchingTask();

    //过了报价截止时间后,交易状态改为交易结束（没有报价）
    @Update("update mydemands t set status='交易结束' where date_add(quoteenddate,interval 1 day) <= date_format(now(),'%Y-%m-%d') and not exists (select 1 from  quotes q where q.demandcode = t.demandcode ) ")
    public void modifyStatusToMatchingTask2();

    //我的需求过了提货截止日，状态改为交易结束
    @Update("update mydemands set status='交易结束' where status='匹配中' and (date_add(deliverydate,interval 1 day) <= date_format(now(),'%Y-%m-%d') or date_add(deliverydateend,interval 1 day) <= date_format(now(),'%Y-%m-%d'))")
    public void modifyStatusToTradeOverTask();

    //将我的需求表，已经匹配结束的记录，提货截开始日当天状态改为交易结束
    @Update("update mydemands set status='交易结束' where status='匹配结束' and (deliverydate <= date_format(now(),'%Y-%m-%d') or deliverydatestart <= date_format(now(),'%Y-%m-%d'))")
    public void modifyStatusToTradeOverTask2();

    //我的需求,过了提货开始时间 的记录状态更改为 交易结束
    @Update("update mydemands t set t.status='交易结束' where t.status='匹配中' and case when t.deliverydate is null then  deliverydatestart else deliverydate end <= date_format(now(),'%Y-%m-%d')")
    public void modifyStatusToTradeOverTask3();

    //将报价表到了报价截止日期的记录改为比价中
    @Update("update quotes set status='比价中' where date_add(quoteenddate,interval 1 day) = date_format(now(),'%Y-%m-%d')")
    public void modifyStatusToCompareQuoteTask();

    //将报价表过了提货开始时间仍没有中标的改为未中标
    @Update("update quotes set status='未中标' where status='比价中' and (deliverydate <= date_format(now(),'%Y-%m-%d') or  deliverydatestart <= date_format(now(),'%Y-%m-%d'))")
    public void modifyStatusToNotBidTask();

    /* ********************招标投标系统定时任务*************************************** */

    //更新 审核通过的,投标日期开始的 招标公告为 发布状态
    @Update("update tenderdeclaration set status='TENDER_START' where status='TENDER_VERIFY_PASS' and now()>=tenderbegindate and now()<= tenderenddate")
    int updateAllVerifiedAsStartedWhenInTime();

    //到投标截止日, 更新投标阶段为选标阶段
    @Update("update tenderdeclaration as t set status='TENDER_CHOOSE_CONFIRM' where status='TENDER_START' and now()>tenderenddate and ((select count(1) from bid as b where b.`tenderdeclarationid`= t.`id` and b.`mytenderstatus`in ('MYTENDER_TENDERED_FREE', 'MYTENDER_TENDERED', 'MYTENDER_TENDERED_CONFIRM'))>0)")
    int updateAllVerifiedAsConfirmWhenInTime();

    //到开标日期, 有人投标, 招标人没有选标, 将该招标公告变为已作废
    @Update("update tenderdeclaration set status='TENDER_CANCEL' where status='TENDER_CHOOSE_CONFIRM' and  CURDATE()> contractconenddate")
    int updateAllVerifiedAsResultWhenInTime();

    //更新申请状态为已作废(如果当前时间大于投标截止时间)
    @Update("update tenderdeclaration set status='TENDER_CANCEL' where status='TENDER_APPLY' and now()>tenderenddate")
    int updateAllVerifiedAsEditWhenInTime();

    //投标时无人竞标, 投标截止日到, 将招标公告变为 已作废
    @Update("update tenderdeclaration as t set status='TENDER_CANCEL' where status='TENDER_START' and now()>tenderenddate and ((select count(1) from bid as b where b.`tenderdeclarationid`= t.`id` and b.`mytenderstatus`in ('MYTENDER_TENDERED_FREE', 'MYTENDER_TENDERED', 'MYTENDER_TENDERED_CONFIRM'))=0)")
    int updateAllVerifiedAsTenderCancel();

    //招标截止日期到, 将暂缓状态的投标改为 错过投标
    @Update("update mytender as m set status='MYTENDER_MISSING' where status='MYTENDER_EDIT' and now()> (select t.tenderenddate from tenderdeclaration as t where t.id=m.`tenderdeclarationid`)")
    int updateAllVerifiedAsMissing();

    //招标截止日期到, 将暂缓状态的投标改为 错过投标
    @Update("update bid as b set mytenderstatus='MYTENDER_MISSING' where mytenderstatus='MYTENDER_EDIT' and now()> (select t.tenderenddate from tenderdeclaration as t where t.id=b.`tenderdeclarationid`)")
    int updateAllVerifiedBidMissing();

    //更新已投标为待选标(不需要支付凭证,当前系统时间大于投标截止时间)
    @Update("update mytender as m set status='MYTENDER_CHOOSE_FREE' where status='MYTENDER_TENDERED_FREE' and now()> (select t.tenderenddate from tenderdeclaration as t where t.id=m.`tenderdeclarationid`) and CURDATE()<= (select contractconenddate  from tenderdeclaration as t where t.id=m.`tenderdeclarationid`)")
    int updateAllVerifiedAsWaitChooseFree();

    //更新已投标为待选标(不需要支付凭证,当前系统时间大于投标截止时间)
    @Update("update bid as b set mytenderstatus='MYTENDER_CHOOSE_FREE' where mytenderstatus='MYTENDER_TENDERED_FREE' and now()> (select t.tenderenddate from tenderdeclaration as t where t.id=b.`tenderdeclarationid`) and CURDATE()<= (select contractconenddate from tenderdeclaration as t2 where t2.id=b.`tenderdeclarationid`)")
    int updateAllVerifiedBidWaitChooseFree();

    //更新已投标(支付凭证审核未通过)为待选标(支付凭证审核未通过,当前系统时间大于选标时间)
    @Update("update mytender as m set status='MYTENDER_WAITING_CHOOSE' where status='MYTENDER_TENDERED' and now()> (select t.tenderenddate from tenderdeclaration as t where t.id=m.`tenderdeclarationid`) and CURDATE()< (select starttenderdate from tenderdeclaration as t where t.id=m.`tenderdeclarationid`)")
    int updateAllVerifiedAsWaitChoose();

    @Update("update bid as b set mytenderstatus='MYTENDER_WAITING_CHOOSE' where mytenderstatus='MYTENDER_TENDERED' and now()> (select t.tenderenddate from tenderdeclaration as t where t.id=b.`tenderdeclarationid`) and CURDATE()< (select starttenderdate from tenderdeclaration as t where t.id=b.`tenderdeclarationid`)")
    int updateAllVerifiedBidWaitChoose();

    //更新已投标(支付凭证审核通过)为待选标(支付凭证审核通过,当前系统时间大于选标时间)
    @Update("update mytender as m set status='MYTENDER_CHOOSE' where status='MYTENDER_TENDERED_CONFIRM' and now()> (select t.tenderenddate from tenderdeclaration as t where t.id=m.`tenderdeclarationid`) and CURDATE() < (select starttenderdate from tenderdeclaration as t where t.id=m.`tenderdeclarationid`)")
    int updateAllVerifiedAsChoose();

    @Update("update bid as b set mytenderstatus='MYTENDER_CHOOSE' where mytenderstatus='MYTENDER_TENDERED_CONFIRM' and now()> (select t.tenderenddate from tenderdeclaration as t where t.id=b.`tenderdeclarationid`)  and CURDATE()< (select starttenderdate from tenderdeclaration as t where t.id=b.`tenderdeclarationid`)")
    int updateAllVerifiedBidChoose();

    //更新待选标(支付凭证审核通过)为未中标(当前系统时间大于开标日期)
    @Update("update mytender as m set status='MYTENDER_FAIL' where status in ('MYTENDER_CHOOSE_FREE', 'MYTENDER_CHOOSE','MYTENDER_WAITING_CHOOSE') and CURDATE()> (select t.contractconenddate from tenderdeclaration as t where t.id=m.`tenderdeclarationid`)")
    int updateAllVerifiedAsFail();

    @Update("update bid as b set mytenderstatus='MYTENDER_FAIL' where mytenderstatus in ('MYTENDER_CHOOSE_FREE', 'MYTENDER_CHOOSE','MYTENDER_WAITING_CHOOSE') and CURDATE()> (select t.contractconenddate from tenderdeclaration as t where t.id=b.`tenderdeclarationid`)")
    int updateAllVerifiedBidFail();

    //更新待选标(支付凭证审核未通过)为已作废(当前系统时间大于开标日期)
    @Update("update mytender as m set status='MYTENDER_CANCEL' where status='MYTENDER_WAITING_CHOOSE' and CURDATE()> (select contractconenddate from tenderdeclaration as t where t.id=m.`tenderdeclarationid`)")
    int updateAllVerifiedAsCancel();

    //更新待选标(支付凭证审核未通过)为已作废(当前系统时间大于开标日期)
    @Update("update bid as b set mytenderstatus='MYTENDER_CANCEL' where mytenderstatus='MYTENDER_WAITING_CHOOSE' and CURDATE()> (select contractconenddate from tenderdeclaration as t where t.id=b.`tenderdeclarationid`)")
    int updateAllVerifiedBidCancel();
}
