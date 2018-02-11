<template>
  <div class="swiper">
    <mt-swipe :auto="0" :showIndicators='swiperList.length>1?true:false'>
      <mt-swipe-item v-for='(item,index) in swiperList' :key='index'>
        <div class="swiper-item" @click='enterDetailOrder(item.orderId)'>
          <!--<div class="top">
            <div class="top-left">
              <p v-if='type == 1'>
                <span class="store-name">{{item.fsshopname || "沙县大酒店"}}</span>
                <span v-if="item.eatStyle ==3&& item.eatStyle ==4">预点</span>
                </span>
              </p>
              <p v-else-if='type==2' class="order-type">
                <span v-if="item.eatStyle==1">现点 堂吃</span>
                <span v-if="item.eatStyle==2">现点 打包</span>
                <span v-if="item.eatStyle==3">预点 堂吃</span>
                <span v-if="item.eatStyle==4">预点 打包</span>
              </p>
              <p class="order-time">
                <span>{{item.eatStyle ==1 || item.eatStyle ==2 ?"下单时间":"取餐时间"}}</span>
                <span>{{item.eatStyle ==1 || item.eatStyle ==2 ?item.createTimeStr:item.eatTimeStr}}</span>
              </p>-->
          <div class='swiper-box'>
            <div class="top">
              <div class="top-left">
                <p v-if='type == 1'>
                  <span class="store-name">{{item.fsshopname || "沙县大酒店"}}</span>
                  <!--<span class="store-type"><span>预点</span></span>-->
                  <span v-if="item.eatStyle ==3&& item.eatStyle ==4">预点</span>
                  </span>
                </p>
                <p v-else-if='type==2' class="order-type">
                  <span v-if="item.eatStyle==1">现点 堂吃</span>
                  <span v-if="item.eatStyle==2">现点 打包</span>
                  <span v-if="item.eatStyle==3">预点 堂吃</span>
                  <span v-if="item.eatStyle==4">预点 打包</span>
                </p>
                <p class="order-time">
                  <span>{{eatTarget(item.eatStyle,item.isSupportDelivery)}}时间</span>
                  <span>{{item.eatStyle ==1 || item.eatStyle ==2 ?item.createTimeStr:item.eatTimeStr}}</span>
                </p>
              </div>
              <div class="top-right">
                <p class="table-number">{{item.eatNumber }}</p>
                <p class="table-dis">{{eatTarget(item.eatStyle,item.isSupportDelivery)}}号</p>
              </div>
            </div>
            <div class="bottom clearfix">
              <p class="pull-right">查看详情 ></p>
              <p>
                <span>已支付￥{{item.paidMoney}}</span>
                <span>共{{item.itemNumber}}份</span>
              </p>
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
    eatTarget (eatStyle,isSupportDelivery){
        if (isSupportDelivery == 1) {
                if (eatStyle == 1 || eatStyle == 3) {
                    return "就餐"
                } else {
                    return "取餐"
                }
            } else {
                return "取餐"
            }
    },
    enterDetailOrder(order) {
      this.$router.push({ name: 'orderDetail', params: { orderId: order } })
    }
  }
}
</script>

<style lang="less">
@import '../../style/mixin.less';
.swiper {
  width: 100%;

  background: #E7EFEF;
  .swiper-item {
    background: #FFFFFF;
    box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.04);
    .swiper-box{
      padding:.5rem .7rem;
      background:#E6EFEE;
    }
    .top {
      border-top-left-radius: 4px;
      border-top-right-radius: 4px;
      background:#fff;
      overflow: hidden;
      padding: .45rem;
      border-bottom: 1px dashed #D0D0D0;
      .top-left {
        float: left;
        .store-name {
          font-size: .71rem;
          color: #333333;
          display: inline-block;
          max-width: 8rem;
          vertical-align: middle;
          .overflow();
        }
        .store-type {
          padding: .1rem .2rem .05rem .2rem;
          color: #00B3A1;
          margin-left: .1rem;
          font-size: .47rem;
          display: inline-block;
          vertical-align: middle;
          border: 1px solid #00B3A1;
          border-radius: 2px;
          >span {
            line-height: .1rem
          }
        }
        .order-type {
          margin-bottom: 10px;
          font-size: 16px;
          color: #333;
          span {
            padding-right: 15px;
          }
        }
        .order-time {
          margin-top: .5rem;
          color: #999;
          font-size: .5rem;
          span {
            padding-right: .2rem;
          }
          >.code {
            color: #666;
          }
        }
      }
      .top-right {
        float: right;
        text-align: right;
        .table-number {
          margin-bottom: 5px;
          color: #FF4D4D;
          font-size: 1rem;
        }
        .table-dis {
          color: #999;
          font-size: .5rem;
        }
      }
    }
    .bottom {
      background:#fff;
      border-bottom-left-radius: 4px;
      border-bottom-right-radius: 4px;
      padding: .45rem;
      overflow: hidden;
      >p {
        font-size: 12px;
        color: #999;
        span {
          padding-right: 12px;
        }
        &.pull-right {
          color: #666;
        }
      }
    }
  }
}
</style>

