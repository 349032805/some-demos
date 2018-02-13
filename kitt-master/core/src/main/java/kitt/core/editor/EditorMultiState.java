package kitt.core.editor;

import java.util.*;

/**
 * Created by liuxinjie on 15/11/11.
 */
public class EditorMultiState implements EditorState {
    private boolean state = false;
    private String info = null;
    private Map<String, Long> intMap = new HashMap();
    private Map<String, String> infoMap = new HashMap();
    private List<String> stateList = new ArrayList();

    public EditorMultiState(boolean state) {
        this.state = state;
    }

    public EditorMultiState(boolean state, String info) {
        this.state = state;
        this.info = info;
    }

    public boolean isSuccess() {
        return this.state;
    }

    public void addState(EditorState editorState) {
        this.stateList.add(editorState.toJSONEditorString());
    }

    public void putInfo(String name, String val) {
        this.infoMap.put(name, val);
    }

    public String toJSONEditorString() {
        String stateVal = this.isSuccess() ? EditorAppInfo.getStateInfo(0):this.info;
        StringBuilder builder = new StringBuilder();
        builder.append("{\"state\": \"" + stateVal + "\"");
        Iterator iterator = this.intMap.keySet().iterator();

        while(iterator.hasNext()) {
            stateVal = (String)iterator.next();
            builder.append(",\"" + stateVal + "\": " + this.intMap.get(stateVal));
        }

        iterator = this.infoMap.keySet().iterator();

        while(iterator.hasNext()) {
            stateVal = (String)iterator.next();
            builder.append(",\"" + stateVal + "\": \"" + (String)this.infoMap.get(stateVal) + "\"");
        }

        builder.append(", list: [");
        iterator = this.stateList.iterator();

        while(iterator.hasNext()) {
            builder.append((String)iterator.next() + ",");
        }

        if(this.stateList.size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append(" ]}");
        return EditorEncoder.toUnicode(builder.toString());
    }

    public void putInfo(String name, long val) {
        this.intMap.put(name, Long.valueOf(val));
    }


}
