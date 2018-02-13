package kitt.core.service;


import com.mysql.jdbc.StringUtils;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.util.SpringContextHolder;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by xiangyang on 15/11/2.
 * 本文件是发送短信封装方法
 * 使用本文件首先考虑使用: CommonMessage, 不能随便添加
 * 如果是admin模块或者定时任务的, 一般情况, 只有一个地方使用, 不需要再添加方法, 使用CommonMessage即可
 * site模块,如果确定只有一个地方使用, 也可以使用CommonMessage, 如果网站,微信都用, 就在下面添加方法
 */
public enum MessageNotice {
    CommonMessage {
        public void noticeUser(String phone, String content) {
            Assert.hasLength(phone, "phone must not be null");
            Assert.hasLength(content, "content must not be null");
            try {
                sms.send(phone, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    ORDER {
        public void noticeUser(String phone, int orderId) {
            final String buyerMsg = "您好,您的订单orderId已经生成，我们会在第一时间安排交易员与您联系!";
            final String sellerMsg = "您好,已有买家购买您编号为supplyId的产品，请您尽快对该笔卖货订单进行处理!";
            Order order = orderMapper.getOrderById(orderId);
            Assert.notNull(order, "order must not be null");
            SellInfo sellInfo = supplyMapper.findSupplyWithOrder(orderId);
            Assert.notNull(sellInfo, "sellinfo must not be null");
            try {
                sms.send(phone, buyerMsg.replace("orderId", order.getOrderid()));
                //非自营，给卖家发短信通知
                if (sellInfo.getType() != 0 && sellInfo.getShopid() != null) {
                    User user = userMapper.getUserById(sellInfo.getSellerid());
                    Assert.notNull(user, "user must not be null");
                    sms.send(user.getSecurephone(), sellerMsg.replace("supplyId", sellInfo.getPid()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    SubmitCompany{
        public void noticeUser(String phone) {
            Assert.hasLength(phone,"phone must not be null");
            final String msg = "您好，您的公司信息已提交给易煤网，我们将尽快给您反馈审核结果。";
            try {
                sms.send(phone, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    GROUPQualify{
        public void noticeUser(String phone,String orderCode) {
            Assert.hasLength(phone, "phone must not be null");
            Assert.hasLength(orderCode, "orderCode must not be null");
            final String msg = "您好，您的编号为orderCode的团购资格申请已成功提交，我们将会尽快给您反馈审核结果。";
            try {
                sms.send(phone, msg.replace("orderCode",orderCode));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    GROUPOrder{
        public void noticeUser(String phone, String orderCode) {
            Assert.hasLength(phone,"phone must not be null");
            Assert.hasLength(orderCode,"orderCode must not be null");
            final String msg = "您好，您的团购订单orderCode已成功生成，请关注后续团购结果。";
            try {
                sms.send(phone, msg.replace("orderCode", orderCode));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    GROUPOrderSuccess{
        public void noticeUser(String phone,String orderId) {
            Assert.hasLength(phone,"phone must not be null");
            Assert.hasLength(orderId, "orderId must not be null");

            final String msg = "您好，您的团购订单orderId已团购成功，我们会在第一时间安排交易员与您联系！";
            try {
                sms.send(phone, msg.replace("orderId",orderId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    GROUPOrderFail{
        public void noticeUser(String phone, String orderId) {
            Assert.hasLength(phone,"phone must not be null");
            Assert.hasLength(orderId, "orderId must not be null");
            final String msg = "您好，您的团购订单orderId团购失败，如需帮助，请拨打客服热线400-960-1180。";
            try {
                sms.send(phone, msg.replace("orderId",orderId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    //已完成 -- 应标提交时 -- 发给招标人
    ALREADYCOMPLETETENDERPACKET {
        public void noticeUser(int id, BigDecimal amout) {
            User user = userMapper.getUserById(id);
            Assert.notNull(user, "user must not be null");
            Assert.hasLength(user.getSecurephone(),"phone must not be null");
            final String msg = "您好，恭喜您本次招标成功，本次共应标 " + amout + " 万吨，感谢您对易煤网的支持！";
            if(user != null && !StringUtils.isNullOrEmpty(user.getSecurephone())){
                try {
                    sms.send(user.getSecurephone(), msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    },
    //已投标 -- 提交投标时 -- 发给投标人
    ALREADYSUBMITTENDERPACKET {
        public void noticeUser(int id, int bidId) {
            User user = userMapper.getUserById(id);
            Assert.notNull(user, "user must not be null");
            Assert.hasLength(user.getSecurephone(), "phone must not be null");
            final String msg = "温馨提示：您的投标已提交成功，请耐心等待招标结果。";
            if(user != null && !StringUtils.isNullOrEmpty(user.getSecurephone())){
                try {
                    sms.send(user.getSecurephone(), msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    },
    //审核支付凭证 -- 审核通过 -- 发给投标人
    TenderPaymentSuccess{
        public  void noticeUser(String phone){
            Assert.hasLength(phone, "phone must not be null");
            final String msg = "温馨提示：您的资格审核已经通过，请及时关注招标结果！";
            try {
                sms.send(phone, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    //审核支付凭证 -- 审核未通过 -- 发给投标人
    TenderPaymentFail{
        public  void noticeUser(String phone, String dealerPhone, String dealerName){
            Assert.hasLength(phone, "phone must not be null");
            final String msg = "温馨提示：您的资格审核没有通过，请及时联系您的交易员[" + dealerPhone + " - " + dealerName + "]";
            try {
                sms.send(phone, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    },
    //用户提交意向单到后台
    LOGISTICSSUBMIT {
        public void noticeUser(String phone) {
            Assert.hasLength(phone, "phone must not be null");
            final String msg = "尊敬的易煤网用户，您的物流意向单已经提交，稍后将由物流专员与您联系，请保持手机畅通。如需帮助请拨打咨询热线：400-960-1180。";
            try {
                sms.send(phone, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    //物流订单已完成
    LOGISTICCOMPLETED {
        public void noticeUser(int id) {
            User user = userMapper.getUserById(id);
            Assert.notNull(user, "user must not be null");
            Assert.hasLength(user.getSecurephone(), "phone must not be null");
            LocalDateTime time=LocalDateTime.now();
            final String msg = "尊敬的易煤网用户，您的货物已于"+time.getYear()+"年"+time.getMonthValue()+"月"+time.getDayOfMonth()+"日完成运输，感谢您的使用，欢迎您再次使用。";
            try {
                sms.send(user.getSecurephone(), msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    ,MessageNotice;

    /**
     * 本文件是发送短信封装方法
     * 使用本文件首先考虑使用: CommonMessage(最上面,第一个方法), 不能随便添加
     * 如果是admin模块 或 定时任务, 一般情况, 只有一个地方使用, 不需要再添加方法, 使用CommonMessage即可
     * site模块,如果确定只有一个地方使用, 也可以使用CommonMessage, 如果网站,微信都用, 就添加新的方法
     */

    public void noticeUser(int id){
        throw new AbstractMethodError();
    }
    public void noticeUser(String phone, int id) {
        throw new AbstractMethodError();
    }
    public void noticeUser(int id, int tenderId){
        throw new AbstractMethodError();
    }
    public void noticeUser(int id, BigDecimal amount){
        throw new AbstractMethodError();
    }
    public void noticeUser(String phone, String id) {
        throw new AbstractMethodError();
    }
    public void noticeUser(String id) {
        throw new AbstractMethodError();
    }
    public void noticeUser(String phone, String dealerName,String dealerPhone){
        throw new AbstractMethodError();
    }

    SMS sms = SpringContextHolder.getBean(SMS.class);
    BuyMapper supplyMapper = SpringContextHolder.getBean(BuyMapper.class);
    OrderMapper orderMapper = SpringContextHolder.getBean(OrderMapper.class);
    UserMapper userMapper = SpringContextHolder.getBean(UserMapper.class);

}
