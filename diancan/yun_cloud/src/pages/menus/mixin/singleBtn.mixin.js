import { mapState } from 'vuex'
const singleBtn = {
    props: {
        count: Number,
        fsitemid: {
            type: String,
            require: true
        },
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

export {singleBtn}