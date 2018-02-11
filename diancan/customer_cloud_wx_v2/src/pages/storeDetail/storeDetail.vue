<template>
   <div class="store-detail">
      <div class="store-banner" :style="[styleObj]">
        <!-- <img src="../../assets/default_store_banner.png" class="store-banner-img" v-if="!store.fsPromote"> -->
        <!-- <img :src="store.fsPromote" class="store-banner-img" v-if="store.fsPromote"> -->
        <div class="mask"></div>
        <img src="../../assets/share.png" class="share favor" @click="share()">
        <img src="../../assets/collect.png" class="collect favor" v-show="isCollected == false" @click="collect()">
        <img src="../../assets/collected.png" class="collect favor" v-show="isCollected == true" @click="cancelCollect()">
      </div>
      <div class="store-ban">
        <div class="store-logo">
          <img :src="store.fsLogo" v-if="store.fsLogo">
          <img src="../../assets/default_store.png" v-if="!store.fsLogo">
        </div>

        <div class="name-service">
          <p class="store-name">{{store.fsShopName}}</p>
          <ul class="clearfix">
            <li v-if="store.orderType==2"><i></i><span>预点</span></li>
            <li><i></i><span>堂食</span></li>
            <li><i></i><span>打包</span></li>
          </ul>
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

        <div class="big-row bottom-line" @click="searchLocation()">
          <table>
            <tr>
              <td style="width:20px;"><img src="../../assets/address.png" class="location"></td>
              <!-- <td style="width:90%;">浦东新区世纪大道100号上海环球金融中心上海柏悦酒店87楼 </td> -->
              <td style="width:90%;">{{store.fsAddr}}</td>
              <td style="text-align:right;"><img src="../../assets/arrow.png" class="arrow"></td>
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
              <td style="width:20px;"><img src="../../assets/clock.png" class="clock"></td>
              <td style="width:90%;word-break:break-all">{{businessHourStr}}</td>
              <td style="text-align:right;opacity:0"><img src="../../assets/arrow.png" class="arrow"></td>
            </tr>
          </table>
        </div>

      </div>

      <!-- 优惠信息 -->
      <div class="info" v-if="showAct==true">
        <div class="title bottom-line">优惠信息</div>
        <div class="item">
          <activity v-for='(item,index) in store.shopActivityOutVoList' :activity='item' :key='index'></activity>
        </div>
      </div>

      <!-- 商户信息 -->
      <div class="info" v-if="store.fsShopDesc">
        <div class="title bottom-line">商户信息</div>
        <div class="intro">
          <p>{{store.fsShopDesc}}</p>
        </div>
      </div>

      <!-- 弹出层 -->
      <mt-actionsheet :actions="actions" v-model="sheetVisible"></mt-actionsheet>

      <div class="bg-layer" v-show="showShare">
        <div class="share-arrow">
          <img src="../../assets/share_arrow.png">
        </div>

      <!-- 分享的模态框 -->
      <div class="share-modal" v-show="showShare">
        <div class="modal-store-banner" :style="[styleObj]">
          <!-- <img src="../../assets/default_store_banner.png" class="banner" v-if="!store.fsPromote"> -->
          <!-- <img :src="store.fsPromote" class="banner" v-if="store.fsPromote"> -->
          <img src="../../assets/share_shape.png" class="share-shape">
         
          <!-- <img src="../../assets/default_store.png" class="store-logo" v-if="!store.fsLogo"> -->
          <!-- <img :src="store.fsLogo" v-if="store.fsLogo" class="store-logo"> -->
          <div class="store-logo" :style="[logoStyle]"></div>
        </div>

        <div class="store-info">
          <div class="name-bus">
            <p class="name">{{store.fsShopName}}</p>
             <div class="mode">
              <span class="mode-item" v-if="store.orderType==2"><i></i><span>预点</span></span>
              <span class="mode-item"><i></i><span>堂食</span></span>
              <span class="mode-item" style="margin-right: 0;"><i></i><span>打包</span></span>
             </div>
          </div>

             <!-- 分割线 -->
          <div class="split-line"><div class="bottom-line"></div></div>

          <div class="intro" v-if="store.fsShopDesc">{{store.fsShopDesc}}</div>
        </div>

        <div class="recommend">
          <span>点击右上角，将餐厅推荐给朋友</span>
         
        </div>
      </div>

       <div class="share-close" @click="showShare=false">
        <img src="../../assets/share_close.png">
       </div>
    </div>
    
   </div>
</template>

<script>
  import activity from '@/vistitorComponents/sendGoods/activity';
  const SUCCESS = 200;
  export default {
    data () {
      return {
        sheetVisible: false,
        actions: [],
        store:{},
        storeId: '',
        isCollected: false,
        showShare: false,
        businessHourStr:'',
        showAct: false
      }
    },
    created () {
      this.$store.commit('SAVE_TITLE','餐厅详情');
      //获取门店详情数据
      this._getStoreDetail();
    },
    computed : {
      styleObj () {
         if(!this.store.fsPromote) {
            return {backgroundImage: 'url(' + require('../../assets/default_store_banner.png') + ')'}
         }else{
            return {backgroundImage : `url(${this.store.fsPromote})`}
         }
      },
       logoStyle () {
         if(!this.store.fsLogo) {
            return {backgroundImage: 'url(' + require('../../assets/default_store.png') + ')'}
         }else{
            return {backgroundImage : `url(${this.store.fsLogo})`}
         }
      }
    },
    methods : {
      share(){
        console.log("share");
        this.showShare = true;
      },

      initShare(){
        let shareUrl = location.origin + '/#/shareStore';
        console.log("要分享的页面地址:"+shareUrl);
        //调用微信api分享
        this.$http("get",this.baseUrl + '/getShareSign',{notifyUrl: shareUrl}).then((response) => {
          if(response.code==SUCCESS){
            let userId = response.model.userId;
            console.log("userId:"+userId);
            let shareUrlFinal = location.origin + '/#/shareStore?storeId='+this.storeId+'&userId='+userId;
            console.log("shareUrlFinal:"+shareUrlFinal);
           this._wxShare(response.model,this.store,shareUrlFinal);
          }
        });
      },
      collect(){
        console.log("collect");
        //removeShopCollection
        //addShopCollection
        this.$http("post",this.baseUrl + '/addShopCollection',{shopId: this.storeId}).then((response) => {
          if(response.code==SUCCESS){
            this.isCollected = true;
            this.$toast("收藏成功");
          }
        });
      },
      cancelCollect(){
        this.$http("post",this.baseUrl + '/removeShopCollection',{shopId: this.storeId}).then((response) => {
          if(response.code==SUCCESS){
            this.isCollected = false;
            this.$toast("已取消收藏");
          }
        });
      },
      callPhone(){
        this.sheetVisible = true;
      },
      dial(){
        window.location.href="tel:" + this.store.fscellphonect;
      },
      searchLocation(){
        let longitude = this.store.fsLng;
        let latitude = this.store.fsLat;
        this.$router.push({name:'storeLocation',params:{longitude:longitude,latitude:latitude}});
      },
      _getStoreDetail(){
        let storeId = this.$route.params.storeId;
        this.storeId = storeId;
        this.$http("get",this.baseUrl + '/getShopInfo',{shopId: storeId}).then((response) => {
          if(response.code==SUCCESS){
            this.store = response.model;

            if(this.store.shopActivityOutVoList){
              if(this.store.shopActivityOutVoList.length>0){
                this.showAct = true;
              }
            }

            // this.store.fsShopDesc = "吃货吃货吃货吃货吃货吃货吃货吃货吃货吃货吃货吃货";
            this.businessHourStr = response.model.businessHoursList.join(',');
            // this.businessHourStr = '10:00:00-15:00:00,16:00:00-23:00:00,16:00:00-23:00:00'
            console.log("该门店是否被关注:"+response.extra.isCollected);
            this.isCollected = response.extra.isCollected;
            this.actions = [{
              name: `商家电话: ${this.store.fscellphonect}`,
              method: this.dial
            }];
            
            //分享到朋友圈和朋友
            this.initShare();

          }
        });
      },
      _wxShare(wxAuth,store,shareUrlFinal){
        console.log("调用微信分享接口");
        let _this = this;
        let imgUrl = "";
        if(store.fsLogo){
          if(store.fsLogo != null && store.fsLogo != ""){
            imgUrl = store.fsLogo;
          }else{
            imgUrl = "http://cdn.51eparty.com/static/images/default_store.png";
          }
          
        }else{
          imgUrl = "http://cdn.51eparty.com/static/images/default_store.png";
        }
        wx.config({
          debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
          appId: wxAuth.appId, // 必填，公众号的唯一标识
          timestamp: wxAuth.timestamp, // 必填，生成签名的时间戳
          nonceStr: wxAuth.nonceStr, // 必填，生成签名的随机串
          signature: wxAuth.signature,// 必填，签名，见附录1
          jsApiList: ['onMenuShareAppMessage',"onMenuShareTimeline"] // 必填，需要使用的JS接口列表
        });
        wx.ready(function() {
          //分享到朋友圈
          wx.onMenuShareTimeline({
            title: store.fsShopName+'-百味云人气餐厅', // 分享标题
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
            fail : function(res) {
              alert(JSON.stringify(res));
            }
          });

          //分享给朋友
          wx.onMenuShareAppMessage({
            title: store.fsShopName+'-百味云人气餐厅', // 分享标题
            desc: '千帆过尽皆不是，唯有这家合我胃，现在我把她推荐给你',
            link: shareUrlFinal, // 分享链接，该链接域名必须与当前企业的可信域名一致
            imgUrl: imgUrl,
            success: function () {
                // 用户确认分享后执行的回调函数
            },
            cancel: function () {
                // 用户取消分享后执行的回调函数
            },
            fail : function(res) {
              alert(JSON.stringify(res));
            }
          });


        });
      }
    },
    components: {
      activity
    }
  }
</script>

<style lang="less">
  @import './storeDetail.less';
</style>