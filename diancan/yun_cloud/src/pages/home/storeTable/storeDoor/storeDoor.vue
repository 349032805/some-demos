<template>
  <div class="store-door" :class='[info.isRest == 0?"isRest":""]'  @click='enterMenus'>
        <div class="clearfix">
          <img v-if='info.fsLogo' v-lazy='info.fsLogo' alt="">
          <img v-else src="../../../../assets/nodish-small.png" alt="">
          <div class="store-box">
              <p class="storeName">{{info.fsShopName}}
                    <span v-if="info.orderType == 2" class="earlyOrder">提前点餐</span>
              </p>
              <p class="storeDetail">
                  <span class="pull-right">{{info.fsAreaname}}</span>
                  <span class="storeType" v-if="info.fsCuisineSeries">{{info.fsCuisineSeries}}</span>
                  <span class="storeDistance" v-if="info.distance">{{distance}}</span></p>

            <activity v-if ="info.shopActivityOutVoList" class ='activity' :actlist = "info.shopActivityOutVoList"></activity>
          </div>
          
        </div>
          
  </div>
</template>

<script>
  import Activity from './activity/activity.vue'
  export default {
      name : "storeDoor",
      props : {
          info : Object
      },
      computed:{

        distance(){
          return Number(this.info.distance)>=1000 ? (Number(this.info.distance)/1000).toFixed(1) +"km" : (this.info.distance).toFixed(1)+"m"

        }

      },
      components:{

        Activity


      },
      methods : {
          enterMenus () {
              var _this = this
              if(this.info.isRest == 0 || this.info.isRest == 2){
                  this.$crib.alert.show({
                      title : "提示",
                      content : "商家休息中...",
                      onSubmit (){
                          _this.$router.push({path:`/menus/${_this.info.fsShopGUID}/${localStorage.token}`,query:{sign:_this.$sign}})
                      }
                  })
              }else{
                  sessionStorage.fromIndex = 'true'
                  this.$router.push({path:`/menus/${_this.info.fsShopGUID}/${localStorage.token}`,query:{sign:_this.$sign}})
              }
          }
      }
  }
</script>

<style lang="less">
  @import './storeDoor.less';
  .isRest {
opacity: 0.5;
background: #EFEFF4;
  }
  .earlyOrder{
font-family: PingFangSC-Regular;
font-size: 0.18rem;
color: #F1503F;
line-height: 0.22rem;
border: 0.01rem solid #F1503F;
border-radius: 0.02rem;
float: right;
  }

.activity{



}
</style>
