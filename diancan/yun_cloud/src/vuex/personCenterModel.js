const personCenterModel = {
    state: {
        USER_IMG : "",
        BIND_PHONE : "",
        USER_NAME : "",
        USER_SEX : "",
        IS_PAGE : ""
    },
    mutations: {
        CHANGE_USERIMG (state,image) {
            state.USER_IMG = image
        },
        CHANGE_BIND_PHONE (state,phone) {
            state.BIND_PHONE = phone
        },
        CHANGE_USER_NAME (state,userName) {
            state.USER_NAME = userName
        },
        CHANGE_USER_SEX (state,sex) {
            state.USER_SEX = sex
        },
        SAVE_IS_PAGE(state,shopId) {
            state.IS_PAGE = shopId
        }
    }    
}


export { personCenterModel }