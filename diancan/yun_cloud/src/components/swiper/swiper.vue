<template>
  <div class="swiper">
    <mt-swipe :auto="2000" :showIndicators='false'>
      <mt-swipe-item v-for='(item,index) in swiperList' :key='index'>
        <div class="swiper-item" @click='enterDetailOrder(item.orderId)'>
          <div class='swiper-box'>
            <!-- <div class="left-bor" v-if="index!=0"></div> -->
            <!-- <div class="right-bor" v-if="swiperList.length>1&&index!=swiperList.length-1"></div> -->
            <div class="swiper-inner-box">
              <div class="top clearfix">
                  <p class="table-number pull-right">桌号B2012<span>&nbsp;&nbsp;共{{item.itemNumber}}份</span></p>
                  <span class="table-name">就餐号({{item.fsshopname}})</span>
              </div>
              <div class="bottom clearfix">
                  <p class="pull-right">查看详情 ></p>
                  <span class="table-number">{{item.eatNumber }}&nbsp;</span>
                  <span class="span-block" v-if="item.eatStyle==3||item.eatStyle==4">提前点餐</span>
                  <span class="span-block">打包</span>
              </div>
            </div>
          </div>
        </div>
      </mt-swipe-item>
    </mt-swipe>
  </div>
</template>
<script>

export default {
  props: {
    type: Number,
    swiperList: Array
  },
  methods: {
    enterDetailOrder(order) {
      this.$router.push({ path: `/orderDetail/${order}`, query:{sign:this.$sign}})
    }
  }
}
</script>

<style lang="less">
@import '../../style/mixin.less';
@import '../../style/color.less';
.swiper {
  width: 100%;
  background: #E7EFEF;
  .swiper-item {
    background: #FFFFFF;
    box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.04);
    .swiper-box{
      position:relative;
      padding:.25rem .3rem;
      background:#f5f5f5;
    }
    .left-bor,
    .right-bor{
      position: absolute;
      top:.2rem;
      bottom:.2rem;
      width:.2rem;
      height: 100%;
    }
    .left-bor{
      left:0;
      background:url("../../assets/right-bor.png") no-repeat;
      background-size:100% 81%;
    }
    .right-bor{
      right:0;
      background:url("../../assets/left-bor.png") no-repeat;
      background-size:100% 81%;
    }
    .swiper-inner-box{
        padding: .28rem .3rem;
        background-image: linear-gradient(-90deg, #FE4E4E 0%, #FE714E 100%);
        box-shadow: 0 2px 8px 0 rgba(149,149,149,0.42);
        border-radius:.08rem;
        color:#fff;
    }
    .top {
        font-size:.26rem;
        opacity:.8;
        margin-bottom:.15rem;
    }
    .bottom{
      .table-number{
        font-size:.56rem;
        vertical-align: middle;
      }
      .span-block{
          display:inline-block;
          padding:0 .1rem;
          margin-right:.02rem;
          background-color:#fff;
          color:@colred;
          font-size:.22rem;
          border-radius:.15rem;
          vertical-align: middle;
      }
      .pull-right{
        margin-top:.12rem;
        opacity: 0.8;
        font-size:.24rem;
      }
    }
  }
}
</style>

