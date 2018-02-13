package kitt.core.util;

import kitt.core.domain.CoalBaseData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by liuxinjie on 16/1/16.
 */
@Service
public class ToolsMethod {

    /**
     * 获取IP方法
     */
    public String getIpAddress(HttpServletRequest request) {
        String IP = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(IP) && !"unKnown".equalsIgnoreCase(IP)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = IP.indexOf(",");
            if(index != -1) {
                return IP.substring(0, index);
            } else {
                return IP;
            }
        }
        IP = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(IP) && !"unKnown".equalsIgnoreCase(IP)){
            return IP;
        }
        return request.getRemoteAddr();
    }

    /**
     * 返回map方法
     * @param map               map
     * @param success           success
     * @param error             错误信息
     * @return                  map对象
     */
    public Map<String, Object> setMapMessage(Map<String, Object> map, boolean success, String error) {
        map.put("success", success);
        map.put("error", error);
        return map;
    }

    /**
     * 返回map方法
     * @param errortype         错误信息类型
     */
    public Map<String, Object> setMapMessage(Map<String, Object> map, boolean success, String errortype, String error) {
        map.put("success", success);
        map.put("errortype", errortype);
        map.put("error", error);
        return map;
    }

    /**
     * 供应信息指标判断,交换位置
     */
    public CoalBaseData doCheckAndChangeIndex(CoalBaseData coal) {
        if (coal.getNCV() != null && coal.getNCV02() != null && coal.getNCV().compareTo(coal.getNCV02()) == 1) {
            Integer temp = coal.getNCV();
            coal.setNCV(coal.getNCV02());
            coal.setNCV02(temp);
        }
        if (coal.getGV() != null && coal.getGV02() != null && coal.getGV().compareTo(coal.getGV02()) == 1) {
            Integer temp = coal.getGV();
            coal.setGV(coal.getGV02());
            coal.setGV02(temp);
        }
        if (coal.getYV() != null && coal.getYV02() != null && coal.getYV().compareTo(coal.getYV02()) == 1) {
            Integer temp = coal.getYV();
            coal.setYV(coal.getYV02());
            coal.setYV02(temp);
        }
        if (coal.getFC() != null && coal.getFC02() != null && coal.getFC().compareTo(coal.getFC02()) == 1) {
            Integer temp = coal.getFC();
            coal.setFC(coal.getFC02());
            coal.setFC02(temp);
        }
        if (coal.getCRC() != null && coal.getCRC02() != null && coal.getCRC() > coal.getCRC02()) {
            Integer temp = coal.getCRC();
            coal.setCRC(coal.getCRC02());
            coal.setCRC02(temp);
        }
        if (coal.getRS() != null && coal.getRS02() != null && coal.getRS().compareTo(coal.getRS02()) == 1) {
            BigDecimal temp = coal.getRS();
            coal.setRS(coal.getRS02());
            coal.setRS02(temp);
        }
        if (coal.getADS() != null && coal.getADS02() != null && coal.getADS().compareTo(coal.getADS02()) == 1) {
            BigDecimal temp = coal.getADS();
            coal.setADS(coal.getADS02());
            coal.setADS02(temp);
        }
        if (coal.getTM() != null && coal.getTM02() != null && coal.getTM().compareTo(coal.getTM02()) == 1) {
            BigDecimal temp = coal.getTM();
            coal.setTM(coal.getTM02());
            coal.setTM02(temp);
        }
        if (coal.getIM() != null && coal.getIM02() != null && coal.getIM().compareTo(coal.getIM02()) == 1) {
            BigDecimal temp = coal.getIM();
            coal.setIM(coal.getIM02());
            coal.setIM02(temp);
        }
        if (coal.getADV() != null && coal.getADV02() != null && coal.getADV().compareTo(coal.getADV02()) == 1) {
            BigDecimal temp = coal.getADV();
            coal.setADV(coal.getADV02());
            coal.setADV02(temp);

        }
        if (coal.getRV() != null && coal.getRV02() != null && coal.getRV().compareTo(coal.getRV02()) == 1) {
            BigDecimal temp = coal.getRV();
            coal.setRV(coal.getRV02());
            coal.setRV02(temp);
        }
        if (coal.getASH() != null && coal.getASH02() != null && coal.getASH().compareTo(coal.getASH02()) == 1) {
            BigDecimal temp = coal.getASH();
            coal.setASH(coal.getASH02());
            coal.setASH02(temp);
        }
        return coal;
    }

}
