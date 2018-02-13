package kitt.site.controller;

import com.fasterxml.jackson.databind.JavaType;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.FileStore;
import kitt.core.service.Freemarker;
import kitt.core.service.PDF;
import kitt.core.util.JsonMapper;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.BeanValidators;
import kitt.site.service.MyTenderService;
import kitt.site.service.TenderApplyService;
import kitt.site.service.TenderInviteService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by zhangbolun on 15/11/14.
 */
@LoginRequired
@Controller
@RequestMapping("/tender")
public class MyTenderController extends JsonController {
    @Autowired
    private MyTenderService myTenderService;
    @Autowired
    private TenderApplyService tenderApplyService;
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private TenderitemMapper tenderitemMapper;
    @Autowired
    private MytenderMapper mytenderMapper;
    @Autowired
    private Freemarker freemarker;
    @Autowired
    private PDF pdf;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private BidMapper bidMapper;
    @Autowired
    private TenderInviteService tenderInviteService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 进入开始投标页(无投标记录)
     * @param tenderid                    招标公告id
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String bidMyTender(@RequestParam("tenderid") int tenderid, Map<String, Object> model) {
        TenderDeclaration declaration = tenderdeclarMapper.findTendDeclarById(tenderid);
        if (declaration == null || !declaration.getStatus().equals(TenderStatus.TENDER_START.toString())) throw new NotFoundException("该招标不存在 或 已经过了投标时间!");
        List<Bid> bidList = bidMapper.getBidByTenderidUserid(tenderid, session.getUser().getId());
        if (bidList==null || bidList.size()==0) {
            TenderDeclaration tenderDeclaration = tenderApplyService.findDeclarewithEditTender(tenderid);
            Bid bid=new Bid();
            bid.setId(0);
            bid.setAttachmentfilename("");
            bid.setAttachmentpath("");
            String ItemListjson = JsonMapper.nonDefaultMapper().toJson(tenderDeclaration.getItemList());
            model.put("data", tenderDeclaration);
            model.put("ItemListjson", ItemListjson);
            model.put("bid", bid);
            model.put("first", "true");
        } else {
            editMyBidMethod(bidList.get(0).getId(), model);
        }
        return "tender/tender";
    }

    /**
     * 进入投标编辑页面
     * @param bidid 投标id
     * @return
     */
    @RequestMapping(value = "/editMyBid", method = RequestMethod.GET)
    public String editMyBid(@RequestParam("bidid") int bidid, Map<String, Object> model) {
        editMyBidMethod(bidid, model);
        return "tender/tender";
    }

    public void editMyBidMethod(int bidid, Map<String, Object> model) {
        Bid bid = bidMapper.getBidByIdUserid(bidid, session.getUser().getId());
        if (bid == null) throw new NotFoundException("该投标不存在!");
        List<String> editBidStatusList = Arrays.asList(TenderStatus.MYTENDER_EDIT.toString(), TenderStatus.MYTENDER_TENDERED.toString(), TenderStatus.MYTENDER_TENDERED_FREE.toString(), TenderStatus.MYTENDER_TENDERED_CONFIRM.toString());
        if (!editBidStatusList.contains(bid.getMytenderstatus())) throw new NotFoundException("该投标已经不能修改!");
        TenderDeclaration declaration = tenderdeclarMapper.findTendDeclarById(bid.getTenderdeclarationid());
        if (declaration == null || !declaration.getStatus().equals(TenderStatus.TENDER_START.toString())) throw new NotFoundException("该招标不存在 或 已经过了投标时间!");
        TenderDeclaration tenderDeclaration = tenderApplyService.findDeclarcDetailByDidId(bid.getTenderdeclarationid(), bidid);
        String ItemListjson = JsonMapper.nonDefaultMapper().toJson(tenderDeclaration.getItemList());
        model.put("data", tenderDeclaration);
        model.put("ItemListjson", ItemListjson);
        model.put("bid", bid);
    }

    /**
     * 保存我的投标(暂存草稿)
     * @param tenderid     公告id
     * @param mytenderlist 投标数据
     * @return
     */
    @RequestMapping(value = "/saveMyBid", method = RequestMethod.POST)
    @ResponseBody
    public Object saveMyBid(@RequestParam(value = "tenderid", required = true) int tenderid,
                            @RequestParam(value = "needpay", required = true) boolean needpay,
                            @RequestParam(value = "attachmentpath", required = false) String attachmentpath,
                            @RequestParam(value = "attachmentfilename", required = false) String attachmentfilename,
                            @RequestParam(value = "mytenderlist", required = false) String mytenderlist,
                            @CurrentUser User user) throws Exception {
        tenderApplyService.checkCompany();
        if (!tenderInviteService.tenderIsOpen(tenderid,user.getId())) {
            throw  new BusinessException("对不起，招标商已对投标规范进行限制,您没有权限进行投标！");
        }
        List<Mytender> mytenders = null;
        if (StringUtils.isNoneBlank(mytenderlist)) {
            JavaType javaType = JsonMapper.nonDefaultMapper().contructCollectionType(List.class, Mytender.class);
            mytenders = JsonMapper.nonDefaultMapper().fromJson(mytenderlist, javaType);
        }
        if (mytenders == null || mytenders.size() == 0) throw new BusinessException("提交参数失败");
        TenderDeclaration declaration = tenderdeclarMapper.findTendDeclarById(tenderid);
        if (declaration.getTenderbegindate().isAfter(LocalDateTime.now())) throw new BusinessException("此公告为非投标阶段");
        List<Bid> bidList = bidMapper.getBidByTenderidUserid(tenderid, session.getUser().getId());
        if (bidList != null && bidList.size() != 0) {
            for (Bid bid : bidList) {
                if (bid.getUserid() != session.getUser().getId() || !bid.getMytenderstatus().equals(TenderStatus.MYTENDER_EDIT.toString())) throw new BusinessException(EnumRemindInfo.Site_System_Error.value());
                myTenderService.deleteMyBid(bid.getId());
            }
        }
        myTenderService.addMyBidEdit(tenderid, attachmentpath, attachmentfilename, needpay, mytenders);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", "succeed");
        return map;
    }

    /**
     * 提交我的投标
     * @param tenderid     公告id
     * @param mytenderlist 投标数据
     * @return
     */
    @RequestMapping(value = "/submitMyBid", method = RequestMethod.POST)
    @ResponseBody
    public Object submitMyBid(@RequestParam(value = "bidid", required = false) int bidid,
                              @RequestParam(value = "needpay", required = true) boolean needpay,
                              @RequestParam(value = "tenderid", required = false) int tenderid,
                              @RequestParam(value = "attachmentpath", required = false) String attachmentpath,
                              @RequestParam(value = "attachmentfilename", required = false) String attachmentfilename,
                              @RequestParam(value = "mytenderlist", required = false) String mytenderlist,
                              @CurrentUser User user) throws Exception {
        tenderApplyService.checkCompany();
        if (!tenderInviteService.tenderIsOpen(tenderid,user.getId())) {
            throw  new BusinessException("对不起，招标商已对投标规范进行限制,您没有权限进行投标！");
        }
        List<Mytender> mytenders = null;
        if (StringUtils.isNoneBlank(mytenderlist)) {
            JavaType javaType = JsonMapper.nonDefaultMapper().contructCollectionType(List.class, Mytender.class);
            mytenders = JsonMapper.nonDefaultMapper().fromJson(mytenderlist, javaType);
        }
        if (mytenders == null || mytenders.size() == 0){
            throw new BusinessException("提交参数失败");
        }
        //后端验证数据正确性
        for(Mytender mytender:mytenders){
            BeanValidators.validateWithException(mytender);
        }
        TenderDeclaration declaration = tenderdeclarMapper.findTendDeclarById(tenderid);
        if (declaration == null) throw new NotFoundException();
        if (declaration.getTenderbegindate().isAfter(LocalDateTime.now())) throw new BusinessException("此招标为非投标阶段");

        Bid bidedit = bidMapper.getBidByDeclarIdUserid(tenderid, session.getUser().getId(), TenderStatus.MYTENDER_EDIT.name());
        if (bidedit != null) {
            if (bidedit.getUserid() != session.getUser().getId()) throw new BusinessException(EnumRemindInfo.Site_System_Error.value());
            myTenderService.deleteMyBid(bidedit.getId());
        }
        myTenderService.addMyBidCommit(bidid, tenderid, attachmentpath, attachmentfilename, needpay, mytenders);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", "succeed");
        return map;
    }


    /**
     * 选标
     * @param mytenderid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/chooseTender", method = RequestMethod.POST)
    @ResponseBody
    public Object chooseTender(@RequestParam("mytenderid") int mytenderid) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Mytender mytender = mytenderMapper.getMyTenderById(mytenderid);
        TenderDeclaration tenderDeclaration = tenderdeclarMapper.findTendDeclarById(mytender.getTenderdeclarationid());
        if(tenderDeclaration.getUserid()!=session.getUser().getId())
            throw new NotFoundException();

        myTenderService.chooseTender(mytenderid);
        map.put("success", true);
        return map;
    }


    /**
     * /下载投标书pdf(项目下的投标结果)
     * @param declareId
     * @param user
     * @return
     */
    @RequestMapping(value = "/downloadTenderPdf/{declareId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public HttpEntity<byte[]> downloadTenderPdf(@PathVariable("declareId") int declareId, @CurrentUser User user) {
        //公告
        TenderDeclaration tenderDeclaration = myTenderService.findByDeclareId(declareId, user.getId());

        List<Map<String, Object>> dataList = tenderitemMapper.findTenderInDeclare(user.getId(), declareId);
        if (dataList.size() == 0) {
            throw new NotFoundException("暂无数据");
        }
        try {
            String str = freemarker.render("contracts/tenderTemplate", new HashMap<String, Object>() {{
                put("companyName", tenderDeclaration.getTenderunits().replace("\"",""));
                put("month", tenderDeclaration.getContractconenddate().getMonthValue());
                put("tenderList", dataList);
            }});
            File file = pdf.createByHtml(str);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", tenderDeclaration.getTendercode() + ".pdf");
            return new HttpEntity<byte[]>(FileUtils.readFileToByteArray(file), headers);
        } catch (Exception e) {
            logger.error("downloadTenderPdf fail", e);
            throw new BusinessException("下载投标书失败，请稍后重试!");
        }
    }

    /**
     * 下载中标Pdf(公告下的所有中标结果)
     * @param declareId
     * @param user
     * @return
     */
    @RequestMapping(value = "/downloadWinningPdf/{declareId}", method = RequestMethod.GET,produces =MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public HttpEntity<byte[]> downloadWinning(@PathVariable("declareId") int declareId, @CurrentUser User user) {
        TenderDeclaration tenderDeclaration = tenderdeclarMapper.findTenderDeclarByIdAndUserId(declareId, user.getId());
        if (tenderDeclaration == null) {
            throw new NotFoundException();
        }
        List<Map<String, Object>> dataList = tenderitemMapper.findWinTenderItem(user.getId(), declareId);
        if (dataList.size() == 0) {
            throw new NotFoundException("暂无数据");
        }
        try {
            String str = freemarker.render("contracts/winningTenderTemplate", new HashMap<String, Object>() {{
                put("companyName", tenderDeclaration.getTenderunits().replace("\"",""));
                put("month", tenderDeclaration.getContractconenddate().getMonthValue());
                put("tenderList", dataList);
            }});
            File file = pdf.createByHtml(str);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", tenderDeclaration.getTendercode() + ".pdf");
            return new HttpEntity<byte[]>(FileUtils.readFileToByteArray(file), headers);
        } catch (Exception e) {
            logger.error("downloadWinningPdf fail", e);
            throw new BusinessException("下载中标书失败，请稍后重试!");
        }
    }

    /**
     * 投标下载附件(公告下的所有附件)
     * @param decelareId
     * @param user
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/downloadAttachment/{decelareId}", method = RequestMethod.GET,produces =MediaType.APPLICATION_OCTET_STREAM_VALUE )
    public HttpEntity<byte[]> download(@PathVariable("decelareId") int decelareId, @CurrentUser User user) throws IOException {
        //公告
        TenderDeclaration tenderDeclaration = myTenderService.findByDeclareId(decelareId, user.getId());
        FileInputStream fis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        BufferedInputStream bis = null;
        File zipFile = null;
        //公告下所有投标记录
        List<Bid> bidList=tenderdeclarMapper.findByInDeclare(user.getId(),decelareId);
        if(bidList.size()==0){
            throw  new NotFoundException("暂无数据");
        }
        try {
            zipFile =File.createTempFile(tenderDeclaration.getTendercode(),".zip");
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(new BufferedOutputStream(fos));
            for(Bid b:bidList){
                File f =  fileStore.getFileByFilePath(b.getAttachmentpath());
                //创建ZIP实体，并添加进压缩包
                ZipEntry zipEntry = new ZipEntry(new Date().getTime()+b.getAttachmentfilename());
                zos.putNextEntry(zipEntry);
                //读取待压缩的文件并写进压缩包里
                fis = new FileInputStream(f);
                bis = new BufferedInputStream(fis, 1024 * 10);
                int read = 0;
                byte[] bufs = new byte[1024*10];
                while((read=bis.read(bufs, 0, 1024*10)) != -1){
                    zos.write(bufs,0,read);
                }
            }
        } catch (NotFoundException e) {
            throw new NotFoundException("文件不存在!");
        } catch (Exception e) {
            logger.error("downloadFile fail", e);
            throw new BusinessException("下载文件失败，请稍后重试!");
        }finally {
            //关闭流
            try {
                if(null != bis) bis.close();
                if(null != zos) zos.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", zipFile.getName());
        return new HttpEntity<byte[]>(FileUtils.readFileToByteArray(zipFile), headers);
    }


    /**
     * 选择应标
     * @param tenderId
     * @param itemsequence
     * @return
     */
    @RequestMapping(value = "/selectMytender", method = RequestMethod.POST)
    @ResponseBody
    public Object selectMytender(@RequestParam(value = "tenderId", required = true) int tenderId, @RequestParam(value = "itemsequence", required = true) int itemsequence) {
        SelectTender selectTender = mytenderMapper.selectMytender(tenderId, itemsequence);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", selectTender);
        return map;
    }


    /**
     * 提交选择应标保存
     * @param id
     * @param commitMyTenderRequestWrapper
     * @return
     */
    @RequestMapping(value = "/selectCommitMytender", method = RequestMethod.POST)
    @ResponseBody
    public Object selectCommitMytender(@RequestParam(value = "id", required = true) int id, @ModelAttribute CommitMyTenderRequestWrapper commitMyTenderRequestWrapper) {
        return myTenderService.commitSelectMytender(id, session.getUser().getId(), commitMyTenderRequestWrapper.selectTenderlist);
    }

    public static class CommitMyTenderRequestWrapper {
        Map<Integer, BigDecimal> selectTenderlist;
        public Map<Integer, BigDecimal> getSelectTenderlist() {
            return selectTenderlist;
        }
        public void setSelectTenderlist(Map<Integer, BigDecimal> selectTenderlist) {
            this.selectTenderlist = selectTenderlist;
        }
    }
}
