package kitt.site.controller;

import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.FileStore;
import kitt.core.util.PageQueryParam;
import kitt.ext.mybatis.Where;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.BeanValidators;
import kitt.site.service.Session;
import kitt.site.service.TenderApplyService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by lich on 15/11/11.
 */
@Controller
@RequestMapping("/tender")
public class InvitationTenderController {
    @Autowired
    private Session session;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private TenderitemMapper tenderitemMapper;
    @Autowired
    private TenderpacketMapper tenderpacketMapper;
    @Autowired
    private TenderApplyService tenderApplyService;
    @Autowired
    protected DemandMapper demandMapper;
    @Autowired
    private TenderQuotesMapper tenderQuotesMapper;
    @Autowired
    private MytenderMapper mytenderMapper;
    @Autowired
    private IndexBannerMapper indexBannerMapper;
    @Autowired
    private MyInterestMapper myInterestMapper;
    @Autowired
    private BidMapper bidMapper;

    @RequestMapping(value = "/findDeclarById")
    public  String findDeclarById(@RequestParam(value = "id", required = true)int id,Map<String, Object> model){
        TenderDeclaration tenderdeclar= findDeclarById(id);
        if (tenderdeclar == null) throw new NotFoundException();
        tenderdeclar.setFocus((session != null && session.getUser() != null && myInterestMapper.getMyInterestBySid(tenderdeclar.getId(), session.getUser().getId(), "tender") != null && !myInterestMapper.getMyInterestBySid(tenderdeclar.getId(), session.getUser().getId(), "tender").isIsdelete()) == true ? 1 : 0);
        if (session.getUser() != null) {
            List<TenderQuotes> list = tenderQuotesMapper.selTenderquote(id, session.getUser().getId());
            if (tenderdeclar.getUserid() == session.getUser().getId()) {
                model.put("flag", "0");
            } else {
                model.put("flag", "1");
            }
        } else {
            tenderdeclar.setFocus(0);
            model.put("flag", "0");
        }
        model.put("tenderdeclar", tenderdeclar);
        Company c = tenderdeclarMapper.findCompanyByDeId(id);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (c != null) {
            model.put("logopic", c.getBannerpic());
        }
        model.put("serverdatetime", df.format(new Date()));
        model.put("tdCount", mytenderMapper.findCount(id));
        model.put("tenderid", id);
        return "/tender/tenderDetail";
    }

    /**
     * 检查用户是否对此投标公告投标
     * @param tenderid
     * @return
     */
    @RequestMapping(value = "/havebid")
    @ResponseBody
    @LoginRequired
    public Object haveBid(@RequestParam(value = "tenderid", required = true)int tenderid) {
        List<Bid> bidList = bidMapper.getBidByTenderidUserid(tenderid, session.getUser().getId());
        Map<String, Object> map = new HashMap<>();
        String message = "";
        if (bidList==null || bidList.size()==0) {
            message = "notbid";
        } else{
            boolean isedit = false;
            for(Bid bid : bidList) {
                if (bid.getMytenderstatus().equals(TenderStatus.MYTENDER_EDIT.toString())) {
                    isedit = true;
                    break;
                }
            }
            message = isedit ? "zhb" : "ytb";
        }
        map.put("message", message);
        return map;
    }


    public TenderDeclaration findDeclarById(int id){
        tenderdeclarMapper.updateDeclarScantimes(id);
        TenderDeclaration tenderdeclar= tenderdeclarMapper.findTendDeclarById(id);
        if(tenderdeclar!=null){
            List<String> statusList = Arrays.asList(new String[]{TenderStatus.TENDER_START.name(), TenderStatus.TENDER_VERIFY_PASS.name(), TenderStatus.TENDER_CHOOSE_CONFIRM.name(), TenderStatus.TENDER_RELEASE_RESULT.name() });
            if(!statusList.contains(tenderdeclar.getStatus())) throw new NotFoundException("该招标已经作废!");
            List<TenderItem>  itemList=tenderitemMapper.findTendItemByDecalarId(id);
            for(TenderItem item:itemList){
                List<TenderPacket> packetList= tenderpacketMapper.findTendpackgeByItemId(item.getId());
                item.setPacketList(packetList);
            }
            tenderdeclar.setItemList(itemList);
            return tenderdeclar;
        } else {
            throw new NotFoundException();
        }
    }

    @LoginRequired
    @RequestMapping(value = "/addMyFocus",method = RequestMethod.POST)
    @ResponseBody
    public boolean addMyFocus(TenderQuotes tenderQuotes){
        List<TenderQuotes> list=   tenderQuotesMapper.selTeQuote(tenderQuotes.getTenderdeclarationid(), session.getUser().getId());
        if(list!=null&&list.size()>0){
            int i=0;
            //修改状态
            if(list.get(0).getIsdelete()==0){
                i=1;
            }
            tenderQuotesMapper.updateQuoteStatusBy(i,tenderQuotes.getTenderdeclarationid(),session.getUser().getId());
        } else {
            String companyName = tenderdeclarMapper.findCompanyName(tenderQuotes.getTenderdeclarationid());
            tenderQuotes.setCompanyname(companyName);
            tenderQuotes.setUserid(session.getUser().getId());
            tenderQuotes.setStatus(TenderStatus.TENDER_FOCUS_TRUE.toString());
            tenderQuotes.setCreatetime(LocalDateTime.now());
            BeanValidators.validateWithException(tenderQuotes);
            int id = tenderQuotesMapper.addTenderquote(tenderQuotes);
        }
        return true;
    }

    @RequestMapping(value = "/downloadTender")
    public HttpEntity<byte[]>  download(@RequestParam(value = "path", required = true)String path,
                                        HttpServletResponse response) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",path);
        return new HttpEntity<byte[]>(FileUtils.readFileToByteArray(fileStore.getFileByFilePath(path)), headers);
    }


    @RequestMapping(value = "")
     public  String  findDeclarByConditions(@RequestParam(value = "status", required = false)String status,
                                            @RequestParam(value = "companyName", required = false)String companyName,
                                            Map<String, Object> model){
        int[] arr=new int[]{1,2,3,4};
        if(status!=null&&!status.equals("")){
            arr=sortStatus(status);
        }
        String cname=companyName;
        if(cname!=null&&!cname.equals("")){
            cname=companyName.trim();
        }
       List<TenderDeclaration> declarationCount= tenderdeclarMapper.findTenderDecalrCount(status, Where.$like$(cname));
        if(declarationCount!=null&&declarationCount.size()>0){
            for(TenderDeclaration declaration:declarationCount){
                List<TenderDeclaration> declarationList= tenderdeclarMapper.findTenderDecalrByCompanyId(declaration.getCompanyid(), status,arr[0], arr[1],arr[2],arr[3]);
                if(declarationList!=null&&declarationList.size()>0){
                    for(int i=0;i<declarationList.size();i++){
                        List<TenderPacket> packetList= tenderpacketMapper.findTendpackgeByDecalId(declarationList.get(i).getId());
                        if(packetList!=null&&packetList.size()>0) {
                            declarationList.get(i).setCoaltype(packetList.get(0).getCoaltype()); //煤种
                            declarationList.get(i).setNCV(packetList.get(0).getNCV());       //NCV
                        }
                        if(declarationList.get(i).getStatus().equals("TENDER_RELEASE_RESULT")){
                            declarationList.get(i).setZbCount(mytenderMapper.findBidCount(Integer.parseInt(String.valueOf(declarationList.get(i).getId()))));
                        }
                    }
                    declaration.setDeclarNum(tenderdeclarMapper.countTenderDecalrByCompanyId(declaration.getCompanyid(), status));

                }
                declaration.setdList(declarationList);
            }
        }

        //获取轮播banner图片
        List<IndexBanner> indexBannerList = indexBannerMapper.getIndexBannnersWithLimit("tenderbanner", 5);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        model.put("serverdatetime",df.format(new Date())); //系统时间
        model.put("declarationList",declarationCount);
        model.put("status",status);
        model.put("companyName",companyName);
        model.put("indexBannerList",indexBannerList);
         return "/tender/invitation";
     }


        public int[] sortStatus(String status){
               int[] arr=new int[4];
            if(status.equals("TENDER_START")){
                arr[0]=1;
                arr[1]=2;
                arr[2]=3;
                arr[3]=4;
            }else if(status.equals("TENDER_VERIFY_PASS")){
                arr[0]=2;
                arr[1]=1;
                arr[2]=3;
                arr[3]=4;
            }else if(status.equals("TENDER_CHOOSE_CONFIRM")){
                arr[0]=2;
                arr[1]=3;
                arr[2]=1;
                arr[3]=4;
            }else if(status.equals("TENDER_RELEASE_RESULT")){
                arr[0]=2;
                arr[1]=3;
                arr[2]=4;
                arr[3]=1;
            }
                return arr;

      }


    @RequestMapping(value = "/toTenderList")
    public  String  findDeclarByCompanyName(@RequestParam(value = "companyid", required = true)int companyid,
                                            @RequestParam(value = "status", required = false,defaultValue = "")String status,
                                            @RequestParam(value = "time", required = false,defaultValue = "")String time,
                                            @RequestParam(value = "coaltype", required = false,defaultValue = "")String coaltype,
                                            @RequestParam(value = "NCV", required = false,defaultValue = "")String NCV,
                                           Map<String, Object> model,PageQueryParam param){
        int[] str=new int[]{0,0};
        int min=0;
        int max=0;
//       if(NCV!=null&&!NCV.equals("")&&!NCV.equals("全部")&&!NCV.equals("3000以下")&&!NCV.equals("6000以上")){
//           str[0]=Integer.parseInt(NCV.split("-")[0]);
//           str[1]=Integer.parseInt(NCV.split("-")[1]);
//       }

        if(NCV!=null&&NCV.equals("3500-4500")||NCV.equals("4000-4500")||NCV.equals("5000-5500")||NCV.equals("5500-6000")){
            str[0]=Integer.parseInt(NCV.split("-")[0]);
            str[1]=Integer.parseInt(NCV.split("-")[1]);
        }

        if(NCV!=null){
            if(NCV.equals("3000以下")){
                min=Integer.parseInt(NCV.substring(0,4));
            }
            if(NCV.equals("6000以上")){
                max=Integer.parseInt(NCV.substring(0,4));
            }
        }

        List<TenderDeclaration>  declarationList=tenderdeclarMapper.findTenders(status, companyid, time);
        List<TenderDeclaration>  deleteList=new ArrayList<TenderDeclaration>();
        if(declarationList!=null&&declarationList.size()>0){
            for(TenderDeclaration declaration:declarationList){
                declaration.setFlag(false);
                List<TenderItem>  itemList=tenderitemMapper.findTendItemByDecalarId(declaration.getId());
                declaration.setItemList(itemList);
                if(itemList!=null&&itemList.size()>0){
                    for(TenderItem item:itemList){
                        List<TenderPacket> packetList= tenderpacketMapper.findTendpackgeByConditions(item.getId(),coaltype,str[0],str[1],min,max);
                        item.setPacketList(packetList);
                        if(packetList!=null&&packetList.size()>0){
                            declaration.setFlag(true);
                        }
                    }
                }else{
                    declaration.setFlag(false);
                }
            }
        }
        if(declarationList!=null&&declarationList.size()>0){
            for(TenderDeclaration declaration:declarationList){
                if(!declaration.isFlag()){
                    deleteList.add(declaration);
                }
            }
        }
        declarationList.removeAll(deleteList);
        List<TenderDeclaration> pagerList=null;
        List<Integer> idlist=new ArrayList<Integer>();
        if(declarationList!=null&&declarationList.size()>0){
            for(TenderDeclaration tenderDeclaration:declarationList){
                idlist.add(tenderDeclaration.getId());
            }
             pagerList=  tenderdeclarMapper.findTenderDecalrByCompanyName(idlist.size(),idlist, param.getPagesize(), param.getIndexNum());
            for(TenderDeclaration declaration:pagerList){
                List<TenderPacket> list= tenderpacketMapper.findTendpackgeByDeclarId1(declaration.getId(), str[0], str[1],min,max);
//                Admin admin=  tenderdeclarMapper.findUserInfoByDecalrId(declaration.getId());
                if(list!=null&&list.size()>0){
                    declaration.setCoaltype(list.get(0).getCoaltype());  //煤种
                    declaration.setNCV(list.get(0).getNCV());      //低位热值
                }

                        declaration.setUserName(declaration.getTradername());  //交易员名字
                        declaration.setUserPhone(declaration.getTraderphone());  //电话

                    if(declaration.getStatus().equals("TENDER_START")||declaration.getStatus().equals("TENDER_CHOOSE_CONFIRM")){
                        declaration.setTdCount(mytenderMapper.findCount(declaration.getId()));
                    }else if(declaration.getStatus().equals("TENDER_RELEASE_RESULT")){
                        declaration.setDecalarBids(mytenderMapper.findDecalrBid(declaration.getId()));
                    }
            }
        }

        //查找所有的中标记录
         List<Map<String,Object>> bidList= tenderdeclarMapper.findBidRecords();
        if(bidList!=null&&bidList.size()>0){
            for(int i=0;i<bidList.size();i++){
                int bid= Integer.parseInt(String.valueOf(bidList.get(i).get("bid")));
                List<Mytender> l=mytenderMapper.findByBidAndStatus(bid);
                if(l!=null&&l.size()>0){
                    bidList.get(i).put("NCV",l.get(0).getNCV());
                    bidList.get(i).put("coalType",l.get(0).getCoaltype());
                    BigDecimal needamout= new BigDecimal("0");
                    for(int j=0;j<l.size();j++){
                        needamout= needamout.add(l.get(j).getNeedamount());

                    }
                    bidList.get(i).put("needamount", needamout);

                }

            }
        }

        int totalCount=0;
        if(declarationList!=null){
            totalCount = declarationList.size();
        }
        param.setCount(totalCount);
        model.put("count", totalCount);
        model.put("pagesize", param.getPagesize());
        if(totalCount==0){
            model.put("page", 0);
        }else{
            model.put("page", param.getPage());
        }
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        model.put("totalPage", totalPage);
        model.put("declarationList", pagerList);
        model.put("companyid", companyid);
        model.put("status", status);
        model.put("time", time);
        model.put("coaltype", coaltype);
        model.put("NCV", NCV);
        Company c= tenderdeclarMapper.findCompanyById(companyid);
        if(c!=null){
            model.put("companyPic", c.getBannerpic());   //公司图片
        }
        model.put("dealList", bidList);//报价成交记录展示
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        model.put("serverdatetime", df.format(new Date())); //系统时间
        return "/tender/tenderList";
    }


    @LoginRequired
    @RequestMapping(value = "/toTenderDecalr")
    public  String  toTenderDecalr(@RequestParam(value = "id", required = true)int id, Map<String, Object> model){
        TenderDeclaration tenderDeclaration= tenderdeclarMapper.findTenderDeclarByIdAndUserId(id, session.getUser().getId());
       if(tenderDeclaration==null)
           throw  new NotFoundException();
        TenderDeclaration tenderdeclar= tenderApplyService.findDeclarById(id);
        model.put("tenderdeclar",tenderdeclar);
        return "/person/mztenderDetail";

    }









}
