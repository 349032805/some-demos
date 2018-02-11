<template>
    <div class="menus">
        <div ref='headerBox'>
            <menu-header :shopInfo="shopInfo"></menu-header>
            <!--<div v-if='swiperList.length>0' class="menus-swiper">
                <swiper :type='2' :swiperList='swiperList'></swiper>
            </div>-->
        </div>
        <food-attribute></food-attribute>
        <components :is="currentView">
        </components>
        <cart v-show='CARD_SHOW' :shopId='shopInfo.fsShopGUID'></cart>
        <transition name="router-slid" mode="out-in">
            <router-view></router-view>
        </transition>
        <balls ref="balls"></balls>
    </div>
</template>
<script>
import { errorTip } from './model/errorTip.js';
import { getShopInfo, getShopActivityList, getShopMenu, getMenusTypeSetting, getUserShopOrder } from '@/config/api.js'
import { getCache } from './model/cache.js'
import { initAddress } from '@/mixin/initAddress.mixin.js'
import MenuHeader from './menuHeader/menuHeader.vue'
import Cart from './cart/cart.vue'
import Balls from './balls/balls.vue'
import UdLayout from './udLayout/udLayout.vue'
import LrLayout from './lrLayout/lrLayout.vue'
import Swiper from '@/components/swiper/swiper.vue'
import { mapState } from 'vuex'
export default {
    components: {
        Swiper,
        MenuHeader,
        UdLayout,
        'FoodAttribute': () =>import('./foodAttribute/foodAttribute.vue'),
        Cart,
        Balls
    },
    mixins: [initAddress],
    created() {
        let shopId, tableId;
        this.$indicator.open('加载中...');
        this.$store.commit('SAVE_TITLE', '百味云点餐系统')
        let params = this.$route.params;
        let querys = this.$route.query;
        ({ shopId, token: localStorage.token } = params);
        ({ tableId } = querys)
        //给定当确认页id和桌号
        sessionStorage.tableId = querys.tableId
        Promise.all([getShopInfo(this.baseUrl, shopId),
        getShopActivityList(this.baseUrl, shopId),
        getMenusTypeSetting(this.baseUrl, shopId),
        getShopMenu(this.baseUrl, shopId, tableId),
        getUserShopOrder(this.baseUrl, shopId)]).then((result) => {
            this.$indicator.close();
            let [shopInfo, shopActivityList, getMenusTypeSetting, shopMenu, getUserShopOrder] = result;


            let error = errorTip(shopMenu, shopInfo, tableId, this);
            if (!error) return;
            if (getMenusTypeSetting.model == 2) {//1：左右排版，2：上下排版
                this.currentView = LrLayout;
            } else {
                this.currentView = UdLayout;
            }
            this.shopInfo = shopInfo.model;
            sessionStorage.shopInfo = JSON.stringify(shopInfo); //传给定单确认确的店铺所有信息
            this.getMenusTypeSetting = getMenusTypeSetting.model
            this.swiperList = getUserShopOrder.model;

            getCache(shopInfo, shopActivityList, shopMenu, this)
        }).then(err => {
            console.log(err)
        })
    },
    data() {
        return {
            currentView: '',
            shopMenu: [],
            shopInfo: {},
            getMenusTypeSetting: {},
            swiperList: []
        }
    },
  
    computed: {
        ...mapState({
            IS_PAGE: state => state.personCenterModel.IS_PAGE,
            CARD_SHOW: state => state.titleModel.CARD_SHOW,
            SALED_FOODS: state => state.menusModel.SALED_FOODS,
            COUNT_FLAG: state => state.foodAttrModel.COUNT_FLAG
        })
    },
    watch: {
        IS_PAGE(value, oldval) {
            if (oldval != value) {
                location.reload()
            }

        },
        SALED_FOODS: {
            handler: function (val, oldval) {
                this.$store.commit('SALED_CLEAR_GOODS_INFO', val)
                this.$store.commit('REDUCE_MENUS_LIST_COUNT', this.COUNT_FLAG)
                this.$store.commit('CHANGE_COUNT_FLAG', true)
            }
        }
    }
}
</script>

<style lang="less" scoped>
@import "./menus.less";
</style>
