package kitt.admin.controller;

import com.mysql.jdbc.StringUtils;
import kitt.admin.annotation.Authority;
import kitt.core.domain.AuthenticationRole;
import kitt.core.persistence.WebLogMapper;
import kitt.ext.mybatis.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuxinjie on 15/9/15.
 * 统计网站日志信息
 */
@RestController
@RequestMapping("/userinfo")
public class WebLogController {
    @Autowired
    private WebLogMapper webLogMapper;

    /**
     * 网站日志信息列表
     * @param page
     * @return
     */
    @RequestMapping("/webloglist")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getUserLoginInfoList(@RequestParam(value = "page", required = false, defaultValue = "0")int page){
        Map<String, Object> map = new HashMap<>();
        map.put("webLogList", webLogMapper.pageAllWebLog(LocalDate.now().minusMonths(1), LocalDate.now().minusWeeks(1), LocalDate.now(), page, 10));
        return map;
    }

}
