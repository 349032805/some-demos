const menusModel = {
    state: {
        MENUS_LIST: [],
        ACTIVITY_LIST: {},
        NO_OPEN: false,
        SALED_FOODS: [],
        COUNT_SIGN : true
    },
    mutations: {
        CHANGE_COUNT_FLAG (state,boolean) {
           state.COUNT_SIGN = boolean
        },
        REDUCE_MENUS_LIST_COUNT(state, params) {
            params.forEach((item, index) => {
                let menuIndexList = item.info.menuIndexList
                menuIndexList.forEach((item2, index2) => {
                    let [outIndex, innerIndex] = item2.split('_')
                    let MENUS_LIST_OUT = state.MENUS_LIST[outIndex]
                    if (MENUS_LIST_OUT.fsmenuclsid != null) {
                        MENUS_LIST_OUT.categoryCount -= item.count
                    }
                    let MENUS_LIST_INNER = MENUS_LIST_OUT.menuItemList[innerIndex];
                    MENUS_LIST_INNER.menuCount -= item.count
                    if(MENUS_LIST_INNER.isShowUnit == 1) {
                       MENUS_LIST_INNER.fiiscout = 1
                    }else{
                      MENUS_LIST_INNER.menuItemUnitList.forEach((unit,unitIndex)=>{
                          if(unit.fsorderunit == item.info.fsorderunit) {
                              unit.fistatusUnit  = 2
                          }
                      })
                    }
                })
            })
        },
        SAVE_SALED_FOODS(state, params) {
            state.SALED_FOODS = params
        },
        SAVE_NO_OPEN(state, boolean) {
            state.NO_OPEN = boolean
        },
        //保存活动信息
        SAVE_ACTIVITY_LIST(state, params) {
            state.ACTIVITY_LIST = params
        },
        SAVE_MENUS_LIST(state, params) {
            state.MENUS_LIST = params
        },
        CLEAR_MENUS_LIST_COUNT(state) {
            state.MENUS_LIST.forEach(item => {
                item.categoryCount = 0;
                item.menuItemList.forEach(item2 => {
                    item2.menuCount = 0
                })
            })
        },
        ADD_ITEM_COUNT(state, menuIndexList) {
            menuIndexList.forEach(item => {
                let [outIndex, innerIndex] = item.split('_')
                let MENUS_LIST_OUT = state.MENUS_LIST[outIndex]
                if (MENUS_LIST_OUT.fsmenuclsid != null) {
                    MENUS_LIST_OUT.categoryCount += 1
                }

                MENUS_LIST_OUT.menuItemList[innerIndex].menuCount += 1
            })
        },
        REDUCE_ITEM_COUNT(state, menuIndexList) {
            menuIndexList.forEach(item => {
                let [outIndex, innerIndex] = item.split('_')
                let MENUS_LIST_OUT = state.MENUS_LIST[outIndex]
                if (MENUS_LIST_OUT.fsmenuclsid != null) {
                    MENUS_LIST_OUT.categoryCount -= 1
                }
                if (MENUS_LIST_OUT.menuItemList[innerIndex].menuCount > 0) {
                    MENUS_LIST_OUT.menuItemList[innerIndex].menuCount -= 1
                }

            })
        }

    }
}


export { menusModel }