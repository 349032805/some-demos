<template>
    <div class="dish-detail">
        <div class="dish-img" :style="{backgroundImage: 'url('+ cutImg(itemDish.fsimageurl,750,750)+')'}">
            <!--<img v-if="itemDish.fsimageurl" :src="cutImg(itemDish.fsimageurl,750,750)">-->
            <img v-if="!itemDish.fsimageurl" src="../../assets/nodish-big.png">
        </div>
        <div class="dish-text" @click.stop='showFoodAttribute'>
            <p class="fsitemname">{{itemDish.fsitemname}}</p>
            <p class="sl_Num">
                <span>已售{{itemDish.soldNum}}</span>
                <span class="likeNum" v-if="itemDish.likeNum>0">赞{{itemDish.likeNum}}</span>
            </p>
            <span class="dish-price">¥{{unitList.bargainPrice?unitList.bargainPrice:unitList.fdsaleprice}}
                <span class="now-price" v-if="unitList.bargainPrice>0">¥{{unitList.fdsaleprice}}</span>
            </span>
            <div class="btn-box">
                <single-style-btn v-if='itemDish.isShowUnit == 1' :fiiscout='itemDish.fiiscout' :menuIndexList='itemDish.menuIndexList' :count='menuCount' :fsitemid='itemDish.fsitemid'></single-style-btn>
                <multi-style-btn v-else :fiiscout='itemDish.fiiscout' :menuIndexList='itemDish.menuIndexList' :count='menuCount'></multi-style-btn>
            </div>
    
        </div>
        <div class="dish-activity" v-if="isActivity">
            <h3>菜品优惠</h3>
            <ul>
                <li v-for='item in activityList'>
                    <img v-if='item.bargainKind == 302' src="../../assets/Hzeng.png" alt="">
                    <img v-if='item.bargainKind == 301' src="../../assets/Hzhe.png" alt="">
                    <span>{{item.bargainName}}</span>
                </li>
            </ul>
        </div>
        <div class="dish-dis" v-if="itemDish.fsitemdesc">
            <h3>菜品信息</h3>
            <p>{{itemDish.fsitemdesc}}</p>
        </div>
    </div>
</template>
<script>
import Vue from 'vue'
import cutImg from '@/config/cutImg.js'
import { foodAttribute } from '@/pages/menus/mixin/foodAttribute.mixin.js'
import { mapState } from 'vuex'
import { routerMenus } from '@/mixin/routerMenus.mixin.js'
import MultiStyleBtn from './multiStyleBtn/multiStyleBtn.vue'
import singleStyleBtn from './singleStyleBtn/singleStyleBtn.vue'
export default {
    created() {
        this.$store.commit('SAVE_TITLE', '菜品详情')
        let itemDish = JSON.parse(sessionStorage.itemDish)
        this.itemDish = itemDish
        this.activity(itemDish);
        //拿到初始化的值
        itemDish.menuItemUnitList.forEach(item => {
            if (item.fidefault == 1) {
                this.unitList = item
            }
        })
    },
    data() {
        return {
            isActivity: false,
            activityList: [],
            itemDish: null,
            unitList: []
        }
    },
    components: {
        MultiStyleBtn,
        singleStyleBtn
    },
    methods: {
        activity(itemDish) {
            let list = [];
            let menuItemUnitList = itemDish.menuItemUnitList
            menuItemUnitList.forEach((item, index) => {
                if (item.menuItemActivity) {
                    this.isActivity = true
                    let { bargainKind, bargainName } = item.menuItemActivity
                    let obj = { bargainKind, bargainName }
                    list.push(obj)
                }
            })
            this.activityList = list
        }
    },
    computed: {
        menuCount() {
            let menuCount = 0;
            let menuIndex = this.itemDish.menuIndexList[0]
            let menuIndexArray = menuIndex.split("_")
            let [outIndex, innerIndex] = menuIndexArray
            let menus = this.MENUS_LIST[outIndex]
            let menuItemList = menus?menus.menuItemList:null
            menuCount =menuItemList?menuItemList[innerIndex].menuCount:0
            return menuCount
        },
        ...mapState({
            MENUS_LIST: state => state.menusModel.MENUS_LIST
        }),
    },
    mixins: [cutImg, foodAttribute, routerMenus],
}
</script>
<style lang="less" scoped>
@import './dishDetail.less';
.btn-box {
    position: absolute;
    right: 10px;
    bottom: 15px;
}

.gray {
    color: #ccc !important;
    border: 1px solid #ccc !important
}
</style>