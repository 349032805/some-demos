<template>
  <div class="storeDoor" :class='[info.isRest == 0?"isRest":""]'  @click='enterMenus'>
      <div>
          <img v-if='info.fsLogo' v-lazy='info.fsLogo' alt="">
          <img v-else src="../../assets/nodish-small.png" alt="">
          <div class="store-box">
              <p class="storeName">{{info.fsShopName}}</p>
              <p class="storeDetail"><span class="storeType">{{info.fsCuisineSeries}}</span><span class="storeDistance">{{info.distance}}km</span></p>
          </div>
      </div>
       <p class="order" v-if='info.orderType == 2'>预点</p>
  </div>
</template>

<script>
  export default {
      name : "storeDoor",
      props : {
          info : Object
      },
      methods : {
          enterMenus () {
              var _this = this
              if(this.info.isRest == 0){
                  this.$crib.alert.show({
                      title : "提示",
                      content : "商家休息中...",
                      onSubmit (){
                          _this.$router.push({name:'menus',params: {shopId:_this.info.fsShopGUID}})
                      }
                  })
              }else{
                  sessionStorage.fromIndex = 'true'
                  this.$router.push({name:'menus',params: {shopId:this.info.fsShopGUID}})
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
</style>
