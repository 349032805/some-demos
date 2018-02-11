<template>
    <div class="prepare-order-unit">
        <div class="theme bottom-line">
            <p class="float-left">提前点餐，稍后再吃</p>
            <div class="float-right" style="padding-top:.12rem">
                <!-- <mt-switch v-model="openPrepareResult"></mt-switch> -->
                 <mt-switch v-model="openPrepareResult" v-if="selectTimeList.length>0"></mt-switch> 
                 <img src="../../assets/switch_close.png" class="switch-close" v-if="selectTimeList.length==0" @click="tipNo"> 
            </div>
        </div>

        <div class="time-phone" v-show="openPrepareResult">

            <div class="arrive-time">
                <p class="item-title">到店时间</p>
                <ul>
                    <li v-for="(item,index) in selectTimeList" @click="selectTime(item,index)" :class="{'active':currentIndex==index}" :key="item.index">{{item}}</li>
                </ul>
            </div>

            <div class="phone-write">
                <p class="item-title">取餐电话</p>
                <div class="phone-input">
                    <input type="tel" placeholder="请输入取餐人联系电话" maxlength="11" v-model="telephoneResult" class="text-input">
                </div>
            </div>

            <div class="agree">
                <!-- <div class="agree-box"  @click="agree=!agree"> -->
                <div class="agree-box" @click="optAgree">
                    <img src="../../assets/agree_deal.png" v-show="agree">
                </div>
                <span class="agree-deal">同意百味云
                    <span style="color: #336CAE" @click="gotoAgreement">《用户协议》</span>
                </span>
            </div>

        </div>

    </div>
</template>

<script>
export default {
    name: 'prepareOrder',
    props: {
        openPrepare: Boolean,
        selectTimeList: Array,
        arriveTime: String,
        telephone: String
    },
    data() {
        return {
            agree: true,
            openPrepareResult: this.openPrepare,
            currentIndex: 0,
            telephoneResult: this.telephone
        }
    },
    created() {
        if (this.arriveTime) {
            this.selectTimeList.forEach((item, index) => {
                if (this.arriveTime == item) {
                    this.currentIndex = index;
                }
            })
        }
    },
    watch: {
        openPrepareResult(val) {
            this.$emit('update:openPrepare', val)
        },
        telephoneResult(val) {
            this.$emit('update:telephone', val)
        }
    },
    methods: {
        gotoAgreement() {
            this.$router.push({ name: 'userAgreement' });
            // /menus/:shopId/:token/confirmOrder/userAgreement',
            // let shopId = this.$route.params.shopId;
            // this.$router.push({ path: `/menus/${shopId}/${localStorage.token}/confirmOrder/userAgreement` })

        },
        selectTime(time, index) {
            this.currentIndex = index;
            this.$emit('update:arriveTime', time)
        },
        optAgree() {
            this.agree = !this.agree;
            if (!this.agree) {
                this.openPrepareResult = false;
                this.agree = true;
            }
        },
        tipNo(){
            this.$toast("餐厅即将休息，不提供提前点餐服务！");
        }
    }
}
</script>

<style lang="less">
@import '../../style/mixin.less';
.prepare-order-unit {
    background: #fff;
    .theme {
        height: .88rem;
        line-height: .88rem;
        padding: 0 .3rem;
        font-size: .32rem;

        .switch-close {
            width: 1.07rem;
            height: .64rem;
        }
    }

    .item-title {
        width: 20%;
        color: #666;
    }

    .time-phone {
        padding: 0 .3rem;
        font-size: .28rem;
        .arrive-time {
            height: .88rem;
            line-height: .88rem;

            * {
                display: inline-block;
            }
            ul {
                float: right;
                width: 80%;
                li {
                    width: 1.64rem;
                    height: .64rem;
                    line-height: .64rem;
                    text-align: center;
                    background: #FAFAFA;
                    color: #333;
                    border-radius: 4px;
                    border: 1px solid #DCDCDC;
                    margin-left: .14rem;
                }

                .active {
                    background: rgba(255, 94, 79, 0.10);
                    border: 1px solid #FF5E4F;
                    color: #FF5E4F;
                }
            }
        }

        .phone-write {
            height: .88rem;
            line-height: .88rem;
            * {
                display: inline-block;
            }
            .phone-input {
                float: right;
                width: 78%;
            }
        }

        .agree {
            font-size: .24rem;
            color: #888;
            padding: 3px 0 5px;

            .agree-box {
                width: .24rem;
                height: .24rem;
                border: 1px solid #D0D0D0;
                border-radius: .04rem;
                margin-right: 5px;
                position: relative;
                top: 2px;
            }
            .agree-deal {
                color: #150000;
            }

            * {
                display: inline-block;
            }
        }
    }
}
</style>
