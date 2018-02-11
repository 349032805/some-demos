<template>
    <div :class="{'small-menus':true,'hide-title':isHide}">
        <span class="search-box" v-if="MENUS_LIST.length>0" @click='enterSearchDishes'>
            <span class="icon-search"></span>搜索
        </span>
        <div class="menu-sidebar" ref="menuWrapper">
            <ul>
                <li v-for="(menu,index) in MENUS_LIST" @click="selectMenu(index)" :class="{'current':currentIndex==index,'menu-list-hook':true}" :key='index'>
                    <img v-if="menu.fsmenuclsname =='热销'" src="../../../assets/hot.png" alt="">
                    <img v-else-if="menu.fsmenuclsname =='吃过'" src="../../../assets/eat.png" alt="">
                    <img v-if="menu.fsmenuclsname =='优惠'" src="../../../assets/nice.png" alt="">
                    {{menu.fsmenuclsname}}
                    <span class="num" v-show="count(index)>0">{{count(index)}}</span>
                </li>
            </ul>
        </div>
        <div class="menu-lists" ref="foodsWrapper" @touchstart="replaceCuIndex=false" @click='showFoodAttribute'>
            <ul>
                <li v-for="(menu,menuIndex) in MENUS_LIST" class="menu-list-hook" :key='menuIndex'>
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
                        <h2>{{menu.fsmenuclsname}}({{menu.menuItemList.length}})</h2>
                        <ul class="list-item">
                            <li v-for="(itemDish,itemIndex) in menu.menuItemList" :key='itemIndex'>
                                <itemDish :itemDish="itemDish"></itemDish>
                            </li>
                        </ul>
                    </div>
                </li>
                <li class="menu-list-hook" v-show="MENUS_LIST.length>0"><h2>图片仅供参考 菜品请以实物为准</h2></li>
            </ul>
        </div>
    </div>
</template>

<script>
import { mapState } from 'vuex'
import { getShopMenu } from '@/config/api.js'
import Vue from 'vue'
import BScroll from 'better-scroll'
import CurrentEat from '../currentEat/currentEat.vue';
import itemDish from './itemDish/itemDish.vue'
import { layoutMixin } from '../mixin/layout.mixin.js'
import { foodAttribute } from '../mixin/foodAttribute.mixin.js'
export default {
    name: 'lrLayout',
    mixins: [layoutMixin, foodAttribute],
    components: {
        itemDish,
        CurrentEat
    },
    mounted() {
        this.$nextTick(() => {
            this.eatlistWidth();
            this._calculateHeight(); // 初始化列表高度列表
            this.$nextTick(() => {
                this.initScroll(); // 初始化scroll
            })
        });
    },
    computed: {
        ...mapState({
            MENUS_LIST: state => state.menusModel.MENUS_LIST
        })
    },
    methods: {
        sildeBarImg (menus) {
            if(menus.fsmenuclsname =='热卖') {
                return ''
            }
        },
        initScroll() {
            //初始化侧边栏滚动
            this.menuScroll = new BScroll(this.$refs.menuWrapper, {
                click: true//打开点击事件
            });
            //初始化菜单列表滚动
            this.foodsScroll = new BScroll(this.$refs.foodsWrapper, {
                click: true,
                probeType: 3
            });
            //初始化已吃菜品列表
            this.eatScroll = new BScroll(this.$refs.eatWrapper[0], {
                //click: true,//打开点击事件
                scrollX: true
            });
            let _this = this;
            this.foodsScroll.on('scroll', (pos) => {
                _this.scrollY = Math.abs(Math.round(pos.y))
                for (let i = 0; i < _this.listHeight.length; i++) {
                    let height1 = _this.listHeight[i];
                    let height2 = _this.listHeight[i + 1];
                    if (!height2 || (_this.scrollY >= height1 && _this.scrollY < height2)) {
                        if (_this.replaceCuIndex) {//点击左边菜单
                            return _this.currentIndex;
                        } else {
                            let liList = _this.$refs.menuWrapper.querySelectorAll('li');
                            let el = liList[i - 1];
                            if (i != 0) {//判断是否滚动了一屏
                                _this.menuScroll.scrollToElement(el, 300);
                                this.isHide = true;
                            } else {
                                _this.menuScroll.scrollTo(0, 0);
                                this.isHide = false;
                            }
                            return _this.currentIndex = i;
                        }
                    }
                }
            })
        },
        //计算每个分类的高度
        _calculateHeight() {
            let foodList = this.$refs.foodsWrapper.getElementsByClassName('menu-list-hook')
            let height = 0;
            this.listHeight.push(height);
            for (let i = 0; i < foodList.length; i++) {
                let item = foodList[i];
                height += item.clientHeight;
                this.listHeight.push(height)
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
        //选择菜单
        selectMenu(index, enent) {
            this.currentIndex = index;
            if (!this.replaceCuIndex) {
                this.replaceCuIndex = true;
            }
            let foodList = this.$refs.foodsWrapper.getElementsByClassName('menu-list-hook')
            let el = foodList[index];
            //获取列表dom，并且滚动到对应位置 
            this.foodsScroll.refresh();
            this.foodsScroll.scrollToElement(el, 300)
        },
        //初始化购物车列表
        _initScroll() {
            this.foodlistScroll = new BScroll(this.$refs.foodlist, {
                click: true
            });
        }
    }
}
</script>

<style lang='less' scoped>
@import './lrLayout.less';
</style>

