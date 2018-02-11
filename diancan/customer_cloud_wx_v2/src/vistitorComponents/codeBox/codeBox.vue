<template>
    <div>
        <div class="detailTop">
            <div class="inner2">
                <div>
                    <p class="orderStatus">订单状态</p>
                    <p class="text">{{status}}</p>
                </div>
                <div>
                    <p class="eatOrder">您的就餐号</p>
                    <p class="textB">{{list.eatNumber}}</p>
                </div>
                <div>
                    <p class="orderType">就餐方式</p>
                    <p class="text">{{eatType}}</p>
                </div>
            </div>
            <p class="confirm">请牢记就餐号，凭号{{eatStyleModel}}。</p>
            <div class="order-info-code">
                <div class="left">
                    <img class='two-code' src="../../assets/twocode.jpg" alt="">
                </div>
                <div class="right">
                    <p class="name">百味云公众号</p>
                    <p class="info">
                        <span>关注后可收到订单通知，</span>
                        <span>可随时随地点餐</span>
                        <span>更可获取红包和优惠信息</span>
                    </p>
                </div>
            </div>
        </div>
        <z-button :styles='btnStyle'>长按二维码关注我们</z-button>
        <img class='close' src="../../assets/share_close.png" alt="" @click='$parent.$parent.dialogShow = false'>
    </div>
</template>

<script>
import { ZButton } from '@/crib-zk'
export default {
    props: {

        list: Object
    },
    data() {
        return {
            btnStyle: {
                background: "#fff",
                marginTop: '12px',
                color: '#00B3A1',
                fontSize: '.7rem',
                borderRadius: '8px'
            }
        }
    },
    computed: {
        eatStyleModel() {
            //服务员送餐
            if (this.list.isSupportDelivery == 1) {
                if (this.list.eatStyle == 3) {
                    return '就餐'
                } else {
                    return '取餐'
                }
            } else {
                return '取餐'
            }
        },
        status() {
            let status = ""
            switch (this.list.status) {
                case 7:
                    status = "已取消"
                    break;
                case 6:
                    status = "已取消"
                    break;
                case 5:
                    status = "已取消 "
                    break;
                case 4:
                    status = "已支付"
                    break;
            }
            return status
        },
        eatType() {
            let eatType = ""
            switch (this.list.eatStyle) {
                case 1:
                    eatType = "现点堂吃";
                    break;
                case 2:
                    eatType = "现点打包 "
                    break;
                case 3:
                    eatType = "预点堂吃";
                    break;
                case 4:
                    eatType = "预点打包";
                    break;
            }
            return eatType
        }
    },
    components: {
        ZButton
    }
}
</script>


<style lang='less' scoped>
.close {
    margin-top: 1rem;
    width: 1.1rem;
    height: 1.1rem;
}

.detailTop {
    border-radius: 8px;
    padding: .5rem;
    background: #fff;

    .order-info-code {
        margin-top: .5rem;
        overflow: hidden;
        background: #fff;
        .left {
            float: left;
            margin-left: .4rem;
            .two-code {
                width: 4.1rem;
                height: 4.1rem;
            }
            >p {
                font-size: .3rem;
            }
        }
        .right {
            width: 55%;
            float: right;
            margin-right: .4rem;
            .name {
                font-size: .69rem;
                color: #333333;
                text-align: left;
                margin-top: .15rem;
            }
            .info {
                >span {
                    display: block;
                }
                line-height: .7rem;
                font-size: .5rem;
                color: #999999;
                text-align: left;
                margin-top: .25rem
            }
        }
    }
    .confirm {
        font-size: .5rem;
        color: #BEA064;
        position: relative;
        &:before {
            content: "";
            display: block;
            width: 2rem;
            height: 1px;

            position: absolute;
            background-color: #BEA064;
            top: .35rem;
            left: .8rem;
            z-index: 100;
        }
        &:after {
            content: "";
            display: block;
            width: 2rem;
            height: 1px;
            position: absolute;
            background-color: #BEA064;
            top: .35rem;
            right: .9rem;
            z-index: 100;
        }
    }
    .inner2 {

        padding-top: .5rem;
        overflow: hidden;
        padding-bottom: .5rem;
        >div {
            float: left;
            width: 33.33%;
            text-align: center;
            .orderStatus,
            .orderType,
            .eatOrder {
                width: 80%;
                margin: 0 auto;
                font-size: .53rem;
                padding: .2rem .2rem;
                color: #646464;
                background: #F0F0F0;
                border-radius: 100px;
            }
            .text {
                font-size: .69rem;
                margin-top: .65rem;
                color: #333333;
            }
            .textB {
                font-size: 1.3rem;
                font-weight: bold;
                color: #FF4D4D;
                margin-top: .43rem;
            }
        }
    }
}
</style>


