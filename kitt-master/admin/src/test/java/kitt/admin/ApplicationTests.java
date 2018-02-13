package kitt.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitt.core.libs.LogisticsShipClient;
import kitt.core.libs.LogisticsVender56Client;
import kitt.core.persistence.ReservationMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {


	@Autowired
	private LogisticsShipClient logisticsShipClient;
	@Autowired
	private ObjectMapper objectMapper=new ObjectMapper();
	@Autowired
	private ReservationMapper reservationMapper;
	@Autowired
	private LogisticsVender56Client vender56Client;



	@Test
	public void contextLoads() {
		String a1="qq+";
		String a2="qq＋";
		a1.toUpperCase();
		a1=a1.replaceAll("[\\p{Punct}\\pP]", "")+"＋";
		System.out.println(a1);
		System.out.println(a1.equals(a2));

	}




	@Test
	public void a() throws IOException{
//		Gson gson=new Gson();
//		String b="{\"TotalRecord\":38,\"TotalPage\":38,\"RetData\":[{\"id\":89820,\"cid\":6964,\"cnShipName\":\"银祥288\",\"enShipName\":\"yinxiang288\",\"levelInfo\":3,\"loadTon\":\"9500\",\"loadDate\":\"2016-01-15T00:00:00\",\"shipTypeID\":0,\"loadPortID\":121,\"goodsTypeID\":0,\"Recommend\":0,\"portName\":\"池州\",\"goodsName\":\"其他\",\"fixedRoute\":0,\"planState\":0,\"editDate\":\"38天以前\",\"theTrue\":0,\"shipLong\":\"103\",\"shipWidth\":\"17\",\"typeDeep\":\"\",\"TypeName\":null,\"Makedate\":\"\",\"addTime\":\"2015-12-08T18:20:45.393\"}],\"RetCode\":0,\"RetMsg\":\"成功\"}";
//		String s="{\"TotalRecord\":38,\"TotalPage\":8,\"RetData\":[{\"id\":89820,\"cid\":6964,\"cnShipName\":\"银祥288\",\"enShipName\":\"yinxiang288\",\"levelInfo\":3,\"loadTon\":\"9500\",\"loadDate\":\"2016-01-15T00:00:00\",\"shipTypeID\":0,\"loadPortID\":121,\"goodsTypeID\":0,\"Recommend\":0,\"portName\":\"池州\",\"goodsName\":\"其他\",\"fixedRoute\":0,\"planState\":0,\"editDate\":\"38天以前\",\"theTrue\":0,\"shipLong\":\"103\",\"shipWidth\":\"17\",\"typeDeep\":\"\",\"TypeName\":null,\"Makedate\":\"\",\"addTime\":\"2015-12-08T18:20:45.393\"},{\"id\":89395,\"cid\":6961,\"cnShipName\":\"长荣505\",\"enShipName\":\"changrong505\",\"levelInfo\":3,\"loadTon\":\"3500\",\"loadDate\":\"2016-01-15T00:00:00\",\"shipTypeID\":4,\"loadPortID\":969,\"goodsTypeID\":0,\"Recommend\":0,\"portName\":\"空长江\",\"goodsName\":\"其他\",\"fixedRoute\":0,\"planState\":0,\"editDate\":\"38天以前\",\"theTrue\":0,\"shipLong\":\"71\",\"shipWidth\":\"13\",\"typeDeep\":\"\",\"TypeName\":\"普通散货船\",\"Makedate\":\"\",\"addTime\":\"2015-12-08T18:20:12.503\"},{\"id\":109165,\"cid\":8254,\"cnShipName\":\"荟通128\",\"enShipName\":\"huitong128\",\"levelInfo\":0,\"loadTon\":\"1000\",\"loadDate\":\"2016-12-08T00:00:00\",\"shipTypeID\":0,\"loadPortID\":6,\"goodsTypeID\":364,\"Recommend\":0,\"portName\":\"湛江港\",\"goodsName\":\"煤灰\",\"fixedRoute\":0,\"planState\":0,\"editDate\":\"46天以前\",\"theTrue\":0,\"shipLong\":\"53\",\"shipWidth\":\"9\",\"typeDeep\":null,\"TypeName\":null,\"Makedate\":\"\",\"addTime\":\"2015-11-30T11:36:21.973\"},{\"id\":106113,\"cid\":6984,\"cnShipName\":\"宏通海1\",\"enShipName\":\"hongtonghai1\",\"levelInfo\":3,\"loadTon\":\"4800\",\"loadDate\":\"2016-01-15T00:00:00\",\"shipTypeID\":0,\"loadPortID\":29,\"goodsTypeID\":0,\"Recommend\":0,\"portName\":\"舟山\",\"goodsName\":\"其他\",\"fixedRoute\":0,\"planState\":1,\"editDate\":\"33天以前\",\"theTrue\":0,\"shipLong\":\"83\",\"shipWidth\":\"14\",\"typeDeep\":\"\",\"TypeName\":null,\"Makedate\":\"2005\",\"addTime\":\"2015-12-13T17:36:09.66\"},{\"id\":112647,\"cid\":2095,\"cnShipName\":\"鸿海188\",\"enShipName\":\"honghai188\",\"levelInfo\":3,\"loadTon\":\"1000\",\"loadDate\":\"2016-01-15T00:00:00\",\"shipTypeID\":0,\"loadPortID\":1335,\"goodsTypeID\":0,\"Recommend\":0,\"portName\":\"广州江门\",\"goodsName\":\"其他\",\"fixedRoute\":0,\"planState\":1,\"editDate\":\"38天以前\",\"theTrue\":0,\"shipLong\":\"53\",\"shipWidth\":\"9\",\"typeDeep\":null,\"TypeName\":null,\"Makedate\":\"0\",\"addTime\":\"2015-12-08T18:28:12.567\"}],\"RetCode\":0,\"RetMsg\":\"成功\"}";
//		ShipQueryRet shipQueryRet=gson.fromJson(s, ShipQueryRet.class);
//		System.out.println("result to class:"+shipQueryRet.toString());
	}
	public  void testOrder() throws UnsupportedEncodingException {
	}
	@Test
	public void testlogisticsShipClient()throws IOException{
//		ShipQuery shipQuery=new ShipQuery();
////		shipQuery.setBeginDate(LocalDateTime.now());
////		shipQuery.setEndDate(LocalDateTime.now());
////		shipQuery.setLoadPortName("京唐");
////		shipQuery.setLowerTon(800);
//		shipQuery.setPage(1);
//		shipQuery.setPageSize(1);
////		shipQuery.setShipName(null);
//		shipQuery.setSortIdent(0);
//		shipQuery.setSortNum(0);
////		shipQuery.setUpperTon(600);
//
//		String data = JsonMapper.nonDefaultMapper().toJson(shipQuery);
//		System.out.println("========="+data);
//
//
//		SubmitShipInfo submitShipInfo=new SubmitShipInfo();
//		submitShipInfo.setAddTime("2016-01-19 16:49:37");
//		submitShipInfo.setArrivalAddress("到达地址");
//		submitShipInfo.setCapacity(20);
//		submitShipInfo.setNo("WLS1239566555645");
//		submitShipInfo.setStartAddress("开始地址");
//		submitShipInfo.setCargoName("CargoName");
//		submitShipInfo.setStartDate("2016-01-19");
//		submitShipInfo.setRemark("Remark");
//		submitShipInfo.setIP("192.168.0.1");
//		submitShipInfo.setMemberMobile("MemberMobile");
//		submitShipInfo.setMemberName("MemberName");
//		submitShipInfo.setCompanyName("CompanyName");
//		submitShipInfo.setIsTrade(1);
//
//		CancelShip cancelShip=new CancelShip();
//		cancelShip.setNo("WLS123456");
//		cancelShip.setIP("192.168.0.1");
//		cancelShip.setRemark("remarktest");
//		cancelShip.setEditDate("2016-01-19 16:49:37");


//		logisticsShipClient.testGet();
//		logisticsShipClient.testPost();
//		logisticsShipClient.shipquery(shipQuery);
//		logisticsShipClient.submitInfo(submitShipInfo);
//		logisticsShipClient.cancelInfo(cancelShip);

	}

	@Test
	public void testVender56Client() throws IOException {
//		vender56Client.initTokenSalt();
//		vender56Client.getToken();
//	 	vender56Client.getSalt();
//		Logisticsintention logisticsintention=new Logisticsintention();
//		logisticsintention.setSouceId("WLC12345678");
//		logisticsintention.setContacts("易煤网用户");
//		logisticsintention.setMobile("00000000000");
//		logisticsintention.setLoadProvince("上海市");
//		logisticsintention.setLoadCity("上海市");
//		logisticsintention.setLoadCountry("静安区");
//		logisticsintention.setLoadAddDetail("淮海路200号");
//		logisticsintention.setUnLoadProvince("北京市");
//		logisticsintention.setUnLoadCity("北京市");
//		logisticsintention.setUnloadCountry("朝阳区");
//		logisticsintention.setUnLoadAddDetail("王府井路300号");
//		logisticsintention.setGoodsType("块煤");
//		logisticsintention.setGoodsWeight(BigDecimal.valueOf(100));
//		logisticsintention.setPriceUp(BigDecimal.valueOf(100));
//
//		vender56Client.purpose(logisticsintention,true);
//		vender56Client.cancelpurpose("WLC12345678","价格高");

	}


}
