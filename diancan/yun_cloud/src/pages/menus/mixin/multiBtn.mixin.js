import { mapState } from 'vuex'
const multiBtn = {
    props: {
        count: Number,
        fiiscout: Number,
        menuIndexList: Array
    },
    computed: {
        isFiiscout() {
            return this.fiiscout == 1 ? true : false
        },
        ...mapState({
            NO_OPEN : state => state.menusModel.NO_OPEN,
        })
    }
    
}

export {multiBtn}