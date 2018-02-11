//打造爆炸款提示
import { Toast } from 'mint-ui';
function activityBuygiftItemListTip(_activityBuygiftItemList, fsitemid, fsorderunit, GOODS_INFO) {
    let _dishesCount = 0;
    let _fdsaleprice = 0
    if (_activityBuygiftItemList) {
        _activityBuygiftItemList.forEach((item, index) => {
            let _kind = item.bargainKind;//活动类型
            let _bargainValue = item.bargainValue;//赠菜，减价，折扣，购菜
            let _fullMoney = item.fullMoney;//满价
            let _minusCount = 0
            if (item.itemId == fsitemid && item.orderUnit == fsorderunit) {
                GOODS_INFO.forEach(item2 => {
                    if (item2.info.fsitemid == fsitemid && item2.info.fsorderunit == fsorderunit) {
                        _dishesCount += item2.count
                        _fdsaleprice = item2.info.fdsaleprice
                    }
                })
                if (_kind == 301) {//301=买几赠几(变价) / 302=第几件半价(折扣) ,
                    //console.log("discount:::::"+_dishesCount)
                    if (_dishesCount % item.saleQty != 0) {//如果点了菜 //如果份数小于规定份数
                        _minusCount = item.saleQty - _dishesCount % item.saleQty;
                        let _text301 = item.dishName + '买' + item.saleQty + '送' + item.saleQtyGift + '，再买' + _minusCount + item.orderUnit + '送' + item.saleQtyGift + item.orderUnit;
                        Toast({
                            message: _text301,
                            duration: 2000
                        });
                    }

                } else if (_kind == 302) {
                    if (_dishesCount > 0) {//如果点了菜
                        let _resultQty = parseInt(item.saleQty + item.saleQtyGift);
                        if (_dishesCount % _resultQty != 0) {//如果份数小于规定份数（第四件5折则，saleQty=3,saleQtyGift=1）
                            _minusCount = _resultQty - _dishesCount % _resultQty;
                            let _text302 = item.dishName + '第' + _resultQty + item.orderUnit + item.bargainValue / 10 + '折，再买' + _minusCount + item.orderUnit + '打' + item.bargainValue / 10 + '折';
                            Toast({
                                message: _text302,
                                duration: 2000
                            });
                        }

                    }
                }
            }
        })
    }
}

export   {activityBuygiftItemListTip}