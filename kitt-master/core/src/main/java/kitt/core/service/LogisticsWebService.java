package kitt.core.service;

import kitt.core.domain.Logistics56responselog;
import kitt.core.domain.LogisticsStatus;
import kitt.core.domain.Logisticsintention;
import kitt.core.domain.User;
import kitt.core.libs.LogisticsVender56Client;
import kitt.core.libs.logistics.PurposeResponse;
import kitt.core.persistence.LogisticsMapper;
import kitt.core.persistence.LogisticsfeedbackMapper;
import kitt.core.persistence.LogisticsvenderMapper;
import kitt.core.persistence.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangbolun on 15/12/14.
 */
@Service
public class LogisticsWebService {
    @Autowired
    private LogisticsVender56Client vender56Client;
    @Autowired
    private LogisticsvenderMapper logisticsvenderMapper;
    @Autowired
    private LogisticsMapper logisticsMapper;
    @Autowired
    private LogisticsfeedbackMapper logisticsfeedbackMapper;
    @Autowired
    private UserMapper userMapper;

    private Map<Integer,String> message=new HashMap<Integer,String>();

    public LogisticsWebService(){
        this.message.put(600000,"接收成功");
        this.message.put(-600000,"非法请求");
        this.message.put(-600009,"该单子已经提交过了");
        this.message.put(600020,"token获得成功");
        this.message.put(600010,"车辆查询成功");
        this.message.put(-600020,"token获得失败");
        this.message.put(-600001,"唯一标识字段为空");
        this.message.put(-600002,"装车地址-省市县为空");
        this.message.put(-600003,"卸车地址-省市县为空");
        this.message.put(-600004,"货物类型为空");
        this.message.put(-600005,"货物重量为空");
        this.message.put(-600006,"联系电话为空");
        this.message.put(-600007,"接口令牌为空");
        this.message.put(-600008,"该意向单已无法取消,请联系56快车");
        this.message.put(-600014,"该意向单已无法更改，请联系56快车");
        this.message.put(-600010,"车辆数据为空");
        this.message.put(-600015,"该意向单不存在");
    }

    /**
     * 提交物流意向单
     * @param intention
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    @Transactional
    public String commitPurpose(Logisticsintention intention,boolean isfirst) {
        try {
            vender56Client.initTokenSalt();
            PurposeResponse purposeResponse = vender56Client.purpose(intention, isfirst);
            Logistics56responselog logistics56responselog = new Logistics56responselog();
            logistics56responselog.setCode(purposeResponse.getCode());
            logistics56responselog.setMessage(purposeResponse.getMessage());
            logistics56responselog.setDatabody(purposeResponse.getDatabody());
            logistics56responselog.setDatabodysize(purposeResponse.getAttributes().getDatabodysize());
            logistics56responselog.setSouceid(intention.getSouceId());
            logisticsvenderMapper.addLogistics56responselog(logistics56responselog);
            if (purposeResponse.getCode() == 600000 && isfirst == true) {
                logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.MATCH_ING.name(), intention.getSouceId());
                if(intention.getUserid()!=0) {
                    User user = userMapper.getUserById(intention.getUserid());
                    final String content = "尊敬的易煤网用户，系统正在为您匹配物流报价，请保持手机畅通。如需帮助请拨打咨询热线：400-960-1180。";
                    MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
                }
            }
            return  message.get(purposeResponse.getCode());
        } catch (IOException ex) {
            return  "56快车外部接口异常，请稍后再试";
        }
    }

    @Transactional
    public String cancelPurpose(Logisticsintention logisticsintention,String customerremark) throws IOException{
        PurposeResponse purposeResponse = vender56Client.cancelpurpose(logisticsintention.getSouceId(), "");
        if(purposeResponse.getCode()==-600000){
            vender56Client.getToken();
            vender56Client.getSalt();
            purposeResponse = vender56Client.cancelpurpose(logisticsintention.getSouceId(), "");
        }
        if(purposeResponse.getCode()==600000){
            logisticsMapper.cancelLogisIntention(LogisticsStatus.NOT_TRADING.name(),logisticsintention.getId());
            logisticsMapper.updatCustomerremarkBySouceid(customerremark,logisticsintention.getSouceId());
            logisticsfeedbackMapper.updatecomment("易煤网客户取消56汽运订单",LogisticsStatus.NOT_TRADING.name(),logisticsintention.getSouceId());
            if(logisticsintention.getUserid()!=0) {
                User user = userMapper.getUserById(logisticsintention.getUserid());
                final String content = "尊敬的易煤网用户，您的意向单已经被取消，如需帮助，请拨打咨询热线：400-960-1180";
                MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
            }
        }

        return message.get(purposeResponse.getCode());
    }
}
