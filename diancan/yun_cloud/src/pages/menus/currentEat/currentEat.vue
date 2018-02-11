<template>
    <div class='currentEat'>
        <p class='foodName'>{{itemDish.fsitemname}}</p>
        <p class="price">
            <span>¥{{firstPrice}}</span>
            <span class="now-price" v-if='secondPriceFlag'>¥{{secondPrice}}</span>
        </p>
        <div class="btn-box" v-if='itemDish.fiiscout != 1'>
            <single-style-btn v-if='itemDish.isShowUnit == 1' :menuIndexList='itemDish.menuIndexList' :fiiscout='itemDish.fiiscout' :count='menuCount' :fsitemid='itemDish.fsitemid'></single-style-btn>
            <multi-style-btn v-else :menuIndexList='itemDish.menuIndexList' :fiiscout='itemDish.fiiscout' :count='menuCount'></multi-style-btn>
        </div>
    </div>
</template>

<script>
import { itemDish } from '../mixin/itemDish.mixin.js'
import SingleStyleBtn from '../lrLayout/itemDish/singleStyleBtn/singleStyleBtn.vue'
import MultiStyleBtn from '../lrLayout/itemDish//multiStyleBtn/multiStyleBtn.vue'
export default {
    mixins: [itemDish],
    components: {
        SingleStyleBtn,
        MultiStyleBtn
    }
}
</script>

<style lang='less' scoped>
@import './currentEat.less';
</style>

