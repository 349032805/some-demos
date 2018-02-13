package kitt.admin.controller;

import com.mysql.jdbc.StringUtils;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.service.Tools;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.MessageNotice;
import kitt.core.service.SMS;
import kitt.core.util.text.HanYuToPinYin;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jack on 15/1/10.
 */
@RestController
public class DealerController {
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private DealerMapper dealerMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private DemandMapper demandMapper;
    @Autowired
    private Tools tools;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private SMS sms;
    @Autowired
    private HanYuToPinYin hanYuToPinYin;
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private UserMapper userMapper;
    Logger logger = LoggerFactory.getLogger(DealerController.class);

    @RequestMapping("/dealer/list")
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showDealerlist(
            @RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")int region,
            @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")int province,
            @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")int harbour,
            @RequestParam(value = "content", required = false)String content,
            int page){
        Map<String, Object> map = new HashMap<String, Object>();
        List<Areaport> provinceList = tools.getMallProvinces(region);
        List<Areaport> harbourList = tools.getMallPorts(province);
        map.put("regionList", areaportMapper.getAllArea());
        map.put("addProvinceList", provinceList);
        map.put("addHarbourList", harbourList);
        map.put("region", region);
        map.put("province", province);
        map.put("harbour", harbour);
        map.put("provinceList", provinceList);
        map.put("harbourList", harbourList);
        map.put("content", content);
        map.put("dealerList",dealerMapper.pageAllDealer(region, province, harbour, content, page, 10));
        return map;
    }

    @RequestMapping(value = "/dealer/addDealer", method = RequestMethod.POST)
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object addDealer(@RequestBody Admin dealer) throws BadHanyuPinyinOutputFormatCombination {
        final String dealerCode="Trader";
        //添加交易员
        dealer.setPassword(DigestUtils.md5Hex("123456"));
        String loginName = hanYuToPinYin.HanYuToPinYinMethod(dealer.getName(), false);
        //检查用户名汉字、拼音重复
        if(adminMapper.nameExists(dealer.getName())>=1||adminMapper.usernameExists(loginName)>=1){
            dealer.setUsername(setDealerLoginName(dealer.getName()));
        } else{
            dealer.setUsername(loginName);
        }
        adminMapper.addAdmin(dealer);
        dealer.setJobnum("YMW"+dealer.getId());
        adminMapper.updateJobNumByid(dealer);
        //添加角色
        roleMapper.addUserRole(dealer.getId(), roleMapper.findIdByCode(dealerCode));
        //添加港口
        dealerMapper.addDealerPort(dealer);
        //发送短信
        sendMessage(dealer);
        return true;
    }

    private String  setDealerLoginName(String name) throws BadHanyuPinyinOutputFormatCombination {
        String username = hanYuToPinYin.HanYuToPinYinMethod(name, false);
        Pattern p = Pattern.compile("[0-9]");
        String loginName="";
        Matcher m = p.matcher(adminMapper.matchUsername("^"+username+"[0-9]?"));
        while (m.find()) {
            loginName+=m.group();
        }
        if(StringUtils.isNullOrEmpty(loginName)){
            return username+1;
        }
        int id= Integer.valueOf(loginName)+1;
        return username+=id;
    }

    private void sendMessage(Admin dealer)  {
        String content="";
        String portName="";
        for(Areaport port:dealer.getPorts()){
            portName+=port.getName()+",";
        }
        content+=dealer.getName()+"您好!";
        content+="您已经成功添加为易煤网的交易员,登陆名为:"+dealer.getUsername();
        content+=",密码为:123456。所管理的港口为:";
        content+=portName.substring(0,portName.length()-1);
        content+="。您现在可以登录易煤网管理员平台(http://admin.yimei180.com)查看相关工作。";
        MessageNotice.CommonMessage.noticeUser(dealer.getPhone(), content);
    }

    @RequestMapping(value = "/dealer/update",method =RequestMethod.POST)
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public boolean updateDealerInfo(@RequestParam("dealerId")int dealerId,
                                    @RequestParam("name")String name,
                                    @RequestParam(value = "phone",required = false) String phone,
                                    @RequestParam(value = "portCheckbox",required = false)Integer[] portIds){
        List<Integer> oldPortIds = dealerMapper.findPortIdByDealerId(dealerId);
        List<Integer> newPortIds=portIds==null?new ArrayList<Integer>():Arrays.asList(portIds);
        //删除角色信息
        List<Integer> deleteIds=getSubSet(oldPortIds,newPortIds);
        //添加角色信息
        List<Integer> addIds=getSubSet(newPortIds,oldPortIds);
        if(deleteIds.size()>0){
            dealerMapper.deleteDealerInPort(dealerId, deleteIds);
        }
        if(addIds.size()>0){
            dealerMapper.addDealerInPort(dealerId,addIds);
        }
        //修改交易员电话
        Admin dealer=dealerMapper.findDealerById(dealerId);
        if(!dealer.getPhone().equals(phone) || (StringUtils.isNullOrEmpty(dealer.getName()) && !StringUtils.isNullOrEmpty(name)) ||(!StringUtils.isNullOrEmpty(dealer.getName()) && !dealer.getName().equals(name))){
            dealerMapper.updateNameAndPhone(name, phone, dealerId);
        }
        //修改供应信息交易员电话
        demandMapper.updateDealerPhone(phone, dealerId);
        //修改需求信息交易员电话
        buyMapper.updateDealerPhone(phone,dealerId);
        return true;
    }

    @RequestMapping("/dealer/forbidden")
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public boolean forbidden(@RequestParam("oldId") int oldId,
                             @RequestParam("newId") int newId) {
        Admin newDealer = adminMapper.getAdminById(newId);
        //把交易员负责的需求,供应,都改为新交易员的信息
        buyMapper.updateSellInfoDealerByDealerId(newDealer, oldId);
        demandMapper.updateDemandDealerByDealerId(newDealer, oldId);
        tenderdeclarMapper.updateTenderDeclarationDealerByDealerId(newDealer, oldId);
        userMapper.updateUserDealerByDealerId(newDealer.getId(), oldId);
        //删除交易员-港口对应关系
        dealerMapper.deletePort(oldId);
        //把该交易员设置为禁用
        adminMapper.updateStatus(EnumAdmin.EndJob.name(), oldId);
        return true;
    }


    @RequestMapping("/dealer/delete")
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteDealer(int id) {
        //把交易员状态设置为Delete
       int flag = adminMapper.loadDealerByStatus(EnumAdmin.Deleted.name(), id);
        if(flag==1){
            throw new BusinessException("该信息不存在,请刷新当前页面!");
        }
        adminMapper.updateStatus(EnumAdmin.Deleted.name(), id);
        return true;
    }

    @RequestMapping("/dealer/listAllDealer")
    public Object getAllDealer(int id){
        return dealerMapper.findyiMeiDealer(id) ;
    }

    @RequestMapping(value = "/dealer/updatePortInDealer")
    public boolean updateDealerPortInfo(
            @RequestParam("portId") int portId,
            @RequestParam("dealeId") List<Integer>newDealerIds){
        List<Integer> oldDealerIds = dealerMapper.findAllDealerIdByPortId(portId);
        List<Integer> deleteIds=getSubSet(oldDealerIds,newDealerIds);
        List<Integer> addIds=getSubSet(newDealerIds,oldDealerIds);
        //删除当前港口信息
        if(deleteIds.size() > 0){
            dealerMapper.deleteDealerPort(portId,deleteIds);
        }
        //添加该港口下的交易员
        if(addIds.size() > 0){
            dealerMapper.addPortInDealer(portId, addIds);
        }
        return true;
    }
    //修改交易员所属团队
    @RequestMapping(value = "/dealer/updateTeam")
    public boolean updateDealerTeam(
            @RequestParam("teamId") int teamId,
            @RequestParam("dealerId") List<Integer>newDealerIds){
        List<Integer> oldDealerIds = dealerMapper.findAdminIdByTeamId(teamId);
        List<Integer> deleteIds=getSubSet(oldDealerIds,newDealerIds);
        List<Integer> addIds=getSubSet(newDealerIds,oldDealerIds);
        //删除
        if(deleteIds.size() > 0){
            dealerMapper.updateTeamIdForDealer(null,deleteIds);
        }
        //添加该港口下的交易员
        if(addIds.size() > 0){
            dealerMapper.updateTeamIdForDealer(teamId, addIds);
        }
        return true;
    }


    @RequestMapping("dealer/listAllPort")
    public Object listAllAreaport(){
        return  areaportMapper.findAllPort();
    }

    //得到两个集合的差集
    private  List<Integer> getSubSet(List<Integer> c1,List<Integer> c2){
        List<Integer> result = new ArrayList<Integer>();
        result.addAll(c1);
        result.removeAll(c2);
        return result;
    }

    @RequestMapping("/dealer/checkIfExist")
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doCheckIfExist(@RequestParam(value = "addDealerName", required = true)String dealername,
                                 @RequestParam(value = "addDealerPhone", required = true)String dealerphone,
                                 @RequestParam(value = "addDeliveryRegion", required = true)int region,
                                 @RequestParam(value = "addDeliveryProvince", required = true)int province,
                                 @RequestParam(value = "addDeliveryPlace", required = true)int place){
        String deliveryregion = areaportMapper.getNameById(region);
        String deliveryprovince = areaportMapper.getNameById(province);
        String deliveryplace = place == 999 ? "其它" : areaportMapper.getNameById(place);
        List<Dealer> oldDealerList = dealerMapper.getDealerByRegionProvincePlace(deliveryregion, deliveryprovince, deliveryplace);
        if(oldDealerList == null || oldDealerList.size() == 0){
            return new Object(){
              public boolean success = true;
            };
        } else if(oldDealerList.size() == 1 && oldDealerList.get(0).getDealername().equals(dealername) && oldDealerList.get(0).getDealerphone().equals(dealerphone)){
            return new Object(){
                public boolean success = false;
                public String error = "exist";
            };
        } else{
            for(final Dealer dealer : oldDealerList){
                if(dealer.getStatus().equals("在职")){
                    return new Object(){
                        public boolean success = false;
                        public String dealerString = dealer.getDealername() + "，电话：" + dealer.getDealerphone();
                    };
                }
            }
            return new Object(){
                public boolean success = true;
            };
        }
    }

    @RequestMapping("/dealer/deleteOldDealers")
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doDeleteOldDealers(
            @RequestParam(value = "addDeliveryRegion", required = true)int region,
            @RequestParam(value = "addDeliveryProvince", required = true)int province,
            @RequestParam(value = "addDeliveryPlace", required = true)int place){
        String deliveryregion = areaportMapper.getNameById(region);
        String deliveryprovince = areaportMapper.getNameById(province);
        String deliveryplace = place == 999 ? "其它" : areaportMapper.getNameById(place);
        List<Dealer> dealerList = dealerMapper.getDealerByRegionProvincePlace(deliveryregion, deliveryprovince, deliveryplace);
        if(dealerList != null && dealerList.size() != 0){
            for(Dealer dealer : dealerList){
                dealerMapper.setDealerStatusById("已删除", dealer.getId());
            }
        }
        return true;
    }

    @RequestMapping("/dealer/getPhone")
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doGetPhone(@RequestParam(value = "name")String name){
        if(adminMapper.getByName(name) != null && adminMapper.getByName(name).size() != 0) {
            Admin admin = adminMapper.getByName(name).get(0);
            if (admin != null) {
                return admin.getPhone();
            }
        }
        return "NULL";
    }

    @RequestMapping("/dealer/getName")
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doGetName(@RequestParam(value = "phone", required = true)String phone){
        Admin admin = adminMapper.getByPhone(phone);
        if(admin != null) {
            return admin.getName();
        }
        return "NULL";
    }

    //加载交易员
    @RequestMapping("/dealer/loadDealer")
    public Object loadDealer(@RequestParam(value = "provinceId", defaultValue = "0") int provinceId,
                             @RequestParam(value = "portId", defaultValue = "-1") int portId){
        List<Admin> dealerList = new ArrayList<>();
        //-1是没有港口的情况，找属于这个省下面的所有交易员
        if(portId == -1){
            dealerList = dealerMapper.findDealerByProvinceId(provinceId);
            //如果省下面没有交易员，找省所属地区的交易员
            if(dealerList.size()==0){
                dealerList =  dealerMapper.findRegionAllDealer(provinceId);
            }
            //地区下面没有交易员，就找易煤网所有交易员
            if(dealerList.size() == 0){
                dealerList = dealerMapper.findyiMeiAllDealer();
            }
        } else{
            dealerList = dealerMapper.findAllDealerByPortId(portId);
        }
        return  dealerList;
    }


    //加载指定团队的交易员
    @RequestMapping(value = "/dealer/loadDealerByTeamId",method = RequestMethod.POST)
    public Object loadDealerByTeamId(@RequestParam(value = "teamId",required = true) Integer teamId){
            return dealerMapper.findAdminByTeamId(teamId);
    }


}

