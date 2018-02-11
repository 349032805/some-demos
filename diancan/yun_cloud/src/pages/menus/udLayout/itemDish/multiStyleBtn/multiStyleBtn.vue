<template>
    <div class="multi-btn">
        <a href="javascript:;" class="btn-add" :class='{"gray":isFiiscout || NO_OPEN}' data-flag='multiBtn' :data-menuIndexList='menuIndexList'>{{isFiiscout?'已售罄':'选规格'}}</a>
        <span class="num" v-show='count>0'>{{count}}</span>
    </div>
</template>
<script>
import {multiBtn} from '../../../mixin/multiBtn.mixin.js'
export default {
   mixins : [multiBtn]
}
</script>


<style lang = 'less' scoped>
.multi-btn{
    position: relative;
}
.btn-add {
    display: block;
    margin-top:.29rem;
    width:100%;
    height:.64rem;
    line-height: .64rem;
    text-align: center;
    text-decoration: none;
    background: #FFFFFF;
    border: 1px solid #CCCCCC;
    border-radius: 4px;
    font-size:.26rem;
    color: #F1503F;
}
.num {
    position:absolute;
    top:-.2rem;
    right:-.1rem;
    width:.36rem;
    text-align: center;
    line-height: .36rem;
    height:.36rem;
    border-radius: 50%;
    background:#F1503F ;
    font-size: .26rem;
    color: #FFFFFF;
}
.gray {
    color: #ccc !important;
    border: 1px solid #ccc !important
}
</style>


