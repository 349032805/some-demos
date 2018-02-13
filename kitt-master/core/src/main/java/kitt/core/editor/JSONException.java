package kitt.core.editor;

/**
 * Created by liuxinjie on 15/11/11.
 */
public class JSONException extends RuntimeException {
    private static final long serialVersionUID = 0L;
    private Throwable cause;

    public JSONException(String message) {
        super(message);
    }

    public JSONException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
