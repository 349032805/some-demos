import { mapState } from 'vuex'
import Bzhe from '@/assets/BZhe.png'
import BZeng from '@/assets/BZeng.png'
const itemDish = {
    props: {
        itemDish: {
            type: Object,
            default: () => {
                return {}
            }
        }
    },
    watch: {
        itemDish: {
            handler: function (val, oldval) {
                this.fiIsHot = val.fiIsHot == 1? true:false
                this.fiisnew = val.fiisnew == 1? true:false
                let priceFlag = "";
                let bragainPriceFlag = "";
                let menuItemUnitList = val.menuItemUnitList
                if (menuItemUnitList) {
                    for (let i = 0; i < menuItemUnitList.length; i++) {
                        if(menuItemUnitList[i].bargainPrice) {
                            priceFlag = menuItemUnitList[i].fdsaleprice;
                            bragainPriceFlag = menuItemUnitList[i].bargainPrice
                            break;
                        }
                        if(priceFlag == "" || menuItemUnitList[i].fdsaleprice<priceFlag) {
                            priceFlag = menuItemUnitList[i].fdsaleprice
                            bragainPriceFlag = menuItemUnitList[i].bargainPrice
                        }

                        if (menuItemUnitList[i].menuItemActivity) {
                            let menuItemActivity = menuItemUnitList[i].menuItemActivity
                            this.bargainName = menuItemActivity.bargainName
                            this.bargainNameFlag = true
                            if(menuItemActivity.bargainKind == 301){
                                this.activityImg = BZeng
                            }else{
                                 this.activityImg = Bzhe
                            }
                        }
                    }
                    if(!bragainPriceFlag) {
                        this.firstPrice = priceFlag
                    }else{
                        this.firstPrice = bragainPriceFlag;
                        this.secondPrice = priceFlag;
                        this.secondPriceFlag = true;
                    }
                }
            },
            deep: true,
            immediate: true
        },
    },
    data() {
        return {
            fiisnew : false,
            fiIsHot : false,
            bargainName: "",
            bargainNameFlag: false,
            activityImg : "",
            firstPrice : "",
            secondPrice : "",
            secondPriceFlag : false
        }
    },

    computed: {
        ...mapState({
            MENUS_LIST: state => state.menusModel.MENUS_LIST
        }),
        menuCount() {
            let menuCount = 0;
            let menuIndex = this.itemDish.menuIndexList[0]
            let menuIndexArray = menuIndex.split("_")
            let [outIndex, innerIndex] = menuIndexArray
            menuCount = this.MENUS_LIST[outIndex].menuItemList[innerIndex].menuCount
            return menuCount
        },
        startMoney () {
            if(this.itemDish.isShowUnit == 1) {
                return false
            }else {
                if(this.itemDish &&　this.itemDish.menuItemUnitList.length > 1 && this.secondPriceFlag == false) {
                    return true
                }else{
                    return false
                }
            }
        }
    },
    methods: {
        enterDishDetail() {
            let { outIndex, innerIndex } = this.$attrs
            sessionStorage.outIndex = outIndex
            sessionStorage.innerIndex = innerIndex
            sessionStorage.itemDish = JSON.stringify(this.itemDish)
            this.$router.push({ name: 'dishDetail' })
        }
    },


}

export { itemDish }