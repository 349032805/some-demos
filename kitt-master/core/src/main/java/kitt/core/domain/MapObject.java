package kitt.core.domain;

import kitt.core.util.text.TextCheck;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * Created by liuxinjie on 15/11/20.
 */
public class MapObject implements Serializable {
    @Autowired
    private TextCheck textCheck;
    private boolean success;
    private String error;
    private String errortype;
    private String value1;
    private String value2;

    public MapObject() {
    }

    public MapObject(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        if (!StringUtils.isBlank(error)) {
            this.error = error.trim();
        } else {
            this.error = error;
        }
    }

    public String getErrortype() {
        return errortype;
    }

    public void setErrortype(String errortype) {
        if (!StringUtils.isBlank(errortype)) {
            this.errortype = errortype.trim();
        } else {
            this.errortype = errortype;
        }
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        if (!StringUtils.isBlank(value1)) {
            this.value1 = value1.trim();
        } else {
            this.value1 = value1;
        }
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        if (!StringUtils.isBlank(value2)) {
            this.value2 = value2.trim();
        } else {
            this.value2 = value2;
        }
    }
}
