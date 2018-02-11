<template>
    <div class="main-content">
            <div class="goods-info">
                <span class="name">
                    <img v-if='discount' src='../../../../assets/Hzhe.png'></img>
                    <span>{{item.info.fsitemname}}</span>
                </span>
                <span class="attr-name">
                    <p>
                        {{item.info.fsorderunit}}
                        <span class='group' v-for="(item,askIndex) in item.info.groupAsk" :key="askIndex">
                            /{{item.fsaskname}}
                        </span>
                    </p>
                    <span class='sendGoods' v-if='item.sendGoods'>
                        <img src="../../../../assets/Hzeng.png" alt="">
                        <span>{{item.sendGoods.dishName}}({{item.sendGoods.count}}{{item.sendGoods.orderUnit}})</span>
                    </span>
                </span>
            </div>
            <div class="price">
                <span>￥{{firstPrice}}</span>
            </div>
            <div class="cartcontrol-wrapper">
                <control-num :count='item.count' :fsorderunit='item.info.fsorderunit' :fsitemid='item.info.fsitemid' :menuIndexList='item.info.menuIndexList' :index='index'></control-num>
            </div>
    </div>
</template>


<script>
import { mapState } from 'vuex'
import { calculateItem } from '../../model/activityItem.js'
import ControlNum from '../listShow/controlNum/controlNum.vue'
export default {
    props: {
        item: Object,
        index: [Number, String]
    },
    components: {
        ControlNum
    },
    computed: {
        ...mapState({
            ACTIVITY_LIST: state => state.menusModel.ACTIVITY_LIST
        })
    },
    inheritAttrs: false,
    data() {
        return {
            PriceShow: false,
            secondPrice: 0,
            firstPrice: 0,
            discount: false
        }
    },
    watch: {
        item: {
            handler: function (val, oldval) {
                let count = val.count
                let { bargainPrice, fdsaleprice, groupAskPriceTotal } = val.info
                let activity = this.ACTIVITY_LIST //活动
                if (bargainPrice < fdsaleprice) {
                    let noprice = (fdsaleprice + groupAskPriceTotal) * count  //不进入活动价格
                    let price = (bargainPrice + groupAskPriceTotal) * count //进入活动价格
                    this.firstPrice = Number(calculateItem(activity, price, val.info, count, this.index,this)).toFixed(2)
                    let obj = { finallbargainPrice: this.firstPrice, index: this.index, finallPrice: Number(noprice).toFixed(2) }
                    this.$store.commit('SAVE_GOODS_INFO_MONEY', obj)
                } else {
                    let noprice = (bargainPrice + groupAskPriceTotal) * count  //不进入活动价格
                    let price = (fdsaleprice + groupAskPriceTotal) * count//进入活动价格
                    this.firstPrice = Number(calculateItem(activity, price, val.info, count,this.index,this)).toFixed(2)
                    let obj = { finallbargainPrice: this.firstPrice, index: this.index, finallPrice: Number(noprice).toFixed(2) }
                    this.$store.commit('SAVE_GOODS_INFO_MONEY', obj)
                }
            },
            deep: true,
            immediate: true
        }
    }
}
</script>

<style lang='less' scoped>
@import './listShowItem.less';
</style>

