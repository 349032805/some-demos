package kitt.core.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by liuxinjie on 15/11/11.
 */
public class EditorBaseState implements EditorState {
    private boolean state = false;
    private String info = null;
    private Map<String, String> infoMap = new HashMap();

    public EditorBaseState() {
            this.state = true;
        }

    public EditorBaseState(boolean state) {
            this.setState(state);
        }

    public EditorBaseState(boolean state, String info) {
        this.setState(state);
        this.info = info;
    }

    public EditorBaseState(boolean state, int infoCode) {
        this.setState(state);
        this.info = EditorAppInfo.getStateInfo(infoCode);
    }

    public boolean isSuccess() {
        return this.state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setInfo(int infoCode) {
        this.info = EditorAppInfo.getStateInfo(infoCode);
    }

    public String toJSONEditorString() {
        return this.toString();
    }

    public String toString() {
        String key = null;
        String stateVal = this.isSuccess()?EditorAppInfo.getStateInfo(0):this.info;
        StringBuilder builder = new StringBuilder();
        builder.append("{\"state\": \"" + stateVal + "\"");
        Iterator iterator = this.infoMap.keySet().iterator();

        while(iterator.hasNext()) {
            key = (String)iterator.next();
            builder.append(",\"" + key + "\": \"" + (String)this.infoMap.get(key) + "\"");
        }

        builder.append("}");
        return EditorEncoder.toUnicode(builder.toString());
    }

    public void putInfo(String name, String val) {
        this.infoMap.put(name, val);
    }

    public void putInfo(String name, long val) {
        this.putInfo(name, String.valueOf(val));
    }
}
