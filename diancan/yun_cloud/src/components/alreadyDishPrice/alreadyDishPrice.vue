<template>
    <div class="order-price-box">
        <span class="favorable">已优惠：¥{{list.paidDiscountMoney}}</span>
        合计 <span class="sumprice">¥{{list.paidMoney}}</span>
    </div>
</template>

<script>
export default {
    props:{
        list:Object
    }
}
</script>

<style lang="less">
   @import '../../style/color.less';
   .order-price-box{
        padding: .22rem .3rem;
        background-color: @colfff;
        border-top: .01rem solid @cole5;
        text-align: right;
        .favorable{
            color: @col999;
            padding-right:.3rem;
        }
        .sumprice{
            color:@colred;
            font-size:.42rem;
            vertical-align: middle;
        }
    }
</style>
