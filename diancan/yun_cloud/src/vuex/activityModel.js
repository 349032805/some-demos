const activityModel = {
    state: {
        ACTIVITY_SHOW : false,
        MONEY : '',
        ACTIVITY_TIP : {}
    },
    mutations: {
        SAVE_ACTIVITY_SHOW (state,boolean) {
            state.ACTIVITY_SHOW = boolean
        },
        SAVE_ACTIVITY_TIP (state,params) {
            state.ACTIVITY_TIP = params
        },
        SAVE_MONEY(state,money) {
            state.MONEY = money
        }
    }
}


export { activityModel }