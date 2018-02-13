package kitt.core.domain;

import java.util.Map;

/**
 * Created by zhangbolun on 15/7/2.
 */
public class ResponseResult<T1,T2> {
    public boolean success;
    public Map<String,String> errors;

    public ResponseResult(Map<String, String> errors) {
        this.errors = errors;
        success= isSuccess(errors);
    }

    private boolean isSuccess(Map<String, String> errors){
        boolean isSuccess=false;
        if(errors==null) {
            isSuccess = true;
        }else {
            if (errors.isEmpty())
                isSuccess = true;
        }
        return  isSuccess;
    }
}
