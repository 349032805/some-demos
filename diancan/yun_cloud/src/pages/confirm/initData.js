export function initData() {
    let box = {};
    box.displayDishList = [];
    box.newDishList = [];

    let cartFoods = sessionStorage.getItem('cartFoods');
    cartFoods = JSON.parse(cartFoods);

    let existSendList = [];

    //封装成展示需要的数据,适应组件的字段名称
    cartFoods.forEach(item => {
        let info = item.info;

        let dish = {};
        dish.fsitemname = info.fsitemname;
        dish.number = item.count;
        dish.unit = info.fsorderunit;

        if(info.zhe){
            dish.isDiscount = 2;
        }

        //finallbargainPrice现价总和,finallPrice原价总和
        dish.price = info.finallPrice;
        dish.discountPrice = info.finallbargainPrice;

        if (info.groupAsk.length > 0) { //如果有多种做法
            dish.orderMenuItemRequirementOutVoList = []

            info.groupAsk.forEach(doMode => {
                let obj = {};
                obj.fsaskname = doMode.fsaskname;
                dish.orderMenuItemRequirementOutVoList.push(obj);
            });
        }
        dish.orderGiveMenuitemOutVoList = [];
        if(item.sendGoods){
            let sendGoods = item.sendGoods;
            let obj = {};
            obj.fsitemname = sendGoods.dishName;
            obj.number = sendGoods.count;
            obj.price = info.fdsaleprice;
            obj.isGive = 1;
            dish.orderGiveMenuitemOutVoList.push(obj);
        }
        box.displayDishList.push(dish);

        if (item.sendGoods) {
            let sendGoods = item.sendGoods;
            let obj = {};
            obj.fsitemid = sendGoods.itemId;
            obj.dishName = sendGoods.dishName;
            obj.unit = sendGoods.orderUnit;
            obj.count = sendGoods.count;
            obj.isGive = 1; //1满赠菜
            //赠送的菜品的打包盒单价和打包盒数量
            obj.fdBoxPrice = info.fdBoxPrice;
            obj.fiBoxQty = info.fiBoxQty;

            existSendList.push(obj);
            window.sessionStorage.setItem('existSendList', JSON.stringify(existSendList));
        }

    });


    //封装成下单需要的数据
    cartFoods.forEach(item => {
        let info = item.info;

        let dish = {};

        dish.fsitemid = info.fsitemid;
        dish.dishname = info.fsitemname;
        dish.count = item.count; //count在item里面
        dish.unit = info.fsorderunit;

        dish.fdBoxPrice = info.fdBoxPrice;
        dish.fiBoxQty = info.fiBoxQty;
        dish.isGive = 0; //0普通菜


        //finallbargainPrice现价总和,finallPrice原价总和
        dish.originTotalPrice = info.finallPrice;
        dish.nowTotalPrice = info.finallbargainPrice;

        if (item.info.groupAsk.length > 0) { //如果有多种做法
            let doIdList = []; //做法id
            let doNameList = []; //做法名称
            let doPrice = 0;

            info.groupAsk.forEach(doMode => {
                doIdList.push(doMode.fsaskid);
                doNameList.push(doMode.fsaskname);
                doPrice += doMode.fdaddprice;
            });

            dish.doIdList = doIdList;
            dish.doNameList = doNameList;
            dish.doNameStr = doNameList.join("/");
        }

        box.newDishList.push(dish);
    });

    window.sessionStorage.setItem('displayDishList', JSON.stringify(box.displayDishList));
    window.sessionStorage.setItem('newDishList', JSON.stringify(box.newDishList));

    // 从sessionStorage获取首页的原价和现价
    // this.indexOriginpPrice = finallPrice;
    // this.indexNowPrice = 
    // console.log("首页的原价:" + this.indexNowPrice + "|首页的现价:" + this.indexNowPrice);
    return box;
};