<template>
    <div>
        <a href="javascript:;" :class='{"gray":isFiiscout || NO_OPEN}' data-flag='multiBtn' class="btn-add" :data-menuIndexList='menuIndexList'>{{isFiiscout?'已售罄':'选规格'}}</a>
        <span class="num" v-show='count>0'>{{count}}</span>
    </div>
</template>

<script>
import { multiBtn } from '../../../mixin/multiBtn.mixin.js'
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
    width: 1.6rem;
    height: .486rem;
    line-height: .486rem;
    font-size: .24rem;
    text-align: center;
    text-decoration: none;
    border: 1px solid #CCCCCC;
    color: #F1503F;
    border-radius: 4px;
}

.num {
    position: absolute;
    width:.28rem;
    height:.28rem;
    line-height: .3rem;
    right: -5px;
    top: -.17rem;
    text-align: center;
    border-radius: 18px;
    background: #F1503F;
    color: #fff;
    font-size: .24rem;
}

.gray {
    color: #ccc !important;
    border: 1px solid #ccc !important
}
</style>


