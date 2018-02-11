<template>
    <div class="search-dishes">
        <div class="search-input-box clearfix">
            <span class="search-cancle pull-right" @click="cancel()">{{textDis}}</span>
            <div class="search-input">
                <img src="../../assets/search-small.png">
                <form action="javascript:return true;">
                <input type="search" placeholder="输入菜品名称" v-model="searchText" @keyup="keyup()">
                </form>
            </div>
        </div>
        <div class="search-result">
            <div class="common-order-dishes" v-show="showCommonList">
                <div class="title">
                    <h3>最常点的菜</h3>
                </div>
                <ul class="list-item">
                    <li v-for="(item,index) in commonDishesList" :key="index">
                        <dish :menusid="menusid" :item="item" :itemExtra="item.menuItemUnitList[0]" @showDishDetial="showDishDetial" @showAttribute="showAttribute" @drop="drop($event)" @openKeyboard="openKeyboard"></dish>
                    </li>
                </ul>
            </div>
            <ul v-show="showSearchList" class="list-item">
                <li v-for="(item,index) in searchData" :key="index">
                    <dish :menusid="item.menusid" :item="item" :itemExtra="item.menuItemUnitList[0]" @showDishDetial="showDishDetial" @showAttribute="showAttribute" @drop="drop($event)" @openKeyboard="openKeyboard"></dish>
                </li>
            </ul>
            <div class="no-search-list" v-show="showNoResult"><img src="../../assets/search.png"><p>没有搜索结果</p></div>
        </div>
    </div>
</template>
<script>
    import Vue from 'vue'
    import Search from '@/components/search/search.vue'
    import dish from '@/vistitorComponents/dish/dish.vue'
    export default {
        props : {
            lists : {
                type: Array
            },
            commonDishesList : {
                type: Array
            },
            menusid:String
        },
        data() {
            return {
                placeholder:'输入菜品名称',
                showSearchList:false,//搜索列表
                showCommonList:true,//常点的菜
                showNoResult:false,//无菜品
                commonDishes:[],
                searchText:'',
                textDis:"取消"
            }
        },
        computed: {
            searchData: function() {
                let foods=[];
                let distinctFoods = [];
                let _text = this.searchText;
                if(_text){
                    this.lists.forEach(function(good){
                        good.menuItemList.forEach((food)=>{
                            food['menusid'] = good.fsmenuclsid;
                            if(food.fsitemname){
                                if(food.fsitemname.indexOf(_text) > -1){
                                    if(distinctFoods[food.fsitemname]==undefined){
                                        distinctFoods[food.fsitemname]=food.fsitemname;
                                        foods.push(food)
                                    }
                                }
                            }
                        })
                    });
                }
                if(foods.length==0){
                    this.showNoResult = true;
                    this.showSearchList = false;
                }else{
                    this.showNoResult = false;
                    this.showSearchList = true;
                }
                return foods;
            }
        },
        methods : {
            keyup() {
                this.showCommonList = false;
                this.textDis = "确定";
                if(this.searchText){
                    this.showNoResult = false;
                    this.showSearchList = true;
                }else{
                    this.showNoResult = true;
                    this.showSearchList = false;
                    this.textDis = "取消";
                }
            },
            cancel() {
                this.$emit('closeSearchBox');
                this.showSearchList = false;
                this.showCommonList = true;
                this.showNoResult = false;
                this.searchText = '';
                this.textDis = "取消";
            },
            showDishDetial(item){
                this.$emit('showDishDetial',item);
            },
            showAttribute(item){
                this.$emit('showAttribute',item);
            },
            drop(target){
                this.$emit('drop',target);
            },
            openKeyboard(obj){
                this.$emit('openKeyboard',obj);
            }
        },
        components : {
            Search,
            dish
        }
    }
</script>
<style lang="less">
    @import './searchDishes.less';
</style>