<template>
    <div :class="{'bigMenus':true,'hide-title':isHide}">
        <div class="menu-box">
            <div class="menu-header">
                <span class="pull-left search-box" v-if="MENUS_LIST.length>0" @click='enterSearchDishes'>
                    <span class="icon-search"></span>搜索</span>
                <div ref="menuHeaderWrapper" style="overflow:hidden;">
                    <ul :style="{'width':menuHeaderWidth}">
                        <li v-for="(menu,index) in MENUS_LIST" @click="selectMenu(index)" :class="{'current':currentIndex==index}" :key="index">
                            {{menu.fsmenuclsname}}
                            <span class="num" v-show="count(index)>0">{{count(index)}}</span>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="menu-list" ref="menuListWrapper" @touchstart="replaceCuIndex=false" @click='showFoodAttribute'>
            <ul class="menu-list-item">
                <li v-for="(menu,menuIndex) in MENUS_LIST" class="menu-list-hook" :key="menuIndex">
                    <div v-if='menu.fsmenuclsname =="吃过" '>
                        <div ref="eatWrapper" style="overflow:hidden;background:#fff;">
                            <ul class='eat-item' :style="{'width':eatWrapperWidth}">
                                <li v-for="(itemDish,itemIndex) in menu.menuItemList" :key='itemIndex'>
                                    <current-eat :itemDish="itemDish"></current-eat>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div v-else>
                        <p class='menuName'>{{menu.fsmenuclsname}}
                            <span>({{menu.menuItemList.length}})</span>
                        </p>
                        <ul class="item-list">
                            <li v-for="(itemDish,itemDishIndex) in menu.menuItemList" :key="itemDishIndex">
                                <itemDish :itemDish="itemDish" :outIndex='menuIndex' :innerIndex="itemDishIndex"></itemDish>
                            </li>
                        </ul>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>
<script>
import { getShopMenu } from '@/config/api.js'
import Vue from 'vue'
import BScroll from 'better-scroll'
import itemDish from './itemDish/itemDish.vue'
import CurrentEat from '../currentEat/currentEat.vue';
import { layoutMixin } from '../mixin/layout.mixin.js'
import { foodAttribute } from '../mixin/foodAttribute.mixin.js'
import { mapState } from 'vuex'
/*
* data 属性说明
  menuHeaderWidth => 存储菜单高度
  menuHeaderWidth => 存储菜单栏宽度
  ---------------------------------------
  created 参数和过程说明
  getShopMenu => 从api.js全局请封装的请求,需要传入 shopId和tableId参数

*/
export default {
    name: 'udLayout',
    computed: {
        ...mapState({
            MENUS_LIST: state => state.menusModel.MENUS_LIST
        })
    },
    mounted() {
        this.$nextTick(() => {
           
            this.eatlistWidth();
            this.headerWidth();
            this.initMenuListHeight()
            this.$nextTick(() => {
                this.initScroll()
            })
        });
    },

    mixins: [layoutMixin, foodAttribute],
    components: {
        itemDish,
        CurrentEat
    },
    methods: {
        initScroll() {
            //初始化侧边栏滚动
            this.headerScroll = new BScroll(this.$refs.menuHeaderWrapper, {
                click: true,//打开点击事件
                scrollX: true
            });

            //初始化菜单列表滚动
            this.menusScroll = new BScroll(this.$refs.menuListWrapper, {
                click: true,
                probeType: 3
            });
             //初始化已吃菜品列表
            this.eatScroll = new BScroll(this.$refs.eatWrapper[0], {
                //click: true,//打开点击事件
                scrollX: true
            });
            //初始化滚动条
            let _this = this;
            _this.menusScroll.on('scroll', (pos) => {
                _this.scrollY = Math.abs(Math.round(pos.y))
                console.log(pos.y)
                for (let i = 0; i < _this.menuListHeight.length; i++) {
                    let height1 = _this.menuListHeight[i]
                    let height2 = _this.menuListHeight[i + 1]
                    if (!height2 || (_this.scrollY >= height1 && _this.scrollY < height2)) {
                        if (_this.replaceCuIndex) {//点击左边菜单
                            return _this.currentIndex;
                        } else {
                            let liList = _this.$refs.menuHeaderWrapper.querySelectorAll('li');
                            if (i != 0) {
                                this.isHide = true;
                                let el = liList[i];
                                _this.headerScroll.scrollToElement(el, 300);
                            } else {
                                this.isHide = false;
                                _this.headerScroll.scrollTo(0, 0);
                            }
                            return _this.currentIndex = i;
                        }
                    }
                }
            })
        },
        headerWidth() { //初始化菜品分类宽度
            let liList = this.$refs.menuHeaderWrapper.querySelectorAll('li');
            let liWidth = liList[0].clientWidth + 35;
            for (let i = 0; i < liList.length; i++) {
                liWidth = liList[i].clientWidth + 35;
                this.menuHeaderWidth += liWidth;
            }
            this.menuHeaderWidth += 80;
            this.menuHeaderWidth = this.menuHeaderWidth + 'px';
            // this.menuHeaderWidth = liList.length * liWidth + 'px';
        },
        initMenuListHeight() {  //初始化菜品分类高度
            let menuList = this.$refs.menuListWrapper.querySelectorAll(".menu-list-hook");
            let height = 0;
            this.menuListHeight.push(height);
            for (let i = 0; i < menuList.length; i++) {
                let item = menuList[i];
                height += item.clientHeight;
                this.menuListHeight.push(height)
            }
        },
        eatlistWidth() { //初始化吃过分类宽度
            this.eatWrapperWidth = 0;
            let liList = this.$refs.eatWrapper[0].querySelectorAll('li');
            let liWidth = 0;
            for (let i = 0; i < liList.length; i++) {
                liWidth = liList[i].clientWidth + 2;
                this.eatWrapperWidth += liWidth;
            }
            this.eatWrapperWidth = this.eatWrapperWidth + 'px';
        },
        selectMenu(index) { //选中菜品分类事件
            this.currentIndex = index;
            if (!this.replaceCuIndex) {
                this.replaceCuIndex = true;
            }
            let menuList = this.$refs.menuListWrapper.querySelectorAll(".menu-list-hook");
            let el = menuList[index];
            this.menusScroll.refresh();
            this.menusScroll.scrollToElement(el, 300)
        }
    }
}
</script>
<style lang="less">
@import "./udLayout.less";
</style>