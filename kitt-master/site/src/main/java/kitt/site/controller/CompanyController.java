package kitt.site.controller;

import kitt.core.domain.Company;
import kitt.core.domain.CompanyVerify;
import kitt.core.domain.District;
import kitt.core.domain.User;
import kitt.core.persistence.*;
import kitt.core.service.FileStore;
import kitt.core.service.MessageNotice;
import kitt.core.util.check.CheckPictureInfo;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.service.Auth;
import kitt.site.service.BeanValidators;
import kitt.site.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanjun on 14-11-12.
 */
@LoginRequired
@Controller
public class CompanyController extends JsonController {
    @Autowired
    protected CompanyMapper companyMapper;
    @Autowired
    protected BuyMapper buyMapper;
    @Autowired
    protected Auth auth;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected OrderMapper orderMapper;
    @Autowired
    protected PaymentMapper paymentMapper;
    @Autowired
    protected FileService fileService;
    @Autowired
    protected FileStore fileStore;
    @Autowired
    private CheckPictureInfo checkPictureInfo;
    @Autowired
    private RegionYMMapper regionMapper;

    //保存或修改公司信息(前台最好提交隐藏域表单,后台用实体类接收)
    @RequestMapping(value = "/account/saveCompany", method = RequestMethod.POST)
    @ResponseBody
    public Object saveCompany(Company company,
                              @CurrentUser User user) throws Exception{
        BeanValidators.validateWithException(company);
        boolean result = false;
        if(!"审核通过".equals(userMapper.getUserById(session.getUser().getId()).getVerifystatus())){
            //先找到临时文件夹temp里客户确认的图片,并复制到upload文件夹
            fileStore.copyFileToUploadDir(company.getBusinesslicense());
            fileStore.copyFileToUploadDir(company.getIdentificationnumber());
            fileStore.copyFileToUploadDir(company.getOrganizationcode());
            fileStore.copyFileToUploadDir(company.getOpeninglicense());
            if(!StringUtils.isBlank(company.getOperatinglicense())){
                fileStore.copyFileToUploadDir(company.getOperatinglicense());
                company.setOperatinglicense(company.getOperatinglicense().replace("temp", "upload"));
            }

            fileStore.copyFileToUploadDir(company.getOpeninglicense());
            if(!StringUtils.isBlank(company.getInvoicinginformation())){
                fileStore.copyFileToUploadDir(company.getInvoicinginformation());
                company.setInvoicinginformation(company.getInvoicinginformation().replace("temp", "upload"));
            }

            company.setBusinesslicense(company.getBusinesslicense().replace("temp", "upload"));
            company.setIdentificationnumber(company.getIdentificationnumber().replace("temp", "upload"));
            company.setOrganizationcode(company.getOrganizationcode().replace("temp", "upload"));
            company.setOpeninglicense(company.getOpeninglicense().replace("temp","upload"));
            company.setUserid(session.getUser().getId());

            if (companyMapper.countCompany(session.getUser().getId()) == 0){
                companyMapper.addCompany(company);
            } else{
                companyMapper.modifyCompany(company);
            }
            companyMapper.addCompVerify(new CompanyVerify("待审核", LocalDateTime.now(), companyMapper.getIdByUserid(session.getUser().getId()), session.getUser().getId()));
            companyMapper.setCompanyStatus("待审核", null, companyMapper.getIdByUserid(session.getUser().getId()));
            userMapper.setUserVerifyStatus("待审核", null, session.getUser().getId());
            result = true;
            MessageNotice.SubmitCompany.noticeUser(user.getSecurephone());
        }
        return result;
    }


    //获取所有的省
    @RequestMapping("/account/getAllProvinces")
    @ResponseBody
    public Object getAllProvinces(){
        Map<String,Object> map=new HashMap<String,Object>();
        List<District> moldPList=regionMapper.getDistinctMold(1,null);
        if(moldPList!=null&&moldPList.size()>0){
            for(District d:moldPList){
                d.setRegionList(regionMapper.getregionymsByMold(d.getMold(),null, 1));
            }
        }
        map.put("provinceList", moldPList);
        return map;
    }

    //根据code获取省下面的市
    @RequestMapping("/account/getCitysByParent")
    @ResponseBody
    public Object getCitysByParent(@RequestParam(value = "code", required = true)String code){
        Map<String,Object> map=new HashMap<String,Object>();
        List<District> moldPList=regionMapper.getDistinctMold(2, code);
        if(moldPList!=null&&moldPList.size()>0){
            for(District d:moldPList){
                d.setRegionList(regionMapper.getregionymsByMold(d.getMold(),code, 2));
            }
        }
        map.put("cityList",moldPList);
        return map;
    }

    //根据code获取市下面的区
    @RequestMapping("/account/getCountrysByParent")
    @ResponseBody
    public Object getCountrysByParent(@RequestParam(value = "code", required = true)String code){
        Map<String,Object> map=new HashMap<String,Object>();
        List<District> moldPList=regionMapper.getDistinctMold(3,code);
        if(moldPList!=null&&moldPList.size()>0){
            for(District d:moldPList){
                d.setRegionList(regionMapper.getregionymsByMold(d.getMold(),code,3));
            }
        }
        map.put("countryList",moldPList);
        return map;
    }

    //返回公司对象信息
    @RequestMapping(value = "/account/getCompany", method = RequestMethod.POST)
    @ResponseBody
    public Company getCompany(){
        return companyMapper.getCompanyByUserid(session.getUser().getId());
    }

    //保存公司图片
    @RequestMapping(value = "/account/saveCompanyPic", method = RequestMethod.POST)
    @ResponseBody
    public Object saveCompanyPic(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (!checkPictureInfo.doCheckPictureType(file)) {
            throw new BusinessException("请选择 .jpg, .bmp, .png, .jpeg 格式的图片上传！");
        } else if(!checkPictureInfo.doCheckPictureSize(file)) {
            throw new BusinessException("上传的图片不能超过10M！");
        } else {
            map.put("filePath", fileService.uploadPicture(file));
            map.put("success", true);
            return map;
        }
    }

    //验证公司名称是否已存在
    @RequestMapping(value = "/account/checkCompanyname", method = RequestMethod.POST)
    @ResponseBody
    public boolean checkCompanyname(@RequestParam("name") String name){
        int i = companyMapper.countCompanyIsExist(name.trim(), session.getUser().getId());
        if(i == 0){
            return true;
        } else{
            return false;
        }
    }
}
