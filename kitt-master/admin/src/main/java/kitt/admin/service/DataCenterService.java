package kitt.admin.service;

import com.mysql.jdbc.StringUtils;
import kitt.admin.basic.exception.BusinessException;
import kitt.core.domain.DataObject;
import kitt.core.domain.IndIndex;
import kitt.core.domain.IndIndexCfg;
import kitt.core.persistence.DataCenterMapper;
import kitt.core.service.NumberDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yimei on 15/7/15.
 */
@Service
public class DataCenterService {
    @Autowired
    private DataCenterMapper dataCenterMapper;
    @Autowired
    private Auth auth;
    @Autowired
    private NumberDateFormat numberDateFormat;

    //批量删除数据
    @Transactional
    public boolean doDeleteSeveralData(String d1s[], String indindexcfgid){
        for(int i=0; i<d1s.length; i++){
            if(dataCenterMapper.deleteIndIndexByIndIndexCfgIdD1(indindexcfgid, d1s[i]) != 1){
                auth.doOutputErrorInfo("------ 删除数据出错， indindexcfgid= " + indindexcfgid + ",  d1=" + d1s[i]);
                throw new BusinessException("删除数据出错，请联系技术人员！");
            }
        }
        return true;
    }

    //根据时间区间 删除数据
    @Transactional
    public boolean doDeleteDataBySearch(String startDate, String endDate, String indindexcfgid) {
        dataCenterMapper.doDeleteDataByDateZone(startDate, endDate, indindexcfgid);
        return true;
    }

    //删除所有数据
    @Transactional
    public boolean doDeleteAllData(String indindexcfgid) {
        dataCenterMapper.doDeleteAllData(indindexcfgid);
        return true;
    }

    /**
     * 把从Excel中取出的数据导入到数据库中
     * @param model    从Excel中取出的数据map
     * @return         导入成功后的提示信息等
     */
    @Transactional
    public Object importExcelDataToDatabase(Map<String, Object> model) {
        List<DataObject> dataObjectList = (List<DataObject>)model.get("dataObjectList");
        String error = (String)model.get("error");
        if(!StringUtils.isNullOrEmpty(error)){
            throw new BusinessException(error);
        } else {
            if (dataObjectList == null || dataObjectList.size() < 1) {
                throw new BusinessException("您上传的Excel表是空文件！");
            } else{
                Map<String, Object> map = new HashMap<String, Object>();
                String parentname = dataObjectList.get(0).getParentname();
                if (StringUtils.isNullOrEmpty(parentname)) {
                    throw new BusinessException("Excel表中，父节点名称为空！");
                } else{
                    String totalSonNamesText = "";
                    String remindMessage = "";
                    int nums = 0;
                    for (int i = 0; i < dataObjectList.size(); i++) {
                        if(i != 0){
                            totalSonNamesText += "、";
                        }
                        totalSonNamesText += "<b>" + dataObjectList.get(i).getName() + "</b>";
                        Map<String, Object> mapTemp = doImportDataToDatabaseMethod(dataObjectList.get(i).getIndIndexList());
                        if(mapTemp != null) {
                            int fqcyTemp = (Integer) mapTemp.get("fqcy");
                            if (fqcyTemp == 2 && mapTemp.get("weekDateErrorNums") != null) {
                                int weekDateErrorNums = (Integer) mapTemp.get("weekDateErrorNums");
                                int weekdateTemp = (Integer) mapTemp.get("weekdate");
                                String expectWeekText = numberDateFormat.getWeekTextByDayOfWeek(DayOfWeek.of(weekdateTemp));
                                if (weekDateErrorNums != 0) {
                                    if (nums == 0) {
                                        remindMessage = "<b>温馨提示</b>: 您导入的数据中，" + mapTemp.get("name") + " 节点(" + expectWeekText + ")，" + " 有 " + weekDateErrorNums + " 条数据不是 " + expectWeekText;
                                    } else {
                                        remindMessage += ", " + mapTemp.get("name") + " 节点(" + expectWeekText + ")，" + " 有 " + weekDateErrorNums + " 条数据不是 " + expectWeekText;
                                    }
                                    nums++;
                                }
                            }
                        }
                    }
                    if(remindMessage != ""){
                        remindMessage += "。";
                    }
                    map.put("parentname", parentname);
                    map.put("totalSonNamesText", totalSonNamesText);
                    map.put("success", true);
                    map.put("message", remindMessage);
                    return map;
                }
            }
        }
    }

    @Transactional
    public Map<String, Object> doImportDataToDatabaseMethod(List<IndIndex> indIndexList1){
        if(indIndexList1 != null && indIndexList1.size() != 0) {
            Map<String, Object> map = new HashMap<>();
            List<IndIndex> indIndexList = new ArrayList<>();
            for(IndIndex indIndex : indIndexList1){
                if(indIndex.getD1().length() == 4){
                    indIndex.setD1(indIndex.getD1() + "-12-31");
                } else if(indIndex.getD1().length() == 7){
                    String d12 = indIndex.getD1().substring(5, 7);
                    if(d12.equals("02")){
                        indIndex.setD1(indIndex.getD1() + "-28");
                    } else if(d12.equals("04") || d12.equals("06") || d12.equals("09") || d12.equals("11")){
                        indIndex.setD1(indIndex.getD1() + "-30");
                    } else{
                        indIndex.setD1(indIndex.getD1() + "-31");
                    }
                }
                indIndexList.add(indIndex);
            }
            int nums = 0;
            String dateExistText = "";
            IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(indIndexList.get(0).getIndindexcfgid());
            if (indIndexCfg == null) throw new BusinessException("导入数据出错，请联系技术人员！");
            List<String> indIndexeD1List = dataCenterMapper.getAllDataD1ValueList(indIndexCfg.getId());
            if(indIndexeD1List != null && indIndexeD1List.size() != 0) {
                if (indIndexCfg.getFqcy() != 2) {
                    for (IndIndex indIndex : indIndexList) {
                        if (indIndexeD1List.contains(indIndex.getD1())) {
                            nums++;
                            if (nums < 6) {
                                if (nums > 1) {
                                    dateExistText += ",";
                                }
                                dateExistText += indIndex.getD1();
                            }
                            if (nums == 6) {
                                dateExistText += " 等";
                            }
                        }
                    }
                } else {
                    int weekDateErrors = 0;
                    for (IndIndex indIndex : indIndexList) {
                        if (numberDateFormat.doCheckDateIfInOneWeek(indIndexeD1List, indIndex.getD1())) {
                            nums++;
                            if (nums < 6) {
                                if (nums > 1) {
                                    dateExistText += ", ";
                                }
                                dateExistText += indIndex.getD1();
                            }
                            if (nums == 6) {
                                dateExistText += " 等";
                            }
                        }
                        DayOfWeek actualWeekDay = LocalDate.parse(indIndex.getD1().toString().replaceAll(" ", "")).getDayOfWeek();
                        DayOfWeek expectWeekDay = DayOfWeek.of(indIndexCfg.getWeekdate());
                        if (!actualWeekDay.equals(expectWeekDay)) {
                            weekDateErrors++;
                        }
                    }
                    map.put("weekDateErrorNums", weekDateErrors);
                    map.put("name", indIndexCfg.getName());
                    map.put("weekdate", indIndexCfg.getWeekdate());
                }
                if (nums != 0) {
                    String nameText = indIndexCfg.getName();
                    switch (indIndexCfg.getFqcy()) {
                        case 1:
                            nameText += "节点(频率：每天一条): ";
                            dateExistText += " 天";
                            break;
                        case 2:
                            nameText += "节点(频率：每周一条): ";
                            dateExistText += " 周";
                            break;
                        case 3:
                            nameText += "节点(频率：每月一条): ";
                            dateExistText += " 月";
                            break;
                        case 4:
                            nameText += "节点(频率：每季度一条): ";
                            dateExistText += " 季度";
                            break;
                        case 5:
                            nameText += "节点(频率：每年一条): ";
                            dateExistText += " 年";
                            break;
                        default:
                            break;
                    }
                    throw new BusinessException("您要导入的数据中，有 " + nameText + " 在日期为：" + dateExistText + " 内已经存在数据！" + " 共有: " + nums + " 条重复数据。");
                }
            }
            dataCenterMapper.doAddIndIndexList(indIndexList);
            map.put("fqcy", indIndexCfg.getFqcy());
            return map;
        } else {
            return null;
        }
    }

    /**
     * 添加数据方法
     * @param indIndexCfg  目录对象
     * @param indIndex     数据对象
     * @return             返回， 是否添加成功，如果不成功， 有error信息
     */
    @Transactional
    public Object doAddMenuDataMethod(IndIndexCfg indIndexCfg, IndIndex indIndex) {
        if(indIndexCfg.getFqcy() == 4){
            indIndex.setD1(numberDateFormat.setQuarterDate(indIndex.getD1()));
        }
        indIndex.setVal(numberDateFormat.setNumberLength(Double.valueOf(indIndex.getVal()), indIndexCfg.getPrecesion()));
        return dataCenterMapper.doAddUpdateIndIndexMethod(indIndex);
    }

    /**
     * 设置 该菜单的子菜单是否在后台显示
     * @param id    菜单id
     * @return      更改了行数
     */
    public int doSetMenuDisplayStyleMethod(String id) {
        return dataCenterMapper.setMenuDisplayStyle(id);
    }

}
