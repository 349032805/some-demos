package kitt.admin.controller;

import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.Session;
import kitt.core.bl.BuyService;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.EnumOrder;
import kitt.core.domain.Order;
import kitt.core.domain.Payment;
import kitt.core.persistence.OrderMapper;
import kitt.core.persistence.PaymentMapper;
import kitt.core.service.FileStore;
import kitt.core.util.Pager;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 14/12/12.
 */
@RestController
public class OrderVerController {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private Auth auth;
    @Autowired
    private BuyService buyService;
    @Autowired
    private Session session;

    @RequestMapping("/certificate/wait")
    public Object waitList(@RequestParam(value = "content", required = false, defaultValue = "")final String scontent,
                           @RequestParam(value = "pid", required = false, defaultValue = "")final String tempPid,
                           final int page){
        return new Object(){
            public String content = scontent;
            public String pid = tempPid;
            public Pager<Order> orderList = orderMapper.pageAllOrderBySelect(EnumOrder.WaitVerify, scontent, pid, page, 10);
        };
    }

    @RequestMapping("/certificate/tail")
    public Object tailList(@RequestParam(value = "content", required = false, defaultValue = "")final String scontent,
                           @RequestParam(value = "pid", required = false, defaultValue = "")final String tempPid,
                           final int page){
        return new Object() {
            public String content = scontent;
            public String pid = tempPid;
            public Pager<Order> orderList = orderMapper.pageAllOrderBySelect(EnumOrder.WaitBalancePayment, scontent, pid, page, 10);
        };
    }

    @RequestMapping("/certificate/pass")
    public Object passList(@RequestParam(value = "content", required = false, defaultValue = "")final String scontent,
                           @RequestParam(value = "pid", required = false, defaultValue = "")final String tempPid,
                           final int page){
        return new Object(){
            public String content = scontent;
            public String pid = tempPid;
            public Pager<Order> orderList = orderMapper.pageAllOrderBySelect(EnumOrder.VerifyPass, scontent, pid, page, 10);
        };
    }

    @RequestMapping("/certificate/fail")
    public Object failList(@RequestParam(value = "content", required = false, defaultValue = "")final String scontent,
                           @RequestParam(value = "pid", required = false, defaultValue = "")final String tempPid,
                           final int page){
        return new Object(){
            public String content = scontent;
            public String pid = tempPid;
            public Pager<Order> orderList = orderMapper.pageAllOrderBySelect(EnumOrder.VerifyNotPass, scontent, pid, page, 10);
        };
    }

    @RequestMapping("/certificate/detail")
    public Object showOrderDetail(@RequestParam(value = "id", required = true)int id,
                                  @RequestParam(value = "version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("order", order);
        List<Payment> paymentList = paymentMapper.getPaymentList(order.getId(), 1);
        switch (paymentList.size()){
            case 3:
                map.put("payment3", paymentList.get(2));
                map.put("payment2", paymentList.get(1));
                map.put("payment1", paymentList.get(0));
                break;
            case 2:
                map.put("payment2", paymentList.get(1));
                map.put("payment1", paymentList.get(0));
                break;
            case 1:
                map.put("payment1", paymentList.get(0));
                break;
            default:
                break;
        }
        map.put("paymentList", paymentList);
        map.put("success", true);
        return map;
    }

    @RequestMapping("/order/checkCertification")
    @Transactional
    public Object doCertificateOrderPaid(@RequestParam(value="id", required = true)int id,
                                         @RequestParam(value="version", required = true)int version,
                                         @RequestParam(value="checkStatus", required = true)String checkStatus,
                                         @RequestParam(value="comment", required = false, defaultValue = "")String comment,
                                         @RequestParam(value="id01", required = false, defaultValue = "0")int id01,
                                         @RequestParam(value="money01", required = false, defaultValue = "0")BigDecimal money01,
                                         @RequestParam(value="id02", required = false, defaultValue = "0")int id02,
                                         @RequestParam(value="money02", required = false, defaultValue = "0")BigDecimal money02,
                                         @RequestParam(value="id03", required = false, defaultValue = "0")int id03,
                                         @RequestParam(value="money03", required = false, defaultValue = "0")BigDecimal money03){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException();
        List<Payment> paymentList = new ArrayList<Payment>();
        paymentList.add(new Payment(id01, money01));
        paymentList.add(new Payment(id02, money02));
        paymentList.add(new Payment(id03, money03));
        EnumOrder status;
        if (checkStatus.equals("审核未通过")) {
            status = EnumOrder.VerifyNotPass;
        } else if(checkStatus.equals("审核通过")) {
            status = EnumOrder.VerifyPass;
        } else {
            status = EnumOrder.WaitBalancePayment;
        }
        try {
            buyService.verifyOrderPayment(order, paymentList, status, session.getAdmin().getId(), session.getAdmin().getUsername(), comment);
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("审核支付凭证（payment）出错，订单id=" + order.getId());
            throw new BusinessException("支付凭证审核出错，请联系技术人员！");
        }
        return true;
    }

    //下载图片
    @RequestMapping("/certificate/downloadCertification")
    public HttpEntity<byte[]> doDownloadCertification(@RequestParam(value = "url", required = true)String url) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",url);
        return new HttpEntity<byte[]>(FileUtils.readFileToByteArray(fileStore.getFileByFilePath(url)), headers);
    }

}
