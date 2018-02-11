function demo(shopActivityList,noDiscountTotal,newDishList){ //活动集合,原总价,已点菜品集合
    let _activityOrder = shopActivityList.listBargainClsOrderResponse;//活动顺序对象
    let _activityBuygiftItemList = shopActivityList.activityBuygiftItemListResponse;//打造爆款对象
    let _activityCutMoneyList = shopActivityList.activityCutMoneyListResponse;//生意好点对象
    let _activityNewCustomer = shopActivityList.activityNewCustomerResponse;//新客多点对象

    //最终所需要展示的活动集合
    let actList = [];
    //活动过后的总价
    let finalPriceNoService = noDiscountTotal;

    //买几赠几送的菜品总价
    let sendTotalPrice = 0;

    if(_activityOrder.length>0){
        for(let i=0;i<_activityOrder.length;i++){

            //新客多点 1减价/2赠菜 ,
            if(_activityOrder[i].bargainCls==1){
            
                if(_activityNewCustomer){//判断对象是否存在
                    if(_activityNewCustomer.boolFirstActivity==1){//是新客，否则不是新客
                       //构造活动对象
                      let act = {};
                      act.word = "新";
                      act.name = "新用户下单";

                      let _discountType = _activityNewCustomer.discountType;//1减价，2赠菜
                      if(_discountType==1){//减价
                          let _discountAmountOrItemId  = _activityNewCustomer.discountAmountOrItemId;//减价金额
                          _discountAmountOrItemId = parseFloat(_discountAmountOrItemId);

                          //总价减去当前金额
                          if(finalPriceNoService > _discountAmountOrItemId){
                              finalPriceNoService = finalPriceNoService - _discountAmountOrItemId;
                          }else{
                              finalPriceNoService = 0;
                          }

                          act.name = act.name + '立减'+_discountAmountOrItemId +'元';
                          act.price = _discountAmountOrItemId;
                      }else{
                        let dishName = _activityNewCustomer.dishName;//赠送的菜
                        act.name = act.name + '赠送【'+dishName+'】';
                        act.price = 0;
                      }

                      actList.push(act);
                    }
                }
            //生意好点
            }else if(_activityOrder[i].bargainCls==2){//生意好点 201=单笔满额赠菜 / 202=单笔满减 / 203=单笔满折 / 204=加价购 
                if(_activityCutMoneyList){//判断对象是否存在

                    _activityCutMoneyList.forEach(function(item,index){
                        let _kind = item.bargainKind;//活动类型
                        let _bargainValue = item.bargainValue;//赠菜，减价，折扣，购菜
                        let _fullMoney = item.fullMoney;//满价
                        let _week = item.planTypeValue.split(",");//营业日期，星期几
                        let _notusableDate = item.notusableDate;//不适用日期
                        let _notusableHoliday = item.notusableHoliday;//法定节假日不适用;0=false不适用/1=true使用
                        let _listPHoliday = item.listPHolidayResponse;//如果法定节假日优惠，则法定节假日排除的日期
                        let _timeKind = item.timeKind;//执行时间段方式 1=餐段/2=时段 / 3=不限 ;预设3 
                        let _listMSection = item.listMSectionResponse;//营业餐段
                        let _listMHour = item.listMHourResponse;//营业时段
                        let _isTrue = isActivityLive(_week,_notusableHoliday,_notusableDate,_listPHoliday,_timeKind,_listMSection,_listMHour);

                        if(_isTrue){
                          console.log("*************满足 生意好点 时间段********");

                            if(_kind==201){//201=单笔满额赠菜 / 202=单笔满减 / 203=单笔满折 / 204=加价购 
                              console.log("*************类型编号201********");
                              //不处理价格
                              if(finalPriceNoService >= _fullMoney){
                                 //构造对象
                                let act = {};
                                act.word = "赠";
                                act.name = "单笔满"+_fullMoney+"元赠菜【" +item.dishName+"】";
                                act.price = 0;
                                actList.push(act);
                              }

                            }else if(_kind==202){ //202=单笔满减
                              console.log("*************类型编号202********");

                              if(finalPriceNoService >= _fullMoney){
                                 //构造对象
                                let act = {};
                                act.word = "减";
                                act.name = "单笔满"+_fullMoney+"元减"+item.bargainValue+"元";
                                act.price = parseFloat(item.bargainValue);
                                act.fullMoney = _fullMoney;
                                actList.push(act);
                              }

                            }else if(_kind==203){ //203=单笔满折
                              console.log("*************类型编号203********");
                              let discount = parseFloat(item.bargainValue);
                              let showDiscount = discount/10;
                              if(finalPriceNoService >= _fullMoney){
                                 //构造对象
                                let act = {};
                                act.word = "折";
                                act.name = "单笔满"+_fullMoney+"元打"+showDiscount+"折";
                                // act.price = finalPriceNoService*(100-discount)/100;
                                act.discount = discount;
                                act.fullMoney = _fullMoney;
                                actList.push(act);
                              }

                            }
                        }
                    })
                }
            //打造爆款
            }else if(_activityOrder[i].bargainCls==3){//打造爆款 301=买几赠几(变价) / 302=第几件半价(折扣) ,
                if(_activityBuygiftItemList){//判断对象是否存在
                     _activityBuygiftItemList.forEach(function(item,index){
                        let _kind = item.bargainKind;//活动类型
                        let _bargainValue = item.bargainValue;//赠菜，减价，折扣，购菜
                        let _fullMoney = item.fullMoney;//满价
                        let _week = item.planTypeValue.split(",");//营业日期，星期几
                        let _notusableDate = item.notusableDate;//不适用日期
                        let _notusableHoliday = item.notusableHoliday;//法定节假日不适用;0=false不适用/1=true使用
                        let _listPHoliday = item.listPHolidayResponse;//如果法定节假日优惠，则法定节假日排除的日期
                        let _timeKind = item.timeKind;//执行时间段方式 1=餐段/2=时段 / 3=不限 ;预设3 
                        let _listMSection = item.listMSectionResponse;//营业餐段
                        let _listMHour = item.listMHourResponse;//营业时段
                        let _isTrue = isActivityLive(_week,_notusableHoliday,_notusableDate,_listPHoliday,_timeKind,_listMSection,_listMHour);

                        if(_isTrue){
                          console.log("*************满足 打造爆款 时间段********");
                                   
                          if(_kind==301){//301=买几赠几(变价) / 302=第几件半价(折扣)
                            console.log("*************类型编号301********");
                            //判断已点的菜是否在活动里
                            newDishList.forEach(dish=>{
                              if(dish.fsitemid == item.itemId && dish.unit == item.orderUnit){
                                if(dish.count >= item.saleQty){
                                    //适合赠送的数量
                                    let suit = parseInt(dish.count/item.saleQty);
                                    suit = suit*item.saleQtyGift;

                                    console.log("301适合赠送的数量:"+suit)
                                    //给newDishList重新赋值
                                    dish.sendName = item.dishName;
                                    dish.sendCount = suit;
                                    // dish.sendTotalPrice = 0;
                                    console.log("dish.sendPrice:"+dish.sendPrice);
                                    dish.sendTotalPrice = (suit*dish.sendPrice).toFixed(2);
                                    console.log("dish.sendTotalPrice:"+dish.sendTotalPrice);

                                    sendTotalPrice += Number(dish.sendTotalPrice);

                                    let act = {};
                                    act.word = "送";
                                    act.name = "买" + item.saleQty +"送" + item.saleQtyGift+"【"+item.dishName+"】";
                                    act.price = 0;
                                    actList.push(act);
                                    }
                              }
                            })

                            //将newDishList再次放入localStorage, 因为只有这里的赠送菜品改变了newDishList
                             window.localStorage.setItem('newDishList', JSON.stringify(newDishList));

                            
                          }else if(_kind==302){//302=第几件半价(折扣) ,
                            console.log("*************类型编号302********");

                            let fullQty = item.saleQty + item.saleQtyGift;
                            let discount = parseFloat(item.bargainValue);
                            discount = discount/10;
                            newDishList.forEach(dish=>{
                              if(dish.fsitemid == item.itemId && dish.unit == item.orderUnit){
                                console.log("有满足302条件的菜品");
                                console.log("有满足302条件的菜品数量:"+dish.count);
                                console.log("第几件几折:"+"第"+fullQty+"件"+discount+"折")
                                if(dish.count >= fullQty){

                                  //合适打折的数量
                                  let suit = parseInt(dish.count/fullQty);
                                  console.log("合适打折的数量:"+suit);

                                  //最终折扣后的价格
                                  let price = dish.sendPrice*suit*(10-discount)/10;
                                  console.log("第几件几折的折扣价:"+price);
                                  if(price <= 0.005){
                                    console.log("极端小数的情况:"+price);
                                    price = 0;
                                  }else{
                                     price = (dish.sendPrice*suit*(10-discount)/10).toFixed(2);
                                  }

                                  let act = {};
                                  act.word = "惠";
                                  act.name = "第"+fullQty+"件"+discount+"折"+"【"+item.dishName+"】";
                                  act.price = price;
                                  actList.push(act);

                                // finalPriceNoService = finalPriceNoService-act.price;
                                }

                              }
                            })

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

    console.log("买几赠几送的东西的总价:"+sendTotalPrice);
    window.localStorage.setItem('sendTotalPrice', sendTotalPrice);
    console.log("买几赠几送的东西的总价:"+sendTotalPrice);

    //存放最终被展示的活动数组
    let newActList = [];

    //新用户
    actList.forEach((item,index)=>{
        if(item.word=="新"){
            newActList.push(item);
        }
    })

    //301送
    actList.forEach((item,index)=>{
        if(item.word=="送"){
            newActList.push(item);
        }
    })

    //302 减去第几件几折的折扣价格
    actList.forEach((item,index)=>{
        if(item.word=="惠"){
            newActList.push(item);
            finalPriceNoService = finalPriceNoService - item.price;
        }
    })
   
      //去除满减，满折中的较小值，取出最大值
      let fullMinusArr = [];
      let fullDiscountArr = [];
      actList.forEach(item=>{
        if(item.word=="减" && finalPriceNoService >= item.fullMoney){
            fullMinusArr.push(item.price)
        }

       if(item.word=="折" && finalPriceNoService >= item.fullMoney){
            fullDiscountArr.push(item.discount);
       }
      })

    //201赠送
    actList.forEach((item,index)=>{
        if(item.word=="赠"){
            newActList.push(item);
        }
    })


      //202 将满减最大的对象放入新数组
      if(fullMinusArr.length >0){
          console.log("fullMinusArr:"+fullMinusArr);
          let maxMinus = Math.max.apply(null, fullMinusArr);
          console.log("maxMinus:"+maxMinus);
          actList.forEach((item,index)=>{
            if(item.word=="减"){
               if(item.price == maxMinus){
                 newActList.push(item);
                }
            }
          })

          finalPriceNoService = finalPriceNoService - maxMinus;
      }

       //203 将满折最大的对象放入新数组
      if(fullDiscountArr.length >0){
          let minDiscount = Math.min.apply(null, fullDiscountArr);
          actList.forEach((item,index)=>{
            if(item.word=="折"){
                 if(item.discount == minDiscount){
                     newActList.push(item);
                  }
             }
          })

          let discountMoney = 0;
        newActList.forEach((item,index)=>{
            if(item.word=="折"){
              item.price = finalPriceNoService*(100-minDiscount)/100;
              item.price = item.price.toFixed(2);
              discountMoney = item.price;
             }
          })
           finalPriceNoService = finalPriceNoService - discountMoney;
      }

    console.log("************* 最终被展示的活动********");
    console.log(newActList);
    console.log("*************最终被展示的活动********");


    //finalPriceNoService保存在localStorage
    finalPriceNoService = Number(finalPriceNoService);
    if(finalPriceNoService <0){
        finalPriceNoService = 0;
    }
    console.log("现价总和(包括活动):"+finalPriceNoService);
    window.localStorage.setItem('finalPriceNoService',finalPriceNoService);

    return newActList;
}

function buildNowDay(){
    let date = new Date();
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


//判断现在是否参与活动
function isActivityLive(_week,_notusableHoliday,_notusableDate,_listPHoliday,_timeKind,_listMSection,_listMHour){
    console.log("判断现在是否参与活动");
    let resultFlag = false;
    //判断周几是否营业
    let _date = new Date();
    let _todayWeek = _date.getDay();//获取今天是星期几 0~6,0是星期日
    let _flag = false;//今天是否营业
    if(_todayWeek==0){
        if(_week[6]==1){
            _flag = true;//营业
        }
    }else{
        if(_week[_todayWeek-1]==1){
            _flag = true;//营业
        }
    }
    if(_flag){//今天营业(周几)
        let _flag2 = false;//今天是否在排除日期内
        // let _nowDay = _date.getFullYear()+'-'+(_date.getMonth()+1)+'-'+_date.getDate();
        let _nowDay = buildNowDay();

        if(_notusableDate){//如果存在排除日期
            _notusableDate.split(',').forEach(function(dd){
                console.log("-------------------dd:"+dd);
                console.log("-------------------_nowDay:"+_nowDay);
                if(dd==_nowDay){
                    console.log("-------------------今天在排除日起内");
                    _flag2 = true;//今天在排除日起内
                }
            })
        }
        if(!_flag2){//如果不存在排除日期或者今天不在排除日期内
            let _flag3 = false;//法定节假日是否适用
            if(_notusableHoliday==1){//如果法定节假日适用
                if(_listPHoliday){//判断今天是否在法定节假日排除的日期内
                    _listPHoliday.forEach(function(pholiday){
                        if(pholiday==_nowDay){
                            _flag3 = true;//今天在法定节假日排除内
                        }
                    })
                }
            }
            if(!_flag3){//如果法定节假日也可以使用
                let _flag4 = false;//判断该时间段是否在餐段内
                if(_timeKind==1){ //1=餐段/2=时段
                    if(_listMSection){
                        _listMSection.forEach(function(mse){
                            if(timeRange(mse.mBeginTime,mse.mEndTime)){
                                _flag4 = true;//现在在餐段内
                            }
                        })
                    }
                }else if(_timeKind==2){
                    if(_listMHour){
                        _listMHour.forEach(function(mh){
                            if(timeRange(mh.beginTime,mh.endTime)){
                                _flag4 = true;//现在在餐段内
                            }
                        })
                    }
                }else if(_timeKind==3){//不限时间段
                    _flag4 = true;
                }
                if(_flag4){//如果当前符合就餐时间段
                    resultFlag = true;
                }
            }
        }
    }
    return resultFlag;
}

 //判断当前时间段是否在某个时间段内
function timeRange(beginTime,endTime){
  console.log("判断当前时间段是否在某个时间段内");
  let flag = false;
  let str_b_t = beginTime.split (":");  
  let str_e_t = endTime.split (":");  
  // if (str_b_t.length != 2 ||str_e_t.length != 2) {  
  //     flag = false;  
  // }  
  var bt = new Date ();  
  var et = new Date ();  
  var nt = new Date ();  
  bt.setHours (str_b_t[0]);  
  bt.setMinutes (str_b_t[1]);  
  et.setHours (str_e_t[0]);  
  et.setMinutes (str_e_t[1]);  
  if (nt.getTime () - bt.getTime () > 0 && nt.getTime () - et.getTime () < 0) {  
      flag = true;  
  } else {  
      // alert ("当前时间是：" + nt.getHours () + ":" + nt.getMinutes () + "，不在该时间范围内！");  
      flag = false;  
  }  
  return flag;
}

export {demo}