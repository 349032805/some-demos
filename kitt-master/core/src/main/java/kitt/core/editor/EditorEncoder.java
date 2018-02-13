package kitt.core.editor;

/**
 * Created by liuxinjie on 15/11/11.
 */
public class EditorEncoder {
    public EditorEncoder() {
    }

    public static String toUnicode(String input) {
        StringBuilder builder = new StringBuilder();
        char[] chars = input.toCharArray();
        for(int i=0; i<chars.length; ++i) {
            char ch = chars[i];
            if(ch < 256) {
                builder.append(ch);
            } else {
                builder.append("\\u" + Integer.toHexString(ch & '\uffff'));
            }
        }
        return builder.toString();
    }
}
