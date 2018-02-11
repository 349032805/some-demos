<template>
    <div v-show="showList">
        <div class="list-header">
            <span>桌号110</span>
            <span>已选菜品({{totalCount}}份)</span>
            <span @click='clearGoodInfo'>
                <span class="icon-remove"></span>清除</span>
            </span>
        </div>
        <div class="list-content" ref="cartFoodList">
            <ul>
                <li class="food" v-for="(item,index) in GOODS_INFO" :key="index">
                     <list-show-item :item='item' :index='index' ></list-show-item>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import BScroll from 'better-scroll'
import ListShowItem from '../listShowItem/listShowItem.vue'
import { activityBuygiftItemListTip } from '../../model/activityToast.js'
import { mapState } from 'vuex'
export default {
    data() {
        return {
            firstPrice: "",
            secondPrice: "",
            priceFlag: false
        }
    },
    props: {
        totalCount: {
            type: [Number, String],
            require: true,
            default: 0
        },
        showList: {
            type: Boolean,
            require: true,
            default: false
        }
    },
    watch :{
        showList(val){
            if(val){
                this.$nextTick(() => {
                    if (!this.foodlistScroll) {
                        this.initCartScroll();
                    } else {
                        this.foodlistScroll.refresh();
                    }
                })
            }
        }
    },
    computed: {
        ...mapState({
            GOODS_INFO: state => state.foodAttrModel.GOODS_INFO
        })
    },
    methods: {
        //初始化购物车列表
        initCartScroll() {
            this.foodlistScroll = new BScroll(this.$refs.cartFoodList, {
                click: true
            });
        },
        clearGoodInfo() {
            let _this = this
            this.$crib.confirm.show({
                title : '提示',
                content : '确定要清除购物车吗?',
                onConfirm () {
                    _this.$store.commit('CLEAR_GOODS_INFO')
                    _this.$store.commit('CLEAR_MENUS_LIST_COUNT')
                    _this.$store.commit('CLEAR_GOODS_INFO_MONEY')
                    _this.$store.commit('CHANGE_COUNT_FLAG',true);
                }
            })
         }
    },
    components: {
        ListShowItem
    }
}
</script>

<style lang='less' scoped>
@import './listShow.less';
</style>



