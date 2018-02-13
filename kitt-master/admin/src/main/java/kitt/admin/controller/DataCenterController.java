package kitt.admin.controller;

import com.mysql.jdbc.StringUtils;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.JsonController;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.DataCenterService;
import kitt.admin.service.FileService;
import kitt.core.domain.*;
import kitt.core.persistence.DataBookMapper;
import kitt.core.persistence.DataCenterMapper;
import kitt.core.service.FileStore;
import kitt.core.service.NumberDateFormat;
import kitt.core.service.ReadExcel;
import kitt.core.util.text.TextCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by jack on 6/8/15.
 */
@RestController
public class DataCenterController extends JsonController {
    @Autowired
    private DataCenterMapper dataCenterMapper;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private DataCenterService dataCenterService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ReadExcel readExcel;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private TextCheck textCheck;
    @Autowired
    private NumberDateFormat numberDateFormat;
    @Autowired
    private Auth auth;

    private IndIndex indIndexTemp;
    private static List<DataMenu> menuList;

    //数据中心，首页请求
    @RequestMapping("/data/show")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object getDataList(@RequestParam(value = "menuid", required = false, defaultValue = "0")String menuid,
                              @RequestParam(value = "startDate", required = false)String startDate,
                              @RequestParam(value = "endDate", required = false)String endDate,
                              @RequestParam(value = "type", required = false, defaultValue = "yes")String type,
                              @RequestParam(value = "page", required = false, defaultValue = "1")int page) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        menuList = dataCenterMapper.getDataMenuList();
        map.put("menuList", menuList);
        map.put("fqcyList", dataBookMapper.getDataBookListByType("fqcy"));
        map.put("showStyleList", dataBookMapper.getDataBookListByType("showstyle"));
        map.put("weekdateList", dataBookMapper.getDataBookListByType("weekdate"));
        map.put("contentList1", dataCenterMapper.getMenuListByParentId("00"));
        map.put("theDefaultOneText", getDefaultNodeFullPath());
        IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(menuid);
        if(indIndexCfg != null) {
            map.put("isLeaf", indIndexCfg.isleaf());
            if (indIndexCfg.isleaf()) {
                map.put("menuTitleText", indIndexCfg.getName());
                String fqcyText = dataBookMapper.getDataBookNameByTypeSequence("fqcy", indIndexCfg.getFqcy());
                map.put("menuDetailText", "频率： " + fqcyText + ",　单位：" + indIndexCfg.getUnit() + "。");
                if(!StringUtils.isNullOrEmpty(startDate) && !StringUtils.isNullOrEmpty(endDate)) {
                    map.put("menuValueList", dataCenterMapper.pageAllDataValue(menuid, startDate, endDate, page, 10));
                } else{
                    map.put("menuValueList", dataCenterMapper.pageAllDataValue(menuid, null, null, page, 10));
                }
                map.put("fqcyType", indIndexCfg.getFqcy());
            }
        }
        return map;
    }

    //添加，修改目录方法
    @RequestMapping("/data/addUpdateMenu")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doAddUpdateContent(IndIndexCfg indIndexCfg){
        indIndexCfg.setName(indIndexCfg.getName().toString().replaceAll(" ", ""));
        if(indIndexCfg.getUnit() != null)
            indIndexCfg.setUnit(indIndexCfg.getUnit().toString().replaceAll(" ", ""));
        if(indIndexCfg.getOrigin() != null)
            indIndexCfg.setOrigin(indIndexCfg.getOrigin().toString().replaceAll(" ", ""));
        if(indIndexCfg.getRemarks() != null)
            indIndexCfg.setRemarks(indIndexCfg.getRemarks().toString().replaceAll(" ", ""));
        if(indIndexCfg.getPrecesion() == null){
            indIndexCfg.setPrecesion(2);
        }
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        String type = "";
        String error = "";
        if (!textCheck.doTextCheckThree(indIndexCfg.getName())){
            type = "name";
            error = "目录名称中包含有非法字符！";
        } else if (!textCheck.doTextCheckThree(indIndexCfg.getUnit())){
            type = "unit";
            error = "单位中包含有非法字符！";
        } else if (!textCheck.doTextCheckThree(indIndexCfg.getOrigin())){
            type = "origin";
            error = "来源中包含有非法字符！";
        } else if (!textCheck.doTextCheckThree(indIndexCfg.getRemarks())){
            type = "remark";
            error = "备注中包含非法字符！";
        } else {
            List<IndIndexCfg> indIndexCfgList = dataCenterMapper.getAllMenuList();
            for (IndIndexCfg newIndIndexCfg : indIndexCfgList) {
                if (newIndIndexCfg.getName().equals(indIndexCfg.getName())) {
                    if (StringUtils.isNullOrEmpty(indIndexCfg.getId()) || !newIndIndexCfg.getId().equals(indIndexCfg.getId())) {
                        return new Object() {
                            public boolean success = false;
                            public String type = "name";
                            public String error = getNodeFullPath(newIndIndexCfg.getParentid()) + " 下已存在相同名称的目录，请更改！";
                        };
                    }
                }
            }
            if (StringUtils.isNullOrEmpty(indIndexCfg.getId())) {
                if (StringUtils.isNullOrEmpty(indIndexCfg.getParentid())) {
                    indIndexCfg.setParentid("00");
                }
                if (indIndexCfg.getSequence() == null) {
                    indIndexCfg.setSequence(10);
                }
                indIndexCfg.setLastupdateuserid(session.getAdmin().getId());
                if (!indIndexCfg.getParentid().equals("00")) {
                    IndIndexCfg parent = dataCenterMapper.getMenuById(indIndexCfg.getParentid());
                    if (parent == null)
                        throw new RuntimeException("parentid: " + indIndexCfg.getParentid() + " 错误!");
                    if (parent.isleaf()) {
                        type = "info";
                        error = "叶子节点下面不能添加目录！";
                    }
                    switch (indIndexCfg.getParentid().length()) {
                        case 2:
                            indIndexCfg.setLevel(2);
                            break;
                        case 6:
                            indIndexCfg.setLevel(3);
                            break;
                        case 10:
                            indIndexCfg.setLevel(4);
                            break;
                        case 14:
                            indIndexCfg.setLevel(5);
                            break;
                        case 18:
                            indIndexCfg.setLevel(6);
                            break;
                        default:
                            break;
                    }
                } else {
                    indIndexCfg.setLevel(1);
                }
                indIndexCfg.setCreatetime(LocalDateTime.now());
                indIndexCfg.setCreateuserid(session.getAdmin().getId());
                success = dataCenterMapper.doAddIndIndexCfg(indIndexCfg);
            } else {
                //根据sequence字段重置orderid,如果是叶子节点直接修改末尾字符
                //如果不是叶子节点,修改orderid同时也得修改其下所有子节点orderid
                String orderid = indIndexCfg.getOrderid();
                String sequenceStr = String.valueOf(indIndexCfg.getSequence());
                int sequenceLength = sequenceStr.length();
                //因为最大的是99,所以顺序最长两位字符
                if(sequenceLength == 1){
                    orderid = orderid.substring(0,orderid.length()-2);
                    orderid = orderid +"0"+sequenceStr;
                }

                if(sequenceLength == 2){
                    if(indIndexCfg.getLevel() == 1){
                        orderid = sequenceStr;
                    }else{
                        orderid = orderid.substring(0,orderid.length()-2);
                        orderid = orderid + sequenceStr;
                    }
                }

                indIndexCfg.setOrderid(orderid);
                dataCenterMapper.updateIndIndexCfg(indIndexCfg);


                if(!indIndexCfg.isleaf()){
                    int level = indIndexCfg.getLevel();
                    if(level ==1) {
                        List<IndIndexCfg> level1MenusList = dataCenterMapper.getMenuListByParentId(indIndexCfg.getId());
                        if (level1MenusList != null) {
                            if(level1MenusList.size()>0) {
                                for (IndIndexCfg menu : level1MenusList) {
                                    menu.setOrderid(orderid + menu.getOrderid().substring(2));
                                    dataCenterMapper.updateIndIndexCfg(menu);
                                }
                            }
                        }

                        List<IndIndexCfg> level2MenusList = null;
                        if (level1MenusList != null) {
                            if(level1MenusList.size()>0) {
                                level2MenusList = dataCenterMapper.getMenuListByParentId(level1MenusList.get(0).getId());
                                if (level2MenusList != null) {
                                    if(level2MenusList.size()>0) {
                                        for (IndIndexCfg menu : level2MenusList) {
                                            menu.setOrderid(orderid + menu.getOrderid().substring(2));
                                            dataCenterMapper.updateIndIndexCfg(menu);
                                        }
                                    }

                                }
                            }
                        }

                        List<IndIndexCfg> level3MenusList = null;
                        if (level2MenusList != null) {
                            if(level2MenusList.size()>0) {
                                level3MenusList = dataCenterMapper.getMenuListByParentId(level2MenusList.get(0).getId());
                                if (level3MenusList != null) {
                                    if(level3MenusList.size()>0) {
                                        for (IndIndexCfg menu : level3MenusList) {
                                            menu.setOrderid(orderid + menu.getOrderid().substring(2));
                                            dataCenterMapper.updateIndIndexCfg(menu);
                                        }
                                    }

                                }
                            }
                        }

                        List<IndIndexCfg> level4MenusList = null;
                        if (level3MenusList != null) {
                            if (level3MenusList.size() > 0) {
                                level4MenusList = dataCenterMapper.getMenuListByParentId(level3MenusList.get(0).getId());
                                if (level4MenusList != null) {
                                    if(level4MenusList.size()>0) {
                                        for (IndIndexCfg menu : level4MenusList) {
                                            menu.setOrderid(orderid + menu.getOrderid().substring(2));
                                            dataCenterMapper.updateIndIndexCfg(menu);
                                        }
                                    }

                                }
                            }
                        }
                    }


                    if(level ==2){
                        List<IndIndexCfg> level1MenusList = dataCenterMapper.getMenuListByParentId(indIndexCfg.getId());
                        if (level1MenusList != null) {
                            if(level1MenusList.size()>0) {
                                for (IndIndexCfg menu : level1MenusList) {
                                    menu.setOrderid(orderid + menu.getOrderid().substring(6));
                                    dataCenterMapper.updateIndIndexCfg(menu);
                                }
                            }
                        }

                        List<IndIndexCfg> level2MenusList = null;
                        if (level1MenusList != null) {
                            if(level1MenusList.size()>0) {
                                level2MenusList = dataCenterMapper.getMenuListByParentId(level1MenusList.get(0).getId());
                                if (level2MenusList != null) {
                                    if(level2MenusList.size()>0) {
                                        for (IndIndexCfg menu : level2MenusList) {
                                            menu.setOrderid(orderid + menu.getOrderid().substring(6));
                                            dataCenterMapper.updateIndIndexCfg(menu);
                                        }
                                    }

                                }
                            }
                        }

                        List<IndIndexCfg> level3MenusList = null;
                        if (level2MenusList != null) {
                            if (level2MenusList.size() > 0) {
                                level3MenusList = dataCenterMapper.getMenuListByParentId(level2MenusList.get(0).getId());
                                if(level3MenusList != null) {
                                    if (level3MenusList.size() > 0) {
                                        for (IndIndexCfg menu : level3MenusList) {
                                            menu.setOrderid(orderid + menu.getOrderid().substring(6));
                                            dataCenterMapper.updateIndIndexCfg(menu);
                                        }

                                    }
                                }
                            }
                        }

                    }

                    if(level ==3){
                        List<IndIndexCfg> level1MenusList = dataCenterMapper.getMenuListByParentId(indIndexCfg.getId());
                        if (level1MenusList != null) {
                            if(level1MenusList.size()>0) {
                                for (IndIndexCfg menu : level1MenusList) {
                                    menu.setOrderid(orderid + menu.getOrderid().substring(10));
                                    dataCenterMapper.updateIndIndexCfg(menu);
                                }
                            }
                        }

                        List<IndIndexCfg> level2MenusList = null;
                        if (level1MenusList != null) {
                            if (level1MenusList.size() > 0) {
                                level2MenusList = dataCenterMapper.getMenuListByParentId(level1MenusList.get(0).getId());
                                if (level2MenusList != null) {
                                    if(level2MenusList.size()>0) {
                                        for (IndIndexCfg menu : level2MenusList) {
                                            menu.setOrderid(orderid + menu.getOrderid().substring(10));
                                            dataCenterMapper.updateIndIndexCfg(menu);
                                        }
                                    }

                                }
                            }
                        }

                    }

                    if(level==4){
                        List<IndIndexCfg> level1MenusList = dataCenterMapper.getMenuListByParentId(indIndexCfg.getId());
                        if (level1MenusList != null) {
                            if(level1MenusList.size()>0) {
                                for (IndIndexCfg menu : level1MenusList) {
                                    menu.setOrderid(orderid + menu.getOrderid().substring(14));
                                    dataCenterMapper.updateIndIndexCfg(menu);
                                }
                            }
                        }
                    }
                }

                success = dataCenterMapper.doUpdateIndIndexCfg(indIndexCfg);
            }
        }
        map.put("success", success);
        map.put("type", type);
        map.put("error", error);
        return map;
    }

    //删除目录
    @RequestMapping("/data/deleteMenu")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doDeleteMenu(@RequestParam(value = "id", required = true)String id){
        if(id == null || dataCenterMapper.getMenuById(id) == null){
            throw new NotFoundException();
        }
        return dataCenterMapper.doDeleteMenuByIdMethod(id);
    }

    //根据 id 获取 目录
    @RequestMapping("/data/getMenuById")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public IndIndexCfg doGetContentById(@RequestParam(value = "id", required = true)String id){
        return dataCenterMapper.getMenuById(id);
    }

    /**
     * 添加数据的方法，频率为 周的 判断 是 星期几
     * @param indIndex   数据对象
     * @return
     */
    @RequestMapping("/data/addMenuData")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doAddMenuData(IndIndex indIndex){
        IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(indIndex.getIndindexcfgid());
        if(indIndexCfg == null) throw new NotFoundException();
        indIndexTemp = indIndex;
        if(indIndexCfg.getFqcy() == 2){
            DayOfWeek actualWeekDay = LocalDate.parse(indIndex.getD1().toString().replaceAll(" ", "")).getDayOfWeek();
            DayOfWeek expectWeekDay = DayOfWeek.of(indIndexCfg.getWeekdate());
            if(!actualWeekDay.equals(expectWeekDay)) {
                String expectWeekDayText = numberDateFormat.getWeekTextByNumber(indIndexCfg.getWeekdate());
                String actualWeekDayText = numberDateFormat.getWeekTextByDayOfWeek(actualWeekDay);
                return new Object(){
                    public boolean success = false;
                    public String type = "weekday";
                    public String error = "该节点为周频率(" + expectWeekDayText + "), 当前日期是：" + actualWeekDayText + ", 你确定要添加吗？";
                };
            }
        }
        return dataCenterService.doAddMenuDataMethod(indIndexCfg, indIndex);
    }

    /**
     * 添加数据的方法，频率为周的 不判断 星期几
     * @return
     */
    @RequestMapping("/data/addMenuDataWithoutWeek")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doAddMenuDataWithoutWeekCheck(){
        IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(indIndexTemp.getIndindexcfgid());
        if(indIndexCfg == null) throw new NotFoundException();
        return dataCenterService.doAddMenuDataMethod(indIndexCfg, indIndexTemp);
    }

    //根据 parentId 获取 目录列表
    @RequestMapping("/data/getMenuByParentId")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object getContentListByParentId(@RequestParam(value = "parentId", required = false, defaultValue = "00")String parentId){
        if(StringUtils.isNullOrEmpty(parentId)) return "NULL";
        IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(parentId);
        if(indIndexCfg == null) throw new NotFoundException();
        List<IndIndexCfg> indIndexCfgList = dataCenterMapper.getMenuListByParentId(parentId);
        Map<String, Object> map = new HashMap<>();
        if(indIndexCfg.isleaf()){
            map.put("dataRecommend", "本节点数据频率为：" + dataBookMapper.getDataBookNameByTypeSequence("fqcy", dataCenterMapper.getMenuById(parentId).getFqcy()) + ",　单位是：" + indIndexCfg.getUnit());
            map.put("indIndexCfgList", "NULL");
        } else{
            map.put("indIndexCfgList", indIndexCfgList.size() == 0 ? "NULL" : indIndexCfgList);
        }
        map.put("isLeaf", indIndexCfg.isleaf());
        map.put("fqcy", String.valueOf(indIndexCfg.getFqcy()));
        return map;
    }

    //根据 parentId 获取 非叶子节点 目录列表
    @RequestMapping("/data/getMenuNotLeafByParentId")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object getContentListNotLeafByParentId(@RequestParam(value = "parentId", required = false, defaultValue = "00")String parentId){
        if(StringUtils.isNullOrEmpty(parentId)){
            return "NULL";
        }
        IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(parentId);
        if(indIndexCfg == null) throw new NotFoundException();
        List<IndIndexCfg> indIndexCfgList = dataCenterMapper.getMenuListNotLeafByParentId(parentId);
        Map<String, Object> map = new HashMap<>();
        map.put("indIndexCfgList", indIndexCfgList.size() == 0 ? "NULL" : indIndexCfgList);
        return map;
    }

    //删除数据
    @RequestMapping("/data/deleteMenuData")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeleteMenuData(@RequestParam(value = "d1s[]", required = true)String[] d1s,
                                    @RequestParam(value = "indindexcfgid", required = true)String indindexcfgid){
        return dataCenterService.doDeleteSeveralData(d1s, indindexcfgid);
    }

    //按照 时间区间 删除数据
    @RequestMapping("/data/deleteDataBySearch")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeleteMenuDataBySearch(@RequestParam(value = "startDate", required = true)String startDate,
                                            @RequestParam(value = "endDate", required = false)String endDate,
                                            @RequestParam(value = "indindexcfgid", required = true)String indindexcfgid){
        return dataCenterService.doDeleteDataBySearch(startDate, endDate, indindexcfgid);
    }

    /**
     * 清空该节点数据
     * @param indindexcfgid       节点id
     * @return
     */
    @RequestMapping("/data/deleteAllData")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeleteAllMenuData(@RequestParam(value = "indindexcfgid", required = true)String indindexcfgid){
        return dataCenterService.doDeleteAllData(indindexcfgid);
    }

    //获取叶子节点的父目录
    @RequestMapping("/data/getParentMenus")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doGetLeafParentContents(@RequestParam(value = "currentId", required = true)String currentId){
        Map<String, Object> map = new HashMap<>();
        map.put("fqcy", String.valueOf(dataCenterMapper.getMenuById(currentId).getFqcy()));
        while (currentId.length() >= 2 && !currentId.equals("00")){
            String parentId = "";
            switch (currentId.length()){
                case 2:
                    String menu1Id = currentId;
                    parentId = "00";
                    List<IndIndexCfg> menuList1 = dataCenterMapper.getMenuListByParentId(parentId);
                    map.put("menu1Id", menu1Id);
                    map.put("menuList1", menuList1);
                    break;
                case 6:
                    String menu2Id = currentId;
                    parentId = currentId.substring(0, currentId.length() - 4);
                    List<IndIndexCfg> menuList2 = dataCenterMapper.getMenuListByParentId(parentId);
                    map.put("menu2Id", menu2Id);
                    map.put("menuList2", menuList2);
                    break;
                case 10:
                    String menu3Id = currentId;
                    parentId = currentId.substring(0, currentId.length() - 4);
                    List<IndIndexCfg> menuList3 = dataCenterMapper.getMenuListByParentId(parentId);
                    map.put("menu3Id", menu3Id);
                    map.put("menuList3", menuList3);
                    break;
                case 14:
                    String menu4Id = currentId;
                    parentId = currentId.substring(0, currentId.length() - 4);
                    List<IndIndexCfg> menuList4 = dataCenterMapper.getMenuListByParentId(parentId);
                    map.put("menu4Id", menu4Id);
                    map.put("menuList4", menuList4);
                    break;
                case 18:
                    String menu5Id = currentId;
                    parentId = currentId.substring(0, currentId.length() - 4);
                    List<IndIndexCfg> menuList5 = dataCenterMapper.getMenuListByParentId(parentId);
                    map.put("menu5Id", menu5Id);
                    map.put("menuList5", menuList5);
                    break;
                default:
                    break;
            }
            currentId = parentId;
        }
        return map;
    }

    //获取叶子节点的父目录
    @RequestMapping("/data/setDefaultMenu")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doSetDefaultMenu(@RequestParam(value = "id", required = true)String id){
        IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(id);
        if(indIndexCfg == null || !indIndexCfg.isleaf() ) throw new NotFoundException();
        return dataCenterMapper.doSetDefaultMenuByIdMethod(id);
    }


    @RequestMapping("/data/checkDataValue")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doCheckDataValue(@RequestParam(value = "id", required = true)String id,
                                    @RequestParam(value = "val", required = true)String val){
        IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(id);
        String pattern1 = "^[-]?(([1-9]\\d{0,9})|0)(\\.\\d{";
        String pattern2 = "})?$";
        String length = "0," + indIndexCfg.getPrecesion();
        String valuePatterns = pattern1 + length + pattern2;
        Pattern pattern = Pattern.compile(valuePatterns);
        if(!pattern.matcher(val).matches()) {
            return new Object(){
                public boolean success = false;
                public String error = "该节点数据不能超过 " + indIndexCfg.getPrecesion() + " 位小数";
            };
        } else{
            return new Object(){
              public boolean success = true;
            };
        }
    }

    @RequestMapping("/data/setCancelShow")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public List<DataMenu> doSetCancelShow(@RequestParam(value = "id", required = true)String id){
        IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(id);
        if(indIndexCfg == null) throw new NotFoundException();
        if(indIndexCfg.isshow()){
            if (dataCenterMapper.doCancelShowById(id) == 1) {
                return dataCenterMapper.getDataMenuList();
            }
            throw new BusinessException("设置前台显示出错，请联系技术人员！");
        } else {
            while(indIndexCfg != null){
                if(dataCenterMapper.doSetShowById(indIndexCfg.getId()) != 1){
                    throw new BusinessException("设置前台显示出错，请联系技术人员！");
                }
                indIndexCfg = dataCenterMapper.getMenuById(indIndexCfg.getParentid());
            }
            return dataCenterMapper.getDataMenuList();
        }
    }

    public String getNodeFullPath(String id){
        if(!id.equals("00")) {
            IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(id);
            if (indIndexCfg == null) {
                throw new NotFoundException();
            } else {
                String theFullPath = indIndexCfg.getName();
                indIndexCfg = dataCenterMapper.getMenuById(indIndexCfg.getParentid());
                while (indIndexCfg != null) {
                    theFullPath = indIndexCfg.getName() + " > " + theFullPath;
                    indIndexCfg = dataCenterMapper.getMenuById(indIndexCfg.getParentid());
                }
                return theFullPath + " ";
            }
        } else{
            return "根目录";
        }
    }

    public String getDefaultNodeFullPath() {
        IndIndexCfg indIndexCfg = dataCenterMapper.getDefaultMenu();
        String theDefaultOneText = "";
        if(indIndexCfg != null){
            theDefaultOneText += indIndexCfg.getName();
            indIndexCfg = dataCenterMapper.getMenuById(indIndexCfg.getParentid());
            while(indIndexCfg != null){
                theDefaultOneText = indIndexCfg.getName() + " > " + theDefaultOneText;
                indIndexCfg = dataCenterMapper.getMenuById(indIndexCfg.getParentid());
            }
        } else{
            theDefaultOneText = "空";
        }
        return theDefaultOneText;
    }

    //从 Excel 中导入数据
    @RequestMapping("/data/importData")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doImportExcelData(@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
        Map<String, Object> map = readExcel.doReadDataExcel(auth.uploadPicMethod(EnumFileType.File_DataCenter.toString(), EnumFileType.EXCEL.toString(), file, null, null).get("filePath").toString());
        return dataCenterService.importExcelDataToDatabase(map);
    }

    /**
     * 设置 该菜单的子菜单是否在后台显示
     * @param id    菜单id
     * @return      菜单list
     */
    @RequestMapping("/data/setMenuDisplayStyle")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doSetMenuDisplayStyle(@RequestParam(value = "id", required = true)String id){
        IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(id);
        if(indIndexCfg == null) throw new NotFoundException();
        if(dataCenterService.doSetMenuDisplayStyleMethod(id) == 1){
            return true;
        }
        throw new BusinessException("系统出错，请联系技术人员！");
    }

    /**
     * 只获取菜单列表
     * @return  菜单list
     */
    @RequestMapping("/data/getMenuListOnly")
    public List<DataMenu> doGetMenuList(){
        return dataCenterMapper.getDataMenuList();
    }



}
