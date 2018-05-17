<template>
  <div class="home">
    <div class="banner-box">
      <!-- <img src="../../images/home/banner.png" class="banner"> -->
      <mt-swipe :auto="5000" :speed="1000">
        <mt-swipe-item v-for="(item, index) in bannerList" :key="index">
          <img :src="item.img" class="banner" @click="goLink(item.jump)">
        </mt-swipe-item>
      </mt-swipe>
      <div class="broadcast">
        <div class="broadcast-box">
          <div class="voice">
            <img src="../../images/home/voice.png">
          </div>
          <vue-marquee v-if="sellList.length" :duration="600" :interval="2000">
            <p v-for="(item, index) in sellList" :key="index">{{item}}</p>
          </vue-marquee>

        </div>
      </div>
    </div>

    <div class="content">
      <div class="phone-box" v-if="status == 1 || status == 2 || status == 10 || status == 3 || status == 4">
        <div class="board">
          <p class="theme">
            <span>您的手机型号</span>
            <img src="../../images/home/triangle.png" v-if="status != 1" @click="selectPhone()">
          </p>

          <div class="info" v-if="status == 1 || status == 2 || status == 10">
            <div class="row">未选择机型</div>
            <div class="row bottom-line">寄送周期({{periodDay}}天)</div>
          </div>

          <div class="phone" v-if="status == 3 || status == 4">
            <div class="current">
              <!-- <img src="../../images/home/iphoneX.png">
              <div class="phone-index">
                <p>iphoneX</p>
                <p>内存: 128G</p>
                <p class="status">回收金额:手机评估中</p>
              </div> -->

              <img :src="selectedPhone.img">
              <div class="phone-index">
                <p>{{selectedPhone.name}}</p>
                <p>内存: {{selectedPhone.mem}}</p>
                <p class="status">回收金额:{{selectedPhone.price}}<span v-if="status == 4">元</span></p>
              </div>
            </div>
          </div>

          <button type="button" class="opt-btn" @click="goAuth()" v-if="status == 1 || status == 10">认证拿钱</button>
          <button type="button" class="opt-btn" @click="selectPhone()" v-if="status == 2">选择机型</button>
          <button type="button" class="opt-btn disabled" v-if="status == 3">立马下单</button>
          <button type="button" class="opt-btn" @click="goBuy()" v-if="status == 4">立马下单</button>
        </div>
      </div>

     <div class="order-box" v-if="status == 5 || status == 6 || status == 7 || status == 8 || status == 9">
        <div class="board">
          <p class="theme on-status" v-if="status == 5">
            <span>订单审核中</span>
            <img src="../../images/home/loading_orange.png" class="loading-icon">
          </p>

          <p class="theme green-status" v-if="status == 7">
            <span>订单放款中</span>
            <img src="../../images/home/loading_green.png" class="loading-icon">
          </p>

          <p class="theme fail-status" v-if="status == 6">
            <span>审核失败</span>
            <img src="../../images/home/warning.png" class="warning-icon">
          </p>

          <p class="theme on-status" v-if="status == 8">
            <span>订单交易中,请尽快寄送手机</span>
          </p>

          <p class="theme fail-status" v-if="status ==9">
            <span>订单已逾期</span>
            <img src="../../images/home/warning.png" class="warning-icon">
          </p>

          <div class="info">
            <div class="row">
              <p class="key">订单金额</p>
              <p class="value">￥{{order.amount}}</p>
            </div>
            <div class="row">
              <p class="key">截至倒计时(天)</p>
              <p class="value">寄送周期({{order.count_date}}天)</p>
            </div>
            <div class="row bottom-line">
              <p class="key">最迟寄送时间</p>
              <p class="value">{{order.last_send_date}}</p>
            </div>
          </div>

          <button type="button" class="opt-btn on" @click="goRevoke()" v-if="status ==8">撤销订单</button>
          <button type="button" class="opt-btn warning" @click="goFinish()" v-if="status ==9">立即结清</button>
        </div>
      </div>


      <!-- 回收流程 -->
      <div class="word-box" v-if="status == 1 || status == 2">
        <img src="../../images/home/word.png">
      </div>
       
    </div>

    <!-- 弹出的模态框 -->
    <mt-popup v-model="phonePopup" position="bottom" pop-transition='popup-fade'>
      <div class="modal-box">
        <div class="theme">
          <p class="pull-left">选择手机型号</p>
          <div class="pull-right">
            <img src="../../images/home/close.png" @click="phonePopup = false">
          </div>
        </div>

        <ul v-for="(item,index) in phoneList" @click="pick(item)" :key="index">
          <li>
            <p class="pull-left">{{item.name}}</p>
            <div class="pull-right">
              <img src="../../images/home/tick.png" v-show="item.selected">
              <img src="../../images/home/circle.png" v-show="item.selected == false">
            </div>
          </li>
        
        </ul>
      </div>
    </mt-popup>

    <!-- 悬浮的图标 to 我的订单 -->
    <img src="../../images/home/my_order.png" class="my-order-icon"  ref="myOrder" @touchstart="touchStart" 
    @touchmove="touchMove"
    @touchend="touchEnd">


  </div>
</template>

<script type="text/ecmascript-6">
import api from "@/api/api";
import { Toast } from "mint-ui";
import Marquee from "vue-marquee";

const SUCCESS = 0;
const ICON_RADIUS = 47;
export default {
  data() {
    return {
      bannerList: [],
      sellList: [],
      phonePopup: false,
      selectedPhone: {},
      phoneList: [],
      status: 0,
      periodDay: 0,
      order: {}
    };
  },
  created() {
    /**
     *  消除右上角的设置
     */
    window["nativeMethod"] && window["nativeMethod"]["returnNativeMethod"] && nativeMethod.returnNativeMethod(
      JSON.stringify({
        "type": 19,
        "data": {
            "btn_text": "",
        }
      })
    );

    this.touch = {}

    this._getIndexData();
  },
  methods: {
    pick(item) {
      this.phoneList.forEach(obj => {
        obj.selected = false;
      });
      item.selected = true;

      api.selectPhone(item.type).then(response => {
        if (response.code == SUCCESS) {
          this._getIndexData();
          this.phonePopup = false;
        }
      });
    },
    _getIndexData() {
      api.getIndexData().then(response => {
        if (response.code == SUCCESS) {
          this.bannerList = response.data.banner;
          this.sellList = response.data.carouse;
          this.selectedPhone = response.data.selected_phone;
          this.phoneList = response.data.phone_list;
          this.status = response.data.status;
          // this.status = 5;
          this.periodDay = response.data.info.count_date;
          this.order = response.data.info;

          this.phoneList.forEach((item, index) => {
            this.$set(item, "selected", false);
          });
        }
      });
    },
    goLink(url) {
      window.location.href = url;
    },
    goAuth() {
      //"status": 1, # 1：未登录， 2:未选择机型， 3：手机回收价格评估中，
      //4：立即下单， 5：:审核中， 6：审核失败， 7：放款中， 8：请尽快寄送手机， 9：已逾期, 10未认证
      if (this.status == 1) {
        window.location.href = "koudaikj://app.launch/login/applogin";
      }

      if (this.status == 10) {
        window["nativeMethod"] && window["nativeMethod"]["returnNativeMethod"] && nativeMethod.returnNativeMethod(
          JSON.stringify({
            "type": 3,
            "verify_type": 4
          })
        );
      }
    },
    selectPhone() {
      this.phonePopup = true;
    },
    goBuy() {
      this.$router.push({
        path: "/recovery",
        query: { id: this.order.product_id }
      });
    },
    goRevoke() {
      this.goBankCard();
    },
    goFinish() {
      this.goBankCard();
    },
    goBankCard() {
      const protocol = window.location.protocol;
      const hostname = window.location.hostname;
      let url =
        protocol +
        "//" +
        hostname +
        "/mobile/web/loan/loan-repayment-type?id=" +
        this.order.order_id;
      window.location.href = url;
    },
    touchStart(e){
      this.touch.initiated = true;
      this.touch.left = e.touches[0].pageX;
      this.touch.top = e.touches[0].pageY;
      this.touch.width = parseInt(document.documentElement.clientWidth) - ICON_RADIUS;
      this.touch.height = parseInt(document.documentElement.clientHeight) - ICON_RADIUS;
    },
    touchMove(e){
      if (!this.touch.initiated) {
        return
      }
      var x = e.touches[0].pageX;
      var y = e.touches[0].pageY;
      if (x >= this.touch.width) {
        x = this.touch.width - ICON_RADIUS
      }
      if (y >= this.touch.height) {
        y = this.touch.height - ICON_RADIUS
      }
      this.$refs.myOrder.style.left = x + 'px';
      this.$refs.myOrder.style.top = y + 'px';
    },
    touchEnd(e){
      this.touch.initiated = false
    }
  },
  components: {
    "vue-marquee": Marquee
  }
};
</script>

<style lang="less">
@import "./home.less";
</style>
