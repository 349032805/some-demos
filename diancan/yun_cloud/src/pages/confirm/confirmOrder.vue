<template>
  <div class="order-confirm">

    <!-- 取餐提醒 -->
    <div class="remind" v-show="showTip">
      <div class="tip">
        <span>支付后点击【完成】，凭就餐号取餐。</span>
        <span class="never-remind" @click="neverRemind">不再提示</span>
      </div>
      <div class="close-tip" @click="closeTip">
        <img src="../../assets/close_tip.png">
      </div>
    </div>

    <!-- 餐厅名字,桌号返回加菜按钮 -->
    <div class="return bottom-line">
      <div class="store-logo">
        <img src="../../assets/store-small-logo.png" v-if="!shopInfo.fsLogo">
        <img :src="shopInfo.fsLogo" v-if="shopInfo.fsLogo">
      </div>
      <span class="store-name overflow">{{storeName}}</span>
      <span class="float-left">桌号 {{tableId}}</span>
      <button type="button" class="return-btn" @click="returnAdd()">返回加菜</button>
    </div>

    <!-- 引入组件 -->
    <div class="menu">
      <div style="padding:0 .3rem">
        <alreadyDishItem :orderMenuitemOutVoList="displayDishList"></alreadyDishItem>
      </div>
      <div style="padding:0 .3rem">
        <alreadyDishActivity :orderActivityOutVoList="displayActList" v-if="displayActList.length>0"></alreadyDishActivity>
      </div>
      <alreadyDishPrice :list="priceObject"></alreadyDishPrice>
    </div>

    <!-- 是否打包 -->
    <div class="package">
      <div class="float-left">打包</div>
      <div class="float-right package-switch">
        <span v-show="needPackage">¥{{serviceTotalPrice}}</span>
        <img src="../../assets/circle.png" v-show="!needPackage" @click="changePackage">
        <img src="../../assets/success_icon.png" v-show="needPackage" @click="changePackage">
      </div>
    </div>

    <!-- 是否预点 -->
    <div class="prepare-order">
      <preOrder :openPrepare.sync="openPrepare" :selectTimeList="selectTimeList" :arriveTime.sync="arriveTime" :telephone.sync="telephone"></preOrder>
    </div>

    <!-- 备注 -->
    <div class="remark">
      <remark :demandList="demandList" :selectDemandList.sync="selectDemandList" :remarkInput.sync="remarkInput"></remark>
    </div>

    <!-- 开具发票-->
    <div class="invoice" v-if="showInvoice">
      <invoice :openInvoice.sync="openInvoice" :isCom.sync="isCom" :isPerson.sync="isPerson" :invoiceHeader.sync="invoiceHeader" :code.sync="code"></invoice>
    </div>

    <!-- 支付区域 -->
    <div class="pay-block">
      <span>合计 &nbsp;</span>
      <span class="pay-money">¥{{finalPrice}}</span>
      <button type="button" class="pay-btn" :class="{'forbidClick':isStop==true}" @click="gotoPay()" :disabled="isDisableClick">确认付款</button>
    </div>

  </div>
</template>

<script>
import { mapState } from 'vuex'
import { initAddress } from '@/mixin/initAddress.mixin.js';
import { dealActivity } from './dealActivity.js';
import { Number } from "@/utils/money.js";
import preOrder from './preOrder';
import remark from './remark';
import invoice from './invoice';
import alreadyDishActivity from '@/components/alreadyDishActivity/alreadyDishActivity';
import alreadyDishItem from '@/components/alreadyDishItem/alreadyDishItem';
import alreadyDishPrice from '@/components/alreadyDishPrice/alreadyDishPrice';
import { initData } from './initData.js';
import { getExtraInfo } from './extraInfo.js';
import { commitForm } from './commitForm.js';
import { getPackagePrice } from './countPackagePrice';
import { verifyForm } from './verifyForm';

const SUCCESS = 200;
export default {
  mixins: [initAddress],
  data() {
    return {
      showTip: true,
      shopId: '',
      storeName: '',
      tableId: '',
      shopInfo: {},
      businessTimeList: [],
      eatMode: 0, // 0堂食 1打包
      orderMode: 0, //0 现场点餐 1预先点餐
      eatTime: "",

      showInvoice: false,
      openInvoice: false,
      isCom: true,
      isPerson: false,
      invoiceHeader: "",
      code: '',

      demandList: [],
      remarkInput: "",
      selectDemandList: [],

      displayDishList: [],
      newDishList: [],
      finalActList: [],
      displayActList: [],

      hasDiscount: 0,
      nowPrice: 0,
      finalPrice: 0,
      priceObject: {
        paidDiscountMoney: 0,
        paidMoney: 0
      },

      indexOriginpPrice: 0,
      indexNowPrice: 0,
      serviceTotalPrice: 0,

      totalCount: 0,
      orderType: 1,
      serviceType: 0,
      serviceUnitPrice: 0,
      isDelivery: 1,
      isDisableClick: false,
      eatStyle: 1,
      sign: this.$sign,
      needPackage: false,
      openPrepare: false,
      arriveTime: '',
      telephone: '',
      selectTimeList: [],
      isStop: false
    }
  },
  created() {
    console.log(this.MONEY_TWO)
    this.$store.commit('SAVE_TITLE', '订单确认');

    if (this.sign == undefined || this.sign == "" || this.sign == null) {
      this.sign = this.$route.query.sign;
    }

    //从localStorage里获取是否需要提示
    let remind = localStorage.bwy_remind;
    if (remind != undefined) {
      this.showTip = false;
    }

    //从sessionStorage获取shop对象
    //获取shopId,shopName,tableId,营业时间段,是否支持发票,点餐方式,送餐方式
    let shopInfoAll = sessionStorage.shopInfo;
    shopInfoAll = JSON.parse(shopInfoAll);

    let shopInfo = shopInfoAll.model;
    this.shopInfo = shopInfo;

    this.shopId = shopInfo.fsShopGUID;
    this.storeName = shopInfo.fsShopName;
    let tabId = sessionStorage.tableId;
    if (tabId != undefined && tabId != "" && tabId != 'null' && tabId != null) {
      this.tableId = tabId;
    }

    if (tabId == undefined || tabId == 'undefined' || tabId == 'null') {
      this.tableId = "";
    }

    let businessHoursList = shopInfo.businessHoursList;
    console.log("-----------------------营业时间原始时间--------------");
    console.log(businessHoursList);
    console.log("-----------------------营业时间原始时间--------------");

    // let businessHoursList = ["09:00:00-22:59:59"];
    // console.log("-----------------------营业时间测试时间--------------");
    // console.log(businessHoursList);
    // console.log("-----------------------营业时间测试时间--------------");

    //是否支持发票
    this.showInvoice = shopInfo.isSupportInvoice;

    //点餐方式
    this.orderType = Number(shopInfo.orderType);

    //送餐方式
    this.isDelivery = shopInfo.isSupportDelivery;

    //从sessionStorage获取菜品数组并进行处理
    this._initData();

    //从sessionStorage里拿首页的现价和原价
    let obj = sessionStorage.fanjun;
    obj = JSON.parse(obj);
    this.indexNowPrice = Number(obj.should);
    if (obj.factPay) {
      this.indexOriginpPrice = Number(obj.factPay);
    } else {
      this.indexOriginpPrice = Number(obj.should);
    }

    console.log("this.indexNowPrice:" + this.indexNowPrice)
    console.log("this.indexOriginpPrice:" + this.indexOriginpPrice)

    //从localStorage获取活动并计算活动
    this._dealActivity();

    //从sessionStorage里查看是否之前已有表单数据
    let formData = sessionStorage.formData;
    if (formData) {
      this._setDataByFormData();
    } else {

      //调用服务费和备注的接口,
      this._getServiceChargeAndRemark();
      // 从localStorage取发票抬头
      this._getTitle();

      //获取用户的资料,拿到手机号
      this._getUser();
    }

    // this.selectTimeList = ['13:15', '13:30', '13:45'];

  },
  methods: {
    neverRemind() {
      this.showTip = false;
      localStorage.setItem('bwy_remind', false);
    },
    closeTip() {
      this.showTip = false;
    },
    returnAdd() {
      let shopId = this.shopId;
      this.$router.push({ path: `/menus/${shopId}/${localStorage.token}`, query: { sign: this.sign, tableId: this.tableId } })
    },
    changePackage() {
      this.needPackage = !this.needPackage;
      if (this.needPackage) {
        this.eatMode = 1; //1打包
        this.finalPrice = Number((this.finalPrice + this.serviceTotalPrice).toFixed(2));
      } else {
        this.eatMode = 0; //0堂吃
        this.finalPrice = Number((this.finalPrice - this.serviceTotalPrice).toFixed(2));
      }
    },
    _dealActivity() { //处理活动
      let shopActivityList = sessionStorage.getItem('shopActivityList');

      //如果有活动的情况
      if (shopActivityList != "" && shopActivityList != 'null' && shopActivityList != undefined) {
        shopActivityList = JSON.parse(shopActivityList);

        //从缓存里取actFoods,用来做判断
        let actFoods = [];
        if (sessionStorage.actFoods) {
          actFoods = sessionStorage.actFoods;
          actFoods = JSON.parse(actFoods);
        }

        //活动集合,原总价,已点菜品集合
        let box = dealActivity(shopActivityList, this.indexNowPrice);
        console.log("----------------------- 最终活动集合--------------");
        console.log(box.finalActList);
        console.log("-----------------------最终活动集合--------------");
        this.nowPrice = box.finalPriceNoService;
        this.finalPrice = box.finalPriceNoService;
        this.priceObject.paidMoney = this.finalPrice;

        //计算已优惠: 打造爆款优惠+新人礼优惠+满减满折优惠
        let hasDiscount = 0;
        if (this.indexOriginpPrice != 0) {
          let discount = this.indexOriginpPrice - this.indexNowPrice;
          hasDiscount += discount;
        }

        box.finalActList.forEach(item => {
          let obj = {};
          obj.logoName = item.word;
          obj.fsBargainName = item.name;
          obj.money = item.price;
          if (this.finalPrice == 0) {
            hasDiscount = this.indexOriginpPrice;
          } else {
            hasDiscount += item.price;
          }

          this.displayActList.push(obj);
        })

        hasDiscount = Number(hasDiscount.toFixed(2));
        this.hasDiscount = hasDiscount;
        this.priceObject.paidDiscountMoney = hasDiscount;

      } else {
        //没有活动直接取首页的金额
        this.originPrice = this.indexOriginpPrice;
        this.nowPrice = this.indexNowPrice;
        this.finalPrice = this.indexNowPrice;
      }
    },
    _initData() {
      let box = initData();
      this.displayDishList = box.displayDishList;
      console.log("---------------------displayDishList------------------------")
      console.log(this.displayDishList)
      console.log("---------------------displayDishList------------------------")
      this.newDishList = box.newDishList;
    },
    gotoPay() {
      console.log("-------------------data:");
      console.log(this.openInvoice);
      console.log(this.isCom);
      console.log(this.isPerson);
      console.log(this.invoiceHeader);
      console.log(this.code);
      console.log("-------------------data:");

      let paramObj = {
        eatMode: this.eatMode,
        openPrepare: this.openPrepare,
        arriveTime: this.arriveTime,
        telephone: this.telephone,
        showInvoice: this.showInvoice,
        openInvoice: this.openInvoice,
        isCom: this.isCom,
        invoiceHeader: this.invoiceHeader,
        code: this.code
      }
      let box = verifyForm(paramObj, this);
      this.orderMode = box.orderMode;
      this.eatStyle = box.eatStyle;
      if (box.flag) {
        this._commitForm();
      }

    },
    async _getServiceChargeAndRemark() {
      let box = await getExtraInfo(this.baseUrl, this.shopId);
      console.log("--------------------------boxbox----------------")
      console.log(box)
      console.log("--------------------------boxbox----------------")

      this.demandList = box.demandList;
      this.serviceType = box.serviceType;
      this.serviceTotalPrice = box.serviceTotalPrice;
      this.serviceUnitPrice = box.serviceUnitPrice;
      this.selectTimeList = box.selectTimeList;
    },
    _getTitle() {
      let invoiceHeader = localStorage.getItem('bwy_invoiceHeader');
      let code = localStorage.getItem('bwy_code');
      if (invoiceHeader) {
        this.invoiceHeader = invoiceHeader;
      }

      if (code) {
        this.code = code;
      }
    },
    _getUser() {
      this.$http("get", this.baseUrl + '/getUserInfo').then((response) => {
        if (response.code == SUCCESS) {
          if (response.model.phone) {
            let telephone = localStorage.getItem('bwy_telephone');
            if (telephone) {
              this.telephone = telephone;
            } else {
              this.telephone = response.model.phone;
            }

          }
        }
      });
    },

    async _commitForm() {
      //禁掉支付按钮
      this.isDisableClick = true;

      let originPrice = 0;

      if (this.needPackage) {
        originPrice = this.indexOriginpPrice + this.serviceTotalPrice;
      } else {
        originPrice = this.indexOriginpPrice;
      }

      originPrice = Number(originPrice.toFixed(2));

      let paramsObj = {
        demandList: this.demandList,
        remarkInput: this.remarkInput,
        showInvoice: this.showInvoice,
        openInvoice: this.openInvoice,
        isCom: this.isCom,
        isPerson: this.isPerson,
        invoiceHeader: this.invoiceHeader,
        code: this.code,
        orderMode: this.orderMode,
        arriveTime: this.arriveTime,
        eatStyle: this.eatStyle,
        shopId: this.shopId,
        tableId: this.tableId,
        telephone: this.telephone,
        originPrice: originPrice,
        finalPrice: this.finalPrice
      }

      //将表单数据保存到sessionStorage
      this.saveLocalData();

      let box = await commitForm(paramsObj, this.sign, this);
      this.isDisableClick = box.isDisableClick;
      this.isStop = box.isStop;

      let isReset = box.isReset;
      if (isReset) {
        this.nowPrice = 0;
        this.originPrice = 0;
        this.serviceTotalPrice = 0;

        //重置服务费
        if (this.serviceType == 2 || this.serviceType == "2") {
          this.serviceTotalPrice = this.serviceUnitPrice;
        }

        if (this.serviceType == 3 || this.serviceType == "3") {
          this.serviceTotalPrice = getPackagePrice();
        }

        //重置活动相关计算
        this._dealActivity();

      }

    },
    saveLocalData() { //sessionStorage缓存表单数据
      let formData = {};
      formData.needPackage = this.needPackage;
      formData.openPrepare = this.openPrepare;
      formData.selectTimeList = this.selectTimeList;
      formData.arriveTime = this.arriveTime;
      formData.telephone = this.telephone;
      formData.demandList = this.demandList;
      formData.remarkInput = this.remarkInput;
      formData.showInvoice = this.showInvoice;
      formData.openInvoice = this.openInvoice;
      formData.isCom = this.isCom;
      formData.isPerson = this.isPerson;
      formData.invoiceHeader = this.invoiceHeader;
      formData.code = this.code;

      formData.serviceTotalPrice = this.serviceTotalPrice;
      formData.serviceUnitPrice = this.serviceUnitPrice;

      sessionStorage.setItem("formData", JSON.stringify(formData))
    },
    _setDataByFormData() {
      let formData = sessionStorage.formData;
      formData = JSON.parse(formData);
      this.needPackage = formData.needPackage;
      this.openPrepare = formData.openPrepare;
      this.selectTimeList = formData.selectTimeList;
      this.arriveTime = formData.arriveTime;
      this.telephone = formData.telephone;
      this.demandList = formData.demandList;
      this.remarkInput = formData.remarkInput;
      this.showInvoice = formData.showInvoice;
      this.openInvoice = formData.openInvoice;
      this.isCom = formData.isCom;
      this.isPerson = formData.isPerson;
      this.invoiceHeader = formData.invoiceHeader;
      this.code = formData.code;

      this.serviceTotalPrice = formData.serviceTotalPrice;
      this.serviceUnitPrice = formData.serviceUnitPrice;
    }

  },
  // computed: {
  //   eatTarget() {
  //     console.log("判断就餐还是取餐");
  //     console.log("eatStyle:" + this.eatStyle);
  //     if (this.isDelivery == 1 || this.isDelivery == '1') {
  //       if (this.eatStyle == 1 || this.eatStyle == 3) {
  //         return "就餐"
  //       } else {
  //         return "取餐"
  //       }
  //     } else {
  //       return "取餐"
  //     }
  //   }

  // },
  components: {
    preOrder,
    remark,
    invoice,
    alreadyDishActivity,
    alreadyDishItem,
    alreadyDishPrice
  }
}
</script>

<style lang="less">
@import './confirm.less';
</style>
