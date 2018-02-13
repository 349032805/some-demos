package kitt.core.editor;

/**
 * Created by liuxinjie on 15/11/11.
 */
public interface EditorState {
    boolean isSuccess();

    void putInfo(String var1, String var2);

    void putInfo(String var1, long var2);

    String toJSONEditorString();
}
