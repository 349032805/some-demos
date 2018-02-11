<template>
  <div class="order-confirm">
    <div class="layer">
      <div class="price-ban">
        <p class="store-name">{{storeName}}</p>
        <p class="total-price">¥
          <span>{{nowPrice}}</span>
        </p>
        <p class="origin-price">原价: ¥
          <span> {{originPrice}}</span>
        </p>
      </div>
  
      <!-- 点餐方式 -->
      <div class="order-mode">
        <div class="row">
          <div class="two-select">
            <div class="mode-change right-line" @click="displayMode()">
              <span>{{text}}</span>
              <span class="triangle" v-if="orderType==2"></span>
            </div>
  
            <div class="eat-mode">
              <button type="button" :class="{'active':eatMode == 0}" @click="changeEatMode(0)">堂吃</button>
              <button type="button" :class="{'active':eatMode == 1}" @click="changeEatMode(1)">打包</button>
            </div>
          </div>
        </div>
  
        <div class="two-rows" v-show="showOrderMode">
          <div class="row top-line" @click="selectOrderMode(0)">
            <p class="float-left" :class="{'active-row':orderMode == 0}">现场点餐</p>
            <div class="tick" v-show="orderMode==0">
              <img src="../../assets/tick_icon.png">
            </div>
          </div>
          <div class="row top-line" @click="selectOrderMode(1)">
            <p class="float-left" :class="{'active-row':orderMode == 1}">预先点餐</p>
            <div class="tick" v-show="orderMode==1">
              <img src="../../assets/tick_icon.png">
            </div>
          </div>
        </div>
  
      </div>
  
    </div>
  
    <!-- 打包的预约时间和联系方式 -->
    <div class="time-phone" v-show="orderMode == 1">
      <div class="row bottom-line" @click="chooseTime">
        <p class="float-left">预约时间:</p>
        <div class="float-right">
          <span class="col999" v-if="!eatTime">请选择预约时间</span>
          <span class="col666" v-if="dueDate">{{dueDate | formatDate}}</span>
          <span class="col666" v-if="eatTime">{{eatTime}}</span>
          <img src="../../assets/arrow.png" class="arrow">
        </div>
      </div>
  
      <div class="row" @click="writePhone">
        <p class="float-left">联系方式:</p>
        <div class="float-right">
          <span class="col999" v-if="!rightPhone">请填写手机号码</span>
          <span class="col666" v-if="rightPhone">{{rightPhone}}</span>
          <img src="../../assets/arrow.png" class="arrow">
        </div>
      </div>
    </div>
  
    <!-- 背景遮罩 -->
    <div class="bg-layer" v-show="showOrderMode==true" @click="showOrderMode=false"></div>
    <div class="bg-layer" v-show="showPhoneModal==true" style="z-index: 100"></div>
  
    <!-- 选择时间弹出层 -->
    <mt-popup v-model="popupVisible" position="bottom">
      <div class="time-picker">
        <!--<div class="date-select">
              <ul>
                <li><span>{{new Date() | formatDate}}</span> &nbsp; <span>今天</span></li>
              </ul>
            </div>-->
        <div class="time-select">
          <ul ref='rightList'>
            <li @click="selectTime(item,index)" v-for="(item,index) in timeArr" :class="{'active':currentTime==index}">
              <span>{{item.date | showNormalDate}}&nbsp;&nbsp;{{item.time}}</span>
              <div class="tick" v-show="currentTime == index">
                <img src="../../assets/tick_icon.png">
              </div>
            </li>
          </ul>
        </div>
      </div>
    </mt-popup>
  
    <!-- 填写手机号模态框 -->
    <div class="phone-modal" v-show="showPhoneModal==true">
      <div class="title">
        <span>请绑定手机号</span>
        <div class="close" @click="showPhoneModal=false">
          <img src="../../assets/error.png">
        </div>
      </div>
      <div class='content'>
        <div class="row">
          <input type="tel" placeholder="输入手机号" maxlength="11" v-model="telephone" :disabled="canWrite">
          <countDownBtn v-on:send="getPhone" ref="countDownBtn"></countDownBtn>
          <img src="../../assets/close_icon.png" class="clear" @click="clear('phone')" v-show="showClearPhone">
        </div>
        <div class="row">
          <input type="tel" placeholder="填写验证码" maxlength="4" v-model="verifyCode">
          <img src="../../assets/close_icon.png" class="clear" @click="clear('code')" v-show="showClearVerifyCode" style="right:15px">
        </div>
  
        <button type="button" class="sure-btn" :class="completePhone" @click="bindPhone()" :disabled="isDisabled">
          <img src="../../assets/loading.gif" class="loading-icon" v-if="showLoading"> 确认
        </button>
      </div>
    </div>
  
    <!-- 购买清单和优惠清单 -->
    <div class="orderlist-discount">
      <p>待支付：¥{{nowPrice}} 共{{totalCount}}份</p>
  
      <!-- 调用组件!!!!!!!!!!!!!!!!!!!!!! -->
      <div class="cart-activity">
        <cartActivity :dishList='newDishList' :serviceList='serviceList' :activityList='activityList' :showService='showServiceRow' :totalCount='totalCount' :serviceTotalPrice='serviceTotalPrice'></cartActivity>
      </div>
    </div>
  
    <!-- 备注 -->
    <div class="remark" @click="switchRemark()">
      <div class="remark-title">备注</div>
      <div class="content overflow" v-show="displayRemark==true">{{remarkAllStr}}</div>
      <div class="float-right">
        <img src="../../assets/display.png" class="display" :class="{'convert':displayRemark == true}">
      </div>
    </div>
  
    <div class="remark-detail" v-show="displayRemark == false">
      <div class="split-line">
        <div class="top-line"></div>
      </div>
      <div class="taste-options">
        <ul class="clearfix">
          <li v-for="(item,index) in demandList">
            <button type="button" @click="demand(item,index)" :class="{'current-select':item.flag == true}">{{item.fsaskname}}</button>
          </li>
        </ul>
      </div>
  
      <div class="other-demand">
        <input type="text" placeholder="其他偏好或要求" maxlength="50" v-model="remarkInput">
      </div>
    </div>
  
    <!-- 开具发票 -->
    <div class="invoice" v-if="showInvoice== true || showInvoice== 'true'">
      <div class="invoice-switch">
        <p class="float-left">开具发票</p>
        <div class="float-right" style="padding-top:10px;">
          <mt-switch v-model="showInvoiceWrite"></mt-switch>
        </div>
      </div>
  
      <!-- 填写发票信息 -->
      <div class="invoice-write top-line" v-show="showInvoiceWrite">
        <div class="invoice-type">
          <span style="margin-right:30px;">抬头: </span>
          <div @click="selectTitle()" style="margin-right:30px;">
            <div class="option" :class="{'type-tick': isCom,'type-circle':!isCom}"></div> &nbsp;
            <span>公司发票</span>
          </div>
          <div @click="selectTitle2()">
            <div class="option" :class="{'type-tick': isPerson,'type-circle':!isPerson}"></div> &nbsp;
            <span>个人发票</span>
          </div>
        </div>
  
        <input type="text" placeholder="请输入发票抬头" maxlength="30" v-model="invoiceHeader" v-show="isCom == true">
      </div>
    </div>
  
    <!-- 支付按钮 -->
    <button type="button" class="pay-btn" :class="{'weixinPay': sign=='wx','aliPay':sign=='ap'}" @click="gotoPay()" :disabled="isDisableClick">
      <span style="font-size: 18px;">{{sign =='wx'?'微信':'支付宝'}}支付 </span>
      <span style="font-size: 25px;">¥{{nowPrice}}</span>
    </button>
  
    <!-- 点餐完成后的提示框 -->
    <div class="bg-layer" v-show="showRemindModal==true" style="z-index: 100"></div>
    <div class="remind-modal" v-show="showRemindModal==true">
      <!--<div class="title">支付完成后凭取餐号取餐</div>-->
      <div class="title">支付完成后凭{{eatTarget}}号{{eatTarget}}</div>
      <img src="../../assets/remind_wx.png" class="remind" v-if="sign=='wx'">
      <img src="../../assets/remind_ap.png" class="remind" v-if="sign=='ap'">
      <div class="no-next bottom-line">
        <div @click="cancelTip()">
          <div class="box">
            <img src="../../assets/tick_icon.png" v-show="showTip==false">
          </div>
          <span>下次不再提示</span>
        </div>
      </div>
      <div class="isee" @click="isee()">知道了</div>
    </div>
  
    <!-- 提示售罄 -->
    <!--  <confirm :dialog.sync="showConfirm" title="提示" :content="confirmText" confirmText="继续下单" @confirm="confirm()" cancelText="返回继续点餐"></confirm> -->
  
  </div>
</template>

<script>

import cartActivity from './cartActivity';
import countDownBtn from '@/components/countDownBtn/countDownBtn';
import { dealActivity } from './confirm.js';
import { splitArray } from './hour.js';
import { getSys } from '@/utils/system.js';
import { Number } from "@/utils/money.js";
import {initAddress} from '@/mixin/initAddress.mixin.js';

const SUCCESS = 200;
export default {
  mixins:[initAddress],
  filters: {
    formatDate(date) {
      let date2 = new Date(date);
      let year = date2.getFullYear();
      let month = (date2.getMonth() + 1);
      let day = date2.getDate();
      return month + "月" + day + "日";
    },
    showNormalDate(dateStr) {
      if (dateStr == "today") {
        let d = new Date();
        let year = d.getFullYear();
        let month = (d.getMonth() + 1);
        let day = d.getDate();
        return month + "月" + day + "日";
      } else {
        let d = new Date();
        let year = d.getFullYear();
        let month = (d.getMonth() + 1);
        let day = d.getDate() + 1;
        return month + "月" + day + "日";
      }
    }
  },
  data() {
    return {
      storeId: '',
      storeName: '',
      tableId: '',
      businessTimeList: [],
      eatMode: 0, // 0堂食 1打包
      orderMode: 0, //0 现场点餐 1预先点餐
      showOrderMode: false,
      popupVisible: false,
      showRegisterPhone: false,
      showPhoneModal: false,
      text: '现场点餐',
      remarkInput: "",
      remarkArr: [],
      remarkAllStr: "",
      displayRemark: false,
      telephone: "",
      eatTime: "",
      rightPhone: "",
      verifyCode: "",
      isDisabled: true,
      showLoading: false,
      showRemindModal: false,
      showTip: false,
      showInvoice: false,
      showInvoiceWrite: false,
      isCom: true,
      isPerson: false,
      invoiceHeader: "",
      demandList: [],
      timeArr: [],
      timeList: [],
      newDishList: [],
      activityList: [],
      discountList: [],
      originPrice: 0,
      nowPrice: 0,
      serviceList: [],
      noDiscountTotal: 0,
      serviceTotalPrice: 0,
      currentTime: 0,
      showServiceRow: false,
      canWrite: false,
      totalCount: 0,
      orderType: 1,
      serviceType: 0,
      serviceUnitPrice: 0,
      dueDate: "",
      isDelivery: 1,
      isDisableClick: false,
      eatStyle: 1,
      sign:this.$sign
    }
  },
  created() {
    this.$store.commit('SAVE_TITLE', '订单确认');

    if(this.sign == undefined || this.sign == "" || this.sign == null){
      this.sign = this.$route.query.sign;
    }

    //从sessionStorage获取shop对象
    //获取shopId,shopName,tableId,营业时间段,是否支持发票,点餐方式,送餐方式
    let shopInfo = sessionStorage.shopInfo;
    shopInfo = JSON.parse(shopInfo);

    this.storeId = shopInfo.fsShopGUID;
    this.storeName = shopInfo.fsShopName;
    let tabId = sessionStorage.tableId;
    if (tabId != undefined && tabId != "" && tabId != 'null' && tabId != null) {
      this.tableId = tabId;
    }

    let businessHoursList = shopInfo.businessHoursList;
    console.log("-----------------------营业时间原始时间--------------");
    console.log(businessHoursList);
    console.log("-----------------------营业时间原始时间--------------");

    // let businessHoursList = ["09:00:00-22:59:59"];
    // console.log("-----------------------营业时间测试时间--------------");
    // console.log(businessHoursList);
    // console.log("-----------------------营业时间测试时间--------------");


    this.timeArr = splitArray(businessHoursList);
    console.log("-----------------------营业时间处理过后--------------");
    console.log(this.timeArr)
    console.log("-----------------------营业时间处理过后--------------");

    //是否支持发票
    this.showInvoice = shopInfo.isSupportInvoice;

    //点餐方式
    this.orderType = Number(shopInfo.orderType);

    //送餐方式
    this.isDelivery = shopInfo.isSupportDelivery;

    //从sessionStorage获取菜品数组并进行处理
    this._getDishListAndBuild();
    console.log("-----------------------处理过的菜品集合--------------");
    console.log(this.newDishList);
    console.log("-----------------------处理过的菜品集合--------------");

    //从localStorage获取活动并计算活动
    //newDishList可能已经改变,所以重新取出来
    this._dealActivity();

    //调用服务费和备注的接口,
    this._getServiceChargeAndRemark();

    // 从localStorage取发票抬头
    this._getTitle();

    //获取用户的资料,拿到手机号
    this._getUser();
   
  },
  methods: {
    writePhone() {
      this.telephone = "";
      this.verifyCode = "";
      this.showPhoneModal = true;
    },
    changeEatMode(num) {
      if (this.eatMode != num) {
        this.eatMode = num;
        this.showServiceRow = !this.showServiceRow;
        if (num == 0) {
          this.nowPrice = Number(this.nowPrice) - Number(this.serviceTotalPrice);
          this.originPrice = Number(this.originPrice) - Number(this.serviceTotalPrice);
        } else {
          this.nowPrice = Number(this.nowPrice) + Number(this.serviceTotalPrice);
          this.originPrice = Number(this.originPrice) + Number(this.serviceTotalPrice);
        }
        this.nowPrice = Number(this.nowPrice).toFixed(2);
        this.originPrice = Number(this.originPrice).toFixed(2);
      }
    },
    _dealActivity() { //处理活动
      let shopActivityList = sessionStorage.getItem('shopActivityList');

      //如果没有活动的情况
      if (shopActivityList != "" && shopActivityList != 'null' && shopActivityList != undefined) {
        shopActivityList = JSON.parse(shopActivityList);

        //从缓存里取actFoods,用来做判断
        let actFoods = [];
        if(sessionStorage.actFoods){
          actFoods = sessionStorage.actFoods;
          actFoods = JSON.parse(actFoods);
        }
       

        //活动集合,原总价,已点菜品集合
        this.activityList = dealActivity(shopActivityList, this.noDiscountTotal, this.newDishList,actFoods);
        console.log("----------------------- 最终活动集合--------------");
        console.log(this.activityList);
        console.log("-----------------------最终活动集合--------------");

        let newDishList = sessionStorage.getItem('newDishList');
        this.newDishList = JSON.parse(newDishList);

        //计算原总价和现总价
        this.newDishList.forEach(item => {
          this.originPrice += Number(item.originTotalPrice);
        })

        let sendTotalPrice = Number(sessionStorage.sendTotalPrice);
        console.log("sendTotalPrice取出来:"+sendTotalPrice);
       
        this.originPrice = Number(this.originPrice) + sendTotalPrice;
        console.log("最终的原价总和:"+this.originPrice);
        this.originPrice = Number(this.originPrice).toFixed(2);

        //从localStoage里拿到计算活动后的总价finalPriceNoService
        let finalPriceNoService = sessionStorage.getItem('finalPriceNoService');
        finalPriceNoService = Number(finalPriceNoService);
        this.nowPrice = finalPriceNoService.toFixed(2);

      } else {
        console.log("门店无活动计算现价总和noDiscountTotal之前:"+this.noDiscountTotal);
        this.nowPrice = this.noDiscountTotal;
        this.newDishList.forEach(item => {
          if (item.originTotalPrice) {
            this.originPrice += Number(item.originTotalPrice);
          } else {
            this.originPrice += Number(item.totalPrice);
          }
        })
        console.log("门店无活动计算原价总和originPrice之前:"+this.originPrice);
        this.originPrice = Number(this.originPrice).toFixed(2);
        console.log("门店无活动计算原价总和originPrice:"+this.originPrice);
      }
    },
    _getDishListAndBuild() {
      let cartFoods = sessionStorage.getItem('cartFoods');
      cartFoods = JSON.parse(cartFoods);
      console.log("-----------------------点菜页已点的菜品--------------");
      console.log(cartFoods);
      console.log("-----------------------点菜页已点的菜品--------------");

      //封装成自己需要的数据
      cartFoods.forEach(item => {
        let info = item.info;
        if(item.info.groupAsk.length==0){ //如果没有多种要求做法等
          let dish = {};
          dish.fsitemid = info.fsitemid;
          dish.dishname = info.fsitemname;
          dish.count = item.count; //count在item里面
          dish.unit = info.fsorderunit;

          dish.fdBoxPrice = info.fdBoxPrice;
          dish.fiBoxQty = info.fiBoxQty;
          dish.isGive = 0; //0普通菜

          //加入总份数
          this.totalCount += dish.count;

          //bargainPrice现价,fdsaleprice原价
          dish.originPrice = info.fdsaleprice;
          dish.originTotalPrice = Number(dish.originPrice*dish.count).toFixed(2);

          dish.nowPrice = info.bargainPrice;
          dish.totalPrice = Number(dish.nowPrice*dish.count).toFixed(2);
          dish.sendPrice = info.fdsaleprice;

          this.newDishList.push(dish);

        }else{
          let dish = {};
          dish.fsitemid = info.fsitemid;
          dish.dishname = info.fsitemname;
          dish.count = item.count;
          dish.unit = info.fsorderunit;

          dish.fdBoxPrice = info.fdBoxPrice;
          dish.fiBoxQty = info.fiBoxQty;
          dish.isGive = 0; //0普通菜

          //加入总份数
          this.totalCount += dish.count;

          let doIdList = []; //做法id
          let doNameList = []; //做法名称
          let doPrice = 0;

          info.groupAsk.forEach(doMode => {
            doIdList.push(doMode.fsaskid);
            doNameList.push(doMode.fsaskname);
            doPrice += doMode.fdaddprice;
          });

          dish.doIdList = doIdList;
          dish.doNameList = doNameList;
          dish.doNameStr = doNameList.join("/");

            //bargainPrice现价,fdsaleprice原价
          dish.originPrice = info.fdsaleprice + doPrice;
          dish.originTotalPrice = Number(dish.originPrice*dish.count).toFixed(2);

          dish.nowPrice = info.bargainPrice + doPrice;
          dish.totalPrice = Number(dish.nowPrice*dish.count).toFixed(2);
          dish.sendPrice = info.fdsaleprice;
         
          this.newDishList.push(dish);
        }
        
      });

      this.newDishList.forEach(item => {
        this.noDiscountTotal += Number(item.totalPrice);
      });

      this.noDiscountTotal = Number(this.noDiscountTotal).toFixed(2);

      console.log("现价总和(不包括活动):" + this.noDiscountTotal);
      window.sessionStorage.setItem('newDishList', JSON.stringify(this.newDishList));
    },
    chooseTime() {
      this.popupVisible = true;
    },
    selectTime(timeObj, index) {
      this.currentTime = index;
      this.popupVisible = false;
      this.eatTime = timeObj.time;
      console.log("预约日期:" + timeObj.date);
      console.log("eatTime:" + this.eatTime);

      if (timeObj.date == "today") {
        this.dueDate = new Date();
      } else { //tomorrow
        let d = new Date();
        let nextDate = new Date(d.getFullYear(), d.getMonth(), d.getDate() + 1);
        console.log("预约日期为明天:" + nextDate);
        this.dueDate = nextDate;
      }
    },

    displayMode() {
      if (this.orderType == 2) {
        this.showOrderMode = !this.showOrderMode;
      }
    },
    selectOrderMode(mode) {
      this.orderMode = mode;
      this.showOrderMode = false;
      if (this.orderMode == 0) {
        this.text = "现场点餐";
      } else {
        this.text = "预先点餐";
      }
    },

    clear(obj) {
      if (obj == "phone") {
        this.telephone = "";
      } else {
        this.verifyCode = "";
      }
    },
    getPhone() {
      let flag = this._verifyPhone();
      if (flag) {
        //按钮倒计时
        this.$refs.countDownBtn.start = true;
        this.$emit('send');

        //发送短信
        this._sendMessage();
      }
    },
    bindPhone() {
      let flag = this._verifyPhone();
      if (!flag) {
        return;
      }
      if (!this.verifyCode) {
        this.$toast("请输入验证码");
        return;
      }
      //验证 验证码
      this._commitPhone();
    },
    _verifyPhone() {
      let telephone = this.telephone;
      let flag = true;
      if (telephone) {
        if (!/^1\d{10}$/gi.test(telephone)) {
          this.$toast("手机号不合法");
          flag = false;
        }
      } else {
        this.$toast("请输入手机号");
        flag = false;
      }

      // if (this.rightPhone == telephone) {
      //   flag = false;
      //   this.$toast("手机号已存在");
      // }

      return flag;
    },
    _sendMessage() {
      console.log("发送短信");
      this.$http("get", this.baseUrl + '/phoneCodes', { phone: this.telephone });

    },
    _commitPhone() {
      console.log("验证验证码");
      this.showLoading = true;
      this.$http("post", this.baseUrl + '/checkSms', { phone: this.telephone, validateCode: this.verifyCode }).then((response) => {
        this.showLoading = false;
        if (response.code == SUCCESS) {
          this.showPhoneModal = false;
          //请求后赋值
          this.rightPhone = this.telephone;
        } else {
          this.$toast(response.message);
        }
      });

    },
    cancelTip() {
      this.showTip = !this.showTip;
      //将结果保存在localStorage
      localStorage.setItem("bwy_showTip", this.showTip);
    },
    gotoPay() {
       //看是堂食还是打包,如果是打包,判断是否选择了预约时间和电话
      if (this.orderMode == 1) {
        if (!this.eatTime) {
          this.$toast("请选择预约时间");
          return;
        }

        if (!this.rightPhone) {
          this.$toast("请填写联系方式");
          return;
        }
      }

      //判断是否支持发票
      //看发票开关是否开着
      //如果开着,如果是公司发票,判断是否填写了抬头
      if (this.showInvoice == true || this.showInvoice == 'true') {
        if (this.showInvoiceWrite) {
          //公司纸质
          if (this.isCom) {
            if (!this.invoiceHeader) {
              this.$toast("请填写抬头");
              return;
            }
          }

        }
      }

      //判断就餐还是取餐,根据eatStyle
      if (this.orderMode == 0 && this.eatMode == 0) {
        this.eatStyle = 1;
      }
      if (this.orderMode == 0 && this.eatMode == 1) {
        this.eatStyle = 2;
      }

      if (this.orderMode == 1 && this.eatMode == 0) {
        this.eatStyle = 3;
      }

      if (this.orderMode == 1 && this.eatMode == 1) {
        this.eatStyle = 4;
      }

      //判断是否需要提醒
      let remind = localStorage.getItem('bwy_showTip');
      if (remind == undefined) {
        this.showRemindModal = true;
      }


      if (remind == true || remind == 'true') {
        this.showRemindModal = true;
      }

      if (remind == false || remind == 'false') {
        this._commitForm();
      }

    },
    isee() {
      localStorage.setItem("bwy_showTip", this.showTip);
      this.showRemindModal = false;
      this._commitForm();
    },
    selectTitle() {
      this.isCom = true;
      this.isPerson = false;
    },
    selectTitle2() {
      this.isPerson = true;
      this.isCom = false;
    },
    demand(option, index) {
      this.demandList[index].flag = !this.demandList[index].flag;
      if (option.flag) {
        this.remarkArr.push(option.fsaskname);
      } else {
        this.remarkArr.forEach((item, index) => {
          if (option.fsaskname == item) {
            this.remarkArr.splice(index, 1);
          }
        });
      }
      this.remarkAllStr = this.remarkArr.join(",");
      console.log("选择的备注:" + this.remarkAllStr);
    },
    switchRemark() {
      //将需求输入的字符放入数组
      //判断依据:1.非空,2.不存在目标数组
      if (this.remarkInput) {
        let isExist = false;
        this.remarkArr.forEach((item, index) => {
          if (item == this.remarkInput) {
            isExist = true;
          }
        });
        if (!isExist) {
          this.remarkArr.push(this.remarkInput);
        }
      } else {
        this.remarkArr = [];
        this.demandList.forEach(item => {
          if (item.flag) {
            this.remarkArr.push(item.fsaskname);
          }
        });
      }
      this.remarkAllStr = this.remarkArr.join(",");
      this.displayRemark = !this.displayRemark;
      console.log("折起选择的备注:" + this.remarkAllStr);
    },
    _getServiceChargeAndRemark() {
      this.$http("get", this.baseUrl + '/getOrderExtraInfo', { shopId: this.storeId }).then((response) => {
        if (response.code == SUCCESS) {
          this.demandList = response.model.menuItemAskOutVoList;
          this.demandList.forEach(item => {
            this.$set(item, 'flag', false);
          });
          if (response.model.orderServiceFeeOutVoList) {
            this.serviceList = response.model.orderServiceFeeOutVoList;
            if (this.serviceList.length > 0) {
              this.serviceList.forEach(item => {
                this.serviceType = item.type;

                //1,没有服务费
                if (item.type == 1 || item.type == '1') {
                  this.serviceList = [];
                }

                //2,固定费用
                if (item.type == 2 || item.type == '2') {
                  this.serviceTotalPrice = item.unitPrice;
                  this.serviceUnitPrice = item.unitPrice;
                }

                //3.拿菜品的打包盒单价乘以打包盒数量,再乘以菜品数量
                //最后还要加上赠送菜品的打包盒费用
                if (item.type == 3 || item.type == '3') {
                  // this.serviceTotalPrice = (item.unitPrice * this.totalCount).toFixed(2);
                  // this.serviceTotalPrice = Number(this.serviceTotalPrice);
                  this._getPackagePrice();
                }
              })

            }
          }

        }
      });
    },
    _getTitle() {
      let invoiceHeader = localStorage.getItem('bwy_invoiceHeader');
      if (invoiceHeader) {
        this.invoiceHeader = invoiceHeader;
      }

    },
    _getUser() {
      this.$http("get", this.baseUrl + '/getUserInfo').then((response) => {
        if (response.code == SUCCESS) {
          if (response.model.phone) {
            this.rightPhone = response.model.phone;
          }
        }
      });
    },
    formatToFullDate(date) {
      let year = date.getFullYear();
      let month = (date.getMonth() + 1);
      if (month < 10) {
        month = "0" + month;
      }
      let day = date.getDate();
      if (day < 10) {
        day = "0" + day;
      }
      return year + "-" + month + "-" + day;
    },
    _getPackagePrice(){
      let serviceTotalPriceAll = 0;
      this.newDishList.forEach(dish=>{
        if(dish.fdBoxPrice > 0){
            serviceTotalPriceAll += Number(dish.fdBoxPrice*dish.fiBoxQty*dish.count);
        }
      })

      //加赠送的菜品打包盒总费用
      let sendDishList = sessionStorage.sendDishList;
      if(sendDishList){
          sendDishList = JSON.parse(sendDishList);
        if(sendDishList.length > 0){
            sendDishList.forEach(sendDish=>{
              if(sendDish.fdBoxPrice>0){
                serviceTotalPriceAll += Number(sendDish.fdBoxPrice*sendDish.fiBoxQty*sendDish.count);
              }
            })
        }
      }

      console.log("serviceTotalPriceAll::::::"+serviceTotalPriceAll);

      this.serviceTotalPrice = Number(serviceTotalPriceAll.toFixed(2));
    },

    _gotoPayPage(res){
      let orderId = res.extra.orderId;
      //判断如果现价等于0,直接跳详情页面,否则跳支付
      if (this.nowPrice == 0 || this.nowPrice == "0" || this.nowPrice == "0.00") {
        this.$router.push({ name: 'orderDetail', params: { orderId: orderId } });
      } else {
        if(this.$sign == "wx"){
            let payUrl = res.extra.url;
            console.log("wx-payUrl:"+payUrl);
            window.location.replace(payUrl) ;
        }else{
          console.log("orderIdsssssssssssssssssssssssssssss"+orderId)
            this.$router.replace({name:'pay',params:{orderId:orderId}});
        }
        
      }
    },
    _commitForm() {
      //禁掉支付按钮
      this.isDisableClick = true;

      //查看备注
      let optionArr = [];
      this.demandList.forEach(item => {
        if (item.flag) {
          optionArr.push(item.fsaskname);
        }
      });

      let optionStr = optionArr.join(",");
      if (this.remarkInput) {
        optionStr = optionStr + "!@#" + this.remarkInput;
      }
      console.log("最终的备注选项字符:" + optionStr);

      let orderMenuitemInVoList = [];
      this.newDishList.forEach((item, index) => {
        let obj = {};
        obj.fsitemid = item.fsitemid; //菜品id
        obj.number = item.count; //菜品数量
        obj.unit = item.unit; //单位
        obj.isGive = item.isGive;
        obj.orderMenuItemRequirementInVoList = []; //做法数组
        if(item.doIdList){
          if(item.doIdList.length>0){
            for (let i = 0; i < item.doIdList.length; i++) {
              let doStyleObj = {};
              doStyleObj.fsaskid = item.doIdList[i];
              doStyleObj.fsaskname = item.doNameList[i];
              obj.orderMenuItemRequirementInVoList.push(doStyleObj);
            }
          }
        }

        //放普通菜品
        orderMenuitemInVoList.push(obj);
      });


       //放赠菜菜品
      let sendDishList = sessionStorage.sendDishList;
      if(sendDishList){
          sendDishList = JSON.parse(sendDishList);
        if(sendDishList.length > 0){
            sendDishList.forEach(item=>{
              let obj = {};
                obj.fsitemid = item.fsitemid; //菜品id
                obj.number = item.count; //菜品数量
                obj.unit = item.unit; //单位
                obj.isGive = item.isGive;
                obj.orderMenuItemRequirementInVoList = []; //做法数组
                orderMenuitemInVoList.push(obj);
            })
        }
      }

      console.log("提交订单所有的菜品集合(包括赠送的菜)");
      console.log(sendDishList);
      console.log("提交订单所有的菜品集合(包括赠送的菜)");

      let orderTicketInVo = {};

      if (this.showInvoice == true || this.showInvoice == 'true') {
        if (this.showInvoiceWrite) {
          if (this.isCom) {
            orderTicketInVo.invoiceType = 3;
          }
          if (this.isPerson) {
            orderTicketInVo.invoiceType = 1;
          }
        } else {
          orderTicketInVo.invoiceType = 0;
        }
      } else {
        orderTicketInVo.invoiceType = 0;
      }

      orderTicketInVo.ticketHeader = this.invoiceHeader;

      let fromatTime = "";
      if (this.orderMode == 1) {
        fromatTime = this.formatToFullDate(this.dueDate) + ' ' + this.eatTime + ':00';
      }

      let orderInVo = {};
      orderInVo.eatStyle = this.eatStyle;
      orderInVo.eatTime = fromatTime;
      orderInVo.fsShopGUID = this.storeId;
      if(this.tableId == undefined || this.tableId == 'undefined'){
        orderInVo.fsmtableid = ""
      }else{
          orderInVo.fsmtableid = this.tableId;
      }
     
      orderInVo.orderMenuitemInVoList = orderMenuitemInVoList;
      orderInVo.orderTicketInVo = orderTicketInVo;
      orderInVo.remark = optionStr;

      //手机号
      orderInVo.phone = this.rightPhone;

      //传给后台原价和现价
      orderInVo.paidMoney = this.nowPrice;
      orderInVo.totalprice = this.originPrice;

      console.log("*************orderInVo**************");
      console.log(orderInVo);
      console.log("************orderInVo***************");

      const _this = this;
      this.$http("post", this.baseUrl + '/createOrder', orderInVo, 'json').then((response) => {
        if (response.code == SUCCESS) {
            //清除已点菜品
          sessionStorage.removeItem('sessionfoods');
          //清除估清菜品
          localStorage.removeItem('sellOutList');

          this._gotoPayPage(response);

        }

        if (response.code == 500) {
          if (response.errorCode == 5000058) { //普通菜被估清
            //放开支付按钮
            _this.isDisableClick = false;

            window.sessionStorage.setItem('sellOutList', JSON.stringify(response.extra.item));

            this.$crib.confirm.show({
              title: " 提示",
              headStyle: { fontSize: '18px' },
              content: response.message,
              confirmText: "继续下单",
              cancelText: "返回继续点餐",
              cancelStyle: { color: '#06C1AE' },
              confirmStyle: { color: '#333' },
              onCancel: function () {
                //返回点菜页
                _this.$router.go(-1);
              },
              onConfirm: function () {
                //关闭弹窗,并重新展示菜品列表,和计算价格

                //获取估清的菜品集合
                let emptyDishList = response.extra.item;

                console.log("------------估清的菜品集合-----------");
                console.log(emptyDishList);
                console.log("------------估清的菜品集合-----------");

                let clearList = [];
                _this.newDishList.forEach((item, index) => {
                  emptyDishList.forEach(emptyObj => {
                    if (item.fsitemid == emptyObj.fsItemId && item.unit == emptyObj.fsItemUnit) {
                      console.log("循环到有估清的菜品:"+item.dishname)
                      item.isClear = true;
                    }
                  });
                });

                _this.newDishList.forEach((item, index) => {
                  if(!item.isClear){
                    clearList.push(item);
                  }
                });

                console.log("------------clearList-----------");
                console.log(clearList);
                console.log("------------clearList-----------");

                _this.newDishList = clearList;

                //如果点的菜为空了,回到点菜页
                if(_this.newDishList.length == 0){
                   _this.$router.go(-1);
                }
                console.log("------------处理估清的菜品之后的菜品列表-----------");
                console.log(_this.newDishList);
                console.log("------------处理估清的菜品之后的菜品列表-----------");

                _this.noDiscountTotal = 0;
                _this.totalCount = 0;
                _this.nowPrice = 0;
                _this.originPrice = 0;
                _this.serviceTotalPrice = 0;
                _this.newDishList.forEach(item => {
                  _this.noDiscountTotal += Number(item.totalPrice);
                  _this.totalCount += item.count;
                });

                if (_this.serviceType == 2 || _this.serviceType == '2') {
                  _this.serviceTotalPrice = _this.serviceUnitPrice;
                }

                if (_this.serviceType == 3 || _this.serviceType == '3') {
                  _this._getPackagePrice();
                }

                _this.noDiscountTotal = _this.noDiscountTotal.toFixed(2);

                console.log("------------处理估清的菜品之后的菜品价格总和-----------");
                console.log(_this.noDiscountTotal);
                console.log("------------处理估清的菜品之后的菜品价格总和-----------");

                window.sessionStorage.setItem('newDishList', JSON.stringify(_this.newDishList));

                //重新处理一遍活动
                _this._dealActivity();

              }
            });
          } else if(response.errorCode == 50000512){ //赠菜被估清

             this.$crib.confirm.show({
              title: " 提示",
              headStyle: { fontSize: '18px' },
              content: response.message,
              confirmText: "继续下单",
              cancelText: "返回继续点餐",
              cancelStyle: { color: '#06C1AE' },
              confirmStyle: { color: '#333' },
              onCancel: function () {
                //返回点菜页
                _this.$router.go(-1);
              },
              onConfirm: function () {
               _this._gotoPayPage(response);
              }
            });
            
          }else{
              _this.$toast(response.message);
          }
        }

      });


    }
  },
  computed: {
    eatTarget() {
      console.log("判断就餐还是取餐");
      console.log("eatStyle:"+this.eatStyle);
      if (this.isDelivery == 1 || this.isDelivery == '1') {
        if (this.eatStyle == 1 || this.eatStyle == 3) {
          return "就餐"
        } else {
          return "取餐"
        }
      } else {
        return "取餐"
      }
    },
    showClearPhone() {
      // if(this.telephone.length > 0){
      //   return true;
      // }else{
      //   return false;
      // }

      if (this.telephone.length > 0 && this.telephone.length < 11) {
        return true;
      } else {
        return false;
      }
    },
    showClearVerifyCode() {
      // if(this.verifyCode.length > 0){
      //   return true;
      // }else{
      //   return false;
      // }

      if (this.verifyCode.length > 0 && this.verifyCode.length < 4) {
        return true;
      } else {
        return false;
      }
    },
    completePhone() {
      // if(this.telephone.length == 11){
      //   if(/^1\d{10}$/gi.test(this.telephone)){
      //     this.isDisabled = false;
      //     return "active-btn";
      //   }
      // }else{
      //   return "";
      // }

      if (this.telephone.length > 0) {
        this.isDisabled = false;
        return "active-btn";
      } else {
        return "";
      }


    },
    styleObj() {
      if (getSys() == "android") {
        return { paddingTop: '158px' }
      } else {
        return { paddingTop: '167px' }
      }
    }
  },
  components: {
    cartActivity,
    countDownBtn
  }
}
</script>

<style lang="less">
@import './confirm.less';
</style>
