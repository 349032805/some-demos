<template>
    <div>
        <div class="item-dish">
            <!--:style="{backgroundImage:'url('+cutImg(itemDish.fsimageurl,134,134)+')'}"-->
            <div class="img-box" @click='enterDishDetail'>
                <div class="layout"  v-if = "itemDish.fiiscout == 1" ></div>
                <img v-if="!itemDish.fsimageurl" src="../../../../assets/nodish-small.png">
                <img v-else :src="itemDish.fsimageurl" alt="">
                <span class='fiiscout' v-if='itemDish.fiiscout == 1'>已售罄</span>
                <div class='tipImg'>
                   <img v-if='fiisnew' src="../../../../assets/Sxinping.png" alt="">
                   <img v-if='fiIsHot' src="../../../../assets/Szhaopai.png" alt="">
                </div>
            </div>
            <div class="food-info">
                <p class="foodName">{{itemDish.fsitemname}}</p>
                <p class="saled">已售{{itemDish.soldNum}}</p>
                <div v-if='bargainNameFlag' class="activity"><img :src='activityImg'><span>{{bargainName}}</span></div>
                <p class="price">
                    <span>¥{{firstPrice}}</span>
                    <span class="start-money" v-if='startMoney'>起</span>
                    <span class="now-price" v-if='secondPriceFlag'>¥{{secondPrice}}</span>
                </p>
            </div>
            <div class="btn-box" v-if='itemDish.fiiscout != 1'>
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
.gray {
    color: #ccc !important;
    border: 1px solid #ccc !important
};
.layout {
    position:absolute;
    background:rgba(0,0,0,.4);
    width:100%;
    height:100%;
    top:0;
    left:0;
    
}
</style>