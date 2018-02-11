<template>
  <ul class="orderGoods">
    <li class="orderGoods-item" v-for='item in orderMenuitemOutVoList'>
      <p class="goods">
        <span class="goodsName">{{item.fsitemname }}</span>
        <span class="goodsCount">x{{item.number}}</span>
        <span class='discount' v-if='item.isDiscount == "1"'>￥{{item.discountPrice}}</span>
        <span class="goodsMoney">￥{{item.price*item.number | addZero}}</span>
      </p>
      <p class='type'>({{item.unit}}
        <span v-if='item.orderMenuItemRequirementOutVoList'>
          <span v-for='item2 in item.orderMenuItemRequirementOutVoList'>/{{item2.fsaskname}}</span>
        </span>)</p>
      <div v-if='item.isGive == 1'>
        <p class="sendGoods" v-for='item2 in item.orderGiveMenuitemOutVoList'>
          <span class="type">送</span>
          <span class="sendName overflow">{{item2.fsitemname}}</span>
          <span class="sendCount">x{{item2.number}}</span>
          <span style='width:28%;text-align:center;text-decoration: line-through;'>￥{{item.number*item.price | addZero}}</span>
          <span style='width:6.9%;text-align:right;'>￥0</span>
        </p>
      </div>
    </li>
  </ul>
</template>
c
<script>
export default {
  props: {
    orderMenuitemOutVoList: Array
  },
  filters : {
     addZero (value) {
        value = value.toFixed(2)
        return value
     }
  }
}
</script>

<style lang="less" scoped>
@import './orderGoods.less';
</style>
