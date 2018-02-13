package kitt.admin.annotation;
import java.lang.annotation.*;
/**
 * Created by zhangbolun on 15/5/15.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorities {
    Authority[] value();
}
