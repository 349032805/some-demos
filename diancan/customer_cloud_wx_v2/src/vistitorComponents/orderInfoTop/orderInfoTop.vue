<template>
    <div>
        <div class="detailTop">
            <div class="inner1">
                <span style='font-size:.68rem'>订单详情</span>
                <div v-if='type!=1'>
                    <img src="../../assets/detailperson.png"  @click='enterPersonCenter'>
                    <img src="../../assets/zhuanfa.png" alt="" @click='showModel'>
                </div>
            </div>
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
        </div>
        <div class="tipOrder">
            <span>{{eatStyle1}}</span>
            <span v-if='list.eatStyle == 3 || list.eatStyle == 4'>{{eatStyle2}}</span>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        type: null,
        list: Object
    },
    methods: {
        enterPersonCenter() {
            this.$router.push({ name: "personCenter" })
        },
        showModel() {
            this.$store.commit("CHANGE_SHARE_MODEL", true)
        }
    }, //1现点堂食 2现点打包  3.预点堂食  4、预点打包
    computed: {
        eatStyle1() {
            if (this.list.isSupportDelivery == 1) {
                if (this.list.eatStyle == 1 || this.list.eatStyle == 3) {
                    return "请牢记就餐号，凭号就餐"
                } else {
                    return "请牢记就餐号，凭号取餐"
                }
            } else {
                return "请牢记就餐号，凭号取餐"
            }
        },
        eatStyle2() {
            if (this.list.isSupportDelivery == 1) {
                if (this.list.eatStyle == 3) {
                    return "请在预约时间到门店就餐"
                }else if(this.list.eatStyle == 4) {
                    return "请在预约时间到门店取餐"
                }
            }else{
                if(this.list.eatStyle == 3){
                    return "请在预约时间到门店取餐"
                }else if(this.list.eatStyle == 4) {
                    return "请在预约时间到门店取餐"
                }
            }
        },
        status() {
            let status = ""
            switch (this.list.status) {
                case 6:
                    status = "已取消"
                    break;
                case 5:
                    status = "已取消 "
                    break;
                case 4:
                    status = "已支付"
                    break;
                case 7:
                    status = "已取消"
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
    }
}
</script>

<style lang='less' scoped>
.detailTop {
    background: #FFFFFF;
    padding: .5rem;
    .inner1 {
        padding-bottom: .5rem;
        box-shadow: inset 0 -1px 0 0 #EBECF1;
        >div {
            float: right;
            >span {
                color: #333333;
                font-size: .66rem;
            }
            img {
                &:nth-of-type(1) {
                    width: .7rem;
                }
                &:nth-of-type(2) {
                    width: .92rem;
                    margin-left: .58rem;
                    margin-right: .4rem;
                }
            }
        }
    }
    .inner2 {
        padding-top: .5rem;
        overflow: hidden; //padding-bottom: .2rem;
        >div {
            float: left;
            width: 33.33%;
            text-align: center;
            .orderStatus,
            .orderType,
            .eatOrder {
                width: 70%;
                margin: 0 auto;
                font-size: .53rem;
                padding: .2rem .5rem;
                color: #646464;
                background: #F0F0F0;
                border-radius: 100px;
            }
            .eatOrder {
                width: 80%;
            }
            .text {
                font-size: .68rem;
                margin-top: .7rem;
                color: #333333;
            }
            .textB {
                font-size: 1.13rem;
                color: #FF4D4D;
                font-weight: bold;
                margin-top: .5rem;
            }
        }
    }
}

.tipOrder {
    padding: .23rem 0 .23rem 6.4%;
    background: #FFF7D6;
    color: #BEA064;
    font-size: .5rem;
    >span {
        display: block;
        position: relative;
        &:before {
            content: '';
            display: block;
            position: absolute;
            top: .3rem;
            left: -.6rem;
            width: 3px;
            height: 3px;
            border-radius: 50%;
            background: #BEA064;
        }
    }
}
</style>
