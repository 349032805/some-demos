package kitt.core.domain;

/**
 * Created by zhangbolun on 16/1/20.
 */
public class PushCancelRequest {
      private String souceId;
      private String remark;
      private String token;
      private String version;

    public String getSouceId() {
        return souceId;
    }

    public void setSouceId(String souceId) {
        this.souceId = souceId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
