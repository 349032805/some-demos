package kitt.core.editor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuxinjie on 15/11/11.
 */
public class EditorPathFormat implements Serializable {
    private final String TIME = "time";
    private final String FULL_YEAR = "yyyy";
    private final String YEAR = "yy";
    private final String MONTH = "mm";
    private final String DAY = "dd";
    private final String HOUR = "hh";
    private final String MINUTE = "ii";
    private final String SECOND = "ss";
    private final String RAND = "rand";
    private LocalDateTime currentDate = LocalDateTime.now();

    public EditorPathFormat() {}

    public String parse(String input) {
        Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}", 2);
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb, getString(matcher.group(1)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String format(String input) {
        return input.replace("\\", "/");
    }

    public String parse(String input, String filename) {
        Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}", 2);
        Matcher matcher = pattern.matcher(input);
        String matchStr = null;
        StringBuffer stringBuffer = new StringBuffer();
        while(matcher.find()) {
            matchStr = matcher.group(1);
            if(matchStr.indexOf("filename") != -1) {
                filename = filename.replace("$", "\\$").replaceAll("[\\/:*?\"<>|]", "");
                matcher.appendReplacement(stringBuffer, filename);
            } else {
                matcher.appendReplacement(stringBuffer, getString(matchStr));
            }
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private String getString(String pattern) {
        pattern = pattern.toLowerCase();
        return pattern.indexOf("time") != -1?getTimestamp():(pattern.indexOf("yyyy") != -1?getFullYear():(pattern.indexOf("yy") != -1?getYear():(pattern.indexOf("mm") != -1?getMonth():(pattern.indexOf("dd") != -1?getDay():(pattern.indexOf("hh") != -1?getHour():(pattern.indexOf("ii") != -1?getMinute():(pattern.indexOf("ss") != -1?getSecond():(pattern.indexOf("rand") != -1?getRandom(pattern):pattern))))))));
    }

    private String getTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    private String getFullYear() {
        return String.valueOf(currentDate.getYear());
    }

    private String getYear() {
        return String.valueOf(currentDate.getYear()).substring(2, 4);
    }

    private String getMonth() {
        return String.valueOf(currentDate.getMonthValue());
    }

    private String getDay() {
        if (currentDate.getDayOfMonth() < 10) {
            return "0" + currentDate.getDayOfMonth();
        } else {
            return String.valueOf(currentDate.getDayOfMonth());
        }
    }

    private String getHour() {
        if(currentDate.getHour() < 10) {
            return "0" + currentDate.getHour();
        } else {
            return String.valueOf(currentDate.getHour());
        }
    }

    private String getMinute() {
        if(currentDate.getMinute() < 10){
            return "0" + currentDate.getMinute();
        } else {
            return String.valueOf(currentDate.getMinute());
        }
    }

    private String getSecond() {
        if(currentDate.getSecond() < 10){
            return "0" + currentDate.getSecond();
        } else {
            return String.valueOf(currentDate.getSecond());
        }
    }

    private String getRandom(String pattern) {
        pattern = pattern.split(":")[1].trim();
        int length = Integer.parseInt(pattern);
        return String.valueOf(Math.random()).replace(".", "").substring(0, length);
    }

}

