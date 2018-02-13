package kitt.site.ext.freemarker.method;

import freemarker.template.*;
import kitt.ext.SuperPeriod;
import kitt.site.ext.freemarker.Java8ObjectWrapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.List;

/**
 * Created by fanjun on 16/2/26.
 */
public class PeriodOrTimeMethod implements TemplateMethodModelEx{
    //文章的时间,参数为年月日时分秒
    //处理成:如果超过24小时,显示按参数格式,如果是24小时之内,显示距离当前的时间的间隔长度

    public Object exec(List arguments) throws TemplateModelException {
        if(arguments.size() !=2)
            throw new TemplateModelException("arguments takes at least 2 parameter");
        Object ret = arguments.get(0);
        LocalDateTime paramTime = null;
        if(ret instanceof Java8ObjectWrapper.SimpleLocalDateTime){
            paramTime=((Java8ObjectWrapper.SimpleLocalDateTime) ret).getLocalDateTime();
        }

        //if you have better method,please rewrite the solution when the paramTime is null,
        //make its type to be LocalDateTime
        if(paramTime == null){
            paramTime = formatStringToLocalDateTime(ret.toString());
        }

        String dateStr ="";
        SuperPeriod period=SuperPeriod.between(paramTime, LocalDateTime.now());
        if(period.getYears() == 0 && period.getMonths()==0 && period.getDays()==0 && period.getHours()<24){
            dateStr = formatPeriod(paramTime, LocalDateTime.now());
        }else{
            String patternStr = arguments.get(1).toString();
            dateStr = paramTime.format(DateTimeFormatter.ofPattern(patternStr));
        }

        return dateStr;
    }
    protected String formatPeriod(Temporal begin, Temporal end){
        SuperPeriod period=SuperPeriod.between(begin, end);
        if(period.getHours()>0)
            return period.getHours()+"小时前";
        if(period.getMinutes()>0)
            return period.getMinutes()+"分钟前";
        return "1分钟内";
    }

    protected LocalDateTime formatStringToLocalDateTime(String timeStr){
        int year = Integer.parseInt(timeStr.substring(0,4));
        int month = Integer.parseInt(timeStr.substring(5,7));
        int day = Integer.parseInt(timeStr.substring(8,10));
        int hour = Integer.parseInt(timeStr.substring(11,13));
        int minute = Integer.parseInt(timeStr.substring(14,16));
        int second = Integer.parseInt(timeStr.substring(17,19));
        return LocalDateTime.of(year,month,day,hour,minute,second);
    }
}
