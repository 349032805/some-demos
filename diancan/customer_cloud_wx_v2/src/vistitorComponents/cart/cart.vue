<template>
    <div class="cart-box">
        <div class="cart clearfix">
            <button type="button" :class="{'btn-primary':true,'btn-grey':total.totalCount<=0}" @click="nextClick()">选好了</button>
            <div class="cart-num-box" @click="listToggle()">
                <span class="num" v-if="total.totalCount>0">{{total.totalCount}}</span>
            </div>
            <p v-show="total.totalCount<=0" class="no-dishes">还没添加菜品</p>
            <p class="price" v-show="total.totalCount>0">¥{{total.totalPrice.totalBargainPrice}} <span class="favorable-price" v-show="total.totalPrice.total>0&&total.totalPrice.totalBargainPrice!=total.totalPrice.total">¥{{total.totalPrice.total}}</span></p>
        </div>
        <div class="cart-list">
            <div>
                <div :class="{'discount-text':true,'showDiv':activityPrice.activityShowText}">•&nbsp;&nbsp;
                    <span v-if="activityPrice.type==202">下单减{{activityPrice.bargainValue}}元，再买<span class="orange">{{activityPrice.minusMoney}}元</span>可减<span class="orange">{{activityPrice.bargainValue}}元</span>
                    </span>
                    <span v-if="activityPrice.type==203">再买<span class="orange">{{activityPrice.minusMoney}}元</span>可享受<span class="orange">{{activityPrice.bargainValue}}折</span></span>
                    </div>
                <!--<div :class="{'discount-text':true,'showDiv':activityPrice.activityShowText&&activityPrice.type==203}">•&nbsp;&nbsp;再买<span class="orange">{{activityPrice.minusMoney}}元</span>可享受<span class="orange">{{activityPrice.bargainValue}}折</span></div>-->
            </div>
            <!--<transition name="slideUp">-->
                <div v-show="listShow">
                    <div class="list-header clearfix">
                        <span class="empty pull-right" @click="setEmptyCart()"><span class="icon-remove"></span>清除</span>
                        <h1 class="title">已选菜品({{total.totalCount}}份)</h1>
                    </div>
                    <div class="list-content" ref="cartFoodList">
                        <ul>
                            <li class="food" v-for="(food,index) in total.cartFoods" :key="index">
                                <div class="main-content" v-if="food.isShowUnit==1">
                                    <div class="price pull-right">
                                        <span>¥{{((food.menuItemUnitList[0].bargainPrice?food.menuItemUnitList[0].bargainPrice:food.menuItemUnitList[0].fdsaleprice) * food.count).toFixed(2)}}</span>
                                        <span class="now-price" style="display:block;padding-top:3px;" v-if="food.menuItemUnitList[0].bargainPrice>0">¥{{(food.menuItemUnitList[0].fdsaleprice*food.count).toFixed(2)}}</span>
                                    </div>
                                    <span class="name">{{food.fsitemname}}</span>
                                    <div class="cartcontrol-wrapper">
                                        <numberControl :item="food" :itemExtra="food.menuItemUnitList[0]" :iscart="true" @drop="drop($event)" @openKeyboard="openKeyboard"></numberControl>
                                    </div>
                                </div>
                                <ul v-if="food.isShowUnit==0">
                                    <li class="food" v-for="(attr,attrIndex) in food.menuItemUnitList" v-if="attr.count>0" :key="attrIndex">
                                        <div class="main-content">
                                            <div class="price pull-right">
                                                <span>¥{{(nowPrice(attr,1) * attr.count).toFixed(2)}}</span>
                                                <span class="now-price" style="display:block;padding-top:3px;" v-if="attr.bargainPrice>0">¥{{(nowPrice(attr,2)*attr.count).toFixed(2)}}</span>
                                            </div>
                                            <span class="name">{{food.fsitemname}}
                                                <span class="attr-name">
                                                    ({{attr.fsorderunit}}<span v-for="(ask,askIndex) in attr.askList" :key="askIndex">
                                                        <span v-for="(subAsk,subIndex) in ask.menuItemAskOutVoList" :key="subIndex" v-show="subAsk.checked">/ {{subAsk.fsaskname}}</span></span>)
                                                </span>
                                            </span>
                                            <div class="cartcontrol-wrapper">
                                                <numberControl :item="food" :itemExtra="attr" :iscart="true" @drop="drop($event)" @openKeyboard="openKeyboard"></numberControl>
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            <!--</transition>-->
        </div>
        <div class="backdrop" v-show="listShow" @click="listShow = false"></div>
    </div>
</template>
<script>
    import Vue from 'vue'
    import {MessageBox} from 'mint-ui';
    Vue.component(MessageBox.name, MessageBox);
    import BScroll from 'better-scroll'
    import numberControl from '@/vistitorComponents/numControl/numControl.vue'
    export default {
        props : {
            total : Object,
            activityPrice : Object,
            activityShowText : {
                type : Boolean,
                default : false
            }
        },
        data(){
            return {
                listShow:false
            }
        },
        components : {
            numberControl
        },
        watch : {
            total : function (val,oldval){
                if(val.totalCount==0){
                    this.listShow = false;
                }
            }
        },
        methods :{
            nowPrice(food,type){//type==1有优惠，type==2无优惠
                let price = {
                    fdsaleprice: food.fdsaleprice,
                    bargainPrice: food.bargainPrice
                }
                if(food.askList){
                    food.askList.forEach(function(ask){
                        ask.menuItemAskOutVoList.forEach(function(subAsk){
                            if(subAsk.checked){
                                if(price.bargainPrice){
                                    price.bargainPrice += subAsk.fdaddprice;
                                }
                                price.fdsaleprice += subAsk.fdaddprice;
                            }
                        })
                    })
                }
                let resultPrice = 0;
                if(type==1){
                    resultPrice = food.bargainPrice?price.bargainPrice:price.fdsaleprice;
                }else{
                    resultPrice = price.fdsaleprice;
                }
                return resultPrice;
            },
            listToggle() {
                if (!this.total.cartFoods.length) {
                    return;
                }
                this.listShow = !this.listShow;
                if (this.listShow) {
                    this.$nextTick(() => {
                        if (!this.foodlistScroll) {
                            this.initCartScroll();
                        } else {
                            this.foodlistScroll.refresh();
                        }
                    })
                }
            },
            //初始化购物车列表
            initCartScroll() {
                this.foodlistScroll = new BScroll(this.$refs.cartFoodList, {
                    click: true
                });
            },
            nextClick(){
                if(this.total.cartFoods.length>0){
                    localStorage.setItem('cartFoods',JSON.stringify(this.total.cartFoods));
                    this.$router.push({name:'confirmOrder'});
                }
            },
            //清空购物车
            setEmptyCart() {
                MessageBox.confirm('确定要清除购物车吗?').then(action => {
                    this.total.cartFoods.forEach((food) => {
                        if(food.isShowUnit == 0){//有规格
                            food.menuItemUnitList.forEach(function(val){
                                if(val.count){
                                    val.count = 0;
                                }
                                //改变选中状态
                                if(val.askList){
                                    val.askList.forEach(function(ask){
                                        ask.menuItemAskOutVoList.forEach(function(subAsk){
                                            if(subAsk.checked){
                                                subAsk.checked = false;
                                            }
                                        })
                                    })
                                }
                            })
                        }
                        if(food.count && food.isShowUnit == 1){
                            food.count = 0
                        }
                    })
                    this.$emit('clearCart');//清空相同类别的菜品数量
                },err=>{
                    console.log(err);
                });
            },
            drop(target){
                this.$emit('drop',target);
            },
            openKeyboard(obj){
                this.$emit('openKeyboard',obj);
            }
        }
    }
</script>
<style lang="less">
    @import './cart.less';
</style>
