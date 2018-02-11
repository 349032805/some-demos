/*
* created 变量和过程说明
  this.shopId => 从url地址params拿到shopid 
  this.tableId => 从url 地址query拿到tableId
  localStorage.token =>把token传入到localStorage里，进行全局共用
  --------------------------
* data 数据说明
  isHide 在滚动时是否隐藏头部  deafult : false  
  currentIndex  滚动时当前下标
  tableId  桌号ID
  shopId  店铺ID
  goods 商品列表
  activityList 头部活动列表
*/
import BScroll from 'better-scroll'
import { getShopActivityList } from '@/config/api.js'
import { mapState } from 'vuex'
let layoutMixin = {
    
    data() {
        return {
            isHide: false,
            currentIndex: "",
            menuListHeight: [],
            listHeight: [],
            eatWrapperWidth: 0,
            menuHeaderWidth: 0
        }
    },
    computed: {
        ...mapState({
            MENUS_LIST: state => state.menusModel.MENUS_LIST,
        })
    },
    methods: {
        count (index) {
             return this.MENUS_LIST[index].categoryCount
        },
        enterSearchDishes () {
            this.$router.push({name:'searchDishes'})
        }
    }

}


export { layoutMixin }