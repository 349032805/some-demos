<template>
    <div>
        <span class="buttn-default single-btn" data-flag='singleBtn' :class='{"gray":isFiiscout || NO_OPEN}'  :data-menuIndexList='menuIndexList' v-show='count == 0'>{{isFiiscout?'已售罄':'添加'}}</span>
        <div class="cart-btn-box" v-show='count>0'>
            <span class="minus" data-flag='singleRdBtn' :data-menuIndexList='menuIndexList'>-</span>
            <span class="dish-count">{{count}}</span>
            <span class="plus" data-flag='singleBtn' :data-menuIndexList='menuIndexList'>+</span>
        </div>
    </div>
</template>

<script>
import { singleBtn } from '../../../mixin/singleBtn.mixin.js'
export default {
    mixins: [singleBtn],
    props:{
        fiiscout: ''
    }
}
</script>

<style lang='less' scoped>
@import './singleStyleBtn.less';
.gray {
    color: #ccc !important;
    border: 1px solid #ccc !important
}
</style>

