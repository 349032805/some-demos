package kitt.site.service.mobile;

import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.ConfigConsts;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 15-5-18.
 */
@Service
@Transactional(readOnly = true)
public class DemandService {


    @Autowired
    private DemandMapper demandMapper;
    @Autowired
    protected MydemandMapper mydemandMapper;
    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private MyInterestMapper watchMapper;
    @Autowired
    private QuoteMapper quoteMapper;
    @Autowired
    protected MyInterestMapper myInterestMapper;

    @Autowired
    private Session session;

    //加载煤炭种类
    public List<Dictionary> loadCoalTypes() {
        return dictionaryMapper.getAllCoalTypes();
    }

    //加载提货方式
    public List<Dictionary> loadDeliveryModes() {
        return dictionaryMapper.getDeliverymodes();
    }

    //加载检验机构
    public List<Dictionary> loadAllInspectionagencys() {
        return dictionaryMapper.getAllInspectionagencys();
    }

    //价值需求列表
    public PageQueryParam loadDemandData(PageQueryParam queryParam, Integer provinceId, Integer portId, Integer lowNCV, Integer highNCV, BigDecimal lowRS, BigDecimal highRS, String coalType,String anchor) {
        int totalCount = demandMapper.demandCount(provinceId,portId, lowNCV, highNCV, lowRS, highRS, coalType);
        List<Demand> demands = demandMapper.demandList(queryParam,provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType,anchor);
        int totalPage = totalCount / queryParam.getPagesize();
        totalPage = totalCount % queryParam.getPagesize() == 0 ? totalPage : totalPage + 1;
        queryParam.setTotalCount(totalCount);
        queryParam.setTotalPage(totalPage);
        queryParam.setList(demands);
        if(StringUtils.isNotBlank(anchor)){
            queryParam.setPage(queryParam.getIndexNum()/queryParam.getPagesize());
        }

        return queryParam;
    }


    @Transactional(readOnly = false)
    public void addDemand(Demand demand) {
        int userId = session.getUser().getId();
        Company company = companyMapper.getCompanyByUserid(session.getUser().getId());
        //设置需求所属区域、省份、港口
        String regionName = areaportMapper.getAreaByProvinceId(demand.getProvinceId()).getName();
        String provinceName = areaportMapper.getNameById(demand.getProvinceId());
        String portName = demand.getPortId() == -1 ? "其它" : areaportMapper.getNameById(demand.getPortId());
        demand.setDeliverydistrict(regionName);
        demand.setDeliveryprovince(provinceName);
        demand.setDeliveryplace(portName);
        demand.setRegionId(areaportMapper.getParentidById(demand.getProvinceId()));
        // 需求所属公司名称
        demand.setDemandcustomer(company.getName());
        demand.setUserid(userId);
        demand.setNoshowdate(demand.getQuoteenddate().plusDays(7));
        demand.setReleasetime(LocalDateTime.now());
        demand.setCheckstatus(EnumDemand.CheckStatus_WaitVerify.value);
        demand.setTradestatus(EnumDemand.TradeStatus_QuoteNotStart.value);
        demand.setClienttype(ConfigConsts.Weixin);
      //替换指标区间值，第一个值比第二个值大的情况
       SupplyService.replaceValue(demand);
        demandMapper.addDemand(demand);
    }


    @Transactional(readOnly = false)
    public void saveMyDemand(Demand demand) {
        Mydemand mydemand = new Mydemand();
        mydemand.setReleasetime(LocalDateTime.now());
        mydemand.setDemandcode(demand.getDemandcode());
        mydemand.setUserid(demand.getUserid());
        mydemand.setDemandamount(demand.getDemandamount());
        mydemand.setStatus(EnumDemand.MydemandStatus_WaitVerify.value);
        mydemand.setQuoteenddate(demand.getQuoteenddate());
        mydemand.setDeliverydate(demand.getDeliverydate());
        mydemand.setDeliverydatestart(demand.getDeliverydatestart());
        mydemand.setDeliverydateend(demand.getDeliverydateend());
        //修改demand状态为可用,状态改为待审核,releasestatus设置为1
        demandMapper.updateDemandAvailable(demand.getDemandcode());
        //判断是save or update
        if (null == mydemandMapper.getMydemandByDemandcode(demand.getDemandcode())) {
            mydemandMapper.addMydemand(mydemand);
        } else {
            mydemandMapper.modifyMyDemand(mydemand);
        }
    }

    public Demand loadDemandDeatil(int id) {
        Demand demand = demandMapper.getDemandByIddelete(id);
        if (demand == null) {
            throw new NotFoundException();
        }
        return demand;
    }


    public Mydemand loadMyDemand(String demandCode) {
        return mydemandMapper.getMydemandByDemandcode(demandCode);
    }


    //修改需求
    @Transactional(readOnly = false)
    public void updateDemand(Demand demand) {
        int userId = session.getUser().getId();
        Company company = companyMapper.getCompanyByUserid(session.getUser().getId());
        //设置需求所属区域、省份、港口
        String regionName = areaportMapper.getAreaByProvinceId(demand.getProvinceId()).getName();
        String provinceName = areaportMapper.getNameById(demand.getProvinceId());
        String portName = demand.getPortId() == -1 ? "其它" : areaportMapper.getNameById(demand.getPortId());
        demand.setDeliverydistrict(regionName);
        demand.setDeliveryprovince(provinceName);
        demand.setDeliveryplace(portName);
        demand.setRegionId(areaportMapper.getParentidById(demand.getProvinceId()));
        // 需求所属公司名称
        demand.setDemandcustomer(company.getName());
        demand.setUserid(userId);
        demand.setNoshowdate(demand.getQuoteenddate().plusDays(7));
        demand.setReleasetime(LocalDateTime.now());
        demand.setTradestatus(EnumDemand.TradeStatus_QuoteNotStart.value);
        demandMapper.modifyDemand(demand);
    }

    public Demand loadDemandDetail(int demandId, int userId) {
        Demand demand = demandMapper.getDemandByIdAndUserId(demandId, userId);
        if (demand == null) {
            throw new NotFoundException();
        }
        return demand;
    }

    public Demand loadDemandDetailForDemandCode(String demandcode, int userId) {
        Demand demand = demandMapper.getDemandByDemandcodeAndUserid(demandcode, userId);
        if (demand == null) {
            throw new NotFoundException();
        }
        return demand;
    }

    /**
     * @param userId   用户id
     * @param demandId 需求id
     * @return
     */
    @Transactional(readOnly = false)
    public boolean watchDemand(int userId, int demandId) {
        final String type = "demand";
        Demand demand = loadDemandDeatil(demandId);
        MyInterest myInterest = new MyInterest();
        myInterest.setUserid(userId);
        myInterest.setSid(demandId);
        myInterest.setPid(demand.getDemandcode());
        myInterest.setNCV(demand.getNCV().toString());
        myInterest.setType(type);
        myInterest.setSeller(demand.getDemandcustomer());
        //需求code
        myInterest.setPid(demand.getDemandcode());
        //煤炭种类
        myInterest.setPname(demand.getCoaltype());
        //需求数量
        myInterest.setAmount(demand.getDemandamount());
        if(demand.getNCV()!=0&&demand.getNCV02()!=0){
          myInterest.setNCV(demand.getNCV()+"-"+demand.getNCV02());
        }
        watchMapper.addMyInterest(myInterest);

        return true;
    }
    @Transactional(readOnly = false)
    public void addMyWatch(int demandId, int userId, String type) {
        //如果没有关注 insert
        MyInterest myInterest=watchMapper.getMyInterestBySid(demandId, userId, type);
        if(null==myInterest){
            watchDemand(userId,demandId);
        }else{
            //状态改为取消关注
            watchMapper.setMyInterestStatusBySid(demandId,userId,type);
        }
    }

    //对需求进行报价
    @Transactional(readOnly = false)
    public void demandQuote(User user, Demand demand, Company company, Quote quote) {
        Quote quoteselect = quoteMapper.getQuoteByUserIdAndDemandcode(user.getId(), demand.getDemandcode());
        if(quoteselect==null) {
            //对需求进行报价
            quote.setUserid(user.getId());
            //quote.setSupplyton(supplyton);
            quote.setDemandid(demand.getId());
            quote.setCompanyname(company.getName());
            quote.setStatus(EnumDemand.MydemandStatus_QuoteStart.value);
            quote.setDeliverymode(demand.getDeliverymode());
            //报价
//            quote.setQuote(unitPirce);
            //报价截止时间
            quote.setQuoteenddate(demand.getQuoteenddate());
            //提货时间
            quote.setDeliverydate(demand.getDeliverydate());
            quote.setTraderphone(demand.getTraderphone());
            quote.setTradername(demand.getTradername());
            quote.setDemandcode(demand.getDemandcode());
            quote.setLastupdatetime(LocalDateTime.now());
            quote.setClienttype(ConfigConsts.Weixin);

            quoteMapper.addQuote(quote);
            demandMapper.plusQuotenum(demand.getDemandcode());
            mydemandMapper.plusQuotenum(demand.getDemandcode());
        }else {
            quote.setId(quoteselect.getId());
            quoteMapper.modifyQuoteByQuoteid(quote);
        }
    }





    /* 个人中心我的关注查询
    * @param
    * @return   PageQueryParam
    */
    public PageQueryParam loadMyInterestList(PageQueryParam param, String type, User user) {
        List<MyInterest> myInterestList = myInterestMapper.getMyInterestList(type, user.getId(), param.getPagesize(), param.getIndexNum());
        if (myInterestList == null)
            throw new NotFoundException();
        int totalCount = myInterestMapper.getMyInterestCount(type, user.getId());
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(myInterestList);
        return param;
    }

    /* 个人中心删除我的关注
    * @param
    * @return   PageQueryParam
    */
    @Transactional(readOnly = false)
    public void mCancelMyInterest(int id) {
        MyInterest myInterest = myInterestMapper.getMyInterestById(id);
        if (myInterest == null)
            throw new NotFoundException();
        myInterestMapper.cancelMyInterest(id);
    }


    /**
     * 个人中心-查询我的需求列表
     *
     * @param param
     * @param user
     * @return
     */
    public PageQueryParam getMyDemand(PageQueryParam param, User user) {
        List<Mydemand> mydemandList = mydemandMapper.getTurnpageListWithUserid(user.getId(), param.getIndexNum(), param.getPagesize());
        int totalCount = mydemandMapper.countAllMydemands(user.getId());
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(mydemandList);
        return param;
    }

    /**
     * 个人中心-我的需求详细信息
     *
     * @param demandCode
     * @return
     */
    public Demand getDemand(String demandCode) {
        Demand demand = demandMapper.getDemandByDemandcode(demandCode);
        if(demand==null)
            throw new NotFoundException();
        return demand;
    }

    public Demand getDemandByDemandId(int demandId) {
        Demand demand = demandMapper.getDemandByDemandId(demandId);
        if(demand==null)
            throw new NotFoundException();
        return demand;
    }




    public List<Quote> getQuoteList(String demandCode) {
        List<Quote> quoteList = quoteMapper.getQuoteByDemandcode(demandCode);
        return quoteList;
    }

    /**
     * 个人中心-查看需求对应的报价
     * @param userid
     * @param demandid
     * @return
     */
    public Quote getQuote(int userid, int demandid) {
       Quote quote=  quoteMapper.getQuoteByUserIdAndDemandid(userid, demandid);
        if(quote==null)
            throw new NotFoundException();
        return quote;
    }

    public Quote getQuote(int quoteid) {
        Quote quote=  quoteMapper.getQuoteById(quoteid);
        if(quote==null)
            throw new NotFoundException();
        return quote;
    }

    /**
     * 个人中心-我的需求删除
     *
     * @param user
     * @param demandCode
     */
    @Transactional(readOnly = false)
    public void deleteMydemand(User user, String demandCode) {
        mydemandMapper.deleteMyDemandByDemandcode(demandCode, user.getId());
    }

    /**
     * 我的需求-取消发布
     *
     */
    @Transactional(readOnly = false)
    public void cancelMydemand(Demand demand) {
        //把状态设置为已删除
        demandMapper.modifyIsdeleteByDemandcodeAndUserid(demand.getDemandcode(),demand.getUserid());
        //个人中心mydemand状态设置为交易结束
        mydemandMapper.modifyStatusByDemandcodeAndUserid("交易结束", demand.getDemandcode(), demand.getUserid());
    }

    /**
     * 个人中心-我的需求-报价中标
     *
     * @param quoteid
     */
    @Transactional(readOnly = false)
    public void quoteBid(int quoteid) {
        Quote quote = quoteMapper.getQuoteById(quoteid);
        if (quote == null)
            throw new NotFoundException();
        quoteMapper.modifyStatusByQuoteid("已中标", quoteid);

        demandMapper.modifyPurchaseNumByDemandcode(quote.getSupplyton(),quote.getDemandcode());
        demandMapper.modifyStatusByDemandCode("已中标",quote.getDemandcode());
        mydemandMapper.modifyPurchaseNumByDemandcode(quote.getSupplyton(), quote.getDemandcode());

        if(quoteMapper.getCountNotBidByDemandcode(quote.getDemandcode())==0){//全部都报价了，则将我的需求状态改为匹配结束
            mydemandMapper.modifyStatusByDemandCode("匹配结束",quote.getDemandcode());
        }
    }


}
