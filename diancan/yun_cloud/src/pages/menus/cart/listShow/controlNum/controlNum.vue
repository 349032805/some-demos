<template>
    <div class="noattr-btn-box">
        <span class="minus" @click='reduce'>-</span>
        <span class="count">{{count}}</span>
        <span class="plus" @click='add'>+</span>
    </div>
</template>

<script>
import { activityBuygiftItemListTip } from '../../../model/activityToast.js'
import { mapState } from 'vuex'
export default {
    props: {
        count: Number,
        index: {
            type: Number,
            require: true
        },
        fsitemid : {
            type : String,
            require: true
        },
        fsorderunit : {
            type : String,
            require: true
        },
        menuIndexList : Array,
        
    },
    inheritAttrs : false,
     computed: {
        ...mapState({
            ACTIVITY_LIST: state => state.menusModel.ACTIVITY_LIST,
              GOODS_INFO: state => state.foodAttrModel.GOODS_INFO,
        })
    },
    methods: {
         activityTipMethods(fsitemid, fsorderunit) {
            //判断是否有爆款活动

            let activityTip = undefined
            if (this.ACTIVITY_LIST && this.ACTIVITY_LIST.activityBuygiftItemListResponse) {
                activityTip = this.ACTIVITY_LIST.activityBuygiftItemListResponse
            }
            if (activityTip) {
                activityBuygiftItemListTip(activityTip, fsitemid, fsorderunit, this.GOODS_INFO)
            }
        },
        add() {
            this.$store.commit('ADD_GOODS_INFO_COUNT', this.index)
            this.$store.commit('ADD_ITEM_COUNT', this.menuIndexList)
            this.activityTipMethods(this.fsitemid, this.fsorderunit)
            this.$store.commit('CHANGE_COUNT_FLAG',true);
        },
        reduce() {
            this.$store.commit('REDUCE_ITEM_COUNT',  this.menuIndexList)
            this.$store.commit('REDUCE_GOODS_INFO_COUNT', this.index)
            this.activityTipMethods(this.fsitemid, this.fsorderunit)
            this.$store.commit('CHANGE_COUNT_FLAG',true);
        }
    }

}
</script>
<style lang="less">
@import './controlNum.less';
</style>
