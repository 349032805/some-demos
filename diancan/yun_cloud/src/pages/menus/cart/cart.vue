<template>
    <div class="cart-box">
        <div class="cart">
            <div class="cart-num-box" @click="isShowlist">
                <span class='img-box'>
                    <img src='../../../assets/cart.png'>
                    <span class="num" v-if="totalCount>0">{{totalCount}}</span>
                </span>
                <span class='box-price'>
                    <p v-show="totalCount<=0" class="no-dishes">还没添加菜品</p>
                    <p class="price" v-show="totalCount>0">¥{{firstPrice}}
                        <span class="favorable-price" v-show='priceFlag'>￥{{secondPrice}}</span>
                    </p>
                </span>
            </div>
            
            <button type="button" :class="{'btn-primary':true,'btn-grey':totalCount<=0 || !CART_BTN_COLOR}" @click='confirm'>
                <spinner v-show='spinnerFlag' class="spinner"></spinner>
                <span>选好了</span>
            </button>
        </div>
        <div class="cart-list">
            <activity-tip></activity-tip>
            <list-show :totalCount='totalCount' :showList='showList'></list-show>
        </div>
        <div class="backdrop" v-show="showList" @click="showList = !showList"></div>
    </div>
</template>
<script>

import { mapState } from 'vuex'
import { finallConcat } from '../model/finallConcat.js'
import BScroll from 'better-scroll'
import { cutMoneyTip } from '../model/cutMoneyTip.js'
import { concat } from '../model/concat.js'
import { Number } from '@/utils/money.js'
import { calculate } from '../model/activity.js'
import { saveCache } from '../model/cache.js'
import { deleteCache } from '../model/cache.js'
import { getMenusItemChingList } from '@/config/api.js'
import ActivityTip from './activityTip/activityTip.vue'
import {Spinner} from '@/crib-zk'
import ListShow from './listShow/listShow.vue'
export default {
    props: {
        shopId: {
            type: String,
            require: true
        }
    },
    data() {
        return {
            spinnerFlag : false,
            totalCount: 0,
            showList: false,
            firstPrice: 0,
            secondPrice: 0,
            priceFlag: false,
            concatArray: []
        }
    },
    computed: {
        ...mapState({
            GOODS_INFO: state => state.foodAttrModel.GOODS_INFO,
            ACTIVITY_LIST: state => state.menusModel.ACTIVITY_LIST,
            MENUS_LIST: state => state.menusModel.MENUS_LIST,
            GOODS_INFO_MONEY: state => state.foodAttrModel.GOODS_INFO_MONEY,
            COUNT_SIGN: state => state.menusModel.COUNT_SIGN,
            CART_BTN_COLOR: state => state.titleModel.CART_BTN_COLOR,
            COUNT_FLAG: state => state.foodAttrModel.COUNT_FLAG
        })
    },
    //高算总数
    watch: {
        GOODS_INFO: {
            handler: function (val, oldval) {
                if (!this.COUNT_SIGN) {
                    return
                }
                let totalCount = 0;
                let fdsaleprice = 0   //原价
                let bargainPrice = 0  //打折价
                for (let i of val) {
                    fdsaleprice += (i.info.fdsaleprice + i.info.groupAskPriceTotal) * i.count
                    bargainPrice += (i.info.bargainPrice + i.info.groupAskPriceTotal) * i.count
                }


                let concatArray = this.concatArray = concat(this.GOODS_INFO)
                if (this.ACTIVITY_LIST) {
                    let activityBuygiftItemListResponse = this.ACTIVITY_LIST.activityBuygiftItemListResponse//有没有爆款活动

                    if (activityBuygiftItemListResponse) {
                        if (activityBuygiftItemListResponse[0].bargainKind == 302) {   //满折 现价打折，原价不动
                            bargainPrice = calculate(this.ACTIVITY_LIST, concatArray, this.GOODS_INFO, bargainPrice, 1, this)
                            fdsaleprice = Number(fdsaleprice).toFixed(2)

                        } else if (activityBuygiftItemListResponse[0].bargainKind == 301) {    //满送 现价不动，原价加钱
                            fdsaleprice = calculate(this.ACTIVITY_LIST, concatArray, this.GOODS_INFO, fdsaleprice, 1, this)
                            bargainPrice = Number(bargainPrice).toFixed(2)

                        }
                    }
                }
                for (let i of val) {
                    totalCount += i.count;
                    totalCount += i.sendGoods ? i.sendGoods.count : 0;
                }
                this.totalCount = totalCount
                if (totalCount == 0) {
                    this.showList = false
                }
                saveCache(this.GOODS_INFO, this.MENUS_LIST, this.shopId)
                // -----------------------------
                //所有原价计算
                //如果打折价小于原价，说明有优惠
                //console.log('原价'+fdsaleprice+'折价'+bargainPrice)
                if (Number(bargainPrice) < Number(fdsaleprice)) {
                    this.firstPrice = Number(bargainPrice).toFixed(2)
                    this.secondPrice = Number(fdsaleprice).toFixed(2)
                    this.priceFlag = true
                    sessionStorage.fanjun = JSON.stringify({ should: this.firstPrice, factPay: this.secondPrice })
                    this.$store.commit('SAVE_MONEY', this.firstPrice)
                } else {

                    this.firstPrice = Number(fdsaleprice).toFixed(2)
                    this.priceFlag = false
                    sessionStorage.fanjun = JSON.stringify({ should: this.firstPrice, factPay: null })
                    this.$store.commit('SAVE_MONEY', this.firstPrice)
                }

            },
            deep: true
        }

    },
    methods: {
        isShowlist() {
            if (this.totalCount > 0) {
                this.showList = !this.showList
            }
        },
        confirm() {
            if (this.totalCount == 0 || !this.CART_BTN_COLOR) {
                return
            }
            this.spinnerFlag = true
            let shopInfo = JSON.parse(sessionStorage.shopInfo)
            let shopId = shopInfo.model.fsShopGUID
            getMenusItemChingList(this.baseUrl, shopId, this.concatArray).then(res => {
                if (res.code == 500) {
                    if (res.errorCode == '50004001') {
                        let _this = this
                        this.$crib.alert.show({
                            title: '提示',
                            content: res.message,
                            onSubmit() {
                                _this.$store.commit('SAVE_NO_OPEN', true)
                                _this.$store.commit('CHANGE_CART_BTN_COLOR', false)
                            }
                        })
                    }
                    return
                }
                if (res.code == 200) {
                    if (res.model.length > 0) {
                        let _this = this
                        let alertString = ''
                        res.model.forEach(item => {
                            let alertStringItem = `${item.fsItemName}(${item.fsorderunit});`
                            alertString += alertStringItem
                        })
                        alertString += '已售罄'
                        this.$crib.confirm.show({
                            title: '提示',
                            content: alertString,
                            cancelText: '重新选择',
                            confirmText: '继续下单',
                            onCancel() {
                                _this.$store.commit('SALED_CLEAR_GOODS_INFO', model)
                                _this.$store.commit('REDUCE_MENUS_LIST_COUNT', _this.COUNT_FLAG)
                                _this.$store.commit('CHANGE_COUNT_FLAG', true)
                            },
                            onConfirm() {
                                _this.$store.commit('SALED_CLEAR_GOODS_INFO', model)
                                _this.$store.commit('REDUCE_MENUS_LIST_COUNT', _this.COUNT_FLAG)
                                _this.$store.commit('CHANGE_COUNT_FLAG', true)
                                let cartFoods = finallConcat(_this.GOODS_INFO, _this.GOODS_INFO_MONEY)
                                sessionStorage.cartFoods = JSON.stringify(cartFoods)
                                sessionStorage.actFoods = JSON.stringify(_this.concatArray)
                                _this.$router.push({ name: 'confirmOrder' })

                            }
                        })
                    } else if (this.concatArray == res.model.length) {
                        this.$crib.alert.show({
                            title: '提示',
                            content: "您选的菜已经全部售罄!",
                            buttonText: '重新点菜',
                            onSubmit() {
                                deleteCache();
                                location.reload();
                            }
                        })
                    } else {
                        let cartFoods = finallConcat(this.GOODS_INFO, this.GOODS_INFO_MONEY)
                        sessionStorage.cartFoods = JSON.stringify(cartFoods)
                        sessionStorage.actFoods = JSON.stringify(this.concatArray)
                        this.$router.push({ name: 'confirmOrder' })
                    }
                }
                this.spinnerFlag = false
            })
        }
    },
    components: {
        ListShow,
        ActivityTip,
        Spinner
    }
}
</script>
<style lang="less">
@import './cart.less';
</style>
