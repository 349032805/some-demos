<template>
    <div class="order-info-code">
        <div class="left">
            <img class='two-code' src="../../assets/twocode.jpg" alt="">
            <p>长按关注我们</p>
        </div>
        <div class="right">
            <p class="name">百味云公众号</p>
            <p class="info">关注后可收到订单通知，可随时随地点餐；更可获取红包和优惠信息</p>
        </div>
    </div>
</template>

<style lang='less' scoped>
.order-info-code {
    margin-top:.5rem;
    overflow: hidden;
    padding: 8px 17px;
    background:#fff;
    .left {
        width: 3.1rem;
        float: left;
        .two-code {
            width: 3rem;
        }
        >p {
            font-size: .5rem;
        }
    }
    .right {
        width: 60%;
        float: right;
        .name {
            font-size: .73rem;
            color: #00B3A1;
            text-align: right;
            margin-top: .2rem;
        }
        .info {
            font-size: .5rem;
            color: #666666;
            text-align: right;
            margin-top:.4rem
        }
    }
}
</style>

