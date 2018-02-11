
import { money } from '@/utils/money.js'
import { Toast } from 'mint-ui';

function calculate(activity, concatArray, GOODS_INFO, price, flagdis, Vue) {      //301=买几赠几(变价) / 302=第几件半价(折扣) ,
    let activityBuygiftItemListResponse = activity.activityBuygiftItemListResponse
    if (activityBuygiftItemListResponse) {
        let allTotal = price
        //console.log('allTotal' + allTotal)
        let _dishesCount = 0;
        let _fdsaleprice = 0
        activityBuygiftItemListResponse.forEach((item, index) => {
            let _kind = item.bargainKind;//活动类型
            let _bargainValue = item.bargainValue;//赠菜，减价，折扣，购菜
            let _fullMoney = item.fullMoney;//满价
            concatArray.forEach((item2, index2) => {
                if (item2.fsitemid == item.itemId && item2.fsorderunit == item.orderUnit) {
                    _dishesCount = item2.count
                    _fdsaleprice = item2.fdsaleprice
                    //console.log(_dishesCount, _fdsaleprice)
                    if (_kind == '302' && flagdis == 1) {
                        if (_dishesCount > 0) {
                            let _resultQty = parseInt(item.saleQty + item.saleQtyGift);
                            let _orderCount = parseInt(_dishesCount / _resultQty);
                            console.log("__resultQty" + _resultQty)
                            console.log("_dishesCount" + _dishesCount)
                            console.log('_orderCount' + _orderCount)
                            if (_dishesCount > 0 && _orderCount > 0) {
                                let _nowPrice = Number((_dishesCount - _orderCount) * _fdsaleprice + _orderCount * item.bargainValue / 100 * _fdsaleprice).toFixed(2)
                                allTotal = Number(allTotal) - (Number(_dishesCount * _fdsaleprice) - Number(_nowPrice))
                                if (allTotal < 0) {
                                    allTotal = 0
                                }
                            }
                        }
                    }
                    if (_kind == '301' && flagdis == 1) {
                        let _orderCount = parseInt(_dishesCount / item.saleQty);
                        if (_dishesCount > 0 && _orderCount > 0) {
                            let _resultDiscount = _fdsaleprice * _orderCount * item.saleQtyGift;
                            if (_resultDiscount <= 0.005) {
                                _resultDiscount = 0;
                            }
                            // console.log("_dishesCount" + _dishesCount)
                            // console.log('_orderCount' + _orderCount)
                            for (let i = GOODS_INFO.length - 1; i < GOODS_INFO.length; i--) {

                                if (GOODS_INFO[i].info.fsitemid == item2.fsitemid) {
                                    console.log(GOODS_INFO[i])
                                    Vue.$store.commit('CLEAR_SEND_GOODS_INFO', item2.fsitemid)
                                    let { itemId, dishName, orderUnit } = item
                                    let count = _orderCount
                                    Vue.$store.commit('ADD_SEND_GOODS_INFO', { index: i, sendGoods: { itemId, dishName, orderUnit,count } });
                                    Vue.$store.commit('CHANGE_COUNT_FLAG',false)
                                    break;
                                }
                            }
                            allTotal = Number(Number(allTotal).toFixed(2)) + Number(Number(_resultDiscount.toFixed(2)));
                        }
                    }
                }
            })
        })
        return Number(allTotal).toFixed(2)
    }
}


export { concat, calculate }