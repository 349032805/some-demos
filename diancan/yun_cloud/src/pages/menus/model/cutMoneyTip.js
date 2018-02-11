import { Number } from '@/utils/money.js'

function cutMoneyTip(actCut, actisNew, price, Vue) {
    console.log(actCut)
    console.log(actisNew)
    let isNewMoney = 0;
    isNewMoney = actisNew ? Number(actisNew.discountAmountOrItemId) : 0   //如果有新人就是新人钱，如果没有新人钱就没有
    if (price == 0) {
        Vue.$store.commit('SAVE_ACTIVITY_SHOW', false)
        return
    }
    Vue.$store.commit('SAVE_ACTIVITY_SHOW', true)
    if (!actCut && actisNew) {

        noCut_hasIsNew(price, isNewMoney, Vue)
    }

    if (!actisNew && actCut) {
        
        noIsNew_hasCut(actCut, price, Vue)
    }

    if(actisNew && actCut) {
       let newPrice =  noCut_hasIsNew (price, isNewMoney, Vue)
       if(Number(newPrice)>0) {
           noIsNew_hasCut(actCut,newPrice,Vue,isNewMoney)
       }else{
           Vue.isFullMoney = false;
       }
    }
}

function noCut_hasIsNew(price, isNewMoney, Vue) {
    Vue.startSaled = true
    if (price >= isNewMoney) {
        Vue.saled = isNewMoney
        return price - isNewMoney
    } else {
        Vue.saled =  price
    }
}


function noIsNew_hasCut(actCut, price, Vue,isNewMoney=0) {
    let Array = [];
    for (let i = 0; i < actCut.length; i++) {
        let item = actCut[i]
        let _kind = item.bargainKind;//活动类型
        let _bargainValue = item.bargainValue;//赠菜，减价，折扣，购菜
        let _fullMoney = item.fullMoney;//满价
        Array.push({ _fullMoney, _bargainValue })
        if (_kind == 202) {
            if(price>=_fullMoney) {
                 Vue.startSaled = true
                 Vue.saled= Number(_bargainValue+isNewMoney).toFixed(2)
            }
            JianTip(Array, price,Vue,isNewMoney)
        } else if (_kind == 203) {
            if(price>=_fullMoney) {
                 Vue.startSaled = true
                 Vue.saled= Number(Number(price - price*(_bargainValue/100)).toFixed(2)+isNewMoney).toFixed(2)
            }
            ZheTip(Array, price, Vue,isNewMoney)
        }
        if(isNewMoney>0) {
            Vue.startSaled = true
        }
    }
}


function JianTip(Array, price, Vue,isNewMoney=0) {
    if (price >= Array[Array.length - 1]._fullMoney) {
        Vue.isFullMoney = false;
        return
    }
    if(price < Array[0]._fullMoney) {
        Vue.startSaled = false
    }
    for (let i = 0; i < Array.length; i++) {
        if (price <= Array[i]._fullMoney) {
            Vue.isFullMoney = true;
            Vue.fullMoney = Number(Array[i]._fullMoney - price).toFixed(2)
            Vue.endSaled = Number(Number(Array[i]._bargainValue)+isNewMoney).toFixed(2)
            break;
        }
    }
}


function ZheTip(Array, price , Vue,isNewMoney=0) {
    let fullMoney = Array[0]._fullMoney
    let bargainValue = Array[0]._bargainValue
    if (price < fullMoney) {
        Vue.startSaled = false;
    }
    if (price >= fullMoney) {
        Vue.isFullMoney = false;
        return
    }
    if (price <= fullMoney) {
        Vue.isFullMoney = true;
        Vue.fullMoney = Number(fullMoney - price).toFixed(2)
        Vue.endSaled = Number((fullMoney - fullMoney * (bargainValue / 100))+isNewMoney).toFixed(2)
    }
}
export { cutMoneyTip }