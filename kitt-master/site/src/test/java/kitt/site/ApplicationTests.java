package kitt.site;


import kitt.core.libs.LogisticsShipClient;
import kitt.core.persistence.LogisticsvenderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {
    @Autowired
    private LogisticsShipClient logisticsShipClient;

    @Autowired
    private LogisticsvenderMapper logisticsvenderMapper;


    @Test
    public void contextLoads() throws Exception {

    }


    @Test
    public void testMatchShip() {
//        Gson gson = new Gson();
//        ShipRequest request = new ShipRequest();
//        request.setNo("WLC201601120004");
//        request.setTransportName("test");
//        request.setTransportContacterName("test123");
//        request.setTransportContacterPhone("15900000000");
//        request.setCCEmpName("jiaoyiyuan");
//        request.setCCEmpPhone("jyyphone");
//        request.setPaymentCondition("账期");
//        request.setSettlementKind("转账");
//        request.setNeedBill("1");
//        request.setPrice(new BigDecimal(234));
//        request.setPriceType("2");
//        request.setBaseFees(new BigDecimal(111));
//        request.setOtherFees(new BigDecimal(222));
//        request.setCapacity(100);
//        request.setShipName("船名字");
//        request.setNoLoadPort("800");
//        request.setNoLoadDate("2015-12-12");
//        request.setDemurrageFees(new BigDecimal(333));
//        request.setDemurrageFeesType("1");
//        request.setTotalAmount(new BigDecimal(200));
//        request.setRemark("备注....");
//        String url = "http://localhost:8080/ship/pushMatchInfo";
//        String param = "data=" + gson.toJson(request);
//        System.out.println("param:" + param);
//        String result = HttpRequest.sendPost(url, param, "11");
//        System.out.println(result);
    }


    @Test
    public void testTransportShip() {
//        Gson gson = new Gson();
//        ShipInfoRequest request = new ShipInfoRequest();
//        request.setNo("WLC201601120006");
//        request.setProcessTime("2015-11-11 11:11:11");
//        request.setInfo("运送开始了....");
//        request.setInfoId(1);
//        request.setSign("1");
//        request.setSort(2);
//        String url = "http://localhost:8080/ship/pushTransportInfo";
//        String param = "data=" + gson.toJson(request);
//        System.out.println("param:" + param);
//        String result = HttpRequest.sendPost(url, param, "11");
//        System.out.println(result);
    }


    @Test
    public void testCompleteShip() {
//        Gson gson = new Gson();
//        ShipRequest request = new ShipRequest();
//        request.setNo("WLC201601120006");
//        request.setContractAttachUrl("url.....");
//        request.setPayTotalAmount(new BigDecimal(1111));
//        request.setPayTotalWeight(new BigDecimal(222));
//        String url = "http://localhost:8080/ship/pushCompleteInfo";
//        String param = "data=" + gson.toJson(request);
//        System.out.println("param:" + param);
//        String result = HttpRequest.sendPost(url, param, "11");
//        System.out.println(result);
    }

    @Test
    public void testCancelShip() {
//        Gson gson = new Gson();
//        test2 request = new test2();
//        request.setNo("WLC201601120006");
//        request.setRemark("取消订单");
//        String url = "http://localhost:8080/ship/pushCancelInfo";
//        String param = "data=" + gson.toJson(request);
//        System.out.println("param:" + param);
//        String result = HttpRequest.sendPost(url, param, "11");
//        System.out.println(result);
    }


    @Test
    public void testUpdateShip() {
//        Gson gson = new Gson();
//        ShipRequest request = new ShipRequest();
//        request.setNo("WLC201601120006");
//        //request.setTransportName("test");
//        //request.setTransportContacterName("test123");
//        //request.setTransportContacterPhone("15900000000");
//        //request.setCCEmpName("jiaoyiyuan");
//        //request.setCCEmpPhone("jyyphone");
//        //request.setPaymentCondition("账期");
//        //request.setSettlementKind("转账");
//        request.setNeedBill("0");
//        request.setPrice(new BigDecimal(234));
//        request.setPriceType("2");
//        request.setBaseFees(new BigDecimal(111));
//        request.setOtherFees(new BigDecimal(222));
//        request.setCapacity(100);
//        request.setShipName("船名字");
//        request.setNoLoadPort("800");
//        request.setNoLoadDate("2015-02-12");
//        request.setDemurrageFees(new BigDecimal(333));
//        request.setDemurrageFeesType("1");
//        request.setTotalAmount(new BigDecimal(200));
//        request.setRemark("备注....");
//        String url = "http://localhost:8080/ship/pushUpdateInfo";
//        String param = "data=" + gson.toJson(request);
//        System.out.println("param:" + param);
//        String result = HttpRequest.sendPost(url, param, "11");
//        System.out.println(result);
    }

    @Test
    public void test() {
        //Gson gson=new Gson();
        //Cc back=new Cc();
        // back.setSouceId("WL333");
        // back.setStatus("COMPLETED_ALREADY");
        // back.setToken("$2a$12$JieUY20RhLk9mxKwbeWk/unLVgXuVKjrHSYO0IOpLj/qpggDjLlgq");
        //back.setVersion("v1.0");
        // back.setTransportstartdate("2015-12-11");
        //back.setTransportenddate("2015-12-11");
        //back.setTransportprices(new BigDecimal(11));
        //back.setTotalprice(new BigDecimal(22));
        //back.setLogisticsphone("12345678909");
        //back.setOrdercode("44444444");
        //back.setVendercreatetime("2015-12-11 11:11:11");
        //String url = "http://localhost:8080/logistics/pushState";
        //String param="data="+gson.toJson(back)+"&sign=1";
        //System.out.println("param:"+param);
        //String result =  HttpRequest.sendPost(url ,param,"56");
        //System.out.println(result);

    }

    @Test
    public void testlogisticsShipClient()throws IOException {
//        ShipQuery shipQuery=new ShipQuery();
//        //shipQuery.setBeginDate(LocalDateTime.now());
//        //shipQuery.setEndDate(LocalDateTime.now());
//        shipQuery.setLoadPortName("京唐");
//        //shipQuery.setLowerTon(800);
//        shipQuery.setPage(1);
//        shipQuery.setPageSize(1);
//        shipQuery.setShipName(null);
//        shipQuery.setSortIdent(0);
//        shipQuery.setSortNum(0);
//        //shipQuery.setUpperTon(600);
//
//        SubmitShipInfo submitShipInfo=new SubmitShipInfo();
//        submitShipInfo.setAddTime("2016-01-19 16:49:37");
//        submitShipInfo.setArrivalAddress("到达地址");
//        submitShipInfo.setCapacity(20);
//        submitShipInfo.setNo("WLS1239566555645");
//        submitShipInfo.setStartAddress("开始地址");
//        submitShipInfo.setCargoName("CargoName");
//        submitShipInfo.setStartDate("2016-01-19");
//        submitShipInfo.setRemark("Remark");
//        submitShipInfo.setIP("192.168.0.1");
//        submitShipInfo.setMemberMobile("MemberMobile");
//        submitShipInfo.setMemberName("MemberName");
//        submitShipInfo.setCompanyName("CompanyName");
//        submitShipInfo.setIsTrade(1);
//
//        CancelShip cancelShip=new CancelShip();
//        cancelShip.setNo("WLS123456");
//        cancelShip.setIP("192.168.0.1");
//        cancelShip.setRemark("remarktest");
//        cancelShip.setEditDate("2016-01-19 16:49:37");
//
////		logisticsShipClient.testGet();
////		logisticsShipClient.testPost();
//        logisticsShipClient.shipquery(shipQuery);
////		logisticsShipClient.submitInfo(submitShipInfo);
////		logisticsShipClient.cancelInfo(cancelShip);

    }

}








