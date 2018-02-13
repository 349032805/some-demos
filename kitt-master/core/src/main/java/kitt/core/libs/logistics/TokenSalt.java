package kitt.core.libs.logistics;

/**
* Created by zhangbolun on 15/12/14.
*/
public class TokenSalt {
    private String token;     //口令
    private String salt;      //秘钥
    private long expire;      //有效期至

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public TokenSalt(String token, String salt, long expire) {
        this.token = token;
        this.salt = salt;
        this.expire = expire;
    }

    public TokenSalt() {}

    @Override
    public String toString() {
        return "TokenSalt{" +
                "token='" + token + '\'' +
                ", salt='" + salt + '\'' +
                ", expire=" + expire +
                '}';
    }
}
