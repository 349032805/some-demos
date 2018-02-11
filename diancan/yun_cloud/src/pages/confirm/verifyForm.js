export function verifyForm(paramObj, instance) {
    let box = {};
    box.flag = true;

    let eatMode = paramObj.eatMode;
    let openPrepare = paramObj.openPrepare;
    let arriveTime = paramObj.arriveTime;
    let telephone = paramObj.telephone;
    let showInvoice = paramObj.showInvoice;
    let openInvoice = paramObj.openInvoice;
    let isCom = paramObj.isCom;
    let invoiceHeader = paramObj.invoiceHeader;
    let code = paramObj.code;

    let orderMode = 0; //现场点餐

    if (openPrepare) {
        orderMode = 1; //1预点
    }

    box.orderMode = orderMode;

      //判断就餐还是取餐,根据eatStyle
    if (orderMode == 0 && eatMode == 0) {
        box.eatStyle = 1;
    }
    if (orderMode == 0 && eatMode == 1) {
        box.eatStyle = 2;
    }

    if (orderMode == 1 && eatMode == 0) {
        box.eatStyle = 3;
    }

    if (orderMode == 1 && eatMode == 1) {
        box.eatStyle = 4;
    }

    // //看是堂食还是预点,如果是预点,判断是否填写了电话
    if (orderMode == 1) {
        if (telephone) {
            if (!/^1\d{10}$/gi.test(telephone)) {
                instance.$toast("手机号不合法");
                box.flag = false;
                return box;
            }
        } else {
            instance.$toast("请填写取餐电话");
            box.flag = false;
            return box;
        }
    }

    // //判断是否支持发票
    // //看发票开关是否开着
    // //如果开着,如果是公司发票,判断是否填写了抬头
    if (showInvoice == true || showInvoice == 'true') {
        if (openInvoice) {
            //公司纸质
            if (isCom) {
                if (!invoiceHeader) {
                    instance.$toast("请填写发票抬头");
                    box.flag = false;
                    return box;
                }

                if (!code) {
                    instance.$toast("请填写纳税人识别号");
                    box.flag = false;
                    return box;
                }
            }

        }
    }

    return box;

}