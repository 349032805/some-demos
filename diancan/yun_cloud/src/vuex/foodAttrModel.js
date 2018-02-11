
let flag = false
function loop(state, params) {
    flag = false
    state.GOODS_INFO.forEach((item, index) => {
        params.forEach((item2, index2) => {
            let info = item.info
            if (info.fsitemid == item2.fsitemid && info.fsorderunit == item2.fsorderunit) {
                state.GOODS_INFO.splice(index, 1)
                state.GOODS_INFO_MONEY.splice(index,1)
                state.COUNT_FLAG.push(item)
                flag = true
            }
            
            if(!flag) {
               return 
            }else{
               loop(state, params)
            }
        })
    })
}

const foodAttrModel = {

    state: {
        IS_SHOW_FA: false,
        FOOD_ATTR: {},
        GOODS_INFO: [],
        GOODS_INFO_MONEY: [],
        COUNT_FLAG: []
    },
    mutations: {
        ADD_GOODS_INFO_ZHE (state,index) {
            state.GOODS_INFO[index].info.zhe = true
        },
        DELEA_GOODS_INFO_ZHE (state,index) {
             state.GOODS_INFO[index].info.zhe = false
        },
        SALED_CLEAR_GOODS_INFO(state, params) {
             state.COUNT_FLAG = []
             loop(state, params)
        },
        CHANGE_FA_MODEL(state, boolean) {
            state.IS_SHOW_FA = boolean
        },
        SAVE_FOOD_ATTR(state, params) {
            state.FOOD_ATTR = params
        },
        CHANGE_UNIT(state, index) {
            state.FOOD_ATTR.menuItemUnitList.forEach((unitItem, unitIndex) => {
                if (unitIndex == index) {
                    unitItem.fidefault = 1
                } else {
                    unitItem.fidefault = 0
                }
            })
        },
        CHANGE_ASK(state, params) {
            let { outIndex, innerIndex } = params
            let menuItemAskGroupOutVoList = state.FOOD_ATTR.menuItemAskGroupOutVoList
            menuItemAskGroupOutVoList.forEach((item, index) => {
                if (index == outIndex) {
                    let menuItemAskOutVoList = menuItemAskGroupOutVoList[index].menuItemAskOutVoList
                    let fiSingleAsk = menuItemAskGroupOutVoList[index].fiSingleAsk

                    menuItemAskOutVoList.forEach((item2, index2) => {
                        if (fiSingleAsk == 1) {
                            item2.fidefault = 0
                        }
                        if (index2 == innerIndex) {

                            if (item2.fidefault == 1) {
                                item2.fidefault = 0
                            } else {
                                item2.fidefault = 1
                            }

                        }
                    })
                }
            })
        },
        INIT_GOODS_INFO(state, params) {
            state.GOODS_INFO = params
        },
        SAVE_GOODS_INFO_MONEY(state, obj) {
            let { finallbargainPrice, index, finallPrice } = obj
            state.GOODS_INFO_MONEY[index] = obj
        },
        CLEAR_GOODS_INFO_MONEY(state) {
            state.GOODS_INFO_MONEY = []
        },
        SAVE_GOODS_INFO(state, params) {
            let goodInfo = state.GOODS_INFO
            for (let i = 0; i < goodInfo.length; i++) {
                if (goodInfo[i].info.fsitemid == params.info.fsitemid) {
                    goodInfo.splice(i, 0, params)
                    return
                }
            }
            state.GOODS_INFO.push(params)
        },
        CLEAR_SEND_GOODS_INFO(state, itemId) {
            state.GOODS_INFO.forEach(item => {
                if (item.info.fsitemid == itemId) {
                    item.sendGoods = null
                }
            })
        },
        ADD_SEND_GOODS_INFO(state, params) {
            let { index, sendGoods } = params
            state.GOODS_INFO[index].sendGoods = sendGoods
        },
        CHANGE_GOODS_INFO(state, index) {
            state.GOODS_INFO[index].count += 1
        },
        CLEAR_GOODS_INFO(state) {
            state.GOODS_INFO = []
        },
        ADD_GOODS_INFO_COUNT(state, index) {
            state.GOODS_INFO[index].count += 1
        },
        REDUCE_GOODS_INFO_COUNT(state, index) {
            if (state.GOODS_INFO[index]) {
                let count = state.GOODS_INFO[index].count
                if (count == 1) {
                    state.GOODS_INFO.splice(index, 1);
                    state.GOODS_INFO_MONEY.splice(index, 1);
                } else {
                    state.GOODS_INFO[index].count -= 1

                }
            }

        },
        REDUCE_ID_GOODS_INFO_COUNT(state, fsitemid) {
            state.GOODS_INFO.forEach((item, index) => {
                if (item.info.fsitemid == fsitemid) {
                    if (item.count == 1) {
                        state.GOODS_INFO.splice(index, 1)
                        state.GOODS_INFO_MONEY.splice(index, 1)
                    } else {
                        item.count -= 1
                    }
                }
            })
        }
    }
}


export { foodAttrModel }