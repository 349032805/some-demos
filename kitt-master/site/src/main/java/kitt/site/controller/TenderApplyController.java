package kitt.site.controller;

import com.fasterxml.jackson.databind.JavaType;
import kitt.core.domain.*;
import kitt.core.persistence.CompanyMapper;
import kitt.core.persistence.TenderInviteMapper;
import kitt.core.persistence.TenderdeclarMapper;
import kitt.core.persistence.UserMapper;
import kitt.core.util.JsonMapper;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 15/11/11.
 */
@RequestMapping("/tender")
@LoginRequired
@Controller
public class TenderApplyController {

    @Autowired
    private TenderApplyService tenderApplyService;
    @Autowired
    protected FileService fileService;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected CompanyMapper companyMapper;
    @Autowired
    protected Session session;
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private TenderInviteService tenderInviteService;
    @Autowired
    private TenderInviteMapper tenderInviteMapper;

    /**
     * 进入标书发布编辑页面
     * @return
     */
    @RequestMapping(value = "/editTenderEmpty",method = RequestMethod.GET)
    public String editTenderEmpty( Map<String, Object> model){
        model.put("timestamp", System.currentTimeMillis());
        return "tender/tenderRelease";
    }

    /**
     *个人中心进入
     * @param model
     * @return
     */
    @RequestMapping(value = "/editMyTender",method = RequestMethod.GET)
    public String editMyTender(@RequestParam(value = "tenderId",required = true)int tenderId,
                               @RequestParam(value = "confirm",required = false)boolean confirm,
                               @CurrentUser User user,
                               Map<String, Object> model){
        TenderDeclaration tenderDeclaration =  tenderApplyService.findDeclarById(tenderId);
        if(tenderDeclaration.getUserid()!=session.getUser().getId())
            throw new NotFoundException();
        List<TenderItem> tenderItems=tenderDeclaration.getItemList();
        String tenderListStr=JsonMapper.nonDefaultMapper().toJson(tenderItems);
        model.put("data", tenderDeclaration);
        model.put("tenderListStr", tenderListStr);
        model.put("timestamp", System.currentTimeMillis());
        model.put("confirm", confirm);
        model.put("invites",tenderInviteMapper.loadInviteByDeclareId(tenderId,user.getId()));
        return "tender/tenderRelease";
    }

    /**
     * 保存招标公告
     * @return
     */
    @RequestMapping(value = "/saveTender",method = RequestMethod.POST)
    @ResponseBody
    public Object saveTender(@RequestParam(value = "itemLists",required = false)String itemList,
                             @RequestParam(value = "tenderinvite") String tenderInviteRs,TenderDeclaration declaration){

        tenderApplyService.checkCompany();

        List<TenderItem> items=null;
        if(StringUtils.isNoneBlank(itemList)){
            JavaType javaType=JsonMapper.nonDefaultMapper().contructCollectionType(List.class, TenderItem.class);
            items=JsonMapper.nonDefaultMapper().fromJson(itemList, javaType);
        }
        declaration.setItemList(items);
        //区分正常发布、暂存   暂存0、发布1
       if(declaration.getIscommit()==1){
           tenderApplyService.verifyTenderCommit(declaration);
       }

        Map<String, Object> map = new HashMap<String, Object>();
        if(declaration.getId()==0){
            int id = tenderApplyService.addTender(declaration);
            if(id>0){
                map.put("message","招标公告保存成功");
                map.put("tenderid",id);
                map.put("version",0);
                //添加投标邀请
                tenderInviteService.addCompositeTenderInvite(id,tenderInviteRs,declaration.getTenderopen());
            }else {
                map.put("error","招标公告保存失败");
            }
        }else{
            TenderDeclaration   tenderDeclaration =  tenderdeclarMapper.findTendDeclarById(declaration.getId());
            if(tenderDeclaration.getUserid()!=session.getUser().getId())
                throw new NotFoundException();
            //删除所有投标邀请
            tenderInviteMapper.deleteAllTenderinvite(declaration.getId(),session.getUser().getId());
            tenderInviteMapper.deleteAllTenderTempinvite(declaration.getId(),session.getUser().getId());
            boolean result = tenderApplyService.editTender(declaration);
            if(result==true){
                map.put("tenderid",declaration.getId());
                map.put("version",declaration.getVersion()+1);
                map.put("message","招标公告保存成功");
                //添加投标邀请
                tenderInviteService.addCompositeTenderInvite(declaration.getId(),tenderInviteRs,declaration.getTenderopen());
            }else {
                map.put("error","招标公告保存失败");
            }
        }
        return map;
    }

    /**
     * 保存公告附件
     * @param file
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveTenderFile", method = RequestMethod.POST)
    @ResponseBody
    public Object saveTenderFile(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        BigDecimal filesize = new BigDecimal(file.getSize());
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if(returnValue<= 30) {
            map.put("filePath", fileService.uploadPicture(file));
            response.setContentType("text/html");
            success = true;
        }else {
            map.put("error","上传文件过大,请选择小于30M文件上传");
        }
        map.put("success", success);
        return map;
    }

    /**
     * 提交公告
     * @param itemList
     * @param filepath
     * @param declaration
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/confirmTender", method = RequestMethod.POST)
    @ResponseBody
    public Object confirmTender(
                                @RequestParam(value = "tenderinvite") String tenderInviteRs,
                                @RequestParam(value = "itemLists",required = false)String itemList,
                                @RequestParam("filepath") String filepath,
                                TenderDeclaration declaration,
                                @CurrentUser User user) throws Exception{
        tenderApplyService.checkCompany();

        List<TenderItem> items=null;
        if(StringUtils.isNoneBlank(itemList)){
            JavaType javaType=JsonMapper.nonDefaultMapper().contructCollectionType(List.class, TenderItem.class);
            items=JsonMapper.nonDefaultMapper().fromJson(itemList, javaType);
        }
        declaration.setItemList(items);
        tenderApplyService.verifyTenderCommit(declaration);

        Map<String, Object> map = new HashMap<String, Object>();

        if(declaration.getId()==0){
            int id = tenderApplyService.addTender(declaration);
            if(id>0){
                map.put("message","招标公告保存成功");
                map.put("tenderid",id);
                map.put("version",0);
                tenderInviteService.addCompositeTenderInvite(declaration.getId(),tenderInviteRs,declaration.getTenderopen());
            }else {
                map.put("error","招标公告保存失败");
            }
            tenderApplyService.confirmTender(filepath,id);
        }else{
            TenderDeclaration tenderDeclaration =  tenderdeclarMapper.findTendDeclarById(declaration.getId());
            if(tenderDeclaration.getUserid()!=session.getUser().getId())
                throw new NotFoundException();
            //删除所有投标邀请
            tenderInviteMapper.deleteAllTenderinvite(declaration.getId(),session.getUser().getId());
            tenderInviteMapper.deleteAllTenderTempinvite(declaration.getId(),session.getUser().getId());
            boolean result = tenderApplyService.editTender(declaration);
            if(result==true){
                map.put("tenderid",declaration.getId());
                map.put("version",declaration.getVersion()+1);
                map.put("message","招标公告保存成功");
            }else {
                map.put("error","招标公告保存失败");
            }
            tenderApplyService.confirmTender(filepath,declaration.getId());
            //添加投标邀请
            tenderInviteService.addCompositeTenderInvite(declaration.getId(),tenderInviteRs,declaration.getTenderopen());
        }

        map.put("success", true);

        return map;
    }
}
