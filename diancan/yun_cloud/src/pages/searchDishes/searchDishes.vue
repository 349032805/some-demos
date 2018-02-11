<template>
    <div class="search-dishes">
        <search class="crib-search" :text='text' :fixed='true' :autoFocus='true' placeholder='输入菜品名称' :inputValue.sync='value' @on-submit='submit'></search>
    
        <div class="search-result">
            <div class="newMenu" v-show='isNewMenu'>
                <span v-for='item in newMenu' @click='chooseItem(item)'>{{item}}</span>
            </div>
            <ul class="list-item" @click='showFoodAttribute'>
                <li v-for="(item,index) in itemDishes" :key="index">
                    <item-dish :itemDish="item" :innerIndex='item.innerIndex' :outIndex='item.outIndex' :key='index'></item-dish>
                </li>
            </ul>
            <div class="no-search-list" v-show="showNoResult">
                <img src="../../assets/search.png">
                <p>没有搜索结果</p>
            </div>
        </div>
    </div>
</template>  
<script>
import { mapState } from 'vuex'
import { foodAttribute } from '@/pages/menus/mixin/foodAttribute.mixin.js'
import Vue from 'vue'
import { Search } from "@/crib-zk"
import ItemDish from '@/pages/menus/lrLayout/itemDish/itemDish.vue'
import { routerMenus } from '@/mixin/routerMenus.mixin.js'
export default {
    created() {
        this.$store.commit('SAVE_TITLE', '菜品搜索')
    },
    mixins: [foodAttribute, routerMenus],
    data() {
        return {
            isNewMenu: true,
            newMenu: ['新品菜', '招牌菜', '特价菜'],
            value: "",
            itemDishes: [],
            activity: "",
            showNoResult: false,
            text: "取消"
        }
    },
    computed: {
        ...mapState({
            MENUS_LIST: state => state.menusModel.MENUS_LIST
        })
    },
    methods: {
        chooseItem(name) {
            this.value = name;
            this.isNewMenu = false;
        },
        submit() {
            this.$router.back()
        },
    },
    watch: {
        itemDishes: {
            handler: function (val, oldval) {
                if (val.length > 0) {
                    let flag = 0
                    val.forEach(item => {
                        flag += item.menuCount
                    })
                    if (flag > 0) {
                        this.text = "完成"
                        return
                    }
                }
                this.text = "取消"
            },
            deep: true
        },
        value(val, oldval) {
            if (val == '新品菜' || val == '招牌菜' || val == '特价菜') {
                this.MENUS_LIST.forEach((item, index) => {
                    if (item.fsmenuclsid) {
                        item.menuItemList.forEach((item2, index2) => {
                            if (val == "新品菜") {
                                if (item2.fiisnew == 1) {
                                    this.itemDishes.push(item2)
                                }
                            } else if (val == '招牌菜') {
                                if (item2.fiIsHot == 1) {
                                    this.itemDishes.push(item2)
                                }
                            } else if (val == '特价菜') {
                                item2.menuItemUnitList.every((item3, index3) => {
                                    if (item3.bargainPrice) {
                                        this.itemDishes.push(item2);
                                        return
                                    }
                                })
                            }
                        })
                    }
                })
                return
            }
            if (val == "") {
                this.isNewMenu = true;
                this.itemDishes = []
                this.showNoResult = false
                return
            }
            this.isNewMenu = false;
            this.itemDishes = []
            let nowArr = []
            this.MENUS_LIST.forEach((item, index) => {
                if (item.fsmenuclsid) {
                    item.menuItemList.forEach((item2, index2) => {
                        let flag = item2.fsitemname.indexOf(val)
                        if (flag > -1) {
                            if (nowArr[item2.fsitemname] == undefined) {
                                nowArr[item2.fsitemname] = item2.fsitemname
                                this.showNoResult = false
                                item2.outIndex = index
                                item2.innerIndex = index2
                                this.itemDishes.push(item2)
                            }
                        }
                    })
                }
                if (this.itemDishes.length == 0) {
                    this.showNoResult = true
                }
            })
        }
    },
    components: {
        ItemDish,
        Search
    }
}
</script>
<style lang="less" scoped>
@import './searchDishes.less';
</style>