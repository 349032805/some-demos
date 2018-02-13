package kitt.core.service;

import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yimei on 15/7/17.
 */
@Service
public class NumberDateFormat {

    /**
     * 设置固定小数长度的数字
     * @param number  number
     * @param length  保留的小数位
     * @return        固定小数长度的数字
     */
    public String setNumberLength(Double number, int length){
        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(); //获得格式化类对象
        if(length > 0) {
            String pattern = "0.";
            for (int i = 0; i < length; i++) {
                pattern += "0";
            }
            decimalFormat.applyPattern(pattern);     //设置小数点位数(两位) 余下的会四舍五入
            return decimalFormat.format(number).toString();
        } else{
            String pattern = "0";
            decimalFormat.applyPattern(pattern);
            return decimalFormat.format(number).toString();
        }
    }

    /**
     * 设置 季度（日期） 的格式
     * @param d1
     * @return
     */
    public String setQuarterDate(String d1) {
        int num = d1.indexOf('-');
        String d11 = d1.substring(0, num + 1);
        String d12 = d1.substring(num + 1, d1.length());
        if(d12.equals("01") || d12.equals("02")){
            d12 = "03";
        } else if(d12.equals("04") || d12.equals("05")){
            d12 = "06";
        } else if(d12.equals("07") || d12.equals("08")){
            d12 = "09";
        } else if(d12.equals("10") || d12.equals("11")){
            d12 = "12";
        }
        return d11 + d12;
    }

    /**
     * 判断是否有日期在同一个星期内
     * @param dateList 日期列表
     * @param dateTemp 要判断的日期
     * @return true 是在一个星期内，false 是不在一个星期内
     */
    public boolean doCheckDateIfInOneWeek(List<String> dateList, String dateTemp){
        LocalDate date1 = LocalDate.parse(dateTemp);
        for(String date : dateList){
            LocalDate date2 = LocalDate.parse(date);
            if(checkTwoDateIfInOneWeek(date1, date2)){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查两个日期是否在同一个星期内
     * @param date1 日期1
     * @param date2 日期2
     * @return true 是在同一个星期内，false 不在同一个星期内
     */
    public boolean checkTwoDateIfInOneWeek(LocalDate date1, LocalDate date2) {
        if(date1.equals(date2)) return true;
        Map<String, LocalDate> map1 = getTheFirstLastDayOfWeek(date1);
        Map<String, LocalDate> map2 = getTheFirstLastDayOfWeek(date2);
        LocalDate date1FirstDay = map1.get("firstDay");
        LocalDate date1LastDay = map1.get("lastDay");
        LocalDate date2FirstDay = map2.get("firstDay");
        LocalDate date2LastDay = map2.get("lastDay");
        if((date1.isBefore(date2) && date1.plusDays(7).isAfter(date2) || date1.isAfter(date2) && date1.minusDays(7).isBefore(date2)) && date1FirstDay.equals(date2FirstDay) && date1LastDay.equals(date2LastDay)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取 某一天 所在周的 第一天 和 最后一天
     * @param localDate    某一天日期
     * @return  firstDay   星期一那一天的日期
     *          lastDay    星期日那一天的日期
     */
    public Map<String, LocalDate> getTheFirstLastDayOfWeek(LocalDate localDate){
        Map<String, LocalDate> map = new HashMap<>();
        LocalDate localDate1 = null;
        LocalDate localDate2 = null;
        if(localDate.getDayOfWeek().getValue() == 1){
            localDate1 = localDate;
            localDate2 = localDate.plusDays(6);
        } else if(localDate.getDayOfWeek().getValue() == 2){
            localDate1 = localDate.minusDays(1);
            localDate2 = localDate.plusDays(5);
        } else if(localDate.getDayOfWeek().getValue() == 3){
            localDate1 = localDate.minusDays(2);
            localDate2 = localDate.plusDays(4);
        } else if(localDate.getDayOfWeek().getValue() == 4){
            localDate1 = localDate.minusDays(3);
            localDate2 = localDate.plusDays(3);
        } else if(localDate.getDayOfWeek().getValue() == 5){
            localDate1 = localDate.minusDays(4);
            localDate2 = localDate.plusDays(2);
        } else if(localDate.getDayOfWeek().getValue() == 6){
            localDate1 = localDate.minusDays(5);
            localDate2 = localDate.plusDays(1);
        } else if(localDate.getDayOfWeek().getValue() == 7){
            localDate1 = localDate.minusDays(6);
            localDate2 = localDate;
        }
        map.put("firstDay", localDate1);
        map.put("lastDay", localDate2);
        return map;
    }

    /**
     * 通过 DayOfWeek（星期几）  获取 对应的String
     * @param dayOfWeek
     * @return
     */
    public String getWeekTextByDayOfWeek(DayOfWeek dayOfWeek){
        switch (dayOfWeek){
            case MONDAY:
                return "星期一";
            case TUESDAY:
                return "星期二";
            case WEDNESDAY:
                return  "星期三";
            case THURSDAY:
                return "星期四";
            case FRIDAY:
                return "星期五";
            case SATURDAY:
                return "星期六";
            case SUNDAY:
                return "星期日";
        }
        return null;
    }


    public String getWeekTextByNumber(int weekday){
        switch (weekday){
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            case 7:
                return "星期日";
        }
        return null;
    }

}
