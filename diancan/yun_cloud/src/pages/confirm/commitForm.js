import axios from '@/config/axios';
export async function commitForm(paramsObj, sign, instance) {
    let orderInVo = buildForm(paramsObj);
    console.log("------------------orderInVo----------------------")
    console.log(orderInVo)
    console.log("------------------orderInVo----------------------")

    let box = {};
    box.isDisableClick = true;
    box.isReset = false;
    box.isStop = false;

    const SUCCESS = 200;
    await axios("post", instance.baseUrl + '/createOrder', orderInVo, 'json').then((response) => {
        if (response.code == SUCCESS) {
            gotoPayPage(response, paramsObj.nowPrice, sign, instance);
        }

        let shopId = paramsObj.shopId;
        let tableId = paramsObj.tableId;
        console.log("返回点餐用的shopId:" + shopId + "|tableId:" + tableId);

        if (response.code == 500) {
            if (response.errorCode == 5000058) { //普通菜被估清
                //放开支付按钮
                box.isDisableClick = false;

                // window.sessionStorage.setItem('sellOutList', JSON.stringify(response.extra.item));
                instance.$store.commit('SAVE_SALED_FOODS', response.extra.item);

                instance.$crib.confirm.show({
                    title: " 提示",
                    headStyle: { fontSize: '18px' },
                    content: response.message,
                    confirmText: "继续下单",
                    cancelText: "返回继续点餐",
                    cancelStyle: { color: '#06C1AE' },
                    confirmStyle: { color: '#333' },
                    onCancel: function () {
                        //返回点菜页
                        // shopId = paramsObj.shopId;
                        instance.$router.push({ path: `/menus/${shopId}/${localStorage.token}`, query: { sign: sign, tableId: tableId } })

                    },
                    onConfirm: function () {
                        // commonDishClear(response);
                        box.isReset = commonDishClear(response, sign, shopId, tableId);
                    }
                });
            } else if (response.errorCode == 50000512) { //赠菜被估清

                instance.$crib.confirm.show({
                    title: " 提示",
                    headStyle: { fontSize: '18px' },
                    content: response.message,
                    confirmText: "继续下单",
                    cancelText: "返回继续点餐",
                    cancelStyle: { color: '#06C1AE' },
                    confirmStyle: { color: '#333' },
                    onCancel: function () {
                        //返回点菜页
                        // shopId = paramsObj.shopId;
                        instance.$router.push({ path: `/menus/${shopId}/${localStorage.token}`, query: { sign: sign, tableId: tableId } })
                    },
                    onConfirm: function () {
                        gotoPayPage(response, nowPrice, sign, instance);
                    }
                });

            } else if(response.errorCode == 50004001){ //停止扫码点餐
                instance.$message.alert(response.message).then(action => {
                    box.isStop = true;
                    box.isDisableClick = true;
              });
            } else {
                instance.$toast(response.message);
            }
        }

    });

    return box;
}

function buildForm(paramsObj) {
    let demandList = paramsObj.demandList;
    let remarkInput = paramsObj.remarkInput;
    let showInvoice = paramsObj.showInvoice;
    let openInvoice = paramsObj.openInvoice;
    let isCom = paramsObj.isCom;
    let isPerson = paramsObj.isPerson;
    let invoiceHeader = paramsObj.invoiceHeader;
    let code = paramsObj.code;
    let orderMode = paramsObj.orderMode;
    let arriveTime = paramsObj.arriveTime;
    let eatStyle = paramsObj.eatStyle;
    let shopId = paramsObj.shopId;
    let tableId = paramsObj.tableId;
    let telephone = paramsObj.telephone;
    let originPrice = paramsObj.originPrice;
    let finalPrice = paramsObj.finalPrice;


    //查看备注
    let optionArr = [];
    demandList.forEach(item => {
        if (item.flag) {
            optionArr.push(item.fsaskname);
        }
    });

    let optionStr = optionArr.join(",");
    if (remarkInput) {
        optionStr = optionStr + "!@#" + remarkInput;
    }
    console.log("最终的备注选项字符:" + optionStr);

    let orderMenuitemInVoList = [];
    let newDishList = sessionStorage.newDishList;
    newDishList = JSON.parse(newDishList);

    newDishList.forEach(item => {
        let obj = {};
        obj.fsitemid = item.fsitemid; //菜品id
        obj.number = item.count; //菜品数量
        obj.unit = item.unit; //单位
        obj.isGive = item.isGive;
        obj.orderMenuItemRequirementInVoList = []; //做法数组
        if (item.doIdList) {
            if (item.doIdList.length > 0) {
                for (let i = 0; i < item.doIdList.length; i++) {
                    let doStyleObj = {};
                    doStyleObj.fsaskid = item.doIdList[i];
                    doStyleObj.fsaskname = item.doNameList[i];
                    obj.orderMenuItemRequirementInVoList.push(doStyleObj);
                }
            }
        }

        //放普通菜品
        orderMenuitemInVoList.push(obj);
    });


    //放赠菜菜品
    let sendDishList = sessionStorage.sendDishList;
    if (sendDishList) {
        sendDishList = JSON.parse(sendDishList);
        if (sendDishList.length > 0) {
            sendDishList.forEach(item => {
                let obj = {};
                obj.fsitemid = item.fsitemid; //菜品id
                obj.number = item.count; //菜品数量
                obj.unit = item.unit; //单位
                obj.isGive = item.isGive;
                obj.orderMenuItemRequirementInVoList = []; //做法数组
                orderMenuitemInVoList.push(obj);
            })
        }
    }

    console.log("提交订单所有的菜品集合(包括赠送的菜)");
    console.log(orderMenuitemInVoList);
    console.log("提交订单所有的菜品集合(包括赠送的菜)");

    let orderTicketInVo = {};

    if (showInvoice == true || showInvoice == 'true') {
        if (openInvoice) {
            if (isCom) {
                orderTicketInVo.invoiceType = 3;
            }
            if (isPerson) {
                orderTicketInVo.invoiceType = 1;
            }
        } else {
            orderTicketInVo.invoiceType = 0;
        }
    } else {
        orderTicketInVo.invoiceType = 0;
    }

    orderTicketInVo.ticketHeader = invoiceHeader;
    orderTicketInVo.ticketRatepayNumber = code;

    let fromatTime = "";
    if (orderMode == 1) {
        fromatTime = formatToFullDate(new Date()) + ' ' + arriveTime + ':00';
    }

    let orderInVo = {};
    orderInVo.eatStyle = eatStyle;
    orderInVo.eatTime = fromatTime;
    orderInVo.fsShopGUID = shopId;
    orderInVo.fsmtableid = tableId;

    orderInVo.orderMenuitemInVoList = orderMenuitemInVoList;
    orderInVo.orderTicketInVo = orderTicketInVo;
    orderInVo.remark = optionStr;

    //手机号
    orderInVo.phone = telephone;

    //传给后台原价和现价
    orderInVo.paidMoney = finalPrice;
    orderInVo.totalprice = originPrice;

    console.log("*************orderInVo**************");
    console.log(orderInVo);
    console.log("************orderInVo***************");

    return orderInVo;
}

function formatToFullDate(date) {
    let year = date.getFullYear();
    let month = (date.getMonth() + 1);
    if (month < 10) {
        month = "0" + month;
    }
    let day = date.getDate();
    if (day < 10) {
        day = "0" + day;
    }
    return year + "-" + month + "-" + day;
}

function gotoPayPage(res, nowPrice, sign, instance) {
    let orderId = res.extra.orderId;
    //判断如果现价等于0,直接跳详情页面,否则跳支付
    if (nowPrice == 0 || nowPrice == "0" || nowPrice == "0.00") {
        instance.$router.push({ name: 'orderDetail', params: { orderId: orderId } });
    } else {
        if (sign == "wx") {
            let payUrl = res.extra.url;
            console.log("wx-payUrl:" + payUrl);
            window.location.replace(payUrl);
        } else {
            console.log("支付宝支付需要用的orderId" + orderId)
            instance.$router.replace({ name: 'pay', params: { orderId: orderId } });
        }

    }
}

function commonDishClear(response, sign, shopId, tableId) {
    let isRest = false;
    //关闭弹窗,并重新展示菜品列表,和计算价格

    //获取估清的菜品集合
    let emptyDishList = response.extra.item;

    let clearList = [];
    let newDishList = sessionStorage.newDishList;
    newDishList = JSON.parse(newDishList);

    newDishList.forEach(item => {
        emptyDishList.forEach(emptyObj => {
            if (item.fsitemid == emptyObj.fsItemId && item.unit == emptyObj.fsItemUnit) {
                item.isClear = true;
            }
        });
    });

    newDishList.forEach(item => {
        if (!item.isClear) {
            clearList.push(item);
        }
    });

    console.log("------------clearList-----------");
    console.log(clearList);
    console.log("------------clearList-----------");

    newDishList = clearList;
    console.log("------------处理估清的菜品之后的菜品列表-----------");
    console.log(newDishList);
    console.log("------------处理估清的菜品之后的菜品列表-----------");

    window.sessionStorage.setItem('newDishList', JSON.stringify(newDishList));

    //如果点的菜为空了,回到点菜页,否则将估清的菜品传给首页的方法,从新获取所有的数据
    if (newDishList.length == 0) {
        instance.$router.push({ path: `/menus/${shopId}/${localStorage.token}`, query: { sign: sign, tableId: tableId } })
    } else {
        //服务费重新计算
        //菜品列表重新计算
        isReset = true;
    }

    return isRest;
}

