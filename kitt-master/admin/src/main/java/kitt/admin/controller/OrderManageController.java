package kitt.admin.controller;

import com.itextpdf.text.DocumentException;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.Session;
import kitt.admin.service.Tools;
import kitt.core.bl.BuyService;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.util.Pager;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shiling on 15-1-22.
 */
@RestController
public class OrderManageController {
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private OrderManageMapper orderManageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private Tools tools;
    @Autowired
    private Auth auth;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private BuyService buyService;
    @Autowired
    private Session session;

    //商城订单进行中 标示的状态参数
    private final EnumOrder[] underway=new EnumOrder[]{EnumOrder.WaitPayment, EnumOrder.WaitSignContract, EnumOrder.WaitVerify, EnumOrder.VerifyPass, EnumOrder.VerifyNotPass, EnumOrder.WaitBalancePayment};
    private final EnumOrder[] sellerUnderWay =new EnumOrder[]{EnumOrder.Completed, EnumOrder.Deleted, EnumOrder.Canceled};
    //商城订单退货中
    private final EnumOrder[] returnway=new EnumOrder[]{ EnumOrder.ReturnGoods };
    private final EnumOrder[] sellerReturnway=new EnumOrder[]{EnumOrder.ReturnCompleted, EnumOrder.Deleted};
    //商城订单已完成
    private final EnumOrder[] completeway=new EnumOrder[]{EnumOrder.Completed, EnumOrder.ReturnCompleted, EnumOrder.Deleted};
    private final EnumOrder[] sellerCompleteway=new EnumOrder[]{EnumOrder.Deleted};
    //商城订单，撮合订单 已取消
    private final EnumOrder[] cancelway=new EnumOrder[]{EnumOrder.Canceled};
    private final EnumOrder[] sellerCancelway=new EnumOrder[]{EnumOrder.Deleted};
    //商城订单待复审
    private final EnumOrder[] recheckway=new EnumOrder[]{EnumOrder.WaitPayment, EnumOrder.WaitSignContract, EnumOrder.WaitVerify, EnumOrder.VerifyPass, EnumOrder.VerifyNotPass, EnumOrder.WaitBalancePayment, EnumOrder.ReturnGoods};
    private final EnumOrder[] sellerRecheckway=new EnumOrder[]{EnumOrder.Deleted, EnumOrder.VerifyNotPass, EnumOrder.NULL};
    //撮合订单进行中
    private final EnumOrder[] matchUnderway=new EnumOrder[]{EnumOrder.MakeMatch};
    private final EnumOrder[] sellerMatchUnderway=new EnumOrder[]{ EnumOrder.Deleted, EnumOrder.Completed, EnumOrder.Canceled};
    //撮合订单已完成
    private final EnumOrder[] matchCompleteway=new EnumOrder[]{EnumOrder.Completed};
    private final EnumOrder[] sellerMatchCompleteway=new EnumOrder[]{EnumOrder.Deleted};
    //撮合订单待复审
    private final EnumOrder[] matchRecheckway=new EnumOrder[]{EnumOrder.MakeMatch};
    private final EnumOrder[] sellerMatchRecheckway=new EnumOrder[]{EnumOrder.Deleted, EnumOrder.NULL, EnumOrder.VerifyNotPass};


    private final int pageSize = 10;
    //进行中
    private final String UNDERWAY="underway";
    //退货中
    private final String RETURNWAY="returnway";
    //已取消
    private final String CANCELWAY="cancelway";
    //待复审
    private final String RECHECKWAY="recheckway";
    //已完成
    private final String COMPLETEWAY="completeway";

    //撮合订单进行中
    private final String MATCHUNDERWAY="matchunderway";
    //撮合订单已完成
    private final String MATCHCOMPLETEWAY="matchcompleteway";
    //撮合订单待复审
    private final String MATCHRECHECKWAY="matchrecheckway";

    private void addStatusParams(Map<String, Object> params){
        String status = (String) params.get("status");
        //判断查询订单状态
        if(status.equals(UNDERWAY)){
            params.put("orderStatus", underway);
            params.put("sellerStatus", sellerUnderWay);
        } else if(status.equals(RETURNWAY)){
            params.put("orderStatus", returnway);
            params.put("sellerStatus", sellerReturnway);
        } else if(status.equals(COMPLETEWAY)){
            params.put("orderStatus", completeway);
            params.put("sellerStatus", sellerCompleteway);
        } else if(status.equals(CANCELWAY)){
            params.put("orderStatus", cancelway);
            params.put("sellerStatus", sellerCancelway);
        } else if(status.equals(RECHECKWAY)){
            params.put("orderStatus", recheckway);
            params.put("sellerStatus", sellerRecheckway);
        } else if(status.equals(MATCHUNDERWAY)){
            //撮合订单进行中
            params.put("orderStatus", matchUnderway);
            params.put("sellerStatus", sellerMatchUnderway);
        } else if(status.equals(MATCHCOMPLETEWAY)){
            //撮合订单已完成
            params.put("orderStatus", matchCompleteway);
            params.put("sellerStatus", sellerMatchCompleteway);
        } else if(status.equals(MATCHRECHECKWAY)){
            //撮合订单待复审
            params.put("orderStatus", matchRecheckway);
            params.put("sellerStatus", sellerMatchRecheckway);
        }
    }

    @RequestMapping(value = "/order/orderlist", method = RequestMethod.POST)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getMallOrders(@RequestParam Map<String, Object> params){
        Map<String,Object> map = new HashMap<String,Object>();
        //区分查询订单状态
        //addStatusParams(params);
        map.put("deliveryRegion", Integer.valueOf((String)params.get("deliveryRegion")));
        map.put("deliveryProvince", Integer.valueOf((String)params.get("deliveryProvince")));
        map.put("deliveryHarbour", Integer.valueOf((String) params.get("deliveryHarbour")));
        map.put("regionList", areaportMapper.getAllArea());
        map.put("provinceList", tools.getMallProvinces(Integer.valueOf((String)params.get("deliveryRegion"))));
        map.put("harbourList", tools.getMallPorts(Integer.valueOf((String)params.get("deliveryProvince"))));
        map.put("startDate", params.get("startDate"));
        map.put("endDate", params.get("endDate"));
        map.put("orderid", params.get("orderid"));
        map.put("clienttype", Integer.valueOf((String)params.get("clienttype")));
        map.put("clientTypeList", dataBookMapper.getDataBookListByType("clienttype"));
        Pager<Map<String, Object>> orderList = orderManageMapper.findOrderByStatus(params, Integer.valueOf((String)params.get("page")), pageSize);
        String totalCountAndMoney = "共有 订单：" + orderList.getCount() + " 个， 总货款为：" + orderManageMapper.countTotalMoneyByStatus(params) + " 元。";
        map.put("totalCountAndMoney", totalCountAndMoney);
        map.put("orderList", orderList);
        return map;
    }

    @RequestMapping(value = "/order/datacount",method =RequestMethod.POST)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Integer searchDataCount(@RequestParam Map<String,Object> params){
        //addStatusParams(params);
        return orderManageMapper.ExportOrderMethod(params).size();
    }

    //export excel
    @RequestMapping(value = "/order/exportExcel",method =RequestMethod.GET)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public void downloadManualsell(@RequestParam Map<String,Object> params,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws IOException, DocumentException {
        //addStatusParams(params);
        List<Map<String,Object>> orders = orderManageMapper.ExportOrderMethod(params);
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow row = sheet.createRow(0);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sheet.setVerticallyCenter(true);
        sheet.setHorizontallyCenter(true);
        sheet.setColumnWidth(0, 1200);
        sheet.setColumnWidth(1, 3500);
        sheet.setColumnWidth(2, 3500);
        sheet.setColumnWidth(3, 2000);
        sheet.setColumnWidth(4, 2500);
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(6, 4000);
        sheet.setColumnWidth(7, 4000);
        sheet.setColumnWidth(8, 4000);
        sheet.setColumnWidth(9, 2500);
        sheet.setColumnWidth(10, 2500);
        sheet.setColumnWidth(11, 2500);
        sheet.setColumnWidth(12, 2000);
        sheet.setColumnWidth(13, 2000);
        sheet.setColumnWidth(14, 3600);
        sheet.setColumnWidth(15, 2000);
        sheet.setColumnWidth(16, 6000);
        sheet.setColumnWidth(17, 6000);

        String[] excelHeader = {"序号 ","订单编号","产品编号","品种","港口","低位热值(kcal/kg)","收到基硫份(%)","空干基挥发份(%)","收到基挥发份(%)","灰份(%)","全水分(%)","内水分(%)","数量(T)","价格(元)","合计(元)","交易员","买家","卖家"};
        for (int i = 0; i < excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(cellStyle);
        }
        for(int i=0;i<orders.size();i++){
            Map<String,Object> resultSet =  orders.get(i);
            row = sheet.createRow(i+1);
            row.setRowStyle(cellStyle);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(String.valueOf(resultSet.get("orderid")));
            row.createCell(2).setCellValue(String.valueOf(resultSet.get("pid")));
            row.createCell(3).setCellValue(String.valueOf(resultSet.get("pname")));
            row.createCell(4).setCellValue(String.valueOf(resultSet.get("deliveryplace")));
            row.createCell(5).setCellValue(String.valueOf(resultSet.get("NCV")));
            row.createCell(6).setCellValue(String.valueOf(resultSet.get("RS")));
            row.createCell(7).setCellValue(String.valueOf(resultSet.get("ADV")));
            row.createCell(8).setCellValue(String.valueOf(resultSet.get("RV")));
            row.createCell(9).setCellValue(String.valueOf(resultSet.get("ASH")));
            row.createCell(10).setCellValue(String.valueOf(resultSet.get("TM")));
            row.createCell(11).setCellValue(String.valueOf(resultSet.get("IM")));
            row.createCell(12).setCellValue(String.valueOf(resultSet.get("amount")));
            row.createCell(13).setCellValue(String.valueOf(resultSet.get("price")));
            row.createCell(14).setCellValue(String.valueOf(resultSet.get("totalmoney")));
            row.createCell(15).setCellValue(String.valueOf(resultSet.get("dealername")));
            row.createCell(16).setCellValue(companyMapper.getCompanyByUserid(Integer.valueOf(String.valueOf(resultSet.get("userid")))).getName());
            row.createCell(17).setCellValue(String.valueOf(resultSet.get("seller")));
        }
        String filename = "订单数据" + LocalDate.now() + ".xls";
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        if(request.getHeader("user-agent").toLowerCase().contains("firefox")) {
            filename =  new String(filename.getBytes("UTF-8"), "ISO-8859-1");
        } else {
            filename = URLEncoder.encode(filename, "UTF-8");
        }
        response.addHeader("Content-Disposition", "attachment; filename=" + filename);
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
    }

    //订单详细
    @RequestMapping("/order/detail")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showDetail(@RequestParam(value = "id", required = true)int id,
                             @RequestParam(value = "version", required = true)int version) {
        Map<String, Object> map = new HashMap<String, Object>();
        Order order = orderMapper.getOrderByIdAndVersionAdmin(id, version);
        if (order == null) throw new NotFoundException();
        User buyer = userMapper.getUserById(order.getUserid());
        User seller = userMapper.getUserById(order.getSellerid());
        Company buyCompany = companyMapper.getCompanyByUserid(buyer.getId());
        if(order.getOrdertype().equals(EnumOrder.MallOrder)){
            if(buyer == null || buyCompany == null){
                auth.doOutputErrorInfo("商城订单， orderiId=" + order.getId() + ", buyer_Id" + buyer.getId() + ", buyer_Company=" + buyCompany.getName());
                throw new BusinessException("服务器出错！请刷新后重试！");
            }
        } else{
            if(buyer == null || seller == null || buyCompany == null){
                auth.doOutputErrorInfo("商城订单， orderiId=" + order.getId() + ", buyer_Id" + buyer.getId() + ", buyer_Company=" + buyCompany.getName() + ", seller_Id=" + seller.getId());
                throw new BusinessException("服务器出错！请刷新后重试！");
            }
        }
        map.put("order", order);
        map.put("buyer", buyer);
        map.put("buyCompany", buyCompany);
        map.put("seller", seller);
        map.put("success", true);
        return map;
    }

    //商城订单 -- 申请完成
    @RequestMapping("/mall/applyComplete")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doApplyCompleteMallOrder(@RequestParam(value = "id", required = true)int id,
                                           @RequestParam(value = "version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException();
        SellInfo sellInfo = buyMapper.getSellInfoById(order.getSellinfoid());
        if(sellInfo == null) throw new NotFoundException();
        try {
            buyService.changeSellerOrderStatusPlusSellInfoQuantity(order, false, EnumOrder.Completed, session.getAdmin().getId(), session.getAdmin().getUsername(), "申请完成");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("申请完成商城订单（orders）出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("申请完成商城订单失败，请刷新页面后重试！");
        }
        return true;
    }

    //撮合订单 -- 申请完成
    @RequestMapping("/order/applyComplete")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doApplyCompleteMatchOrder(@RequestParam(value = "id", required = true)int id,
                                            @RequestParam(value = "version", required = true)int version,
                                            @RequestParam(value = "amount", required = true)int amount){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException();
        SellInfo sellInfo = buyMapper.getSellInfoById(order.getSellinfoid());
        if(sellInfo == null) throw new NotFoundException();
        if(sellInfo.getAvailquantity() >= amount) {
            try {
                buyService.changeSellerOrderStatusMinusSellInfoQuantity(order, true, EnumOrder.Completed, session.getAdmin().getId(), session.getAdmin().getUsername(), "申请完成");
            } catch (SQLExcutionErrorException e) {
                auth.doOutputErrorInfo("申请完成撮合订单（orders）出错， 订单id=" + order.getId() + ", version=" + order.getVersion() + ", 供应信息id=" + order.getSellinfoid());
                throw new BusinessException("可用库存不足，请查看产品详细！");
            }
            return true;
        }
        return false;
    }

    //商城订单  ---  提报退货申请
    @RequestMapping("/mall/applyReturn")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doApplyReturnOrder(@RequestParam(value = "id", required = true)int id,
                                     @RequestParam(value = "version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException();
        try {
            buyService.changeSellerOrderStatusPlusSellInfoQuantity(order, false, EnumOrder.ReturnCompleted, session.getAdmin().getId(), session.getAdmin().getUsername(), "提报退货申请");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("申请退货完成，商城订单（orders）出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("申请退货完成订单失败，请刷新页面后重试！");
        }
        return true;
    }

    //申请取消  ---  商城订单 撮合订单
    @RequestMapping("/order/applyCancel")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doApplyCancelOrder(@RequestParam(value = "id", required = true)int id,
                                     @RequestParam(value = "version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException();
        try {
            buyService.changeSellerOrderStatusPlusSellInfoQuantity(order, false, EnumOrder.Canceled, session.getAdmin().getId(), session.getAdmin().getUsername(), "申请取消");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("申请取消订单（orders）出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("申请取消订单失败，请刷新页面后重试！");
        }
        return true;
    }

    //商城订单 -- 确认退货
    @RequestMapping("/mall/confirmReturn")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doConfirmReturnOrder(@RequestParam(value = "id", required = true)int id,
                                       @RequestParam(value = "version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException();
        SellInfo sellInfo = buyMapper.getSellInfoById(order.getSellinfoid());
        if(sellInfo == null) throw new NotFoundException();
        try {
            buyService.changeOrderStatusPlusSellInfoQuantity(order, true, EnumOrder.ReturnCompleted, session.getAdmin().getId(), session.getAdmin().getUsername(), "确认退货");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("商城订单（orders），确认退货出错，订单id=" + order.getId() + ", version=" + order.getVersion() + ", 供应信息id=" + order.getSellinfoid());
            throw new BusinessException("确认退货失败，请刷新页面后重试！");
        }
        return true;
    }

    //确认完成 --- 商城订单，撮合订单
    @RequestMapping("/mall/confirmComplete")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doConfirmCompleteOrder(@RequestParam(value = "id", required = true)int id,
                                         @RequestParam(value = "version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException();
        try {
            buyService.changeOrderStatus(order, EnumOrder.Completed, session.getAdmin().getId(), session.getAdmin().getUsername(), "确认完成");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("商城订单（orders），申请完成出错，订单id=" + order.getId() + ", version=" + order.getVersion() + ", 供应信息id=" + order.getSellinfoid());
            throw new BusinessException("申请完成订单失败，请刷新页面后重试！");
        }
        return true;
    }

    //确认取消  ---  商城订单，撮合订单
    @RequestMapping("/order/confirmCancel")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doConfirmCancel(@RequestParam(value = "id", required = true)int id,
                                  @RequestParam(value = "version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException();
        boolean isPlusQuantity = (order.getOrdertype().equals(EnumOrder.OtherOrder) && !order.getSellerstatus().equals(EnumOrder.Completed) ) ? false : true;
        try {
            buyService.changeOrderStatusPlusSellInfoQuantity(order, isPlusQuantity, EnumOrder.Canceled, session.getAdmin().getId(), session.getAdmin().getUsername(), "确认取消");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("订单（orders），确认退货出错，订单id=" + order.getId() + ", version=" + order.getVersion() + ", 供应信息id=" + order.getSellinfoid());
            throw new BusinessException("确认取消订单失败，请刷新页面后重试！");
        }
        return true;
    }

    //删除订单
    @RequestMapping("/order/deleteOrder")
    @Transactional
    public Object doDeleteOrder(@RequestParam(value = "id", required = true)int id,
                                @RequestParam(value = "version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException();
        try {
            buyService.changeSellerOrderStatusPlusSellInfoQuantity(order, false, EnumOrder.Deleted, session.getAdmin().getId(), session.getAdmin().getUsername(), "管理员删除订单");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("删除订单（orders）出错，订单id=" + order.getId() + ", version=" + order.getVersion() + ", 供应信息id=" + order.getSellinfoid());
            throw new BusinessException("删除订单失败，请刷新页面后重试！");
        }
        return true;
    }

    //复审未通过  ---  商城订单，撮合订单
    @RequestMapping("/order/verifyNotPass")
    @Transactional
    public Object doVerifyNotPass(@RequestParam(value = "id", required = true)int id,
                                  @RequestParam(value = "version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException();
        boolean isPlusQuantity = (order.getOrdertype().equals(EnumOrder.OtherOrder) && order.getSellerstatus().equals(EnumOrder.Completed)) ? true : false;
        try {
            buyService.changeSellerOrderStatusPlusSellInfoQuantity(order, isPlusQuantity, EnumOrder.VerifyNotPass, session.getAdmin().getId(), session.getAdmin().getUsername(), "复审未通过");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("复审不通过订单（orders)出错，订单id=" + order.getId() + ", version=" + order.getVersion() + ", 供应信息id=" + order.getSellinfoid());
            throw new BusinessException("复审不通过失败，请刷新页面后重试！");
        }
        return true;
    }

}

