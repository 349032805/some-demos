<template>
  <div class="assessmentDetail">
    <ul>
      <li v-for="item in list">
        <div>
          <img :src='item.orderMenuitemOutVo.fsimageurl' alt="">
          <div>
            <p class="name">{{item.orderMenuitemOutVo.fsitemname}}</p>
            <p class="count">已售
              <span>{{item.dishSoldOutVo.number}}</span>{{item.orderMenuitemOutVo.unit}}</p>
            <p class="price">
              <span>￥{{price(item.orderMenuitemOutVo.discountPrice,item.orderMenuitemOutVo.price)}}/份</span>
              <span class="cancel_money" v-show='item.orderMenuitemOutVo.isDiscount == 1'>￥{{anotherPrice(item.orderMenuitemOutVo.discountPrice,item.orderMenuitemOutVo.price)}}</span>
            </p>
          </div>
          <div class="love">
            <div v-if="item.likeNumber">
              <img src="../../assets/love.png" alt="">
              <span class="use_count">{{item.likeNumber}}</span>
              <!--<span class="love_text">喜欢</span>-->
            </div>
            <p v-else>暂无</p>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'assessmentDetail',
  created() {
    this.$store.commit('SAVE_TITLE', '菜品评价详情')
    this.orderId = sessionStorage.orderId
    if(sessionStorage.type==1){
       
       this.shopId = sessionStorage.storeId
       this.url = 'getUserDishEvaluateInWeiXin'
    }else{
       this.url = 'getUserDishEvaluate'
    }
    this.$http('get', `${this.baseUrl}/${this.url}`, { orderId: this.orderId,shopId:this.shopId })
      .then(res => {
        if (res.code == 200) {
          this.list = res.model
        }
      })
  },
  data() {
    return {
      orderId : "",
      url : "",
      list: []
    }
  },
  methods : {
    //有优惠价显示优惠价，没有优惠价显示原价
    price(discount,price) {
      if (discount) {
        return discount
      } else {
        return price
      }
    },
    anotherPrice(discount,price) {
      if (discount) {
        return price
      } else {
        return ""
      }
    }
  }

}
</script>

<style lang="less">
@import './assessmentDetail.less';
</style>
