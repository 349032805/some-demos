package kitt.core.libs.logistics;

/**
 * Created by zhangbolun on 15/12/21.
 */

public class PurposeResponse {
    private int code;                 //状态码
    private String message;           //针对编码的描述
    private String databody;          //数据对象
    private Attributes attributes;    //属性

    public PurposeResponse(int code, String message, String databody, Attributes attributes) {
        this.code = code;
        this.message = message;
        this.databody = databody;
        this.attributes = attributes;
    }

    public PurposeResponse() {}

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatabody() {
        return databody;
    }

    public void setDatabody(String databody) {
        this.databody = databody;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "PurposeResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", databody='" + databody + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
