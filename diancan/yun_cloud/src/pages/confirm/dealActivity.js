import { Number } from "@/utils/money.js"
function dealActivity(shopActivityList, indexNowPrice) { //活动集合,原总价,已点菜品集合
    let _activityOrder = shopActivityList.listBargainClsOrderResponse;//活动顺序对象
    let _activityBuygiftItemList = shopActivityList.activityBuygiftItemListResponse;//打造爆款对象
    let _activityCutMoneyList = shopActivityList.activityCutMoneyListResponse;//生意好点对象
    let _activityNewCustomer = shopActivityList.activityNewCustomerResponse;//新客多点对象

    //将赠送的总菜品放入集合
    let sendDishList = [];

    let existSendList = sessionStorage.existSendList;
    if(existSendList){
        existSendList = JSON.parse(existSendList);
        if(existSendList.length>0){
            sendDishList = existSendList;
        }
    }


    //最终所需要展示的活动集合
    let actList = [];
    //活动过后的总价
    let finalPriceNoService = indexNowPrice;

    //买几赠几送的菜品总价
    let sendTotalPrice = 0;

    if (_activityOrder.length > 0) {
        for (let i = 0; i < _activityOrder.length; i++) {

            //新客多点 1减价/2赠菜 ,
            if (_activityOrder[i].bargainCls == 1) {

                if (_activityNewCustomer) {//判断对象是否存在
                    if (_activityNewCustomer.boolFirstActivity == 1) {//是新客，否则不是新客
                        //构造活动对象
                        let act = {};
                        act.word = "新";
                        act.name = "新用户下单";

                        let _discountType = _activityNewCustomer.discountType;//1减价，2赠菜
                        if (_discountType == 1) {//减价
                            let _discountAmountOrItemId = _activityNewCustomer.discountAmountOrItemId;//减价金额
                            _discountAmountOrItemId = Number(_discountAmountOrItemId);

                            //总价减去当前金额
                            if (finalPriceNoService > _discountAmountOrItemId) {
                                finalPriceNoService = finalPriceNoService - _discountAmountOrItemId;
                            } else {
                                finalPriceNoService = 0;
                            }

                            act.name = act.name + '立减' + _discountAmountOrItemId + '元';
                            act.price = _discountAmountOrItemId;
                        } else {
                            let dishName = _activityNewCustomer.dishName;//赠送的菜
                            act.name = act.name + '赠送【' + dishName + '】';
                            act.price = 0;

                            let obj = {};
                            obj.fsitemid = _activityNewCustomer.discountAmountOrItemId;
                            obj.dishName = dishName;
                            obj.unit = _activityNewCustomer.orderUnit;
                            obj.count = 1;
                            obj.isGive = 1; //1满赠菜
                            //赠送的菜品的打包盒单价和打包盒数量
                            obj.fdBoxPrice = _activityNewCustomer.fdBoxPrice;
                            obj.fiBoxQty = _activityNewCustomer.fiBoxQty;

                            sendDishList.push(obj);
                        }

                        actList.push(act);
                    }
                }
                //生意好点
            } else if (_activityOrder[i].bargainCls == 2) {//生意好点 201=单笔满额赠菜 / 202=单笔满减 / 203=单笔满折 / 204=加价购 
                if (_activityCutMoneyList) {//判断对象是否存在

                    _activityCutMoneyList.forEach(function (item, index) {
                        let _kind = item.bargainKind;//活动类型
                        let _bargainValue = item.bargainValue;//赠菜，减价，折扣，购菜
                        let _fullMoney = item.fullMoney;//满价

                        console.log("*************满足 生意好点活动********");

                        if (_kind == 201) {//201=单笔满额赠菜 / 202=单笔满减 / 203=单笔满折 / 204=加价购 
                            console.log("*************类型编号201********");
                            //不处理价格
                            if (finalPriceNoService >= _fullMoney) {
                                //构造对象
                                let act = {};
                                act.word = "赠";
                                act.name = "单笔满" + _fullMoney + "元赠菜【" + item.dishName + "】";
                                act.price = 0;
                                actList.push(act);

                                let obj = {};
                                obj.fsitemid = item.bargainValue;
                                obj.dishName = item.dishName;
                                obj.unit = item.orderUnit;
                                obj.count = 1;
                                obj.isGive = 1; //1满赠菜
                                //赠送的菜品的打包盒单价和打包盒数量
                                obj.fdBoxPrice = item.fdBoxPrice;
                                obj.fiBoxQty = item.fiBoxQty;

                                sendDishList.push(obj);


                            }

                        } else if (_kind == 202) { //202=单笔满减
                            console.log("*************类型编号202********");

                            if (finalPriceNoService >= _fullMoney) {
                                //构造对象
                                let act = {};
                                act.word = "减";
                                act.name = "单笔满" + _fullMoney + "元减" + item.bargainValue + "元";
                                act.price = Number(item.bargainValue);
                                act.fullMoney = _fullMoney;
                                actList.push(act);
                            }

                        } else if (_kind == 203) { //203=单笔满折
                            console.log("*************类型编号203********");
                            let discount = Number(item.bargainValue);
                            let showDiscount = discount / 10;
                            if (finalPriceNoService >= _fullMoney) {
                                //构造对象
                                let act = {};
                                act.word = "折";
                                act.name = "单笔满" + _fullMoney + "元打" + showDiscount + "折";
                                // act.price = finalPriceNoService*(100-discount)/100;
                                act.discount = discount;
                                act.fullMoney = _fullMoney;
                                actList.push(act);
                            }

                        }
                    })
                }

            } 
        }
    }

    console.log("************* 所有满足的活动********");
    console.log(actList)
    console.log("*************所有满足的活动********");

    //存放最终被展示的活动数组
    let newActList = [];

    //新用户
    actList.forEach((item, index) => {
        if (item.word == "新") {
            newActList.push(item);
        }
    })

    //去除满减，满折中的较小值，取出最大值
    let fullMinusArr = [];
    let fullDiscountArr = [];
    actList.forEach(item => {
        if (item.word == "减" && finalPriceNoService >= item.fullMoney) {
            fullMinusArr.push(item.price)
        }

        if (item.word == "折" && finalPriceNoService >= item.fullMoney) {
            fullDiscountArr.push(item.discount);
        }
    })

    //201赠送
    actList.forEach((item, index) => {
        if (item.word == "赠") {
            newActList.push(item);
        }
    })


    //202 将满减最大的对象放入新数组
    if (fullMinusArr.length > 0) {
        console.log("fullMinusArr:" + fullMinusArr);
        let maxMinus = Math.max.apply(null, fullMinusArr);
        console.log("maxMinus:" + maxMinus);
        actList.forEach((item, index) => {
            if (item.word == "减") {
                if (item.price == maxMinus) {
                    newActList.push(item);
                }
            }
        })

        finalPriceNoService = finalPriceNoService - maxMinus;
    }

    //203 将满折最大的对象放入新数组
    if (fullDiscountArr.length > 0) {
        let minDiscount = Math.min.apply(null, fullDiscountArr);
        actList.forEach((item, index) => {
            if (item.word == "折") {
                if (item.discount == minDiscount) {
                    newActList.push(item);
                }
            }
        })

        let discountMoney = 0;
        newActList.forEach((item, index) => {
            if (item.word == "折") {
                item.price = finalPriceNoService * (100 - minDiscount) / 100;
                let priceFixed = Number(finalPriceNoService - item.price).toFixed(2);
                item.price = (finalPriceNoService - Number(priceFixed)).toFixed(2);
                item.price = Number(item.price);

                discountMoney = item.price;
            }
        })
        finalPriceNoService = finalPriceNoService - discountMoney;
    }

    window.sessionStorage.setItem('finalActList', JSON.stringify(newActList));
    window.sessionStorage.setItem('sendDishList', JSON.stringify(sendDishList));

    finalPriceNoService = finalPriceNoService.toFixed(2);
    finalPriceNoService = Number(finalPriceNoService);
    if (finalPriceNoService < 0) {
        finalPriceNoService = 0;
    }

    console.log("现价总和(包括活动):" + finalPriceNoService);

    let box = {};
    box.finalActList = newActList;
    box.finalPriceNoService = finalPriceNoService;
    console.log("----------------------box")
    console.log(box)
    console.log("----------------------box")
    return box;
}

export { dealActivity }