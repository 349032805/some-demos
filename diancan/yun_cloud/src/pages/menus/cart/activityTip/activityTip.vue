<template>
    <div class="activityTip" v-if='ACTIVITY_SHOW'>• 
        
        <span v-if='isNewSendFood'>新用户赠【{{newFood}}】</span>
        <span v-show = 'startSaled'>下单减<span class="redColor">{{saled}}元</span></span>
        <span v-show='isFullMoney'>再满<span  class="redColor">{{fullMoney}}元</span>可共减<span  class="redColor">{{endSaled}}元</span></span>
    </div>
</template>

<script>
import { cutMoneyTip } from '../../model/cutMoneyTip.js'
import { mapState } from 'vuex'
export default {
    data() {
        return {
            isActivty: false,
            isNewSendFood: false,
            newFood: '',
            saled : "",
            isFullMoney : false,
            fullMoney : "",
            startSaled : false,
            endSaled : ""
        }
    },
    computed: {
        ...mapState({
            ACTIVITY_LIST: state => state.menusModel.ACTIVITY_LIST,
            ACTIVITY_SHOW: state => state.activityModel.ACTIVITY_SHOW,
            MONEY: state => state.activityModel.MONEY
        })
    },
    watch: {
        ACTIVITY_LIST: {
            handler: function (val, oldval) {
                if (!val) {
                    return
                }
                let newCustomerResponse = val.activityNewCustomerResponse
                if (newCustomerResponse && newCustomerResponse.dishName && newCustomerResponse.boolFirstActivity == 1) {
                    this.isNewSendFood = true;
                    this.$store.commit('SAVE_ACTIVITY_SHOW', true)
                    let food = val.activityNewCustomerResponse.dishName;
                    this.newFood = food
                }

            },
            deep: true,
            
        },
        MONEY: {
            handler: function (val, oldval) {
                if(this.ACTIVITY_LIST && (this.ACTIVITY_LIST.activityCutMoneyListResponse || this.ACTIVITY_LIST.activityNewCustomerResponse)){
                    let activityNewCustomerResponse = this.ACTIVITY_LIST.activityNewCustomerResponse
                    if(!activityNewCustomerResponse || activityNewCustomerResponse.discountType != 1){
                                activityNewCustomerResponse = null
                    }
                    cutMoneyTip(this.ACTIVITY_LIST.activityCutMoneyListResponse,activityNewCustomerResponse,val,this)
                    //cutMoneyTip(this.ACTIVITY_LIST.activityCutMoneyListResponse,null,val,this)
                }
            },
            immediate : true
        }

    }
}
</script>

<style lang='less' scoped>
@import './activityTip.less';
</style>


