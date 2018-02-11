function calculateItem(activity, price, foodInfo, count, indexItem, Vue) {
    Vue.discount = false;
    let activityBuygiftItemListResponse = activity ? activity.activityBuygiftItemListResponse : null;
    if (activityBuygiftItemListResponse) {
        let allTotal = price;
        let _dishesCount = 0;
        let _fdsaleprice = 0;
        activityBuygiftItemListResponse.forEach((item, index) => {
            let _kind = item.bargainKind;//活动类型
            let _bargainValue = item.bargainValue;//赠菜，减价，折扣，购菜
            let _fullMoney = item.fullMoney;//满价
            if (foodInfo.fsitemid == item.itemId && foodInfo.fsorderunit == item.orderUnit) {
                _dishesCount = count
                _fdsaleprice = foodInfo.fdsaleprice
                if (_kind == '302') {
                    if (_dishesCount > 0) {
                        let _resultQty = parseInt(item.saleQty + item.saleQtyGift);
                        let _orderCount = parseInt(_dishesCount / _resultQty);
                        // console.log("__resultQty" + _resultQty)
                        // console.log("_dishesCount" + _dishesCount)
                        // console.log('_orderCount' + _orderCount)
                        if (_dishesCount > 0 && _orderCount > 0) {
                            Vue.discount = true;
                            Vue.$store.commit('ADD_GOODS_INFO_ZHE', indexItem)
                            let _nowPrice = Number((_dishesCount - _orderCount) * _fdsaleprice + _orderCount * item.bargainValue / 100 * _fdsaleprice).toFixed(2)
                            allTotal = Number(allTotal) - (Number(_dishesCount * _fdsaleprice) - Number(_nowPrice))
                            if (allTotal < 0) {
                                allTotal = 0
                            }
                        } else {
                            Vue.$store.commit('DELEA_GOODS_INFO_ZHE', indexItem)
                        }
                    }
                }
            }
        })
        return allTotal
    }
    return price
}


export { calculateItem }