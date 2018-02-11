<template>
   <div class="menuHeader clearfix">
       <div class="clearfix" style="margin-bottom:5px">
        <div class="right-box pull-right">
            <div class="peronEnter clearfix" @click="topersonCenter()">
                    <img  class='person' src="../../assets/centerIcon.png" alt="">
                    <span>个人中心</span>
                    <img class='arrow' src="../../assets/rArrow.png" alt="">
            </div>
        </div>
        <div class="left-box" @click="enterStoreDetial()">
            <div class="left-box-top clearfix">
             <div>
                <img v-if="shopInfo.fsLogo" :src="shopInfo.fsLogo">
                <img v-else src="../../assets/store-logo.png">
             </div>
            <div>
                <p class="store-name">{{shopInfo.fsShopName}}</p>
                <ul class="type">
                    <li v-if="shopInfo.orderType==1">堂食 打包</li>
                    <li v-if="shopInfo.orderType==2">预点 堂食 打包</li>
                </ul>
            </div>
            </div>
        </div>
      </div>
      <div class="activity clearfix">
          <div class="dropDown pull-right" @click="showMore=!showMore" v-if="shopActivityCount>0">
            <span>{{shopActivityCount}}个活动</span>
            <img v-if="shopActivityCount>1" :style="{transform:showMore==true?'rotate(180deg)':''}" src="../../assets/updown.png">
          </div>
          <ul class="left-box-bottom" v-if="shopActivityCount>0&&!showMore">
              <li><span class="box" :class="colorFilter(shopInfo.shopActivityOutVoList[0].logoNameCode)">{{shopInfo.shopActivityOutVoList[0].logoName}}</span><span class="name">{{shopInfo.shopActivityOutVoList[0].activityName}}</span></li>
          </ul>
          <ul class="left-box-bottom" v-show="shopActivityCount>0&&showMore">
              <li v-for="(item,index) in shopInfo.shopActivityOutVoList" :key="index"><span class="box" :class="colorFilter(item.logoNameCode)">{{item.logoName}}</span><span class="name">{{item.activityName}}</span></li>
          </ul>
      </div>
   </div>
</template>
<script>
  export default {
      name : "menuHeader",
      props : {
          shopInfo : Object,
          shopActivityCount : Number
      },
      data(){
          return {
              showMore : false
          }
      },
      methods : {
        colorFilter(status){
            let _color = '';
            if(status == '1'){
                _color = 'green';
            }
            if(status == '2' || status == '3' || status == '4'){
                _color  = 'orange';
            }
            
            if(status == '5' || status == '6'){
                _color = 'red'
            }
            return _color;
        },
        topersonCenter(){
            this.$router.push('/personCenter');
        },
        enterStoreDetial(){
            this.$router.push('/storeDetail/'+localStorage.shopId)
        }
      }
  }
</script>

<style lang="less">
  @import './menuHeader.less';
</style>
