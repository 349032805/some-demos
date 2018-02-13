package kitt.site.service;

import freemarker.template.TemplateException;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.Freemarker;
import kitt.site.basic.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/11/13.
 */
@Service
public class BuyMethod {
    @Autowired
    private PriceLadderMapper priceLadderMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private Freemarker freemarker;
    @Autowired
    private Auth auth;


    /**---------------------------------------第一部分 供应信息 start ------------------------------------**/
    /**---------------------------------------第一部分 供应信息 start ------------------------------------**/

    /**
     * 显示阶梯价
     * @param id               供应信息 id
     */
    public void showJTJ(int id, Map<String, Object> model) {
        List<PriceLadder> priceLadders = priceLadderMapper.getPriceLadderListBySellinfoId(id);
        switch (priceLadders.size()){
            case 0:
                break;
            case 5:
                model.put("jtj05Obj", priceLadders.get(4));
            case 4:
                model.put("jtj04Obj", priceLadders.get(3));
            case 3:
                model.put("jtj03Obj", priceLadders.get(2));
            case 2:
                model.put("jtj01Obj", priceLadders.get(0));
                model.put("jtj02Obj", priceLadders.get(1));
                break;
            default:
                break;
        }
    }

    /**---------------------------------------第一部分 供应信息 end ------------------------------------**/
    /**---------------------------------------第一部分 供应信息 end ------------------------------------**/


    /**---------------------------------------第二部分 订单 start -------------------------------------**/
    /**---------------------------------------第二部分 订单 start -------------------------------------**/

    /**
     * 标准合同详细内容
     * @param sellInfo                      供应信息对象
     */
    public String standardContractContent(SellInfo sellInfo, final HttpServletRequest request) throws IOException, TemplateException, ServletException {
        if (sellInfo == null) throw new NotFoundException();
        String contracttype = null;
        if(sellInfo.getDeliverymode().equals(EnumDeliverymode.港口平仓.toString())) {
            contracttype = "/contracts/portUnwinding";
        } else if(sellInfo.getDeliverymode().equals(EnumDeliverymode.到岸舱底.toString())) {
            contracttype = "/contracts/shoreBottom";
        } else if(sellInfo.getDeliverymode().equals(EnumDeliverymode.场地自提.toString())) {
            contracttype = "/contracts/spaceDeliveryPayAll";
        }
        String contract = freemarker.render(contracttype, new HashMap<String, Object>() {{
            put("localhost", getCurrentURL(request));
        }});
        return contract;
    }

    /**
     * 正式合同详细内容
     * @param id                            订单 id
     */
    public String contractContent(int id, final HttpServletRequest request) throws IOException, TemplateException, ServletException {
        final Order order = orderMapper.getOrderById(id);
        if (order == null) throw new NotFoundException();
        auth.doCheckUserRight(order.getUserid(), order.getSellerid());
        if(order.getOrdertype().equals(EnumOrder.MallOrder)) {
            final SellInfo sellInfo = buyMapper.getSellInfoById(order.getSellinfoid());
            final Company company = companyMapper.getCompanyByUserid(order.getUserid());
            if (sellInfo == null || company == null) throw new NotFoundException();
            String contracttype = null;
            if (order.getDeliverymode().equals(EnumDeliverymode.港口平仓.toString())) {
                contracttype = "/contracts/portUnwinding";
            } else if (order.getDeliverymode().equals(EnumDeliverymode.到岸舱底.toString())) {
                contracttype = "/contracts/shoreBottom";
            } else if (order.getDeliverymode().equals(EnumDeliverymode.场地自提.toString())) {
                if (order.getPaytype().equals(EnumOrder.PayTheWhole)) {
                    contracttype = "/contracts/spaceDeliveryPayAll";
                } else {
                    contracttype = "/contracts/spaceDeliveryPayHalf";
                }
            }
            String contract = freemarker.render(contracttype, new HashMap<String, Object>() {{
                //公司表信息
                put("companyname", company.getName());
                put("companyaddress", company.getAddress());
                put("companylegalpersonname", company.getLegalpersonname());
                put("companyphone", company.getPhone());
                put("companyopeningbank", company.getOpeningbank());
                put("companyaccount", company.getAccount());
                put("companyidentificationnumword", company.getIdentificationnumword());
                put("companyfax", company.getFax());
                put("companyzipcode", company.getZipcode());
                //order表信息
                put("contractno", order.getContractno());
                put("createtime", order.getCreatetime().toLocalDate());
                put("orderpid", order.getPid());
                put("orderamount", order.getAmount());
                put("orderprice", order.getPrice());
                put("orderdeliveryregion", order.getDeliveryregion());
                put("orderdeliveryprovince", order.getDeliveryprovince());
                put("orderdeliveryplace", order.getDeliveryplace().equals("其它") == true ? order.getOtherharbour() : order.getDeliveryplace());
                put("orderdeliverytime1year", String.valueOf(order.getDeliverytime1().getYear()));
                put("orderdeliverytime1month", order.getDeliverytime1().getMonthValue());
                put("orderdeliverytime1day", order.getDeliverytime1().getDayOfMonth());
                if (order.getDeliverymode().equals("场地自提")) {
                    put("orderdeliverytime2year", String.valueOf(order.getDeliverytime2().getYear()));
                    put("orderdeliverytime2month", order.getDeliverytime2().getMonthValue());
                    put("orderdeliverytime2day", order.getDeliverytime2().getDayOfMonth());
                }
                //sellinfo表信息
                if(sellInfo.getNCV() == 0 || sellInfo.getNCV02() == 0) {
                    put("sellInfoNCV", String.valueOf("--"));
                } else if(sellInfo.getNCV() == sellInfo.getNCV02()) {
                    put("sellInfoNCV", String.valueOf(sellInfo.getNCV()));
                } else{
                    put("sellInfoNCV", String.valueOf(sellInfo.getNCV() + "-" + sellInfo.getNCV02()));
                }

                if(sellInfo.getRS().compareTo(BigDecimal.ZERO) == 0 || sellInfo.getRS02().compareTo(BigDecimal.ZERO) == 0){
                    put("sellInfoRS", String.valueOf("--"));
                } else if(sellInfo.getRS().compareTo(sellInfo.getRS02()) == 0){
                    put("sellInfoRS", String.valueOf(sellInfo.getRS()));
                } else {
                    put("sellInfoRS", String.valueOf(sellInfo.getRS() + "-" + sellInfo.getRS02()));
                }
                put("sellInfoinspectorg", sellInfo.getInspectorg().equals("其它") ? sellInfo.getOtherinspectorg() : sellInfo.getInspectorg());
                put("localhost", getCurrentURL(request));
            }});
            return contract;
        } else {
            return "";
        }
    }

    /**
     * 获取当前页面serverName  和 端口
     */
    public Object getCurrentURL(HttpServletRequest request) throws UnknownHostException {
        return "http://" + request.getServerName() + ":" + request.getServerPort();
    }

    /**---------------------------------------第二部分 订单 end -------------------------------------**/
    /**---------------------------------------第二部分 订单 end -------------------------------------**/




}
