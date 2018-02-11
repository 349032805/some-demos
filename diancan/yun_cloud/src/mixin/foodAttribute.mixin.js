import Vue from 'vue'
import { mapState } from 'vuex'
import { activityBuygiftItemListTip } from '@/page/me/activityToast.js'
const foodAttribute = {
    computed: {
        ...mapState({
            GOODS_INFO: state => state.foodAttrModel.GOODS_INFO,
            NO_OPEN: state => state.menusModel.NO_OPEN,
            ACTIVITY_LIST: state => state.menusModel.ACTIVITY_LIST
        })
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
        showFoodAttribute(e) {
            let { menuindexlist } = e.target.dataset
            if (!menuindexlist) {
                
                return
            }

            this.$store.commit('CHANGE_COUNT_FLAG',true);
            let menuIndexArrays = menuindexlist.split(',')
            let menuIndexArray = menuIndexArrays[0].split("_")
            let [outIndex, innerIndex] = menuIndexArray
            let foodAttr = this.MENUS_LIST[outIndex].menuItemList[innerIndex];
            if (foodAttr.fiiscout == 1 || this.NO_OPEN == true) {
                return
            }
            if (e.target.dataset.flag == 'multiBtn') {
                if (foodAttr.menuItemAskGroupOutVoList) {
                    foodAttr.menuItemAskGroupOutVoList.forEach(outItem => {
                        outItem.menuItemAskOutVoList.forEach(innerItem => {
                            Vue.set(innerItem, 'fidefault', 0)
                        })
                    });
                }
                this.$store.commit('SAVE_FOOD_ATTR', foodAttr);
                this.$store.commit('CHANGE_FA_MODEL', true);
            } else if (e.target.dataset.flag == 'singleBtn') {
                let fsorderunit, bargainPrice, fdsaleprice, groupAsk = [], count = 1, fdBoxPrice = 0, fiBoxQty = 0,finallbargainPrice=0,finallPrice=0
                let { fsitemname, fsitemid, menuIndexList } = foodAttr
                let menuItemUnitInfo = foodAttr.menuItemUnitList[0];
                ({ fsorderunit, bargainPrice, fdsaleprice } = menuItemUnitInfo);
                ({ fdBoxPrice, fiBoxQty } = menuItemUnitInfo);
                bargainPrice = bargainPrice ? bargainPrice : fdsaleprice;
                //console.log( fsitemname, fsitemid,fsorderunit, bargainPrice, fdsaleprice, groupAsk)
                //原价+口味(未精度)
                let groupAskPriceTotal = 0
                let fdsalepriceTotal = fdsaleprice
                //打折价+口味(未精度) 如果没有打拆价就是 原价+口味价
                let bargainPriceTotal = bargainPrice ? bargainPrice : fdsaleprice
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
                    count
                }
                if (this.$parent.$refs.balls) {
                    this.$parent.$refs.balls.drop(e);
                }
                this.$store.commit('ADD_ITEM_COUNT', menuIndexList)
                if (this.GOODS_INFO.length == 0) {
                    this.$store.commit('SAVE_GOODS_INFO', goodsInfo)
                    this.activityTipMethods(fsitemid, fsorderunit)
                    return
                }
                for (let i = 0; i < this.GOODS_INFO.length; i++) {
                    let GoodInfo = this.GOODS_INFO[i].info
                    let goodInfo = goodsInfo.info
                    console.log(goodInfo)
                    if (GoodInfo.fsitemid== goodInfo.fsitemid && GoodInfo.fsorderunit== goodInfo.fsorderunit && JSON.stringify(GoodInfo.groupAsk) == JSON.stringify(goodInfo.groupAsk)) {
                        this.$store.commit('CHANGE_GOODS_INFO', i)
                        this.activityTipMethods(fsitemid, fsorderunit)
                        return
                    }
                }
                this.$store.commit('SAVE_GOODS_INFO', goodsInfo)
                this.activityTipMethods(fsitemid, fsorderunit)
            } else if (e.target.dataset.flag == 'singleRdBtn') {
                let { fsitemid, menuIndexList ,fsorderunit} = foodAttr
                this.$store.commit('REDUCE_ITEM_COUNT', menuIndexList)
                this.$store.commit('REDUCE_ID_GOODS_INFO_COUNT', fsitemid)
                this.activityTipMethods(fsitemid, fsorderunit)
            }
        }
    }
}

export { foodAttribute }