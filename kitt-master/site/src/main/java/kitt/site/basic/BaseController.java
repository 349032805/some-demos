package kitt.site.basic;

import kitt.site.service.Session;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by joe on 10/26/14.
 */
public class BaseController {
    @Autowired
    protected Session session;
}
