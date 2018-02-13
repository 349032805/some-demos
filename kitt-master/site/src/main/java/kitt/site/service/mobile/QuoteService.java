package kitt.site.service.mobile;

import kitt.core.domain.*;
import kitt.core.persistence.AreaportMapper;
import kitt.core.persistence.BuyMapper;
import kitt.core.persistence.DemandMapper;
import kitt.core.persistence.QuoteMapper;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xiangyang on 15-5-18.
 */
@Service
@Transactional(readOnly = true)
public class QuoteService {

    @Autowired
    private QuoteMapper quoteMapper;

    @Autowired
    private DemandMapper demandMapper;

    //检查是否已经报价
    public boolean checkRepeatQuote(int usreId,int demandId){
        Quote q = quoteMapper.getQuoteByUserIdAndDemandid(usreId,demandId);
        if(q!=null){
            return false;
        }
        return true;
    }
    public boolean checkSelfQuote(int usreId,String demandCode){
        Demand demand= demandMapper.getDemandByDemandcodeAndUserid(demandCode,usreId);
        if(demand!=null){
            return false;
        }
        return true;
    }

    /**
     * 个人中心-我的报价查询-进行中
     * @param param
     * @param user
     * @return
     */
    public PageQueryParam loadTurnpageUnderwayList(PageQueryParam param,User user){
        List<Quote> quoteList= quoteMapper.getTurnpageUnderwayList(user.getId(), param.getIndexNum(), param.getPagesize());
        //quoteList=mergeQuoteDemandStatus(quoteList);
        int totalCount= quoteMapper.countAllMyQuoteUnderway(user.getId());
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(quoteList);
        return param;
    }

//    //同步报价与需求状态
//    @Transactional(readOnly = false)
//    public List<Quote> mergeQuoteDemandStatus(List<Quote> quoteList){
//        for(Quote quote:quoteList){
//            Demand demand= demandMapper.getDemandById(quote.getDemandid());
//            if(demand==null)
//                throw new NotFoundException();
//            if(demand.getTradestatus().equals("报价结束")){
//                quote.setStatus("未中标");
//                quoteMapper.modifyStatusByQuoteid("未中标",quote.getId());
//            }
//        }
//        return quoteList;
//    }

    /**
     * 个人中心-我的报价查询-已中标
     * @param param
     * @param user
     * @return
     */
    public PageQueryParam loadMyQuoteBid(PageQueryParam param,User user){
        List<Quote> quoteList= quoteMapper.getTurnpageBidList(user.getId(), param.getIndexNum(), param.getPagesize());
        //quoteList=mergeQuoteDemandStatus(quoteList);
        int totalCount= quoteMapper.countAllMyQuoteBid(user.getId());
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(quoteList);
        return param;
    }

    /**
     * 个人中心-我的报价查询-未中标
     * @param param
     * @param user
     * @return
     */
    public PageQueryParam loadGetMyQuoteNotBid(PageQueryParam param,User user){
        List<Quote> quoteList= quoteMapper.getTurnpageNotBidList(user.getId(), param.getIndexNum(), param.getPagesize());
        //quoteList=mergeQuoteDemandStatus(quoteList);
        int totalCount= quoteMapper.countAllMyQuoteNotBid(user.getId());
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(quoteList);
        return param;
    }

    /**
     * 个人中心-我的报价-删除
     * @param id
     * @param user
     */
    @Transactional(readOnly = false)
    public void deleteMyquote(int id,User user){
        Quote quote= quoteMapper.getQuoteById(id);
        if(quote==null)
            throw new NotFoundException();
        quoteMapper.modifyIsdeleteById(id,user.getId());
    }




}
