<template>
    <div class="menuHeader"  @click='enterStoreDetail'>
        <div>
            <div class="shopbox">
                <div class="shopImg">
                    <img v-if="shopInfo.fsLogo" :src="shopInfo.fsLogo">
                    <img v-else src="../../../assets/store-logo.png">
                </div>
                <div class='shopInfo'>
                    <p class="store-name">
                        <span>{{shopInfo.fsShopName}}</span>
                        <img src='../../../assets/shopArrow.png'></img>
                    </p>
                    <ul class="type">
                        <li v-if="shopInfo.orderType==1">
                            <span>
                                <img src="../../../assets/shopTrue.png" alt="">堂食</span>
                            <span>
                                <img src="../../../assets/shopTrue.png" alt="">打包</span>
                        </li>
                        <li v-if="shopInfo.orderType==2">
                            <span>
                                <img src="../../../assets/shopTrue.png" alt="">预点</span>
                            <span>
                                <img src="../../../assets/shopTrue.png" alt="">堂食</span>
                            <span>
                                <img src="../../../assets/shopTrue.png" alt="">打包</span>
                        </li>
                    </ul>
                </div>
            </div>
            <div class='personCenter' @click.stop.prevent="enterPersonCenter()">
                <img class='person' src="../../../assets/centerIcon.png" alt="">
                <span>个人中心</span>
            </div>
        </div>
        <div class="notice" v-if='shopInfo.fsPublicMsg'>
            <img src="../../../assets/headInformation.png" alt="">
            <span>{{shopInfo.fsPublicMsg}}</span>
        </div>
        <i class="line" v-if='lineActivity'></i>
        <menu-activity :shopActivityOutVoList='shopInfo.shopActivityOutVoList' v-if='lineActivity'></menu-activity>
    </div>
</template>
<script>
import MenuActivity from './menuActivity/menuActivity.vue'
export default {
    name: "menuHeader",
    components: {
        MenuActivity
    },
    props: {
        shopInfo: {
            type: Object,
            deafult: () => {
                return {}
            }
        },

    },
    computed : {
       lineActivity () {
           let {fsPublicMsg,shopActivityOutVoList} = this.shopInfo
           if(!fsPublicMsg || !shopActivityOutVoList) {
               return false
           }else{
               return true
           }
       }
    },
    data() {
        return {
            showMore: false,
        }
    },
    methods: {
        enterPersonCenter() {
            this.$router.push({ name: 'personCenter', params: { shopId: this.shopInfo.fsShopGUID, token: localStorage.token } })
        },
        enterStoreDetail() {
            this.$router.push({ name: 'storeDetail', params: { shopId: this.shopInfo.fsShopGUID } })
        }
    }
}
</script>

<style lang="less">
@import './menuHeader.less';
</style>
