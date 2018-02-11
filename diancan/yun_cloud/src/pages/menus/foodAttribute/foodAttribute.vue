<template>
    <div class="attr-out-box" v-show='IS_SHOW_FA'>
        <div class="food-attribute">
            <div class="food-title">
                <span class="close">
                    <span class="icon-error" @click.stop='closeFoodAttribute'></span>
                </span>{{FOOD_ATTR.fsitemname}}</div>
            <div class="food-content">
                <div class="rule-item" v-if="FOOD_ATTR.menuItemUnitList">
    
                    <p>选择规格</p>
                    <span v-for='(item,index) in FOOD_ATTR.menuItemUnitList' @click.stop='changeUnit(index)' class="attr-span">
                        <span class='guige' :class='{active:item.fidefault == 1,choose:item.fistatusUnit!=1}'>{{item.fsorderunit}}</span>
                        <!--<span class="sell-out" v-if="item.fistatusUnit!=1">售罄</span>-->
                    </span>
                    <p class="tipText">{{tipText}}</p>
                </div>
                <div class="rule-item">
                    <div v-for='(item,outIndex) in FOOD_ATTR.menuItemAskGroupOutVoList'>
                        <p>{{item.fsAskGpName}}</p>
                        <span v-for='(item2,innerIndex) in item.menuItemAskOutVoList' @click.stop.prevent="changemenuItemAskOutVoList(outIndex,innerIndex)">
                            <span class='guige' :class='{active:item2.fidefault == 1}'>{{item2.fsaskname}}</span>
                        </span>
                    </div>
                </div>
            </div>
            <div class="food-footer clearfix">
                <button class="btn-confirm" @click='confirm' :class="{gray:fistatusUnit}">添加</button>
                <div class="btn-box">
                    <span>￥{{firstPrice}}</span>
                    <span style='padding-left:3px;text-decoration: line-through;font-size:12px;color:#ccc;' v-show='flag'>￥{{secondPrice}}</span>
                </div>
            </div>
        </div>
        <div class="backdrop" v-show="IS_SHOW_FA"></div>
    </div>
</template>
<script>
import { mapState } from 'vuex'
import { activityBuygiftItemListTip } from '../model/activityToast.js'
export default {
    data() {
        return {
            util: "",
            firstPrice: "",
            secondPrice: "",
            flag: false,
            fistatusUnit: false,
            tipText: ""
        }
    },
    computed: {
        ...mapState({
            IS_SHOW_FA: state => state.foodAttrModel.IS_SHOW_FA,
            FOOD_ATTR: state => state.foodAttrModel.FOOD_ATTR,
            GOODS_INFO: state => state.foodAttrModel.GOODS_INFO,
            ACTIVITY_LIST: state => state.menusModel.ACTIVITY_LIST
        })
    },
    watch: {
        FOOD_ATTR: {
            handler: function (val, oldval) {
                let bargainPrice,
                    fdsaleprice,
                    firstPrice = 0,
                    secondPrice = 0,
                    AskGroupPrice = 0;
                if (val.menuItemUnitList) {
                    val.menuItemUnitList.forEach(item => {
                        if (item.fidefault == 1) {
                            if (item.menuItemActivity) {
                                this.tipText = `${val.fsitemname}(${item.fsorderunit})${item.menuItemActivity.bargainName}`
                            } else {
                                this.tipText = ""
                            }
                            if (item.fistatusUnit != 1) {
                                this.fistatusUnit = true
                            } else {
                                this.fistatusUnit = false;
                            }
                            this.util = item.fsorderunit;
                            ({ bargainPrice, fdsaleprice } = item);
                            if (bargainPrice) {
                                this.flag = true
                                firstPrice = bargainPrice
                                secondPrice = fdsaleprice
                            } else {
                                this.flag = false
                                firstPrice = fdsaleprice
                            }

                        }
                    });
                    if (val.menuItemAskGroupOutVoList) {
                        val.menuItemAskGroupOutVoList.forEach(outItem => {
                            outItem.menuItemAskOutVoList.forEach(innerItem => {
                                if (innerItem.fidefault == 1) {
                                    AskGroupPrice += innerItem.fdaddprice
                                }
                            })
                        })
                    }
                    this.firstPrice = Number(firstPrice + AskGroupPrice).toFixed(2)
                    this.secondPrice = Number(secondPrice + AskGroupPrice).toFixed(2)
                }
            },
            deep: true,
            immediate: true
        }
    },
    methods: {
        activityTipMethods(fsitemid, fsorderunit) {
            //判断是否有爆款活动

            let activityTip = undefined
            if (this.ACTIVITY_LIST && this.ACTIVITY_LIST.activityBuygiftItemListResponse) {
                activityTip = this.ACTIVITY_LIST.activityBuygiftItemListResponse
            }
            if (activityTip) {
                activityBuygiftItemListTip(activityTip, fsitemid, fsorderunit, this.GOODS_INFO)
            }
        },
        closeFoodAttribute() {
            this.$store.commit('CHANGE_FA_MODEL', false)
        },
        changeUnit(index) {
            this.$store.commit('CHANGE_UNIT', index)
        },
        changemenuItemAskOutVoList(outIndex, innerIndex) {
            this.$store.commit('CHANGE_ASK', { outIndex, innerIndex })
        },
        confirm() {
            if (this.fistatusUnit == true) {
                return
            }
            let fsorderunit, bargainPrice, fdsaleprice, groupAsk = [], count = 1, fdBoxPrice = 0, fiBoxQty = 0;
            let { fsitemname, fsitemid, menuItemUnitList, menuItemAskGroupOutVoList, menuIndexList } = this.FOOD_ATTR
            menuItemUnitList.forEach(utilItem => {
                if (utilItem.fidefault == 1) {
                    ({ fsorderunit, bargainPrice, fdsaleprice } = utilItem);
                    bargainPrice = bargainPrice ? bargainPrice : fdsaleprice;
                    ({ fdBoxPrice, fiBoxQty } = utilItem);
                }
            })
            if (menuItemAskGroupOutVoList) {
                menuItemAskGroupOutVoList.forEach(groupItem => {
                    groupItem.menuItemAskOutVoList.forEach(item => {
                        if (item.fidefault == 1) {
                            let { fsaskname, fsaskid, fdaddprice } = item;
                            groupAsk.push({ fsaskname, fsaskid, fdaddprice });
                        }
                    })
                })
            }
            //口味价
            let groupAskPriceTotal = 0
            groupAsk.forEach(item => {
                groupAskPriceTotal += item.fdaddprice
            })
            //原价+口味(未精度)
            let fdsalepriceTotal = fdsaleprice + groupAskPriceTotal
            //打折价+口味(未精度) 如果没有打拆价就是 原价+口味价
            let bargainPriceTotal = bargainPrice ? bargainPrice + groupAskPriceTotal : fdsaleprice + groupAskPriceTotal
            let goodsInfo = {
                info: {
                    fsitemname,
                    fsitemid,
                    fsorderunit,
                    bargainPrice,
                    fdsaleprice,
                    groupAsk,
                    menuIndexList,
                    fdsalepriceTotal,
                    bargainPriceTotal,
                    groupAskPriceTotal,
                    fdBoxPrice,
                    fiBoxQty
                },
                count,
                sendGoods: null
            }

            //关闭FA_MODEL
            this.$store.commit('CHANGE_FA_MODEL', false)
            //对应添加数加1
            this.$store.commit('ADD_ITEM_COUNT', menuIndexList)
            if (this.GOODS_INFO.length == 0) {
                this.$store.commit('SAVE_GOODS_INFO', goodsInfo)
                this.activityTipMethods(fsitemid, fsorderunit)
                return
            }
            for (let i = 0; i < this.GOODS_INFO.length; i++) {
                let GoodInfo = this.GOODS_INFO[i].info
                let goodInfo = goodsInfo.info
                if (GoodInfo.fsitemid == goodInfo.fsitemid && GoodInfo.fsorderunit == goodInfo.fsorderunit && JSON.stringify(GoodInfo.groupAsk) == JSON.stringify(goodInfo.groupAsk)) {
                    this.$store.commit('CHANGE_GOODS_INFO', i)
                    this.activityTipMethods(fsitemid, fsorderunit)
                    return
                }
            }
            this.$store.commit('SAVE_GOODS_INFO', goodsInfo)
            this.activityTipMethods(fsitemid, fsorderunit)
        }
    }
}
</script>
<style lang="less" scoped>
@import './foodAttribute.less';
</style>