<template>
    <div class="order-detail" v-if="pageShow">
        <orderDetailTop :list='list' :shopInfo='shopInfo' :orderTableOutVo="orderTableOutVo" :userId='userId' :type='type' :from="from"></orderDetailTop>
        <div class="retreat-msg" v-if="list.status==5||list.status==6||list.status==7">•&nbsp;&nbsp;您的退款将从即日起<span>3-7个工作日</span>内返回至您的付款账号。</div>
        <img v-if="showTwoCode&&$sign=='wx'" src="../../assets/banner.png" alt="微信公众号">
        <img v-if="showTwoCode&&$sign=='ap'" src="../../assets/banner-ap.png" alt="支付宝生活号">
        <section class="store-name title-box clearfix" @click="enterStore">
            <div class="store-img">
                <img v-if="shopInfo.fsLogo" :src="shopInfo.fsLogo">
                <img v-else src="../../assets/store-small-logo.png">
            </div>
            {{orderShopOutVo.fsshopname}}
            <span class="right-icon no-rotate"><img src="../../assets/arrow.png"></span>
        </section>
        <div class="title-box" @click="showDishItem=!showDishItem">
            <span class="pull-right col999">{{list.total}}份</span>
            已点菜单
            <span class="right-icon" :class="{'rotate':showDishItem}"><img src="../../assets/arrow.png"></span>
        </div>
        <section class="order-list" v-show="showDishItem">
            <alreadyDishItem :orderMenuitemOutVoList='orderMenuitemOutVoList' :isConfirm="false"></alreadyDishItem>
            <div class="plr">
                <alreadyDishActivity v-if="orderActivityOutVoList" :orderActivityOutVoList="orderActivityOutVoList"></alreadyDishActivity>
                <div class="unpack" v-if="orderServiceFeeOutVoList">
                    <span class="pull-right">¥{{orderServiceFeeOutVoList[0].totalPrice}}</span>
                    {{orderServiceFeeOutVoList[0].serviceName}}
                </div>
            </div>
            <alreadyDishPrice :list="list"></alreadyDishPrice>
        </section>
        <orderContent :orderTicketOutVo="orderTicketOutVo" :list="list"></orderContent>
        <a class="adver" :href="advertisementUrl" v-if="advertisementUrl"><img :src="advertisementImg" alt="微信公众号"></a>
        <attention v-show="dialogShow"></attention>
    </div>
</template>
<script>
    import mapAxios from '../../config/mapAxios.js'
    import alreadyDishItem from '@/components/alreadyDishItem/alreadyDishItem.vue'
    import orderContent from './orderContent/orderContent.vue'
    import orderDetailTop from './orderDetailTop/orderDetailTop.vue'
    import alreadyDishActivity from '@/components/alreadyDishActivity/alreadyDishActivity.vue'
    import alreadyDishPrice from '@/components/alreadyDishPrice/alreadyDishPrice.vue'
    import attention from './attention/attention.vue'
    import shareModal from '@/components/shareModal/shareModal.vue'
    export default {
        data(){
            return {
                showDishItem:false,
                advertisementImg: '',
                advertisementUrl:'', 
                storeId: '',
                orderShopOutVo: {},//店信息
                orderMenuitemOutVoList: [],//菜单详情
                orderActivityOutVoList: [],//优惠活动信息
                orderServiceFeeOutVoList: null,//服务费信息
                orderTicketOutVo: {}, //订单发票
                orderTableOutVo:{},//餐桌信息
                list: {},
                shopInfo : {},
                userId : "",//分享的时候使用
                type: '',//判断是从个人中心还是推送消息进入的
                from: '',
                dialogShow: false,//是否关注公众号
                showTwoCode: false,//是否显示关注公众号图片
                orderId: '', //订单id
                list: {},
                pageShow: false
            }
        },
        components: {
            orderDetailTop,
            alreadyDishItem,
            orderContent,
            alreadyDishActivity,
            alreadyDishPrice,
            attention,
            shareModal
        },
        mounted(){
            //广告位
            mapAxios('get', 'https://portal.51eparty.com/api/ad?type=ORDER_DETAIL', {})
            .then(res => {
                let result = res.data
                this.advertisementImg = result.imageUrl
                this.advertisementUrl = result.adUrl
            })
            // this.$indicator.open('加载中...');
            const type = this.$route.query.type
            const from = this.$route.query.from
            this.from = from
            if (type == 1) {
                this.storeId = this.$route.query.storeId
                this.type = type
                this.orderId = this.$route.query.orderId
            } else {
                this.orderId = this.$route.params.orderId
            }
            sessionStorage.type = type
            sessionStorage.storeId = this.storeId
            sessionStorage.orderId = this.orderId
            let _this = this
            this.$store.commit('SAVE_TITLE', '订单详情')
            this.$http('get', `${this.baseUrl}/getUserInfo`)
            .then(res => {
                if (res.model.subscribe == 0) {
                    this.showTwoCode = true;
                }
            })
            //如果是公众号推送和从个人中心进来的时候都不进此接口
            if (type != 1 && from != 1 && sessionStorage.firstIn != 'true') {
                if(this.showTwoCode){
                    this.dialogShow = true
                    sessionStorage.firstIn = 'true'
                }
            }
            //分享
            this.$http('get', `${this.baseUrl}/getOrderInfo`, {
                storeId: this.storeId,
                orderId: this.orderId,
                type: this.type
            }).then(res => {
                this.pageShow = true
                let list = this.list = res.model
                this.orderShopOutVo = list.orderShopOutVo
                this.orderTableOutVo = list.orderTableOutVo
                this.shopId = this.orderShopOutVo.fsShopGUID
                this.storeId = this.shopId
                this.orderMenuitemOutVoList = list.orderMenuitemOutVoList
                this.orderActivityOutVoList = list.orderActivityOutVoList
                this.orderServiceFeeOutVoList = list.orderServiceFeeOutVoList;
                this.orderTicketOutVo = list.orderTicketOutVo;
                return this.$http('get', `${this.baseUrl}/getShopInfo`, { shopId: this.shopId })
            }).then(res => {
                // this.$indicator.close()
                this.userId = res.extra.userId
                this.shopInfo = res.model
            })
        },
        methods:{
            enterStore() {
                if (this.type == 1)  return;
                this.$router.push({ path: `/menus/${this.orderShopOutVo.fsShopGUID}/${localStorage.token}`, query: {sign:this.sign} })
            }
        }
        
    }
</script>
<style lang="less">
   @import './orderDetail.less';
</style>
