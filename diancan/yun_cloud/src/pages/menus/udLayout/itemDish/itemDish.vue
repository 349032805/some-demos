<template>
    <div>
        <div class="item-dish">
            <div class="img-box" @click='enterDishDetail' :class="{layout:itemDish.fiiscout == 1}" :style="{backgroundImage:'url('+cutImg(itemDish.fsimageurl,134,134)+')'}">
                <img v-if="!itemDish.fsimageurl" src="../../../../assets/nodish-small.png">
                <div class='tipImg'>
                       <img v-if='fiisnew' src="../../../../assets/Sxinping.png" alt="">
                       <img v-if='fiIsHot' src="../../../../assets/Szhaopai.png" alt="">
                </div>
            </div>
            <div class="food-info">
                <p class="foodName">{{itemDish.fsitemname}}</p>
                <div v-if='bargainNameFlag' class="activity">
                    <img :src='activityImg'>
                    <span>{{bargainName}}</span>
                </div>
                <div class="price">
                    <div>
                        <span class="bragin-price">¥{{firstPrice}}</span>
                        <span class="start-money" v-if='startMoney'>起</span>
                        <span class="now-price" v-if='secondPriceFlag'>¥{{secondPrice}}</span>
                    </div>
                    <p class="saled">已售{{itemDish.soldNum}}</p>
                </div>
            </div>
            <div class="btn-box">
                <single-style-btn v-if='itemDish.isShowUnit == 1' :menuIndexList='itemDish.menuIndexList' :fiiscout='itemDish.fiiscout' :count='menuCount' :fsitemid='itemDish.fsitemid'></single-style-btn>
                <multi-style-btn v-else :menuIndexList='itemDish.menuIndexList' :fiiscout='itemDish.fiiscout' :count='menuCount'></multi-style-btn>
            </div>
        </div>
    </div>
</template>
<script>
import cutImg from '@/config/cutImg.js'
import { itemDish } from '../../mixin/itemDish.mixin.js'
import SingleStyleBtn from './singleStyleBtn/singleStyleBtn.vue'
import MultiStyleBtn from './multiStyleBtn/multiStyleBtn.vue'
export default {
    mixins: [cutImg, itemDish],
    components: {
        SingleStyleBtn,
        MultiStyleBtn
    }
}
</script>
<style lang="less" scoped>
@import './itemDish.less';
</style>