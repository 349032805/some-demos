<template>
  <div class="store-detail">
    <div class="store-banner" :style="[styleObj]">
      <!-- <img src="../../assets/default_store_banner.png" class="store-banner-img" v-if="!store.fsPromote"> -->
      <!-- <img :src="store.fsPromote" class="store-banner-img" v-if="store.fsPromote"> -->
      <div class="mask"></div>
      <img src="../../assets/return_back.png" class="returnback favor" @click="returnBack()">
      <img src="../../assets/share.png" class="share favor" @click="share()">
      <img src="../../assets/collect.png" class="collect favor" v-show="isCollected == false" @click="collect()">
      <img src="../../assets/collected.png" class="collect favor" v-show="isCollected == true" @click="cancelCollect()">
    </div>
    <div class="store-ban">
      <div class="store-logo">
        <img :src="store.fsLogo" v-if="store.fsLogo">
        <img src="../../assets/store-logo.png" v-if="!store.fsLogo">
      </div>
  
      <div class="name-service">
        <p class="store-name overflow">{{store.fsShopName}}</p>
        <ul class="clearfix">
          <li v-if="store.orderType==2">
            <i></i>
            <span>提前点餐</span>
          </li>
          <li>
            <i></i>
            <span>打包</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- 店铺公告 -->
    <div class="info" v-if="store.fsPublicMsg">
      <div class="title bottom-line">店铺公告</div>
      <div class="intro">
        <p>{{store.fsPublicMsg}}</p>
      </div>
    </div>
  
    <!-- 地址,电话,营业时间 -->
    <div class="info-item">
      <div class="row bottom-line" @click="callPhone" v-if="store.fscellphonect">
        <div class="info-icon">
          <img src="../../assets/store_phone.png" class="phone">
        </div>
        <p>{{store.fscellphonect}}</p>
        <div class="float-right">
          <img src="../../assets/arrow.png" class="arrow">
        </div>
      </div>
  
      <div class="big-row bottom-line" @click="searchLocation">
        <table>
          <tr>
            <td style="width:.4rem">
              <img src="../../assets/address.png" class="location">
            </td>
  
            <td style="width:90%;">{{store.fsAddr}}</td>
            <td style="text-align:right"><img src="../../assets/arrow.png" class="arrow"></td>
          </tr>
        </table>
      </div>
  
      <!-- <div class="row">
            <div class="info-icon">
              <img src="../../assets/clock.png" class="clock">
            </div>
            <p>营业时间: <span>{{businessHourStr}}</span></p>
          </div> -->
  
      <div class="big-row">
        <table>
          <tr>
            <td style="width:.4rem">
              <img src="../../assets/clock.png" class="clock">
            </td>
            <td style="width:90%;word-break:break-all">{{businessHourStr}}</td>
            <td style="text-align:right;opacity:0">
              <img src="../../assets/arrow.png" class="arrow">
            </td>
          </tr>
        </table>
      </div>
  
    </div>
  
    <!-- 优惠信息 -->
    <div class="info" v-if="showAct">
      <div class="title bottom-line">优惠信息</div>
      <div class="item">
        <activity :orderActivityOutVoList="shopActivityOutVoList"></activity>
      </div>
    </div>
  
    <!-- 商户信息 -->
    <div class="info" v-if="store.fsShopDesc">
      <div class="title bottom-line">商户信息</div>
      <div class="intro">
        <p>{{store.fsShopDesc}}</p>
      </div>
    </div>
  
    <!-- 拨打电话弹出层 -->
    <mt-actionsheet :actions="actions" v-model="sheetVisible"></mt-actionsheet>

    <!-- 分享弹出框(调用分享组件)-->
    <shareModal :showShare.sync="showShare" :store="store"></shareModal>
  
  </div>
</template>

<script>
import { routerMenus } from '@/mixin/routerMenus.mixin.js';
import { initAddress } from '@/mixin/initAddress.mixin.js';
import activity from './activity';
import { wxShare } from '@/utils/wxShare.js';
import { apShare } from '@/utils/apShare.js';
import shareModal from '@/components/shareModal/shareModal';

const SUCCESS = 200;
export default {
  mixins : [initAddress, routerMenus],
  data() {
    return {
      sheetVisible: false,
      actions: [],
      store: {},
      storeId: '',
      isCollected: false,
      showShare: false,
      businessHourStr: '',
      showAct: false,
      userId: '',
      shopActivityOutVoList: []
    }
  },
  created() {

    this.$store.commit('SAVE_TITLE', '餐厅详情');
    //获取门店详情数据
    this._getStoreDetail();
  },
  computed: {
    styleObj() {
      if (!this.store.fsPromote) {
        return { backgroundImage: 'url(' + require('../../assets/default_store_banner.png') + ')' }
      } else {
        return { backgroundImage: `url(${this.store.fsPromote})` }
      }
    },
    // logoStyle() {
    //   if (!this.store.fsLogo) {
    //     return { backgroundImage: 'url(' + require('../../assets/default_store.png') + ')' }
    //   } else {
    //     return { backgroundImage: `url(${this.store.fsLogo})` }
    //   }
    // }
  },
  methods: {
    share() {
      console.log("share");
      if (this.$sign == "wx") {
        this.showShare = true;
      } else {
        apShare(this.storeId, this.store, this.userId);
      }

    },
    collect() {
      console.log("collect");
      this.$http("post", this.baseUrl + '/addShopCollection', { shopId: this.storeId }).then((response) => {
        if (response.code == SUCCESS) {
          this.isCollected = true;
          this.$toast("收藏成功");
        }
      });
    },
    cancelCollect() {
      this.$http("post", this.baseUrl + '/removeShopCollection', { shopId: this.storeId }).then((response) => {
        if (response.code == SUCCESS) {
          this.isCollected = false;
          this.$toast("已取消收藏");
        }
      });
    },
    callPhone() {
      this.sheetVisible = true;
    },
    dial() {
      window.location.href = "tel:" + this.store.fscellphonect;
    },
    searchLocation() {
      let longitude = this.store.fsLng;
      let latitude = this.store.fsLat;
      this.$router.push({ name: 'storeLocation', params: { longitude: longitude, latitude: latitude } });
    },
    _getStoreDetail() {
      let storeId = this.$route.params.shopId;
      this.storeId = storeId;

      //从缓存sessionStorage里拿门店详情
      let shopInfo = JSON.parse(sessionStorage.shopInfo);
      this.store = shopInfo.model;

      console.log(this.store);
      // this.store.fsLogo = "";

        //如果是支付宝,可以从extra里拿userId参数,然后给分享接口调用
        if (this.$sign == "ap") {
          this.userId = shopInfo.extra.userId;
        }

        if (this.store.shopActivityOutVoList) {
          if (this.store.shopActivityOutVoList.length > 0) {
            this.showAct = true;
            this.shopActivityOutVoList = this.store.shopActivityOutVoList;
          }
        }


        // this.store.fsShopDesc = "吃货吃货吃货吃货吃货吃货吃货吃货吃货吃货吃货吃货";
        this.businessHourStr = this.store.businessHoursList.join(',');
        // this.businessHourStr = '10:00:00-15:00:00,16:00:00-23:00:00,16:00:00-23:00:00'
        console.log("该门店是否被关注:" + shopInfo.extra.isCollected);
        this.isCollected = shopInfo.extra.isCollected;
        this.actions = [{
          name: `商家电话: ${this.store.fscellphonect}`,
          method: this.dial
        }];

        //如果是微信,调用分享到朋友圈和朋友
        if (this.$sign == "wx") {
          wxShare(this.baseUrl, this.storeId, this.store, this._succCallback);
        }

    },
    _succCallback() {
      this.showShare = false;
    },
    returnBack(){
       this.$router.go(-1);
    }
  },
  components: {
    activity,
    shareModal
  }
}
</script>

<style lang="less">
@import './storeDetail.less';
</style>