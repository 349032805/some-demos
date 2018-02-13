package kitt.site.controller;

import kitt.core.domain.*;
import kitt.core.persistence.DataCenterMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by fanjun on 15-6-8.
 */
@Controller
public class DataCenterController {
    @Autowired
    protected DataCenterMapper dataCenterMapper;

    public static final String SINGLE_QUOTE = "\'";

    //数据表格一页的记录数
    public static final int DATATABLE_LIMIT = 20;

    //图表分割段数,分割规则:当超过分割段数,用集合长度除以段数,不满段数时为0,显示全部数据坐标
    public static final int CHART_SPLITNUM = 12;

    //一上来不能直接获取图表数据,图表得经过js处理
    @RequestMapping("/dataCenter")
    public String dataCenter(Map<String, Object> model) {
        StringBuilder menus = treeData();

        model.put("title",Seoconfig.dataCenter_title);
        model.put("keywords",Seoconfig.dataCenter_keywords);
        model.put("description",Seoconfig.dataCenter_description);
        model.put("menus", menus);
        return "dataCenter";
    }

    //拼接树结构字符串
    public StringBuilder treeData() {
        //List<IndIndexCfg> menuList = dataCenterMapper.getAllShowMenuList();
        List<IndIndexCfg> allMenuList = dataCenterMapper.getAllMenuList();
        List<IndIndexCfg> menuList = new ArrayList<>();
        List<IndIndexCfg> noShowMenuList = dataCenterMapper.getAllNoShowMenuList();

        List<String> idList = doGetAllIdList(noShowMenuList);

        for(int i=0; i<allMenuList.size(); i++){
            if(!idList.contains(allMenuList.get(i).getId())){
                menuList.add(allMenuList.get(i));
            }
        }

        StringBuilder menusStr = new StringBuilder("");
        if (menuList != null && menuList.size() > 0) {
            menusStr.append("[");
            for (IndIndexCfg indIndexCfg : menuList) {
                menusStr.append("{id:" + indIndexCfg.getId());
                menusStr.append(",pId:" + indIndexCfg.getParentid());
                menusStr.append(",cId:" + SINGLE_QUOTE + "id" + indIndexCfg.getId() + SINGLE_QUOTE);
                menusStr.append(",isLeaf:"+ indIndexCfg.isleaf());
                menusStr.append(",name:" + SINGLE_QUOTE + indIndexCfg.getName() + SINGLE_QUOTE);
                if (!indIndexCfg.isleaf()) {
                    menusStr.append(",nocheck:true");
                }else{
                    menusStr.append(",font:{'color':'#888888'}");
                }
                if(indIndexCfg.getLevel()<=2) {
                    menusStr.append(",open:true");
                }
                menusStr.append("},");
            }
            menusStr.append("]");
        }
        return menusStr;
    }

    private List<String> doGetAllIdList(List<IndIndexCfg> indIndexCfgList) {
        List<String> idList = new ArrayList<>();
        if(indIndexCfgList != null && indIndexCfgList.size() != 0) {
            for (IndIndexCfg indIndexCfg : indIndexCfgList) {
                if (!idList.contains(indIndexCfg.getId())) {
                    idList.add(indIndexCfg.getId());
                    if (!indIndexCfg.isleaf()) {
                        List<IndIndexCfg> indIndexCfgList1 = dataCenterMapper.getMenuListByParentId(indIndexCfg.getId());
                        if(indIndexCfgList1 != null && indIndexCfgList1.size() != 0) {
                            idList.addAll(doGetAllIdList(indIndexCfgList1));
                        }
                    }
                }
            }
        }
        return idList;
    }

    @RequestMapping(value = "/dataCenter/getDefaultChart", method = RequestMethod.POST)
    @ResponseBody
    public Object getDefaultChart(String startDate,String endDate,
                                  @RequestParam(value = "currentPage", required = false, defaultValue = "1") int currentPage){
        IndIndexCfg menu = null;
        menu = dataCenterMapper.getDefaultMenu();
        if(menu == null){ // 如果后台没设置默认图表,取树结构第一个指标
            menu = dataCenterMapper.getFirstLeafMenu();
        }

        boolean success = true;
        Map map = new HashMap<>();
        if(menu != null) {
        //完整的指标路径名称，从根节点开始，用／隔开
        /*String completeChartName = "";
        List<IndIndexCfg> parentMenuList = new ArrayList<>();
        String menuId = menu.getParentid();
        for(int i=0;i<5;i++){
            IndIndexCfg parentMenu = dataCenterMapper.getMenuById(menuId);
            parentMenuList.add(parentMenu);
            menuId = parentMenu.getParentid();
            if(menuId.equals("00")){
                break;
            }
        }

        List<IndIndexCfg> sortParentMenuList = new ArrayList<>();
        //按照节点的id排序
        parentMenuList.stream().sorted((m1, m2) -> m1.getId().compareTo(m2.getId())).forEach(e -> sortParentMenuList.add(e));

        for(int i=0;i<sortParentMenuList.size();i++){
            completeChartName += sortParentMenuList.get(i).getName();
            if(i == sortParentMenuList.size() -1){
            }else{
                completeChartName += "/";
            }
        }*/

            //获取默认图表的数据和表格数据
            int frequency = menu.getFqcy(); //频率，1：日、2：周、3：月、4：季、5：年
            int totalCount = 0;
            List<IndIndex> chartIndexList = null;
            List<IndIndex> tableIndexList = null;
            if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) { //当开始时间和结束时间为空的时
                chartIndexList = dataCenterMapper.getDataByMenuId(menu.getId());
                tableIndexList = dataCenterMapper.getDataByMenuIdWithLimit(menu.getId(),(currentPage-1)*DATATABLE_LIMIT,DATATABLE_LIMIT);
                totalCount = dataCenterMapper.countIndexDataById(menu.getId());

            } else {
                chartIndexList = dataCenterMapper.getDataByMenuIdAndDate(menu.getId(), startDate, endDate);
                tableIndexList = dataCenterMapper.getDataByMenuIdByDataWithLimit(menu.getId(), startDate, endDate,(currentPage-1)*DATATABLE_LIMIT,DATATABLE_LIMIT);
                totalCount = dataCenterMapper.countIndexDataByIdAndDate(menu.getId(),startDate, endDate);
            }
            if(tableIndexList.size()>0){
                map.put("tableIndexList", tableIndexList);
            }else{
                success = false;
            }

            //截取日期到其频率
            //最小维度为3或者4,月份或者季度的时候,截取到年月
            if(frequency == 3 ||frequency == 4){
                for(IndIndex indIndex :chartIndexList){
                    indIndex.setD1(indIndex.getD1().substring(0,indIndex.getD1().lastIndexOf("-")));
                }

                for(IndIndex indIndex :tableIndexList){
                    indIndex.setD1(indIndex.getD1().substring(0,indIndex.getD1().lastIndexOf("-")));
                }
            }

            //最小维度为5,年的时候,截取到年
            if(frequency == 5){
                for(IndIndex indIndex :chartIndexList){
                    indIndex.setD1(indIndex.getD1().substring(0,indIndex.getD1().indexOf("-")));
                }

                for(IndIndex indIndex :tableIndexList){
                    indIndex.setD1(indIndex.getD1().substring(0,indIndex.getD1().indexOf("-")));
                }
            }

            //图表切换,属性和数据都得拼
            String xData = "[{";
            String yData = "[{";
            if (menu.getShowstyle() == 1) {
                //xData += "splitNumber:12,"; //横坐标是数字类型起作用,字符类型无效,日期类型测试后也无效
//                 xData += "boundaryGap:true,";
                yData += "type:" + SINGLE_QUOTE + "line" + SINGLE_QUOTE + ",";
            } else {
               //xData += "splitLine:{lineStyle:{color:" + SINGLE_QUOTE + "rgba(0,0,0,0)" + SINGLE_QUOTE + "}},";
                yData += "type:" + SINGLE_QUOTE + "bar" + SINGLE_QUOTE + ",";
            }

            xData += "data:[";
            yData += "name:" + SINGLE_QUOTE + menu.getName() +"("+menu.getUnit()+")"+ SINGLE_QUOTE + ",";
            yData += "data:[";

            //x轴和y轴的值,如果末尾多了逗号,IE8以上没问题,IE8下插件会报解析错误
            if(chartIndexList.size()>0) {
                for (int i = 0; i < chartIndexList.size(); i++) {
                    xData += SINGLE_QUOTE + chartIndexList.get(i).getD1()+ SINGLE_QUOTE;
                    yData += SINGLE_QUOTE + chartIndexList.get(i).getVal() + SINGLE_QUOTE;
                    if (i == chartIndexList.size() - 1) {
                    } else {
                        xData += ",";
                        yData += ",";
                    }
                }
                xData += "],";
                yData += "]";
                int intervalNum = 0;
                //如果集合长度大于分割段数,就做除法,如果小于等于,用0显示全部数据
                if(chartIndexList.size()>CHART_SPLITNUM) {
                     intervalNum = chartIndexList.size() / CHART_SPLITNUM;
                }else{
                    intervalNum = 0;
                }

                xData += "axisLabel:{interval:"+intervalNum+",rotate:45}";
                xData += "}]";
                yData += "}]";
            }else{
                success = false;
            }

            map.put("menu", menu);
            map.put("xData", xData);
            map.put("yData", yData);

            //处理分页的数据
            int totalPage = 0;
            if(totalCount>DATATABLE_LIMIT){
                int a = totalCount/DATATABLE_LIMIT;
                totalPage = totalCount % DATATABLE_LIMIT == 0?a:a+1;
            }else{
                totalPage = 1;
            }

            map.put("totalCount",totalCount);
            map.put("currentPage", currentPage);
            map.put("totalPage", totalPage);
        }else{
            success = false;
        }

        map.put("success",success);
        return map;
}

    //获取勾选的checkbox的数据
    @RequestMapping(value = "/dataCenter/getSelectCheckboxData", method = RequestMethod.POST)
    @ResponseBody
    public Object getSelectCheckboxData(@RequestParam(value = "idArr[]") String[] idArr,
                                        String startDate,String endDate,
                                        @RequestParam(value = "gotoPage", required = false, defaultValue = "1") int gotoPage) {
        //表头菜单集合
        List<IndIndexCfg> selectMenusList = dataCenterMapper.getMenusByIdArr(idArr);
        int menuNum = selectMenusList.size();
        String firstMenuId = null;
        String secMenuId = null;
        String thirdMenuId = null;
        String fourthMenuId = null;
        String fifthMenuId = null;
        List<DataCenter> dataList = null;
        int totalCount = 0;
        if(menuNum == 1){
            firstMenuId = selectMenusList.get(0).getId();
            dataList = dataCenterMapper.getDataByOneMenu(firstMenuId,startDate,endDate,(gotoPage-1)*DATATABLE_LIMIT,DATATABLE_LIMIT,"desc","needLimit");
            totalCount = dataCenterMapper.countIndexDataByOneMenu(firstMenuId,startDate,endDate);
        }

        if(menuNum == 2){
            firstMenuId = selectMenusList.get(0).getId();
            secMenuId = selectMenusList.get(1).getId();
            dataList = dataCenterMapper.getDataByTwoMenus(firstMenuId,secMenuId,startDate,endDate,(gotoPage-1)*DATATABLE_LIMIT,DATATABLE_LIMIT,"desc","needLimit");
            totalCount = dataCenterMapper.countIndexDataByTwoMenus(firstMenuId, secMenuId,startDate,endDate);
        }

        if(menuNum == 3){
            firstMenuId = selectMenusList.get(0).getId();
            secMenuId = selectMenusList.get(1).getId();
            thirdMenuId = selectMenusList.get(2).getId();
            dataList = dataCenterMapper.getDataByThreeMenus(firstMenuId,secMenuId,thirdMenuId,startDate,endDate,(gotoPage-1)*DATATABLE_LIMIT,DATATABLE_LIMIT,"desc","needLimit");
            totalCount = dataCenterMapper.countIndexDataByThreeMenus(firstMenuId, secMenuId, thirdMenuId,startDate,endDate);
        }

        if(menuNum == 4){
            firstMenuId = selectMenusList.get(0).getId();
            secMenuId = selectMenusList.get(1).getId();
            thirdMenuId = selectMenusList.get(2).getId();
            fourthMenuId = selectMenusList.get(3).getId();
            dataList = dataCenterMapper.getDataByFourMenus(firstMenuId,secMenuId,thirdMenuId,fourthMenuId,startDate,endDate,(gotoPage-1)*DATATABLE_LIMIT,DATATABLE_LIMIT,"desc","needLimit");
            totalCount = dataCenterMapper.countIndexDataByFourMenus(firstMenuId, secMenuId, thirdMenuId, fourthMenuId,startDate,endDate);
        }

        if(menuNum == 5){
            firstMenuId = selectMenusList.get(0).getId();
            secMenuId = selectMenusList.get(1).getId();
            thirdMenuId = selectMenusList.get(2).getId();
            fourthMenuId = selectMenusList.get(3).getId();
            fifthMenuId = selectMenusList.get(4).getId();
            dataList = dataCenterMapper.getDataByFiveMenus(firstMenuId,secMenuId,thirdMenuId,fourthMenuId,fifthMenuId,startDate,endDate,(gotoPage-1)*DATATABLE_LIMIT,DATATABLE_LIMIT,"desc","needLimit");
            totalCount = dataCenterMapper.countIndexDataByFiveMenus(firstMenuId, secMenuId, thirdMenuId, fourthMenuId, fifthMenuId,startDate,endDate);
        }

        //找出最小维度,截取日期
        int fqcyArr[] = new int[selectMenusList.size()];
        for(int i=0;i<selectMenusList.size();i++){
            fqcyArr[i] = selectMenusList.get(i).getFqcy();
        }

        Arrays.sort(fqcyArr);

        //最小维度为3或者4,月份或者季度的时候,截取到年月
        if(fqcyArr[0] == 3 || fqcyArr[0] == 4){
            for(DataCenter dataCenter :dataList){
                dataCenter.setD1(dataCenter.getD1().substring(0,dataCenter.getD1().lastIndexOf("-")));
            }
        }

        //最小维度为5,年的时候,截取到年
        if(fqcyArr[0] == 5){
            for(DataCenter dataCenter :dataList){
                dataCenter.setD1(dataCenter.getD1().substring(0,dataCenter.getD1().indexOf("-")));
            }
        }
        //处理分页的数据
        int totalPage = 0;
        if(totalCount>DATATABLE_LIMIT){
            int a = totalCount/DATATABLE_LIMIT;
            totalPage = totalCount % DATATABLE_LIMIT == 0?a:a+1;
        }else{
            totalPage = 1;
        }

        Map map = new HashMap<>();
        map.put("selectMenusList",selectMenusList);
        map.put("menuNum",menuNum);
        map.put("dataList",dataList);

        map.put("totalCount",totalCount);
        map.put("currentPage", gotoPage);
        map.put("totalPage", totalPage);

        return map;
    }

    //勾选右侧checkbox组合显示图表(逻辑处理和拼接比较复杂)
    @RequestMapping(value = "/dataCenter/groupChart", method = RequestMethod.POST)
    @ResponseBody
    public Object groupChart(@RequestParam(value = "clickCheckboxArr[]") String[] clickCheckboxArr,
                             String startDate,String endDate,
                             @RequestParam(value = "gotoPage", required = false, defaultValue = "1") int gotoPage){

        //名称,纵坐标单位,数据来源
        String chartName = "";
        String unitName1 = "";
        String unitName2 = "";
        String originName = "";
        List<String> unintList = new ArrayList<>();
        List<String> unintList2 = new ArrayList<>();
        List<String> dataOriginList = new ArrayList<>();
        List<String> dataOriginList2 = new ArrayList<>();


        int[] fqcyArr = new int[clickCheckboxArr.length];
        List<IndIndexCfg> indIndexCfgList = new ArrayList<>();
        for(int i=0;i<clickCheckboxArr.length;i++){
            IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(clickCheckboxArr[i]);
            fqcyArr[i] = indIndexCfg.getFqcy();
            indIndexCfgList.add(indIndexCfg);

            dataOriginList.add(indIndexCfg.getOrigin());
        }

        if(indIndexCfgList.size() == 1){
            chartName = indIndexCfgList.get(0).getName()+"(";
            int fqcy = indIndexCfgList.get(0).getFqcy();
            String fqcyName = null;
            if(fqcy == 1){
                fqcyName = "日";
            }
            if(fqcy == 2){
                fqcyName = "周";
            }
            if(fqcy == 3){
                fqcyName = "月";
            }
            if(fqcy == 4){
                fqcyName = "季";
            }
            if(fqcy == 5){
                fqcyName = "年";
            }
            chartName += fqcyName+")";
            unitName1 = indIndexCfgList.get(0).getUnit();
            originName = indIndexCfgList.get(0).getOrigin();
        }else{
            chartName = "组合图表";

            for(int i=0;i<dataOriginList.size();i++){
                if(!dataOriginList2.contains(dataOriginList.get(i))){
                    dataOriginList2.add(dataOriginList.get(i));
                }
            }

            for(int i=0;i<dataOriginList2.size();i++){
                originName +=dataOriginList2.get(i)+",";
            }
            originName = originName.substring(0,originName.lastIndexOf(","));
        }

        //排列数组从小到大,第一个是最小频率
        Arrays.sort(fqcyArr);

        //设置横坐标
        int menuNum = indIndexCfgList.size();
        String firstMenuId = null;
        String secMenuId = null;
        String thirdMenuId = null;
        String fourthMenuId = null;
        String fifthMenuId = null;
        List<DataCenter> dataCenterList = null;
        int totalCount = 0;
        if(menuNum == 1){
            firstMenuId = indIndexCfgList.get(0).getId();
            dataCenterList = dataCenterMapper.getDataByOneMenu(firstMenuId, startDate, endDate, (gotoPage - 1) * DATATABLE_LIMIT, DATATABLE_LIMIT, "","");
        }

        if(menuNum == 2){
            firstMenuId = indIndexCfgList.get(0).getId();
            secMenuId = indIndexCfgList.get(1).getId();
            dataCenterList = dataCenterMapper.getDataByTwoMenus(firstMenuId,secMenuId,startDate,endDate,(gotoPage-1)*DATATABLE_LIMIT,DATATABLE_LIMIT,"","");
        }

        if(menuNum == 3){
            firstMenuId = indIndexCfgList.get(0).getId();
            secMenuId = indIndexCfgList.get(1).getId();
            thirdMenuId = indIndexCfgList.get(2).getId();
            dataCenterList = dataCenterMapper.getDataByThreeMenus(firstMenuId, secMenuId, thirdMenuId, startDate, endDate, (gotoPage - 1) * DATATABLE_LIMIT, DATATABLE_LIMIT, "","");
        }

        if(menuNum == 4){
            firstMenuId = indIndexCfgList.get(0).getId();
            secMenuId = indIndexCfgList.get(1).getId();
            thirdMenuId = indIndexCfgList.get(2).getId();
            fourthMenuId = indIndexCfgList.get(3).getId();
            dataCenterList = dataCenterMapper.getDataByFourMenus(firstMenuId, secMenuId, thirdMenuId, fourthMenuId, startDate, endDate, (gotoPage - 1) * DATATABLE_LIMIT, DATATABLE_LIMIT, "","");
        }

        if(menuNum == 5){
            firstMenuId = indIndexCfgList.get(0).getId();
            secMenuId = indIndexCfgList.get(1).getId();
            thirdMenuId = indIndexCfgList.get(2).getId();
            fourthMenuId = indIndexCfgList.get(3).getId();
            fifthMenuId = indIndexCfgList.get(4).getId();
            dataCenterList = dataCenterMapper.getDataByFiveMenus(firstMenuId, secMenuId, thirdMenuId, fourthMenuId, fifthMenuId, startDate, endDate, (gotoPage - 1) * DATATABLE_LIMIT, DATATABLE_LIMIT,"","");
        }

        String firstDayParam = null;
        String lastDayParam = null;
        if(StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
            if(dataCenterList.size()>0) {
                firstDayParam = dataCenterList.get(0).getD1();
                lastDayParam = dataCenterList.get(dataCenterList.size() - 1).getD1();
            }
        }else{
            firstDayParam = startDate;
            lastDayParam = endDate;

        }

        //设置带所有日期的数据集合,等待填充
        List<IndIndex> chartDataList = new ArrayList<>();
        for(DataCenter dataCenter : dataCenterList){
            IndIndex indIndex = new IndIndex();
            indIndex.setD1(dataCenter.getD1());
            chartDataList.add(indIndex);
        }


        //截取日期
        if(fqcyArr[0] == 3 || fqcyArr[0] == 4){
            for(DataCenter dataCenter : dataCenterList){
                dataCenter.setD1(dataCenter.getD1().substring(0,dataCenter.getD1().lastIndexOf("-")));
            }
        }

        if(fqcyArr[0] == 5){
            for(DataCenter dataCenter : dataCenterList){
                dataCenter.setD1(dataCenter.getD1().substring(0,dataCenter.getD1().indexOf("-")));
            }
        }


        boolean success = true;

        //-----------------------------------------
        String xData = "";
       // String xData = "[{";
        //xData += "axisLine:{show:false},";
        if(dataCenterList != null && dataCenterList.size()>0) {
            //x轴
            xData += "[{";
            xData += "data:[";
            for(int i=0;i<dataCenterList.size();i++){
                xData += SINGLE_QUOTE +dataCenterList.get(i).getD1()+SINGLE_QUOTE;
                if(i == dataCenterList.size()-1){
                }else{
                    xData += ",";
                }
            }

            int intervalNum = 0;
            //如果集合长度大于分割段数,就做除法,如果小于等于,用0显示全部坐标
            if(dataCenterList.size()>CHART_SPLITNUM) {
                intervalNum = dataCenterList.size() / CHART_SPLITNUM;
            }else{
                intervalNum = 0;
            }

            xData += "],";
            xData += "axisLabel:{interval:"+intervalNum+",rotate:45}";
            xData += "}]";
        }else{
            success = false;
        }
        //-----------------------------------------

        //处理单位集合的逻辑(新增逻辑)

        if(chartName.equals("组合图表")) {
            //将日期范围内没有数据的指标单位去除
            if (firstDayParam != null && lastDayParam != null) {
                for (IndIndexCfg indIndexCfg : indIndexCfgList) {
                    String menuid = indIndexCfg.getId();
                    List<IndIndex> paramValueList = dataCenterMapper.getListByFirstDayAndLastDay(menuid, firstDayParam, lastDayParam);
                    if (paramValueList != null && paramValueList.size() > 0) {
                        unintList.add(indIndexCfg.getUnit());
                    }
                }
            }

            //处理单位集合
            if (unintList != null && unintList.size() > 0) {
                for (int i = 0; i < unintList.size(); i++) {
                    if (!unintList2.contains(unintList.get(i))) {
                        unintList2.add(unintList.get(i));
                    }
                }

                if (unintList2.size() == 1) {
                    //unitName1 = indIndexCfgList.get(0).getUnit();
                    unitName1 = unintList2.get(0);
                } else {
               /* unitName1 = indIndexCfgList.get(0).getUnit();
                unitName2 = indIndexCfgList.get(1).getUnit();*/
                    if (!unintList2.contains("%")) {
                        unitName1 = unintList2.get(0);
                        unitName2 = unintList2.get(1);
                    } else {
                        if (!unintList2.get(0).equals("%")) {
                            unitName1 = unintList2.get(0);
                        } else {
                            unitName1 = unintList2.get(1);
                        }
                        unitName2 = "%";
                    }
                }

            }
        }

        //y轴数据
        String yData = "[";
        String legend = "[";

        //判断数据在纵坐标的哪边,%单位放在右边
        List<YaxisIndex> yaxisIndexList = new ArrayList<>();
        if(unintList2.size() == 2){
            if(!unintList2.contains("%")) {
                YaxisIndex yaxisIndex0 = new YaxisIndex();
                yaxisIndex0.setUnitname(unintList2.get(0));
                yaxisIndex0.setIndex(0);
                yaxisIndexList.add(yaxisIndex0);

                YaxisIndex yaxisIndex1 = new YaxisIndex();
                yaxisIndex1.setUnitname(unintList2.get(1));
                yaxisIndex1.setIndex(1);
                yaxisIndexList.add(yaxisIndex1);
            }else{
                if(!unintList2.get(0).equals("%")){
                    YaxisIndex yaxisIndex0 = new YaxisIndex();
                    yaxisIndex0.setUnitname(unintList2.get(0));
                    yaxisIndex0.setIndex(0);
                    yaxisIndexList.add(yaxisIndex0);
                }else{
                    YaxisIndex yaxisIndex0 = new YaxisIndex();
                    yaxisIndex0.setUnitname(unintList2.get(1));
                    yaxisIndex0.setIndex(0);
                    yaxisIndexList.add(yaxisIndex0);
                }
                YaxisIndex yaxisIndex1 = new YaxisIndex();
                yaxisIndex1.setUnitname("%");
                yaxisIndex1.setIndex(1);
                yaxisIndexList.add(yaxisIndex1);
            }
        }

        //y轴
        for(int i=0;i<indIndexCfgList.size();i++){
            legend += SINGLE_QUOTE+indIndexCfgList.get(i).getName()+"("+indIndexCfgList.get(i).getUnit()+")"+SINGLE_QUOTE;
            if(i == indIndexCfgList.size()-1){
            }else{
                legend += ",";
            }
            yData += "{";
            if(indIndexCfgList.get(i).getShowstyle() ==1){
                yData += "type:"+SINGLE_QUOTE+"line"+SINGLE_QUOTE+",";
                for(IndIndex indIndex : chartDataList){
                    indIndex.setVal("-");
                }
            }else{
                yData += "type:"+SINGLE_QUOTE+"bar"+SINGLE_QUOTE+",";
                for(IndIndex indIndex : chartDataList){
                    indIndex.setVal("");
                }
            }
            yData += "name:"+SINGLE_QUOTE+indIndexCfgList.get(i).getName()+"("+indIndexCfgList.get(i).getUnit()+")"+SINGLE_QUOTE+",";
           /* if(i > 0 && !indIndexCfgList.get(i).getUnit().equals(indIndexCfgList.get(i-1).getUnit())){
                yData += "yAxisIndex: 1,";

            }*/

            //List<IndIndex> indexList = dataCenterMapper.getDataByMenuId(indIndexCfgList.get(i).getId());

            if(unintList2.size() ==2){
                    if (indIndexCfgList.get(i).getUnit().equals(yaxisIndexList.get(0).getUnitname())) {
                        yData += "yAxisIndex:" + yaxisIndexList.get(0).getIndex() + ",";
                    } else {
                        yData += "yAxisIndex:" + yaxisIndexList.get(1).getIndex() + ",";
                    }
            }
            /*List<IndIndex> sortIndIndexList2 = new ArrayList<>();
            indexList.stream().sorted((m1, m2) -> m1.getD1().compareTo(m2.getD1())).forEach(e -> sortIndIndexList2.add(e));*/

            List<IndIndex> indexList = dataCenterMapper.getListByFirstDayAndLastDay(indIndexCfgList.get(i).getId(),firstDayParam,lastDayParam);
            for(int j=0;j<chartDataList.size();j++){
                for(int k=0;k<indexList.size();k++) {
                    if (chartDataList.get(j).getD1().equals(indexList.get(k).getD1())){
                        chartDataList.get(j).setVal(indexList.get(k).getVal());
                    }
                }
            }

            //截取日期
           /* if(fqcyArr[0] == 3 || fqcyArr[0] == 4){
                for(IndIndex indIndex : chartDataList){
                    indIndex.setD1(indIndex.getD1().substring(0,indIndex.getD1().lastIndexOf("-")));
                }
            }

            if(fqcyArr[0] == 5){
                for(IndIndex indIndex : chartDataList){
                    indIndex.setD1(indIndex.getD1().substring(0,indIndex.getD1().indexOf("-")));
                }
            }
*/
            yData += "data:[";
           /* for(IndIndex indIndex :groupList){
                System.out.println("---------------val:" + indIndex.getVal());
                yData += SINGLE_QUOTE + indIndex.getVal()+ SINGLE_QUOTE + ",";
            }*/


            for (int m=0;m<chartDataList.size();m++) {
                yData += SINGLE_QUOTE + chartDataList.get(m).getVal() + SINGLE_QUOTE;
                if(m == chartDataList.size() -1){
                }else{
                    yData += ",";
                }
            }

            if(i == indIndexCfgList.size()-1){
                yData += "]}";
            }else{
                yData += "]},";
            }

        }
        yData += "]";
        legend += "]";

        Map map = new HashMap<>();
        map.put("xData",xData);
        map.put("yData",yData);
        map.put("legend",legend);
        map.put("chartName",chartName);
        map.put("unit1",unitName1);
        map.put("unit2",unitName2);
        map.put("dataOrigin", originName);
        map.put("success", success);
        return map;
    }

    //判断右侧勾选的checkbox的菜单有几种单位
    @RequestMapping(value = "/dataCenter/unitCount", method = RequestMethod.POST)
    @ResponseBody
    public Object unitCount(@RequestParam(value = "clickCheckboxArr[]") String[] clickCheckboxArr) {
        List<IndIndexCfg> indIndexCfgList = new ArrayList<>();
        for(int i=0;i<clickCheckboxArr.length;i++){
            IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(clickCheckboxArr[i]);
            indIndexCfgList.add(indIndexCfg);
        }
        List<String> unitList = new ArrayList<>();
        for(IndIndexCfg indIndexCfg :indIndexCfgList){
            //没有数据的指标不累计在单位的集合里
            List<IndIndex> indexDataList = dataCenterMapper.getDataByMenuId(indIndexCfg.getId());
            if(indexDataList.size()>0 && indexDataList != null) {
                if (!unitList.contains(indIndexCfg.getUnit())) {
                    unitList.add(indIndexCfg.getUnit());
                }
            }
        }
        Map map = new HashMap<>();
        map.put("unitCount",unitList.size());
        return map;
    }

    //获取默认图表表格数据
    @RequestMapping(value = "/dataCenter/getDefaultTableData", method = RequestMethod.POST)
    @ResponseBody
    public Object getDefaultTableData(String id, String startDate, String endDate, int gotoPage) {
        Map map  = new HashMap<>();
        IndIndexCfg menu = dataCenterMapper.getMenuById(id);
        if (menu != null) {
            int frequency = menu.getFqcy();
            int totalCount = 0;
            List<IndIndex> tableIndexList = null;
            if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) { //当开始时间和结束时间为空的时
                tableIndexList = dataCenterMapper.getDataByMenuIdWithLimit(id, (gotoPage - 1) * DATATABLE_LIMIT, DATATABLE_LIMIT);
                totalCount = dataCenterMapper.countIndexDataById(id);
            } else {
                tableIndexList = dataCenterMapper.getDataByMenuIdByDataWithLimit(id, startDate, endDate, (gotoPage - 1) * DATATABLE_LIMIT, DATATABLE_LIMIT);
                totalCount = dataCenterMapper.countIndexDataByIdAndDate(menu.getId(), startDate, endDate);
            }
            //截取日期到其频率
            //最小维度为3或者4,月份或者季度的时候,截取到年月
            if (frequency == 3 || frequency == 4) {
                for (IndIndex indIndex : tableIndexList) {
                    indIndex.setD1(indIndex.getD1().substring(0, indIndex.getD1().lastIndexOf("-")));
                }
            }

            //最小维度为5,年的时候,截取到年
            if (frequency == 5) {
                for (IndIndex indIndex : tableIndexList) {
                    indIndex.setD1(indIndex.getD1().substring(0, indIndex.getD1().indexOf("-")));
                }
            }

            //处理分页的数据
            int totalPage = 0;
            if (totalCount > DATATABLE_LIMIT) {
                int a = totalCount / DATATABLE_LIMIT;
                totalPage = totalCount % DATATABLE_LIMIT == 0 ? a : a + 1;
            } else {
                totalPage = 1;
            }
            map.put("menu", menu);
            map.put("totalCount", totalCount);
            map.put("currentPage", gotoPage);
            map.put("totalPage", totalPage);
            map.put("tableIndexList", tableIndexList);
            return map;
        } else {
            return null;
        }
    }

    //判断树菜单勾选的指标有没有数据
    @RequestMapping(value = "/dataCenter/checkMenuHasData", method = RequestMethod.POST)
    @ResponseBody
    public Object checkMenuHasData(String menuid) {
        Map map = new HashMap<>();
        int dataCount = dataCenterMapper.countIndexDataById(menuid);
        map.put("dataCount",dataCount);
        return map;
    }

}