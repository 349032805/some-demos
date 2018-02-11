<template>
   <div class="share-store">
      <div class="friend-talk">
        <div class="friend-head">
           <img src="../../assets/head_small.png" v-if="!store.headImgUrl">
           <img :src="store.headImgUrl" v-if="store.headImgUrl">
        </div>
        <div class="friend-word">
          <p>您的好友推荐 {{store.fsShopName}} 给你</p>
          <p>咱们吃这个吧，感觉还不错～</p>
        </div>
      </div>

       <!-- 正文内容 -->
       <div class="share-content clearfix">
        <div class="store-logo">
          <img :src="store.fsLogo" v-if="store.fsLogo">
          <img src="../../assets/default_store.png" v-if="!store.fsLogo">
          <!-- <img src="../../assets/default_store.png"> -->
        </div>

        <!-- 店名,预点 -->
       <div class="name-bus">
          <p class="name">{{store.fsShopName}}</p>
           <div class="mode">
              <span class="mode-item" v-if="store.orderType==2"><i></i><span>预点</span></span>
              <span class="mode-item"><i></i><span>堂食</span></span>
              <span class="mode-item" style="margin-right: 0;"><i></i><span>打包</span></span>
           </div>
        </div>

        <!-- 优惠消息 -->
        <div class="discount-item top-line" v-if="activityList.length>0">
          <div class="row" v-for="(item,index) in activityList" v-show="index==0 || item.flag==true">
            <div class="box" :class="whichColor(item.logoName)">{{item.logoName}}</div>
            <p class="discount-message overflow">{{item.activityName}}</p>
            <div @click="displayActivity()" v-if="index==0"><span>{{activityList.length}}个活动</span><span class="triangle" :class="{'display':display == true}" v-if="activityNum>1"></span></div>
          </div>
         <!--  <div class="row" v-show="display==true">
            <div class="box">赠</div>
            <p class="discount-message overflow">满150随机赠送饮料（周一、三、五可享）</p>
          </div> -->
          
        </div>
      </div>

      <!-- 中间分割 -->
      <img src="../../assets/share_circle.png" style="display:block;margin-top:-1px;">

        <!-- 热销菜品 -->
        <div class="hot-dish clearfix">
          <div class="theme">热销菜品</div>
          <div class="dishes">
            <div class="dish-box" v-for="(item,index) in dishList" :class="{'float-left':index % 2 == 0,'float-right':index % 2 == 1}">
              <img src="../../assets/nodish.png" v-if="!item.imageUrl">
              <img :src="item.imageUrl" v-if="item.imageUrl">
              <p class="dish-name overflow">{{item.name}}</p>
              <p class="dish-sell">已售{{item.soldNum}}{{item.unit}}</p>
              <p class="dish-price">¥{{item.price}} /{{item.unit}}</p>
            </div>


          </div>
        </div>

       <button type="button" class="go-btn" @click="goStore()">进店看看</button>
   </div>
</template>

<script>
  const SUCCESS = 200;
  export default {
    data () {
      return {
        display: false,
        store:{},
        activityList:[],
        dishList:[],
        activityNum:0
      }
    },
    created () {
      this.$store.commit('SAVE_TITLE','百味云点餐系统');
      let storeId = this.$route.query.storeId;
      let userId = this.$route.query.userId;
      this.$http("get",this.baseUrl + '/getShopShareOUtVo',{userId:userId,shopId:storeId}).then((response) => {
        if(response.code==SUCCESS){
           this.store = response.model;
           if(response.model.shopActivityOutVoList){
              this.activityNum = response.model.shopActivityOutVoList.length;
              if(response.model.shopActivityOutVoList.length>0){
                response.model.shopActivityOutVoList.forEach((item,index)=>{
                  if(index >0){
                    this.$set(item,'flag',false);
                  }
                  this.activityList.push(item);
                })
              }
           }
           console.log(this.activityList)
           if(response.model.hotMenuItemList){
            if(response.model.hotMenuItemList.length >0){
              response.model.hotMenuItemList.forEach(item=>{
                let dish = {};
                dish.name = item.fsitemname;
                dish.imageUrl = item.fsimageurl;
                dish.soldNum = item.soldNum;

                if(item.menuItemUnitList.length ==1){
                  dish.price = item.menuItemUnitList[0].fdsaleprice;
                }

                if(item.menuItemUnitList.length >1){
                  let priceArr = [];
                   item.menuItemUnitList.forEach(obj=>{
                    priceArr.push(obj.fdsaleprice);
                   })
                  let minVal = Math.max.apply(null, priceArr);
                  console.log('*******************最小值:'+minVal);
                  dish.price = minVal + '起';
                }

                dish.unit = item.menuItemUnitList[0].fsorderunit;

                this.dishList.push(dish);

              });
            }
           }

        }
      });
    },
    methods : {
      goStore(){
        // let storeId = this.$route.params.storeId;
        let storeId = this.$route.query.storeId;

        // this.$http("get",this.baseUrl + '/grantShareShop',{shopId: storeId}).then((response) => {
        //   console.log("后台微信授权");
        // });
        window.location.href=this.baseUrl+"/grantShareShop?shopId="+storeId;
        
      },
      displayActivity(){
        this.display = !this.display;
        this.activityList.forEach((item,index)=>{
          if(index>0){
            item.flag = !item.flag;
          }
        })
      },
      whichColor(word){
        if(word == '新'){
          return 'green'
        }

        if(word == '减' || word == '折' || word == '赠'){
          return 'orange'
        }

        if(word == '送' || word == '惠'){
          return 'red'
        }
        
      }
    }
  }
</script>

<style lang="less">
  @import './shareStore.less';
</style>
