const shareModel = {
    state: {
       sharem : false
        
    },
    mutations: {
    	 CHANGE_SHARE_MODEL(state, polyload) {
            state.sharem = polyload
            // alert(state.sharem+"-----")
        },
    }
}


export { shareModel }