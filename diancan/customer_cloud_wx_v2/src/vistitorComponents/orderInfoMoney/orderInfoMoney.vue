<template>
    <div class="order-info_money">
        <p class="small">
            <span>原价 :</span>
            <span>￥{{list.totalprice}}</span>
        </p>
        <p class="small">
            <span>优惠 :</span>
            <span>￥{{list.paidDiscountMoney}}</span>
        </p>
        <p class="curpay">
            <span>实付 :</span>
            <span style='vertical-align: middle;'>￥{{list.paidMoney}}</span>
        </p>
    </div>
</template>


<script>

export default {
    props: {
        list: Object
    }
}
</script>



<style lang='less' scoped>
.order-info_money {
    >p {
        display: inline-block;
    }
    .small {
        color: #999999;
        font-size: .59rem;
    }
    .curpay {
        color: #FF4D4D;
        font-size: .68rem;
    }
}
</style>
