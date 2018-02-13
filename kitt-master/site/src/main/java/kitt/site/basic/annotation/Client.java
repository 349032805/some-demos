package kitt.site.basic.annotation;

import java.lang.annotation.*;

/**
 * Created by liuxinjie on 16/1/15.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Client {
}
