<template>
    <header>
        <div class="title clearfix">
            <a href="javascript:;" v-if="from==1" class="pull-left" @click="enterStore"><img src="../../../assets/icon-left.png"></a>
            <a href="javascript:;" v-if='type!=1' class="pull-right" @click='showModel'><img src="../../../assets/share.png"></a>
            <h4>就餐号(凭号取餐)</h4>
        </div>
        <h1>{{list.eatNumber}}</h1>
        <p class="status">{{status}}</p>
        <p class="notes">
            <span v-if="list.eatStyle==3||list.eatStyle==4"><img src="../../../assets/dui.png">提前点餐</span>
            <span v-if="list.eatStyle==2||list.eatStyle==4"><img src="../../../assets/dui.png">打包</span>
            <span v-if="orderTableOutVo&&orderTableOutVo.fsmtablename">桌号{{orderTableOutVo.fsmtablename}}</span>
        </p>
        <div class="header-btn-box">
            <a href="javascript:;" v-if="list.eatStyle==3||list.eatStyle==4" @click="callPhone">联系商家</a>
            <a href="javascript:;" class="fill-btn" @click="enterGivingAsment">{{btnContent}}</a>
        </div>
        <!-- 弹出层 -->
        <mt-actionsheet :actions="actions" v-model="sheetVisible"></mt-actionsheet>
        <share-modal :store='shopInfo' :showShare.sync="showShare"></share-modal>
    </header>
</template>

<script>
import { wxShare } from '@/utils/wxShare.js';
import {apShare} from '@/utils/apShare.js' 
import shareModal from '@/components/shareModal/shareModal.vue'
export default {
    props: {
        type: null,
        from: null,
        list: Object,
        shopInfo:Object,
        orderTableOutVo:Object,
        userId : String
    },
    data(){
        return {
            sheetVisible: false,
            showShare: false,
            actions: [],
        }
    },
    components: {
        shareModal
    },
    computed:{
        status() {
            let status = ""
            switch (this.list.status) {
                case 6:
                    status = "已取消"
                    break;
                case 5:
                    status = "已取消 "
                    break;
                case 4:
                    status = "已支付"
                    break;
                case 7:
                    status = "已取消"
                    break;
            }
            return status
        },
        eatType() {
            let eatType = ""
            switch (this.list.eatStyle) {
                case 1:
                    eatType = "现点堂吃";
                    break;
                case 2:
                    eatType = "现点打包 "
                    break;
                case 3:
                    eatType = "预点堂吃";
                    break;
                case 4:
                    eatType = "预点打包";
                    break;
            }
            return eatType
        },
        btnContent() {
            if (this.list.evaluateStatus == 0) {
                return "去评价"
            } else {
                return "查看评价"
            }
        }
    },
    methods: {
        enterStore() {
            if (this.type == 1) return;
            this.$router.push({ path: `/menus/${this.shopInfo.fsShopGUID}/${localStorage.token}`, query: {sign:this.$sign} })
        },
        showModel() {
            if(this.$sign == 'wx') {
                this.showShare = true;
                //如果是微信,调用分享到朋友圈和朋友
                wxShare(this.baseUrl, this.shopInfo.fsShopGUID, this.shopInfo, this._succCallback);
            }else{
                apShare(this.shopInfo.fsShopGUID,this.shopInfo,this.userId)
            }
        },
        callPhone() {
            this.actions = [{
                name: `商家电话: ${this.shopInfo.fscellphonect}`,
                method: this.dial
            }];
            if(this.shopInfo.fscellphonect){
                this.sheetVisible = true;
            }else{
                this.$message({
                    title: '提示',
                    message: '餐厅没留电话哦',
                    showCancelButton: false
                });
            }
        },
        dial() {
            window.location.href = "tel:" + this.shopInfo.fscellphonect;
        },
        enterGivingAsment() {
            if (this.list.evaluateStatus == 0) {
                //没有评价
                this.$router.push({ name: "givingAssessment", params: { orderId: this.list.orderId, fsShopGuid: this.shopInfo.fsShopGUID } })  //这里以后要换 fsShopGuid: this.orderShopOutVo.fsShopGUID
                return
            }
            //评价过
            this.$router.push({ name: "assessmentDetail", params: { orderId: this.list.orderId } })
        },
        _succCallback() {
            this.showShare = false;
        }
    }
}
</script>

<style lang="less">
   @import './orderDetailTop.less';
</style>
