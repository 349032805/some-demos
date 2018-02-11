<template>
  <div class="orderDetail" v-show='pageShow'>
    <z-dialog :isClose='false' :value.sync='dialogShow' :dialogStyle='dialogStyle'>
      <code-box :list='list'></code-box>
    </z-dialog>
    <order-info-top :list='list' :type='type'></order-info-top>
    <div class="appointmentTime" v-if='list.eatStyle==3 || list.eatStyle ==4'>
      <p class="time">{{eatStyleModel}}时间:
        <span>{{list.eatTimeStr}}</span>
      </p>
      <p class="phone">联系方式:
        <span>{{list.phone}}</span>
      </p>
    </div>
    <div class="detailCenter">
      <div class="inner1" @click='enterStore'>
        <span>{{orderShopOutVo.fsshopname}}</span>
        <img src="../../assets/0001.png" alt="" v-if='type !=1'>
      </div>
      <div class="inner2">
        <order-goods :orderMenuitemOutVoList='orderMenuitemOutVoList'></order-goods>
        <p class="outdoorMoney" v-if='orderServiceFeeOutVoList != null'>
          <span>{{orderServiceFeeOutVoList.serviceName}}</span>
          <span>￥{{orderServiceFeeOutVoList.totalPrice}}元</span>
        </p>
      </div>
    </div>
    <div class="sendGood" v-if="orderActivityOutVoList">
      <div>
        <send-goods :orderActivityOutVoList='orderActivityOutVoList'></send-goods>
      </div>
    </div>
    <div class="totalmoney">
      <order-info-money :list='list'></order-info-money>
      <div class="inner2">
        <div @click='enterStoreDetail' v-if='type !=1'>
          <div class="button-item">
            <img src="../../assets/orderDetail.png" alt="">
            <span>商家详情</span>
          </div>
        </div>
        <div :style="type1">
          <div class="button-item" @click="sheetVisible = true">
            <img src="../../assets/phone.png" alt="">
            <span>联系商家</span>
          </div>
        </div>
      </div>
    </div>
    <order-info :list='list' :orderTicketOutVo='orderTicketOutVo'></order-info>
    <img :src="advertisementImg" alt="" style='padding:.4rem 0' @click='advertisement'>
    <img src="../../assets/banner1.jpg" alt="" style='margin-bottom:.4rem'>
    <div class="zbutton-box">
      <z-button :styles='btnStyles' @click.native="enterGivingAsment">{{btnContent}}</z-button>
    </div>
    <mt-actionsheet :actions="actions" cancelText='取消' v-model="sheetVisible">
    </mt-actionsheet>
    <share-model :store='shopDetail'></share-model>
  </div>
</template>

<script>
import { mapState } from "vuex";
import { ZDialog } from '@/crib-zk';
import OrderGoods from '@/vistitorComponents/orderGoods/orderGoods.vue'
import SendGoods from '@/vistitorComponents/sendGoods/sendGoods.vue'
import orderInfoTop from '@/vistitorComponents/orderInfoTop/orderInfoTop.vue' //没有flex布局
import OrderInfo from '@/vistitorComponents/orderInfo/orderInfo.vue'  //没有flex布局
import OrderInfoCode from '@/vistitorComponents/orderInfoCode/orderInfoCode.vue' //没有flex布局
import OrderInfoMoney from '@/vistitorComponents/orderInfoMoney/orderInfoMoney.vue'  //没有flex布局 
import CodeBox from '@/vistitorComponents/codeBox/codeBox.vue' //没有flex布局
import ShareModel from '@/vistitorComponents/shareModel/shareModel.vue'
import { ZButton } from '@/crib-zk'
import mapAxios from '../../config/mapAxios.js'
const SUCCESS = 200;
export default {
  name: "orderDetail",
  created() {
    //清除已点菜品，add by fangdx
    sessionStorage.removeItem('sessionfoods');
    //清楚售罄菜品
    localStorage.removeItem('sellOutList');
    //分享到朋友圈和朋友
    
    //广告位
    mapAxios('get', 'https://portal.51eparty.com/api/ad?type=ORDER_DETAIL', {})
      .then(res => {
        let result = res.data
        this.advertisementImg = result.imageUrl
        this.advertisementUrl = result.adUrl
      })
     


    this.$indicator.open('加载中...');
    const type = this.$route.query.type
    const from = this.$route.query.from
    if (type == 1) {
      this.url = 'getByOrderIdAndStoreIdOrderInfo'
      this.storeId = this.$route.query.storeId
      this.type = type
      this.orderId = this.$route.query.orderId
      this.storeUrl = 'getShopInfoInWeiXin'
    } else {
      this.url = 'getOrderInfo'
      this.orderId = this.$route.params.orderId
      this.storeUrl = 'getShopInfo'
    }

    sessionStorage.type = type
    sessionStorage.storeId = this.storeId
    sessionStorage.orderId = this.orderId
    let _this = this
    this.$store.commit('SAVE_TITLE', '订单详情')
    //如果是公众号推送和从个人中心进来的时候都不进此接口
    if (type != 1 && from != 1 && sessionStorage.firstIn != 'true') {
      this.$http('get', `${this.baseUrl}/getUserInfo`)
        .then(res => {
          if (res.model.subscribe == 0) {
            this.dialogShow = true
            sessionStorage.firstIn = 'true'
          }
        })
    }
    //分享


    this.$http('get', `${this.baseUrl}/${this.url}`, {
      storeId: this.storeId,
      orderId: this.orderId,
      type: this.type
    }).then(res => {
      this.pageShow = true
      let list = this.list = res.model
      this.orderShopOutVo = list.orderShopOutVo
      this.shopId = this.orderShopOutVo.fsShopGUID
      this.storeId = this.shopId
      this.orderMenuitemOutVoList = list.orderMenuitemOutVoList
      this.orderActivityOutVoList = list.orderActivityOutVoList
      if (list.orderServiceFeeOutVoList != null) {
        this.orderServiceFeeOutVoList = list.orderServiceFeeOutVoList[0]
      }
      this.orderTicketOutVo = list.orderTicketOutVo;
      this.evaluateStatus = list.evaluateStatus
      console.log(this.evaluateStatus == 0)
      let fstel = this.orderShopOutVo.fstel || '商家未留电话'
      this.actions = [{
        name: fstel,
        method() {
          if (fstel != "商家未留电话") {
            location.href = `tel:${fstel}`
          }
        }
      }]

      return this.$http('get', `${this.baseUrl}/${this.storeUrl}`, { shopId: this.shopId })
    }).then(res => {
      this.$indicator.close()
      this.shopDetail = res.model
      this.store = res.model
            //分享
      this.initShare();
    })
  },
  computed: {
    eatStyleModel() {
      //服务员送餐
      if(this.list.isSupportDelivery == 1){
          if(this.list.eatStyle == 3) {
             return '就餐'
          }else{
             return '取餐'
          }
      }else{
             return '取餐'
      }
    },
    type1() {
      if (this.type == 1) {
        return {
          float: 'none',
          margin: '0 auto'
        }
      }
    },
    btnStyles() {
      if (this.evaluateStatus == 0) {
        return this.btnStyles1
      }
      return this.btnStyles0

    },
    btnContent() {
      if (this.evaluateStatus == 0) {
        return "菜品评价"
      } else {
        return "查看菜品评价"
      }

    }
  },
  components: {
    ShareModel,
    OrderGoods,
    SendGoods,
    ZButton,
    OrderInfo,
    orderInfoTop,
    OrderInfoCode,
    OrderInfoMoney,
    ZDialog,
    CodeBox
  },
  data() {
    return {
      store : "",
      advertisementUrl: '',
      advertisementImg: "",
      storeUrl: "",
      shareModel: false,
      shopDetail: {},
      pageShow: false,
      storeId: '',
      url: '',
      type: '',
      dialogStyle: {
        background: 'transparent',
        width: '90%',
        maxWidth: 'none',
        top: '45%'
      },
      dialogShow: false,
      btnStyles0: {
        color: '#505050',
        background: ' #FFFFFF',
        border: '1px solid #DCDCDC',
        borderRadius: '4px',
        height: '44px',
        fontSize: '.68rem'
      },
      btnStyles1: {
        color: '#505050',
        background: '#FFFFFF',
        border: '1px solid #DCDCDC',
        height: '44px',
        borderRadius: '4px',
        fontSize: '.68rem'
      },

      orderId: '',
      list: {},
      fstel: '',
      //店信息
      orderShopOutVo: {},
      //菜单详情
      orderMenuitemOutVoList: [],
      //优惠活动信息
      orderActivityOutVoList: [],
      //服务费信息
      orderServiceFeeOutVoList: null,
      //订单发票
      orderTicketOutVo: {},
      sheetVisible: false,
      actions: [],
      evaluateStatus: 0,
      text: "菜品评价"
    }
  },
  methods: {
    advertisement() {
      location.href = `${this.advertisementUrl}`
    },
    enterStore() {
      if (this.type == 1) {
        return
      }
      this.$router.push({ name: 'menus', params: { shopId: this.orderShopOutVo.fsShopGUID } })
    },
    enterStoreDetail() {
      this.$router.push({ name: "storeDetail", params: { storeId: this.orderShopOutVo.fsShopGUID } })
    },
    enterGivingAsment() {
      if (this.evaluateStatus == 0) {
        //没有评价
        this.$router.push({ name: "givingAssessment", params: { orderId: this.list.orderId, fsShopGuid: this.orderShopOutVo.fsShopGUID } })  //这里以后要换 fsShopGuid: this.orderShopOutVo.fsShopGUID
        return
      }
      //评价过
      this.$router.push({ name: "assessmentDetail", params: { orderId: this.list.orderId } })

    },

    _wxShare(wxAuth, store, shareUrlFinal) {
      console.log("调用微信分享接口");
      let _this = this;
      let imgUrl = "";
      if (store.fsLogo) {
        if (store.fsLogo != null && store.fsLogo != "") {
          imgUrl = store.fsLogo;
        } else {
          imgUrl = "http://cdn.51eparty.com/static/images/default_store.png";
        }

      } else {
        imgUrl = "http://cdn.51eparty.com/static/images/default_store.png";
      }
      wx.config({
        debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
        appId: wxAuth.appId, // 必填，公众号的唯一标识
        timestamp: wxAuth.timestamp, // 必填，生成签名的时间戳
        nonceStr: wxAuth.nonceStr, // 必填，生成签名的随机串
        signature: wxAuth.signature,// 必填，签名，见附录1
        jsApiList: ['onMenuShareAppMessage', "onMenuShareTimeline"] // 必填，需要使用的JS接口列表
      });
      wx.ready(function () {
        //分享到朋友圈
        wx.onMenuShareTimeline({
          title: store.fsShopName + '-百味云人气餐厅', // 分享标题
          // link: wxAuth.url, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
          link: shareUrlFinal, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
          imgUrl: imgUrl,
          success: function () {
            // 用户确认分享后执行的回调函数
            //关闭分享模态框并给出提示
            _this.showShare = false;
            _this.$toast("分享成功");

          },
          cancel: function () {
            // 用户取消分享后执行的回调函数
          },
          fail: function (res) {
            alert(JSON.stringify(res));
          }
        });

        //分享给朋友
        wx.onMenuShareAppMessage({
          title: store.fsShopName + '-百味云人气餐厅', // 分享标题
          desc: '千帆过尽皆不是，唯有这家合我胃，现在我把她推荐给你',
          link: shareUrlFinal, // 分享链接，该链接域名必须与当前企业的可信域名一致
          imgUrl: imgUrl,
          success: function () {
            // 用户确认分享后执行的回调函数
          },
          cancel: function () {
            // 用户取消分享后执行的回调函数
          },
          fail: function (res) {
            alert(JSON.stringify(res));
          }
        });


      });
    },
    initShare() {
      let shareUrl = location.origin + '/#/shareStore';
      console.log("要分享的页面地址:" + shareUrl);
      //调用微信api分享
      this.$http("get", this.baseUrl + '/getShareSign', { notifyUrl: shareUrl }).then((response) => {
        if (response.code == SUCCESS) {
          console.log(response)
          let userId = response.model.userId;
          console.log("userId:" + userId);
          let shareUrlFinal = location.origin + '/#/shareStore?storeId=' + this.storeId + '&userId=' + userId;
          console.log("shareUrlFinal:" + shareUrlFinal);
          this._wxShare(response.model, this.store, shareUrlFinal);
        }
      });
    },
  }
}
</script>

<style lang="less" scoped>
@import './orderdetail.less';
</style>
