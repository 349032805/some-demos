package kitt.admin.annotation;

import kitt.core.domain.AuthenticationRole;

import java.lang.annotation.*;

/**
 * Created by zhangbolun on 15/5/15.
 */
@Repeatable(Authorities.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authority  {
    AuthenticationRole role();
}
