<template>
    <div>
        <a href="javascript:;" :class='{"gray":isFiiscout || NO_OPEN}' data-flag='multiBtn' class="btn-add" :data-menuIndexList='menuIndexList'>{{isFiiscout?'已售罄':'选规格'}}</a>
        <span class="num" v-show='count>0'>{{count}}</span>
    </div>
</template>

<script>
import { multiBtn } from '@/pages/menus/mixin/multiBtn.mixin.js'
export default {
    mixins: [multiBtn],
    props: {
        fiiscout: ''
    }
}
</script>

<style lang='less' scoped>
.btn-add {
    display: block;
    width: 2.057rem;
    height:.6rem;
    line-height: .6rem;
    font-size: .28rem;
    text-align: center;
    text-decoration: none;
    border: 1px solid #CCCCCC;
    color: #F1503F;
    border-radius: 4px;
}

.num {
    position: absolute;
    right: -5px;
    top: -.17rem;
    padding: 1px 5px;
    border-radius: 18px;
    background: #F1503F;
    color: #fff;
    font-size: .26rem;
}

.gray {
    color: #ccc !important;
    border: 1px solid #ccc !important
}
</style>


