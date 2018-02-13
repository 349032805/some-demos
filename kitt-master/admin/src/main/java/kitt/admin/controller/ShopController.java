package kitt.admin.controller;

import kitt.admin.basic.JsonController;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.ShopService;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/9/9.
 */
@RestController
@RequestMapping("/shop")
public class ShopController extends JsonController {
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ShopService shopService;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RegionYMMapper regionMapper;
    @Autowired
    private Auth auth;

    /**
     * 店铺列表方法
     * @param page                 页数
     * @return
     */
    @RequestMapping("/list")
    public Object doGetShopList(@RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return shopMapper.getShopListPager(page, 10);
    }

    /**
     * 店铺详细页面
     * @param id                   Shop id
     * @return
     */
    @RequestMapping("/detail")
    public Object doShowShopDetail(@RequestParam(value = "id", required = true) int id){
        Map<String, Object> map = new HashMap<>();
        Shop shop = shopMapper.getShopById(id);
        if(shop == null) throw new NotFoundException();
        User user = userMapper.getUserById(shop.getUserid());
        if(user == null) throw new NotFoundException();
        map.put("shop", shop);
        map.put("traderid", user.getTraderid());

        List<District> provinceList =  regionMapper.getAllProvinces();
        List<District> portList = regionMapper.getDistrictByParent(shop.getProvinceId(),2);
        map.put("provinceList", provinceList);
        map.put("portList", portList);
        return map;
    }

    /**
     * 保存店铺草稿
     * @param shop                 Shop对象
     * @return
     */
    @RequestMapping("/save")
    public Object doSaveShop(Shop shop){
        //保存前判断店铺名称和关联公司是否存在
        Map map = beforeSaveOrAdd(shop);
        String result = map.get("success").toString();
        if(result.equals("true")) {
            if (StringUtils.isEmpty(shop.getId())) {
                shopService.doSaveShopMethod(shop);
            } else {
                shopService.doUpdateSaveShopMethod(shop);
            }
        }
        return map;
    }

    /**
     * 添加更新店铺
     * @param shop                 Shop对象
     * @return
     */
    @RequestMapping("/add")
    public Object doAddUpdateShop(Shop shop){
        //保存前判断店铺名称和关联公司是否存在
        Map map = beforeSaveOrAdd(shop);
        String result = map.get("success").toString();
        if(result.equals("true")) {
            if (StringUtils.isEmpty(shop.getId())) {
                shopService.doAddShopMethod(shop);
            } else {
                if(StringUtils.isEmpty(shop.getShopid())) {
                    shopService.doUpdateAddShopMethod(shop);
                }else{
                    shopService.doUpdateSaveShopMethod(shop);
                }
            }
        }

        return map;
    }

    //保存前判断店铺名称和关联公司是否存在
    public Map beforeSaveOrAdd(Shop shop){
        Map map = new HashMap<>();
        boolean success = true;
        String errorMsg = null;

//        if(StringUtils.isEmpty(shop.getShopid())) {
            //判断是否店铺名称是否重复
            Shop s = shopMapper.countShopByName(shop.getName());
            if (s != null) {
                if(!s.getId().equals(shop.getId())) {
                    success = false;
                    errorMsg = "店铺名称已存在";
                }
            }

            //根据公司名称判断是否存在
            Company company = companyMapper.getCompanyByName(shop.getCompanyname());
            if (company == null) {
                success = false;
                errorMsg = "关联公司不存在,或待审核,审核未通过";
            } else {
                Shop s2 = shopMapper.countShopCompanyname(shop.getCompanyname());
                if(s2 != null){
                    if(!s2.getId().equals(shop.getId())) {
                        success = false;
                        errorMsg = "关联公司已被使用";
                    }else{
                        shop.setUserid(company.getUserid());
                    }
                }else{
                    shop.setUserid(company.getUserid());
                }
            }
//        }

        map.put("success",success);
        map.put("errorMsg",errorMsg);
        return map;
    }



    /**
     * 上传店铺图片
     * @param file                 图片文件对象
     */
    @RequestMapping("/uploadPic")
    public Object doUploadShopPic(@RequestParam("file") MultipartFile file) throws Exception{
        return auth.uploadPicMethod(EnumFileType.File_ShopCoal.toString(), EnumFileType.IMG.toString(), file, null, null);
    }

    /**
     * 店铺上架，下架
     * @param id
     * @return
     */
    @RequestMapping("/updownproduct")
    public boolean doUpDownProduct(@RequestParam(value = "id", required = true)int id){
        Shop shop = shopMapper.getShopById(id);
        if(shop == null) throw new NotFoundException();
        return shopService.doUpDownProductMethod(shop);
    }

    /**
     * 改变店铺的排列次序
     * @param id                    店铺id
     * @param sequence              店铺sequence
     * @return
     */
    @RequestMapping("/changesequence")
    public Object doChangeShopSequence(@RequestParam(value = "id", required = true)int id,
                                        @RequestParam(value = "sequence", required = true)int sequence){
        Map map = new HashMap<>();
        boolean success = true;
        String errorMsg = null;
        int countSequence = shopMapper.countSequence(sequence);
        if(countSequence ==0) {
            shopMapper.doChangeShopSequenceMethod(id, sequence);
        }else{
            success = false;
            errorMsg = "顺序已存在";
        }
        map.put("success",success);
        map.put("errorMsg",errorMsg);
        return map;
    }

    //删除店铺
    @RequestMapping("/delete")
    public boolean deleteShop(int id){
        shopMapper.deleteShopById(id);
        return true;
    }

    //获取所有审核通过且未被关联的的公司给输入框自动补全
    @RequestMapping("/getAllCompanies")
    public Object getAllCompanies(){
        List<String> companyNameList = companyMapper.getAllPassCompanies();
        List<String> usedCompanyNameList = shopMapper.getUsedCompanyname();
        for(int i=0;i<usedCompanyNameList.size();i++){
            String usedName = usedCompanyNameList.get(i);
            for(int j=0;j<companyNameList.size();j++){
                String companyname = companyNameList.get(j);
                if(usedName.equals(companyname)){
                    companyNameList.remove(companyNameList.get(j));
                }

            }
        }

        //获取所有省份集合
        Map map = new HashMap<>();
        map.put("companyNameList",companyNameList);
        map.put("provinceList", regionMapper.getAllProvinces());
        return map;
    }

    //单独更新特色文字描述
    @RequestMapping("/modifyFeatureWord")
    public boolean modifyFeatureWord(int id,String featuretext){
        shopMapper.modifyFeatureWordById(id, featuretext);
        return true;
    }

    //单独更新合作文字描述
    @RequestMapping("/modifyPartnerWord")
    public boolean modifyPartnerWord(int id,String partnertext){
        shopMapper.modifyPartnerWordById(id, partnertext);
        return true;
    }
}
