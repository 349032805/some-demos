<template>
    <div>
        <span class="add-btn"  :class='{"gray":isFiiscout || NO_OPEN}' data-flag='singleBtn' :data-menuIndexList='menuIndexList' v-show='count == 0'>{{isFiiscout?'已售罄':'添加'}}</span>
        <div v-show='count>0' class="noattr-btn-box">
            <span class="minus" data-flag='singleRdBtn' :data-menuIndexList='menuIndexList'>-</span>
            <span>{{count}}</span>
            <span class="plus" data-flag='singleBtn' :data-menuIndexList='menuIndexList'>+</span>
        </div>
    </div>
</template>

<script>
import {singleBtn} from '@/pages/menus/mixin/singleBtn.mixin.js'
//:class='{"gray":item.fiiscout == 1 || SHOP_STATUS != 1}'>{{item.fiiscout == 1 || SHOP_STATUS != 1?'已售罄':'添加'}}
export default {
  mixins : [singleBtn],
  props:{
      fiiscout: ''
  }
}
</script>
<style lang="less" scoped>
.add-btn {
    display: inline-block;
    width: 2.057rem;
    height:.6rem;
    font-size: .28rem;
    color: #F1503F;
    line-height: .6rem;
    border: 1px solid #D8D8D8;
    text-align: center;
    border-radius: 4px;
}
.gray {
    color: #ccc !important;
    border: 1px solid #ccc !important
}
.noattr-btn-box {
    width: 2.057rem;
    height:.6rem;
    font-size: 14px;
    position: absolute;
    right: 0;
    top: -.6rem;
    background: url(../../../assets/smallAdd.png) no-repeat center;
    background-size: 100%;
    border: none;
    line-height: .6rem;
    text-align: center;
    >span {
        width: 48%;
        border: 1px solid transparent;
        text-align: center;
        height: 100%;
        background: transparent;
        font-size: .28rem;
        color: #000000;
    }
    .minus,
    .plus {
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: .8rem;
        color: #F1503F;
        font-size: 0;
        opacity: 0;
    }
    .plus {
        left: inherit;
        right: 0;
    }
    
}
</style>
