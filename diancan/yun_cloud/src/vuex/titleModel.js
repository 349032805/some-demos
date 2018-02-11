const titleModel = {
    state: {
        TITLE:'',
        CARD_SHOW : true,
        CART_BTN_COLOR : true
    },
    mutations : {
        SAVE_TITLE(state, title) {
            state.TITLE = title
        },
        CHANGE_CARD_SHOW (state,boolean) {
            state.CARD_SHOW = boolean
        },
        CHANGE_CART_BTN_COLOR (state,boolean) {
            state.CART_BTN_COLOR = boolean
        }
    }
}

export { titleModel }