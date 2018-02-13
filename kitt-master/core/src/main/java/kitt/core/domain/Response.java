package kitt.core.domain;

/**
 * Created by zhangbolun on 15/9/8.
 */
public class Response {
    private String message;

    public Response(String message) {
        this.message = message;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
