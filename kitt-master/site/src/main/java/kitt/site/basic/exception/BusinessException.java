package kitt.site.basic.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by hongpf on 15/4/15.
 */

public class BusinessException extends RuntimeException {
    private String message;
    private String url;


    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(String message, String url) {
        this.message = message;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        if (StringUtils.isNotBlank(this.getUrl())) {
            return "{\"message\":\"" + this.getMessage() + "\",\"url\":\"" + this.getUrl() + "\"}";
        } else {
            return "{\"message\":\"" + this.getMessage() + "\"}";
        }

    }

}