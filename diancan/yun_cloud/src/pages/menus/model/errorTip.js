function errorTip(shopMenu, shopInfo, tableId,Vue) {
    let {onlineOrdering,isRest} =  shopInfo.model
    //当菜有问题的时候
    let errorFlag = false
    if (shopMenu.errorCode == 5000401 || shopMenu.errorCode == 5000070 || shopMenu.errorCode == 5000408) {
        errorFlag = true
        Vue.$crib.alert.show({
            title : '提示',
            content : shopMenu.message,
            onSubmit : function() {
                if(!tableId) {
                      Vue.$router.back()
                }
            }
        })
    }
    
    
    //餐厅线上暂停服务
    // if(onlineOrdering == 1) {
    //      Vue.$store.commit('SAVE_NO_OPEN', true)
    //      Vue.$crib.alert.show({
    //         title : '餐厅线上点餐暂停服务提示',
    //         content : '餐厅暂停了线上点餐服务，如有疑问请咨询餐厅',
    //     })
    // }

    //当商家休息的时候弹出提示
    if (isRest == 0 || isRest == 2) {//0餐厅休息，1，餐厅营业，2，停业时间
        Vue.$store.commit('SAVE_NO_OPEN', true)
        let closeTime = shopInfo.model.closingTimeInfo;
        let timeRange = "";
        if (closeTime) {
            timeRange = closeTime.startDate + "至" + closeTime.endDate;
        }
        let _text = "";
        if (isRest == 0) {
            _text = "当前非营业时间，";
        } else if (isRest == 2) {
            _text = '该餐厅在' + timeRange + '休息，';
        }
        Vue.$crib.alert.show({
            title : '提示',
            content : _text
        })
    }

    if(errorFlag) {
        return false
    }else{
        return true
    }
}

export { errorTip }