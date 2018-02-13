package kitt.core.util.text;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Created by yimei on 15/7/2.
 */
@Service
public class TextCheck {

    /**
     * 只能包含字母，汉字，数字，下划线
     */
    public boolean doTextCheck(String content) {
        if(StringUtils.isBlank(content)){
            return true;
        }
        String reg = "[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        return Pattern.compile(reg).matcher(content).matches();
    }

    /**
     * 只能包含字母，汉字，数字，下划线 百分号 斜杠 反斜杠
     */
    public boolean doTextCheckTwo(String content) {
        if(StringUtils.isBlank(content)){
            return true;
        }
        String reg = "[|/%\\\\a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        return Pattern.compile(reg).matcher(content).matches();
    }

    /**
     * 包括键盘上所有的字符
     */
    public boolean doTextCheckThree(String content) {
        if(StringUtils.isBlank(content)){
            return true;
        }
        String reg = "[ΑαΒβΓγΔδΕεΖζΗηΘθΙι℩Κκ∧λΜμΝνΞξΟοΠπΡρΣσςΤτΥυΦφΧχΨψΩω①②③④⑤⑥⑦⑧⑨⑩μm³㎡℃–「」IVX〔〕＞\uF70Dｖ＂¬µ≈＇｀·＃¥￥＄\\\\≠．± 　\n^p≤≥～~·•——（）‘’`!！@＠#＄$%％^……＾&＆*×＊(（)）_＿\\-+＋－=＝\\[\\]\\{}｛［｝］|｜＼／、【】、;:\"'；：‘“”,，<＜.。．>?/，《。》、？ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ０１２３４５６７８９a-zA-Z0-9_\\u4e00-\\u9fa5]{1,100000}+$";
        return Pattern.compile(reg).matcher(content).matches();
    }

    /***
     * 去掉字符串前后的空间，中间的空格保留
     */
    public String trimStartEndBackSpace(String str){
        if (StringUtils.isBlank(str) || str.equals("null")) return str;
        str = str.trim();
        while (str.startsWith(" ") || str.startsWith(" ") || str.startsWith("　")) {
            str = str.substring(1, str.length());
        }
        while (str.endsWith(" ") || str.endsWith(" ") || str.endsWith("　")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }



}
