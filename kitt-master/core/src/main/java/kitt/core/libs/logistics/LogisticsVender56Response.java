package kitt.core.libs.logistics;

import java.util.List;

/**
* Created by zhangbolun on 15/12/14.
*/
public class LogisticsVender56Response {
    /**状态码
     600020：token获得成功
     600010：车辆查询成功
     600000：接收成功
     -600020：token获得失败
     -600000：非法请求
     -600001：唯一标识字段为空
     -600002：装车地址-省市县为空
     -600003：卸车地址-省市县为空
     -600004：货物类型为空
     -600005：货物重量为空
     -600006：联系电话为空
     -600007：接口令牌为空
     -600009：该单子已经提交过了
     -600008,"该意向单已无法取消，请联系56快车"
     -600014,"该意向单已无法更改，请联系56快车"
     -600010：车辆数据为空
     -600015：该意向单不存在
     */
    private int code;         //状态码
    private String message;   //针对编码的描述
    private TokenSalt databody;//数据对象
    private Attributes attributes;    //属性

    public LogisticsVender56Response() {}

    public LogisticsVender56Response(int code, String message, TokenSalt databody, Attributes attributes) {
        this.code = code;
        this.message = message;
        this.databody = databody;
        this.attributes = attributes;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

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

    public TokenSalt getDatabody() {
        return databody;
    }

    public void setDatabody(TokenSalt databody) {
        this.databody = databody;
    }

    @Override
    public String toString() {
        return "LogisticsVender56Response{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", databody=" + databody +
                ", attributes=" + attributes +
                '}';
    }
}
