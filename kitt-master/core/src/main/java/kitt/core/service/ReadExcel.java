package kitt.core.service;

import kitt.core.bl.DataService;
import kitt.core.domain.*;
import kitt.core.persistence.DataCenterMapper;
import kitt.core.persistence.PricePortMapper;
import kitt.core.util.text.TextCheck;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;


/**
 * Created by yimei on 15/7/15.
 */
@Service
public class ReadExcel {
    @Autowired
    private DataCenterMapper dataCenterMapper;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private NumberDateFormat numberDateFormat;
    @Autowired
    private DataService dataService;
    @Autowired
    private TextCheck textCheck;
    @Autowired
    private PricePortMapper pricePortMapper;

    private static final String EXTENSION_XLS = "xls";
    private static final String EXTENSION_XLSX = "xlsx";

    private Workbook getWorkbook(String filePath) throws Exception {
        Workbook workbook = null;
        InputStream inputStream = new FileInputStream(fileStore.getFileByFilePath(filePath));
        if (filePath.endsWith(EXTENSION_XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (filePath.endsWith(EXTENSION_XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }

    /**
     * 读取数据中心Excel
     * @param filePath       Excel文件路径
     */
    public Map<String, Object> doReadDataExcel (String filePath) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String error = "";
        List<DataObject> dataObjectList = new ArrayList<>();
        Workbook workbook = this.getWorkbook(filePath);
        Sheet sheet = workbook.getSheetAt(0);
        if(sheet == null) {
            error = EnumRemindInfo.Admin_Excel_File_NULL.value();
        } else {
            int firstRowIndex = sheet.getFirstRowNum();
            int lastRowIndex = sheet.getLastRowNum();
            Row firstRow = sheet.getRow(firstRowIndex);
            String parentname = String.valueOf(firstRow.getCell(0));
            if (StringUtils.isBlank(parentname)) {
                error = "父节点名称为空！";
            } else {
                parentname = parentname.trim();
                if (StringUtils.isBlank(dataCenterMapper.getIdByName(parentname))) {
                    error = "父节点名称错误，不存在名称为： " + parentname + " 的节点！";
                } else if (dataCenterMapper.getMenuById(dataCenterMapper.getIdByName(parentname)).isleaf()) {
                    error = "父节点：" + parentname + " 是叶子节点，不能添加数据！";
                } else {
                    String parentidid = dataCenterMapper.getIdByName(parentname);
                    int nums = dataCenterMapper.getMenuCountLeafByParentId(parentidid) + 1;
                    if (nums > firstRow.getLastCellNum()) nums = firstRow.getLastCellNum();
                    loop:
                    for (int i = 1; i < nums; i++) {
                        if (StringUtils.isBlank(firstRow.getCell(i).toString().replace(" ", ""))) break;
                        String name = "";
                        try {
                            name = firstRow.getCell(i).getStringCellValue().replace(" ", "");
                        } catch (Exception e) {
                            name = numberDateFormat.setNumberLength(Double.valueOf(firstRow.getCell(i).toString()), 0);
                        }
                        String indindexcfgid = dataCenterMapper.getIdByName(name);
                        if (StringUtils.isBlank(indindexcfgid) || !dataCenterMapper.getMenuById(indindexcfgid).getParentid().equals(parentidid)) {
                            error = parentname + " 节点下，不存在名称为：" + name + " 的子节点！";
                        } else {
                            IndIndexCfg indIndexCfg = dataCenterMapper.getMenuById(indindexcfgid);
                            List<IndIndex> indIndexList = new ArrayList<>();
                            List<String> dateList = new ArrayList<>();
                            for (int j = firstRowIndex + 1; j <= lastRowIndex; j++) {
                                if (sheet.getRow(j) != null && sheet.getRow(j).getCell(0) != null && sheet.getRow(j).getCell(i) != null && !StringUtils.isBlank(sheet.getRow(j).getCell(0).toString()) && !StringUtils.isBlank(sheet.getRow(j).getCell(i).toString())) {
                                    String dateTemp = "";
                                    String date = "";
                                    String value = "";
                                    try {
                                        dateTemp = (new java.text.SimpleDateFormat("yyyy-MM-dd")).format(sheet.getRow(j).getCell(0).getDateCellValue());
                                    } catch (Exception e) {
                                        error = "第 " + (j + 1) + " 行日期格式错误！";
                                        break loop;
                                    }
                                    switch (indIndexCfg.getFqcy()) {
                                        case 3:
                                            date = dateTemp.substring(0, 7);
                                            break;
                                        case 4:
                                            date = numberDateFormat.setQuarterDate(dateTemp.substring(0, 7));
                                            break;
                                        case 5:
                                            date = dateTemp.substring(0, 4);
                                            break;
                                        default:
                                            date = dateTemp;
                                            break;
                                    }
                                    try {
                                        Double doubleValue = Double.valueOf(sheet.getRow(j).getCell(i).toString());
                                        value = numberDateFormat.setNumberLength(doubleValue, indIndexCfg.getPrecesion());
                                    } catch (NumberFormatException e) {
                                        error = "第 " + (j + 1) + " 行，数据格式错误，不是合法的数字！";
                                        break loop;
                                    }
                                    if (dateList.contains(date)) {
                                        switch (indIndexCfg.getFqcy()) {
                                            case 1:
                                                error = indIndexCfg.getName() + " 节点(频率：每天一条)，" + dateTemp + " 这一天出现多条数据！";
                                                break;
                                            case 3:
                                                error = indIndexCfg.getName() + " 节点(频率：每月一条)，" + dateTemp + " 这一月出现多条数据！";
                                                break;
                                            case 4:
                                                error = indIndexCfg.getName() + " 节点(频率：每季度一条)，" + dateTemp + " 这一季度出现多条数据！";
                                                break;
                                            case 5:
                                                error = indIndexCfg.getName() + " 节点(频率：每年一条)，" + dateTemp + " 这一年出现多条数据！";
                                                break;
                                            default:
                                                break;
                                        }
                                        break loop;
                                    } else if (indIndexCfg.getFqcy() == 2 && numberDateFormat.doCheckDateIfInOneWeek(dateList, dateTemp)) {
                                        error = indIndexCfg.getName() + " 节点(频率：每周一条)，" + dateTemp + " 这一周出现多条数据！";
                                        break loop;
                                    } else {
                                        dateList.add(date);
                                    }
                                    indIndexList.add(new IndIndex(indindexcfgid, date, value));
                                }
                            }
                            dataObjectList.add(new DataObject(parentname, name, indIndexList));
                        }
                    }
                }
            }
        }
        map.put("error", error);
        map.put("dataObjectList", dataObjectList);
        return map;
    }


    /**
     * 读取港口价格Excel
     * @param filePath              Excel路径
     */
    private final String Group_Str = "团队";
    private final String Region_Offer = "区域";
    private final String Name_Product = "煤种";
    private final String Quality_Coal = "煤炭质量";
    private final String Type_Price = "价格类型";
    private final String Place_Dispatch = "发运地";
    private final String Place_Receipt = "收货地";
    private final String TM_Title = "全水";
    private final String ASH_Title = "灰分";
    private final String RV_Title = "挥发分";
    private final String RS_Title = "硫分";
    private final String NCV_Title = "热值";
    private final String Bond_Index = "粘结指数";

    public MapObject doReadPricePortExcel (String filePath, Admin admin) throws Exception {
        MapObject map = new MapObject();
        map.setSuccess(false);
        Workbook workbook = this.getWorkbook(filePath);
        Sheet sheet = workbook.getSheetAt(0);
        if(sheet == null) {
            map.setError(EnumRemindInfo.Admin_Excel_File_NULL.value());
        } else {
            int firstRowIndex = sheet.getFirstRowNum();
            int lastRowIndex = sheet.getLastRowNum();
            int totalColumns = 0;
            try {
                totalColumns = sheet.getRow(0).getPhysicalNumberOfCells();
            } catch (Exception e) {
                map.setError(EnumRemindInfo.Admin_ImportExcel_BadTemplate.value());
                return map;
            }
            if (totalColumns < 13) {
                map.setError(EnumRemindInfo.Admin_Excel_Invalid_NotTemplate.value());
            } else {
                Row row1 = sheet.getRow(firstRowIndex);
                Row row2 = sheet.getRow(firstRowIndex + 1);
                try {
                    if (textCheck.trimStartEndBackSpace(row1.getCell(0).getStringCellValue()).equals(Group_Str)
                            && textCheck.trimStartEndBackSpace(row1.getCell(1).getStringCellValue()).equals(Region_Offer)
                            && textCheck.trimStartEndBackSpace(row1.getCell(2).getStringCellValue()).equals(Name_Product)
                            && textCheck.trimStartEndBackSpace(row1.getCell(3).getStringCellValue()).equals(Quality_Coal)
                            && textCheck.trimStartEndBackSpace(row1.getCell(9).getStringCellValue()).equals(Type_Price)
                            && textCheck.trimStartEndBackSpace(row1.getCell(10).getStringCellValue()).equals(Place_Dispatch)
                            && textCheck.trimStartEndBackSpace(row1.getCell(11).getStringCellValue()).equals(Place_Receipt)
                            && textCheck.trimStartEndBackSpace(row2.getCell(3).getStringCellValue()).equals(TM_Title)
                            && textCheck.trimStartEndBackSpace(row2.getCell(4).getStringCellValue()).equals(ASH_Title)
                            && textCheck.trimStartEndBackSpace(row2.getCell(5).getStringCellValue()).equals(RV_Title)
                            && textCheck.trimStartEndBackSpace(row2.getCell(6).getStringCellValue()).equals(RS_Title)
                            && textCheck.trimStartEndBackSpace(row2.getCell(7).getStringCellValue()).equals(NCV_Title)
                            && textCheck.trimStartEndBackSpace(row2.getCell(8).getStringCellValue()).equals(Bond_Index)) {
                        List<String> dateList = new ArrayList<String>();
                        for (int j = 12; j < totalColumns; j++) {
                            if (row1.getCell(j) != null && row1.getCell(j).getDateCellValue() != null) {
                                try {
                                    dateList.add((new java.text.SimpleDateFormat("yyyy-MM-dd")).format(row1.getCell(j).getDateCellValue()));
                                } catch (Exception e) {
                                    map.setError("第 " + (j + 1) + " 列日期格式错误");
                                    return map;
                                }
                            } else {
                                break;
                            }
                        }
                        for (int i = firstRowIndex + 2; i <= lastRowIndex; i++) {
                            Row row = sheet.getRow(i);
                            if (row != null && row.getCell(1) != null && row.getCell(2) != null && row.getCell(9) != null && row.getCell(10) != null && row.getCell(11) != null) {
                                String team = textCheck.trimStartEndBackSpace(String.valueOf(row.getCell(0)));
                                String region = textCheck.trimStartEndBackSpace(String.valueOf(row.getCell(1)));
                                String name = textCheck.trimStartEndBackSpace(String.valueOf(row.getCell(2)));
                                String pricetype = textCheck.trimStartEndBackSpace(String.valueOf(row.getCell(9)));
                                String placedispatch = textCheck.trimStartEndBackSpace(String.valueOf(row.getCell(10)));
                                String placereceipt = textCheck.trimStartEndBackSpace(String.valueOf(row.getCell(11)));
                                String unit = textCheck.trimStartEndBackSpace(String.valueOf(row2.getCell(12)));
                                if (!StringUtils.isBlank(region) && !StringUtils.isBlank(name) && !StringUtils.isBlank(pricetype)
                                        && !StringUtils.isBlank(unit)) {
                                    if (region.length() > 30) {
                                        map.setError("第 " + (i + 1) + "行 地区字段错误, 地区字段不能超过30个文字");
                                    } else if (name.length() > 30) {
                                        map.setError("第 " + (i + 1) + "行 煤种字段错误, 煤种字段不能超过30个文字");
                                    } else if (pricetype.length() > 20) {
                                        map.setError("第 " + (i + 1) + "行 价格类型字段错误, 价格类型字段不能超过20个文字");
                                    } else if (unit.length() > 20) {
                                        map.setError("第 " + 13 + "列 单位字段错误, 单位字段不能超过20个文字");
                                    } else if (!StringUtils.isBlank(team) && team.length() > 30) {
                                        map.setError("第 " + (i + 1) + "行 团队字段错误, 团队字段不能超过30个文字");
                                    } else if (!StringUtils.isBlank(placedispatch) && placedispatch.length() > 30) {
                                        map.setError("第 " + (i + 1) + "行 发运地字段错误, 发运地字段不能超过30个文字");
                                    } else if (!StringUtils.isBlank(placereceipt) && placereceipt.length() > 30) {
                                        map.setError("第 " + (i + 1) + "行 收货地字段错误, 收货地字段不能超过30个文字");
                                    }
                                    if (!StringUtils.isBlank(map.getError())) return map;
                                    String placeInfo = "第 " + (i + 1) + " 行 ";
                                    BigDecimal tempBigDecimal = BigDecimal.valueOf(0);
                                    int tempInt = 0;
                                    BigDecimal TM;
                                    BigDecimal TM02;
                                    if (!StringUtils.isBlank(String.valueOf(row.getCell(3)))) {
                                        try {
                                            map = doHandleDataBigDecimal(String.valueOf(row.getCell(3)), BigDecimal.valueOf(50.0), placeInfo + 6 + " 列");
                                            if (!map.isSuccess()) return map;
                                            TM = StringUtils.isBlank(map.getValue1()) ? null : BigDecimal.valueOf(Double.valueOf(map.getValue1()));
                                            TM02 = StringUtils.isBlank(map.getValue2()) ? null : BigDecimal.valueOf(Double.valueOf(map.getValue2()));
                                            if ((TM != null && (TM.compareTo(BigDecimal.valueOf(50.0)) == 1 || TM.compareTo(BigDecimal.valueOf(0.1)) == -1)) || (TM02 != null && (TM02.compareTo(BigDecimal.valueOf(50.0)) == 1 || TM02.compareTo(BigDecimal.valueOf(0.1)) == -1))) {
                                                map.setError(placeInfo + 4 + " 列 数据不在合法范围内");
                                                return map;
                                            } else if ((TM != null && (TM.toString().length() - TM.toString().indexOf(".") > 3)) || (TM02 != null && (TM02.toString().length() - TM02.toString().indexOf(".") > 3))) {
                                                map.setError(placeInfo + 4 + " 列 数据不能超过2位小数");
                                                return map;
                                            }
                                            if (TM != null && TM02 != null && TM.compareTo(TM02) == 1) { tempBigDecimal = TM.add(TM02); TM = tempBigDecimal.subtract(TM); TM02 = tempBigDecimal.subtract(TM02); }
                                        } catch (Exception e) {
                                            map.setError(placeInfo + 4 + " 列数据非法");
                                            return map;
                                        }
                                    } else {
                                        TM = null;
                                        TM02 = null;
                                    }
                                    BigDecimal ASH;
                                    BigDecimal ASH02;
                                    if (!StringUtils.isBlank(String.valueOf(row.getCell(4)))) {
                                        try {
                                            map = doHandleDataBigDecimal(String.valueOf(row.getCell(4)), BigDecimal.valueOf(50.0), placeInfo + 3 + " 列");
                                            if (!map.isSuccess()) return map;
                                            ASH = StringUtils.isBlank(map.getValue1()) ? null : BigDecimal.valueOf(Double.valueOf(map.getValue1()));
                                            ASH02 = StringUtils.isBlank(map.getValue2()) ? null : BigDecimal.valueOf(Double.valueOf(map.getValue2()));
                                            if ((ASH != null && (ASH.compareTo(BigDecimal.valueOf(50.0)) == 1 || ASH.compareTo(BigDecimal.valueOf(0.1)) == -1)) || (ASH02 != null && (ASH02.compareTo(BigDecimal.valueOf(50.0)) == 1 || ASH02.compareTo(BigDecimal.valueOf(0.1)) == -1))) {
                                                map.setError(placeInfo + 5 + " 列 数据不在合法范围内");
                                                return map;
                                            } else if ((ASH != null && (ASH.toString().length() - ASH.toString().indexOf(".") > 2)) || (ASH02 != null && (ASH02.toString().length() - ASH02.toString().indexOf(".") > 2))) {
                                                map.setError(placeInfo + 5 + " 列 数据不能超过1位小数");
                                                return map;
                                            }
                                            if (ASH != null && ASH02 != null && ASH.compareTo(ASH02) == 1) { tempBigDecimal = ASH.add(ASH02); ASH = tempBigDecimal.subtract(ASH); ASH02 = tempBigDecimal.subtract(ASH02); }
                                        } catch (Exception e) {
                                            map.setError(placeInfo + 5 + " 列数据非法");
                                            return map;
                                        }
                                    } else {
                                        ASH = null;
                                        ASH02 = null;
                                    }
                                    BigDecimal RV;
                                    BigDecimal RV02;
                                    if (!StringUtils.isBlank(String.valueOf(row.getCell(5)))) {
                                        try {
                                            map = doHandleDataBigDecimal(String.valueOf(row.getCell(5)), BigDecimal.valueOf(50.0), placeInfo + 4 + " 列");
                                            if (!map.isSuccess()) return map;
                                            RV = StringUtils.isBlank(map.getValue1()) ? null : BigDecimal.valueOf(Double.valueOf(map.getValue1()));
                                            RV02 = StringUtils.isBlank(map.getValue2()) ? null : BigDecimal.valueOf(Double.valueOf(map.getValue2()));
                                            if ((RV != null && (RV.compareTo(BigDecimal.valueOf(50.0)) == 1 || RV.compareTo(BigDecimal.valueOf(0.1)) == -1)) || (RV02 != null && (RV02.compareTo(BigDecimal.valueOf(50.0)) == 1 || RV02.compareTo(BigDecimal.valueOf(0.1)) == -1))) {
                                                map.setError(placeInfo + 6 + " 列 数据不在合法范围内");
                                                return map;
                                            } else if ((RV != null && (RV.toString().length() - RV.toString().indexOf(".") > 3)) || (RV02 != null && (RV02.toString().length() - RV02.toString().indexOf(".") > 3))) {
                                                map.setError(placeInfo + 6 + " 列 数据不能超过2位小数");
                                                return map;
                                            }
                                            if (RV != null && RV02 != null && RV.compareTo(RV02) == 1) { tempBigDecimal = RV.add(RV02); RV = tempBigDecimal.subtract(RV); RV02 = tempBigDecimal.subtract(RV02); }
                                        } catch (Exception e) {
                                            map.setError(placeInfo + 6 + " 列数据非法");
                                            return map;
                                        }
                                    } else {
                                        RV = null;
                                        RV02 = null;
                                    }
                                    BigDecimal RS;
                                    BigDecimal RS02;
                                    if (!StringUtils.isBlank(String.valueOf(row.getCell(6)))) {
                                        try {
                                            map = doHandleDataBigDecimal(String.valueOf(row.getCell(6)), BigDecimal.valueOf(10.0), placeInfo + 5 + " 列");
                                            if (!map.isSuccess()) return map;
                                            RS = StringUtils.isBlank(map.getValue1()) ? null : BigDecimal.valueOf(Double.valueOf(map.getValue1()));
                                            RS02 = StringUtils.isBlank(map.getValue2()) ? null : BigDecimal.valueOf(Double.valueOf(map.getValue2()));
                                            if ((RS != null && (RS.compareTo(BigDecimal.valueOf(10.0)) == 1 || RS.compareTo(BigDecimal.valueOf(0.1)) == -1)) || (RS02 != null && (RS02.compareTo(BigDecimal.valueOf(10.0)) == 1 || RS02.compareTo(BigDecimal.valueOf(0.1)) == -1))) {
                                                map.setError(placeInfo + 7 + " 列 数据不在合法范围内");
                                                return map;
                                            } else if ((RS != null && (RS.toString().length() - RS.toString().indexOf(".") > 3)) || (RS02 != null && (RS02.toString().length() - RS02.toString().indexOf(".") > 3))) {
                                                map.setError(placeInfo + 7 + " 列 数据不能超过2位小数");
                                                return map;
                                            }
                                            if (RS != null && RS02 != null && RS.compareTo(RS02) == 1) { tempBigDecimal = RS.add(RS02); RS = tempBigDecimal.subtract(RS); RS02 = tempBigDecimal.subtract(RS02); }
                                        } catch (Exception e) {
                                            map.setError(placeInfo + 7 + " 列数据非法");
                                            return map;
                                        }
                                    } else {
                                        RS = null;
                                        RS02 = null;
                                    }
                                    Integer NCV;
                                    Integer NCV02;
                                    if (!StringUtils.isBlank(String.valueOf(row.getCell(7)))) {
                                        try {
                                            map = doHandleDataInteger(String.valueOf(row.getCell(7)), 7500, placeInfo + 8 + " 列");
                                            if (!map.isSuccess()) return map;
                                            NCV = StringUtils.isBlank(map.getValue1()) ? null : Integer.valueOf(Double.valueOf(map.getValue1()).intValue());
                                            NCV02 = StringUtils.isBlank(map.getValue2()) ? null : Integer.valueOf(Double.valueOf(map.getValue2()).intValue());
                                            if ((NCV != null && (NCV < 1 || NCV > 7500)) || (NCV02 != null && (NCV02 < 1 || NCV02 > 7500))) {
                                                map.setError(placeInfo + 8 + " 列 数据不在合法范围内");
                                                return map;
                                            }
                                            if (NCV != null && NCV02 != null && NCV > NCV02) { tempInt = NCV + NCV02; NCV = tempInt - NCV;  NCV02 = tempInt - NCV02; }
                                        } catch (Exception e) {
                                            map.setError(placeInfo + 8 + " 列数据非法");
                                            return map;
                                        }
                                    } else {
                                        NCV = null;
                                        NCV02 = null;
                                    }
                                    Integer BondIndex;
                                    Integer BondIndex02;
                                    if (!StringUtils.isBlank(String.valueOf(row.getCell(8)))) {
                                        try {
                                            map = doHandleDataInteger(String.valueOf(row.getCell(8)), 200, placeInfo + 7 + " 列");
                                            if (!map.isSuccess()) return map;
                                            BondIndex = StringUtils.isBlank(map.getValue1()) ? null : Integer.valueOf(Double.valueOf(map.getValue1()).intValue());
                                            BondIndex02 = StringUtils.isBlank(map.getValue2()) ? null : Integer.valueOf(Double.valueOf(map.getValue2()).intValue());
                                            if ((BondIndex != null && (BondIndex < 1 || BondIndex > 200)) || (BondIndex02 != null && (BondIndex02 < 1 || BondIndex02 > 200))) {
                                                map.setError(placeInfo + 9 + " 列 数据不在合法范围内");
                                                return map;
                                            }
                                            if (BondIndex != null && BondIndex02 != null && BondIndex > BondIndex02) { tempInt = BondIndex + BondIndex02; BondIndex = tempInt - BondIndex; BondIndex02 = tempInt - BondIndex02; }
                                        } catch (Exception e) {
                                            map.setError(placeInfo + 9 + " 列数据非法");
                                            return map;
                                        }
                                    } else {
                                        BondIndex = null;
                                        BondIndex02 = null;
                                    }
                                    PricePort pricePort = pricePortMapper.getPricePortBySelect(region, name, pricetype, NCV, NCV02, RS, RS02, TM, TM02, RV, RV02, ASH, ASH02, BondIndex, BondIndex02);
                                    if (pricePort == null) {
                                        pricePort = new PricePort(NCV, NCV02, RS, RS02, TM, TM02, RV, RV02, ASH, ASH02, BondIndex, BondIndex02, region, name, pricetype, unit, team, placedispatch, placereceipt, admin.getId(), admin.getUsername(), admin.getName());
                                        if (!dataService.addPricePort(pricePort)) {
                                            map.setError(EnumRemindInfo.Admin_System_Error.value());
                                            return map;
                                        }
                                    }
                                    List<PricePortValue> pricePortValueList = new ArrayList<>();
                                    for (int j = 12; j < dateList.size() + 12; j++) {
                                        if (!StringUtils.isBlank(String.valueOf(row.getCell(j)))) {
                                            String info = "第 " + (i+1) + " 行, " + (j + 1) + " 列";
                                            map = doHandleDataBigDecimal(String.valueOf(row.getCell(j)), BigDecimal.valueOf(1500.00), info);
                                            if (!map.isSuccess()) return map;
                                            try {
                                                BigDecimal value1 = StringUtils.isBlank(map.getValue1()) ? null : BigDecimal.valueOf(Double.valueOf(map.getValue1()));
                                                BigDecimal value2 = StringUtils.isBlank(map.getValue2()) ? null : BigDecimal.valueOf(Double.valueOf(map.getValue2()));
                                                BigDecimal tempBigDec = BigDecimal.valueOf(0);
                                                if (value1 != null && value2 != null && value1.compareTo(value2) == 1) { tempBigDec = value1.add(value2); value1 = tempBigDec.subtract(value1); value2 = tempBigDec.subtract(value2); }
                                                if (value1 != null || value2 != null) {
                                                    pricePortValueList.add(new PricePortValue(pricePort.getId(), String.valueOf(value1), String.valueOf(value2), dateList.get(j - 12), admin.getId(), admin.getUsername(), admin.getName()));
                                                }
                                            } catch (Exception e) {
                                                map.setError(info + "数据非法");
                                                return map;
                                            }
                                        }
                                    }
                                    map = pricePortMapper.getValueByPricePortIdAndDateList(pricePort.getId(), dateList, "第 " + (i + 1) + " 行, 已存在日期为: ");
                                    if (!map.isSuccess()) return map;
                                    if (pricePortValueList.size() > 0) {
                                        try {
                                            dataService.addPricePortValueList(pricePortValueList);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            map.setError(EnumRemindInfo.Admin_System_Error.value());
                                            return map;
                                        }
                                    }
                                } else if (!(StringUtils.isBlank(region) && StringUtils.isBlank(name) && StringUtils.isBlank(pricetype))) {
                                    map.setError("第 " + (i + 1) + "行 数据非法, 必填项( 地区, 煤种, 价格类型, 单位 ) 有空缺");
                                    return map;
                                }
                            }
                        }
                        map.setSuccess(true);
                    } else {
                        map.setError(EnumRemindInfo.Admin_ImportExcel_BadTemplate.value());
                        return map;
                    }
                } catch (Exception e) {
                    map.setError(EnumRemindInfo.Admin_ImportExcel_BadTemplate.value());
                    return map;
                }
            }
        }
        return map;
    }

    /**
     * 把字符串 拆成 2个值方法,
     * @param str               字符串
     * @param MaxValue          最大值
     * @param info              错误信息
     */
    public MapObject doHandleDataInteger(String str, int MaxValue, String info) {
        str = textCheck.trimStartEndBackSpace(str);
        MapObject map = new MapObject();
        boolean success = true;
        String[] res = {"", ""};
        if (str.contains("-")) {
            res = str.split("-");
            if (res.length != 2) {
                success = false;
                map.setError(info + "数据非法");
            }
        } else if (str.contains("<") || str.contains("≤")) {
            res[0] = String.valueOf(1);
            res[1] = str.substring(1, str.length());
        } else if (str.contains(">") || str.contains("≥")) {
            res[0] = str.substring(1, str.length());
            res[1] = String.valueOf(MaxValue);
        } else {
            res[0] = str;
        }
        if (!StringUtils.isBlank(textCheck.trimStartEndBackSpace(res[0])) && !textCheck.trimStartEndBackSpace(res[0]).equals("null")) {
            map.setValue1(textCheck.trimStartEndBackSpace(res[0]));
        }
        if (!StringUtils.isBlank(textCheck.trimStartEndBackSpace(res[1])) && !textCheck.trimStartEndBackSpace(res[1]).equals("null")) {
            map.setValue2(textCheck.trimStartEndBackSpace(res[1]));
        }
        map.setSuccess(success);
        return map;
    }

    /**
     * 把字符串 拆成 2个值方法,
     * @param str               字符串
     * @param MaxValue          最大值
     * @param info              错误信息
     */
    public MapObject doHandleDataBigDecimal(String str, BigDecimal MaxValue, String info) {
        str = textCheck.trimStartEndBackSpace(str);
        MapObject map = new MapObject();
        boolean success = true;
        String[] res = {"", ""};
        if (str.contains("-")) {
            res = str.split("-");
            if (res.length != 2) {
                success = false;
                map.setError(info + "数据非法");
            }
        } else if (str.startsWith("<") || str.startsWith("≤")) {
            res[0] = "0.1";
            res[1] = str.substring(1, str.length());
        } else if (str.startsWith(">") || str.startsWith("≥")) {
            res[0] = str.substring(1, str.length());
            res[1] = String.valueOf(MaxValue);
        } else {
            res[0] = str;
        }
        if (!StringUtils.isBlank(textCheck.trimStartEndBackSpace(res[0])) && !textCheck.trimStartEndBackSpace(res[0]).equals("null")) {
            map.setValue1(textCheck.trimStartEndBackSpace(res[0]));
        }
        if (!StringUtils.isBlank(textCheck.trimStartEndBackSpace(res[1])) && !textCheck.trimStartEndBackSpace(res[1]).equals("null")) {
            map.setValue2(textCheck.trimStartEndBackSpace(res[1]));
        }
        map.setSuccess(success);
        return map;
    }


}
