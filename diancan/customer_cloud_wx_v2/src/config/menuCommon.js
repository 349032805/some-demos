import Vue from 'vue'
import {Toast} from 'mint-ui';
Vue.component(Toast.name, Toast);
var menuMixin = {
    data() {
        return {
            goods:[],
            commonDishesList:[],//存储常点菜品
            menusid:"",//菜品的分类id
            detailMenusid:"",
            dishDetail:{},//存储菜品详情对象
            unitList:{},//存储菜品规格
            attrifoods:{},//存储点击对象属性
            currentIndex:0,//存储目前选中的菜品分类索引
            replaceCuIndex:false,//是否点击的左边菜单栏目
            showAttributeBox:false,//显示菜品属性框
            showDetail:false,//显示菜品详情
            showSearchDish:false,//显示搜索菜品
            isHide:false,//隐藏头部
            activityList:{},//店铺活动
            activityPrice:{//活动价格
                minusMoney:0,
                bargainValue:0,
                type:0,
                activityShowText:false//是否显示活动描述
            },
            total:{
                totalCount : 0,
                totalPrice : 0,
                selectFoods:[]
            },
            nowDishesId:'',//所点菜品的菜品id
            nowItemUnit:'',//所点菜品的单位
            showKeyboard: false,
            keyboardItem : {}//键盘对象
        }
    },
    computed:{
        selectFoods(){
            let sellOutList = JSON.parse(localStorage.getItem("sellOutList"));
            let foods=[];
            let flag=[];//用于判断是否已经操作过ID
            this.goods.forEach(function(good){
                good.menuItemList.forEach((food)=>{
                    //判断是否有估清菜品
                    if(sellOutList&&sellOutList.length>0){
                        sellOutList.forEach((sell) => {
                            if(food.fsitemid == sell.fsItemId){
                                food.menuItemUnitList.forEach(function(unit,index){
                                    if(unit.fsorderunit == sell.fsItemUnit){
                                        flag[food.fsitemid] = food.fsitemid;
                                    }
                                })
                            }
                        })
                    }
                    if(flag[food.fsitemid]==undefined){
                        flag[food.fsitemid] = food.fsitemid;
                        if(food.isShowUnit==0){//有规格
                            if(food.attrCount){
                                foods.push(food);
                            }
                        }else{//无规格
                            if(food.count){
                                foods.push(food);
                            }
                        }
                    }
                })
            });
            return foods;
        },
        totalPrice(){
            let _this = this;
            let total=0;
            let totalBargainPrice=0;
            this.selectFoods.forEach(function(food) {
                if(food.isShowUnit==0){//有规格
                    total+=parseFloat(food.total);
                    totalBargainPrice+=parseFloat(food.totalBargainPrice);
                }else{
                    if(food.menuItemUnitList){//无规格
                        total+=parseFloat(food.menuItemUnitList[0].fdsaleprice*food.count)
                        if(food.menuItemUnitList[0].bargainPrice){
                            totalBargainPrice+=parseFloat(food.menuItemUnitList[0].bargainPrice*food.count);
                        }else{
                            totalBargainPrice+=parseFloat(food.menuItemUnitList[0].fdsaleprice*food.count);
                        }
                    }
                }
            });
            //存在活动,并判断计算顺序
            if(this.activityList){
                let _activityOrder = this.activityList.listBargainClsOrderResponse;//活动顺序对象
                let _activityBuygiftItemList = this.activityList.activityBuygiftItemListResponse;//打造爆款对象
                let _activityCutMoneyList = this.activityList.activityCutMoneyListResponse;//生意好点对象
                let _activityNewCustomer = this.activityList.activityNewCustomerResponse;//新客多点对象
                if(_activityOrder.length>0){
                    for(let i=0;i<_activityOrder.length;i++){
                        //新客多点 1减价/2赠菜 ,
                        if(_activityOrder[i].bargainCls==1){
                            if(_activityNewCustomer){//判断对象是否存在
                                if(_activityNewCustomer.boolFirstActivity==1){//是新客，否则不是新客
                                    let _discountType = _activityNewCustomer.discountType;//1减价，2赠菜
                                    if(_discountType==1){//减价
                                        let _discountAmountOrItemId  = _activityNewCustomer.discountAmountOrItemId;//减价金额
                                        if(totalBargainPrice < parseFloat(_discountAmountOrItemId)){//如果点的菜的价格小于立减的金额，总价格为0
                                            totalBargainPrice = 0;
                                        }else{
                                            totalBargainPrice = totalBargainPrice - parseFloat(_discountAmountOrItemId);
                                        }
                                    }
                                }
                            }
                        //生意好点
                        }else if(_activityOrder[i].bargainCls==2){//生意好点 201=单笔满额赠菜 / 202=单笔满减 / 203=单笔满折 / 204=加价购 
                            if(_activityCutMoneyList){//判断对象是否存在
                                let _totalBargainPrice = totalBargainPrice;
                                let _preMoney = 0;
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
                                    let _isTrue = _this.isActivityLive(_week,_notusableHoliday,_notusableDate,_listPHoliday,_timeKind,_listMSection,_listMHour);
                                    if(_isTrue){
                                        //判断活动类型
                                        if(_kind==201){//201=单笔满额赠菜 / 202=单笔满减 / 203=单笔满折 / 204=加价购 
                                            //不处理价格
                                        }else if(_kind==202){
                                            if(_fullMoney > _totalBargainPrice){
                                                if(_preMoney == 0){
                                                    _preMoney = _fullMoney;
                                                    _this.activityPrice = {
                                                        minusMoney:(parseFloat(_fullMoney)-parseFloat(_totalBargainPrice)).toFixed(2),
                                                        bargainValue:parseInt(_bargainValue),
                                                        type:202
                                                    }
                                                }
                                            }else{
                                                totalBargainPrice = parseFloat(_totalBargainPrice) - parseFloat(_bargainValue);
                                            }
                                        }else if(_kind==203){
                                            if(_fullMoney > _totalBargainPrice){
                                                if(_preMoney == 0){
                                                    _preMoney = _fullMoney;
                                                    _this.activityPrice = {
                                                        minusMoney:(parseFloat(_fullMoney)-parseFloat(_totalBargainPrice)).toFixed(2),
                                                        bargainValue:_bargainValue/10,
                                                        type:203
                                                    }
                                                }
                                            }else{
                                            totalBargainPrice = parseFloat(_totalBargainPrice) - parseFloat(_totalBargainPrice*(100-_bargainValue)/100);
                                            }
                                        }
                                        if(_kind!=201){
                                            if(totalBargainPrice && _fullMoney > _totalBargainPrice){
                                                _this.activityPrice['activityShowText'] = true;
                                            }else{
                                                _this.activityPrice['activityShowText'] = false;
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
                                    let _isTrue = _this.isActivityLive(_week,_notusableHoliday,_notusableDate,_listPHoliday,_timeKind,_listMSection,_listMHour);
                                    let isGetSession = true;
                                    if(_isTrue){
                                        if(_this.nowDishesId){//如果是点餐的时候，有当前点餐的菜品id
                                           if(_this.nowDishesId == item.itemId&&_this.nowItemUnit==item.orderUnit){//如果当前点击的菜参与活动
                                                isGetSession = false;
                                                let _dishesCount = 0;//菜品已点份数
                                                let _minusCount = 0;//距离活动相差的份数
                                                let _fdsaleprice = 0;//菜品的价格
                                                _this.selectFoods.forEach(function(_selectFood) {
                                                    //获取当前点的菜品id  
                                                    if(_selectFood.fsitemid == _this.nowDishesId){//菜品的id
                                                        if(_selectFood.isShowUnit==0){//有规格
                                                            _selectFood.menuItemUnitList.forEach(function(attr){
                                                                if(attr.fsorderunit == item.orderUnit&&_this.nowItemUnit==item.orderUnit){
                                                                    _dishesCount= attr.count;
                                                                    Vue.set(attr,"activityUnit",item.orderUnit);//存储活动类型，用于判断当前点的菜品的规格是哪个
                                                                    _fdsaleprice = attr.fdsaleprice;
                                                                }
                                                            })
                                                        }else{//无规格
                                                            _dishesCount = _selectFood.count
                                                            _fdsaleprice = _selectFood.menuItemUnitList[0].fdsaleprice;
                                                        }
                                                       
                                                    }
                                                    // _this.nowDishesId = '';
                                                    // _this.nowItemUnit = '';
                                                });
                                                if(_kind==301){//301=买几赠几(变价) / 302=第几件半价(折扣) ,
                                                    if(_dishesCount%item.saleQty!=0){//如果点了菜 //如果份数小于规定份数
                                                        _minusCount = item.saleQty - _dishesCount%item.saleQty;
                                                        let _text301 = item.dishName+'买'+item.saleQty+'送'+item.saleQtyGift+'，再买'+_minusCount+item.orderUnit+'送'+item.saleQtyGift+item.orderUnit;
                                                        Toast({
                                                            message: _text301,
                                                            duration: 2000
                                                        });
                                                    }
                                                    let _orderCount = parseInt(_dishesCount/item.saleQty);
                                                    if(_dishesCount>0&&_orderCount>0){
                                                        let _resultDiscount = _fdsaleprice*_orderCount*item.saleQtyGift;
                                                        if(_resultDiscount<=0.005){
                                                            _resultDiscount = 0;
                                                        }
                                                        isGetSession = true;
                                                        // total = parseFloat(total) + parseFloat(_resultDiscount.toFixed(2));
                                                    }
                                                }else if(_kind==302){
                                                    if(_dishesCount>0){//如果点了菜
                                                        let _resultQty = parseInt(item.saleQty + item.saleQtyGift);
                                                        if(_dishesCount % _resultQty !=0){//如果份数小于规定份数（第四件5折则，saleQty=3,saleQtyGift=1）
                                                            _minusCount = _resultQty - _dishesCount%_resultQty;
                                                            let _text302 = item.dishName+'第'+_resultQty+item.orderUnit+item.bargainValue/10+'折，再买'+_minusCount+item.orderUnit+'打'+item.bargainValue/10+'折';
                                                            Toast({
                                                                message: _text302,
                                                                duration: 2000
                                                            });
                                                        }
                                                        let _orderCount = parseInt(_dishesCount/_resultQty);
                                                        if(_dishesCount>0&&_orderCount>0){
                                                            let _resultDiscount = _fdsaleprice*(100-item.bargainValue)/100*_orderCount;
                                                            if(_resultDiscount<=0.005){
                                                                _resultDiscount = 0;
                                                            }
                                                            totalBargainPrice = parseFloat(totalBargainPrice) - _resultDiscount.toFixed(2);
                                                        }
                                                    }
                                                }
                                           }
                                        }
                                        if(isGetSession){//如果是刷新或者从订单确认页面返回的时候，无当前点餐的id时，从购物车里面取值判断
                                            let _cartFoods = _this.selectFoods;
                                            if(_this.selectFoods){
                                                _this.selectFoods.forEach(function(selectItem){
                                                    if(selectItem.fsitemid == item.itemId){//如果当前点击的菜参与活动
                                                        let _dishesCount = 0;//菜品已点份数
                                                        let _minusCount = 0;//距离活动相差的份数
                                                        let _fdsaleprice = 0;//菜品的价格
                                                        _this.selectFoods.forEach(function(_selectFood) {
                                                            //获取当前点的菜品id  
                                                            if(_selectFood.fsitemid == selectItem.fsitemid){//菜品的id
                                                                if(_selectFood.isShowUnit==0){//有规格
                                                                    _selectFood.menuItemUnitList.forEach(function(attr){
                                                                        if(attr.fsorderunit == item.orderUnit&&attr.activityUnit == item.orderUnit){
                                                                           //判断菜品是点过的菜品规格
                                                                            _dishesCount= attr.count;
                                                                            _fdsaleprice = attr.fdsaleprice;
                                                                        }
                                                                    })
                                                                }else{//无规格
                                                                    _dishesCount = _selectFood.count
                                                                    _fdsaleprice = _selectFood.menuItemUnitList[0].fdsaleprice;
                                                                }
                                                            }
                                                        });
                                                        if(_kind==301){//301=买几赠几(变价) / 302=第几件半价(折扣) ,
                                                            let _orderCount = parseInt(_dishesCount/item.saleQty);
                                                            if(_dishesCount>0&&_orderCount>0){
                                                                let _resultDiscount = _fdsaleprice*_orderCount*item.saleQtyGift;
                                                                if(_resultDiscount<=0.005){
                                                                    _resultDiscount = 0;
                                                                }
                                                                total = parseFloat(total) + parseFloat(_resultDiscount.toFixed(2));
                                                                isGetSession = false;
                                                            }
                                                        }else if(_kind==302){
                                                            if(_dishesCount>0){//如果点了菜
                                                                let _resultQty = parseInt(item.saleQty + item.saleQtyGift);
                                                                let _orderCount = parseInt(_dishesCount/_resultQty);
                                                                if(_dishesCount>0&&_orderCount>0){
                                                                    let _resultDiscount = parseFloat(_fdsaleprice*(100-item.bargainValue)/100*_orderCount);
                                                                    if(_resultDiscount<=0.005){
                                                                        _resultDiscount = 0;
                                                                    }
                                                                    totalBargainPrice = parseFloat(totalBargainPrice) - _resultDiscount.toFixed(2);
                                                                }
                                                            }
                                                        }
                                                    }
                                                })
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }
            let totalObj = {
                total:total.toFixed(2),
                totalBargainPrice:totalBargainPrice.toFixed(2)
            }
            return totalObj;
        },
        totalCount(){
            let total=0;
            this.selectFoods.forEach(function(food) {
                if(food.isShowUnit==0){//有规格
                    total+=parseInt(food.attrCount);
                }else{//无规格
                    total+=parseInt(food.count)
                }
            });
            return total;
        }
    },
    methods : {
        buildNowDate(){
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
        },
        isActivityLive(_week,_notusableHoliday,_notusableDate,_listPHoliday,_timeKind,_listMSection,_listMHour){
            let _this = this;
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
                 let _nowDay = this.buildNowDate();
                if(_notusableDate){//如果存在排除日期
                    if(_notusableDate.indexOf(",")>-1){
                        _notusableDate.split(',').forEach(function(dd){
                            if(dd==_nowDay){
                                _flag2 = true;//今天在排除日起内
                            }
                        })
                    }else{
                        if(_notusableDate==_nowDay){
                            _flag2 = true;//今天在排除日起内
                        }
                    }
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
                                    if(_this.timeRange(mse.mBeginTime,mse.mEndTime)){
                                        _flag4 = true;//现在在餐段内
                                    }
                                })
                            }
                        }else if(_timeKind==2){
                            if(_listMHour){
                                _listMHour.forEach(function(mh){
                                    if(_this.timeRange(mh.beginTime,mh.endTime)){
                                        _flag4 = true;//现在在餐段内
                                    }
                                })
                            }
                        }else if(_timeKind==3){//全天
                            _flag4 = true;
                        }
                        if(_flag4){//如果当前符合就餐时间段
                            resultFlag = true;
                        }
                    }
                }
            }
            return resultFlag;
        },
        //判断当前时间段是否在某个时间段内
        timeRange(beginTime,endTime){
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
        },
        getShopActivityList(){
            this.$http('get',this.baseUrl+'/getShopActivityList',{
                shopId:this.shopId
            }).then((res) => {
                this.activityList = res.model;
                localStorage.setItem('shopActivityList',JSON.stringify(res.model))
            })
        },
        clearCart(){
            this.goods.forEach((menu) => {
                menu.menuItemList.forEach((food) => {
                    food.menuItemUnitList.forEach(function(val){
                        if(val.count){
                            val.count = 0;
                        }
                    })
                    if(food.count){
                        food.count = 0;
                    }
                    if(food.attrCount){
                        food.attrCount = 0;
                    }
                })
                if(menu.total){
                    menu.total = 0;
                }
            })
        },
        showAttribute(food) {
            //如果菜品已卖完
            let _this = this;
            if(food.status == 1) return;
            Vue.set(food,'selAttrObj',food.menuItemUnitList[0]);
            this.$refs.attrItem.unitIndex = 0;
            //初始化选中的菜品
            let _attrs= [];
            this.$http('get',this.baseUrl+'/getMenusAskGroup',{
                shopId:this.shopId,
                itemId:food.fsitemid
            }).then(function(res){
                if(res.code==200){
                    _attrs = res.model;
                    let selectCount = true;
                    let isFideFault = true;//是否是默认属性
                    food.menuItemUnitList.forEach(function(unit,index){
                        if(!unit['askList']){
                            Vue.set(unit,'askList',JSON.parse(JSON.stringify(_attrs)));
                        }
                        if(isFideFault){//初始化的时候，默认选中后台给出的默认选项
                            if(unit.fidefault==1){//0不默认，1默认
                                Vue.set(food,'selAttrObj',unit);
                                _this.$refs.attrItem.unitIndex = index;
                                isFideFault = false;
                            }
                        }
                        if(selectCount){//有选菜的时候，默认选中选中的首个菜
                            if(unit.count>0){
                                Vue.set(food,'selAttrObj',food.menuItemUnitList[index]);
                                _this.$refs.attrItem.unitIndex = index;
                                selectCount = false;
                            }
                        }
                    })
                }else{
                    alert(res.message);
                }
                _this.attrifoods = food;
                if(_attrs){
                    _this.$refs.attrItem.isHasAskList = true;
                }
                _this.showAttributeBox = !_this.showAttributeBox;
                sessionStorage.setItem("foodFsitemid",food.fsitemid);
            })
        },
        selectAsk(item){//处理相同菜品的菜品属性选中问题
            let _attrMenusid = sessionStorage.getItem("attrMenusid");
            if(_attrMenusid == 'null'){
                _attrMenusid = null;
            }
            let _fsitemid = sessionStorage.getItem("foodFsitemid");
            this.goods.forEach(function(good){
               if(good.fsmenuclsid != _attrMenusid){//不同的分类
                    good.menuItemList.forEach((food)=>{
                        if(food.isShowUnit==0){
                            if(food.fsitemid == _fsitemid){//与当前选中的商品相同
                                food.menuItemUnitList.forEach(function(menuUnit){
                                    if(menuUnit.fsorderunit == item.fsorderunit){//操作的是某个属性
                                        Vue.set(menuUnit,'askList',item.askList);
                                    }
                                })
                            }
                        }
                    })
               }
            });
        },
        showDishDetial(item){
            let _this = this;
            this.showDetail = !this.showDetail;
            this.dishDetail = item;
            this.detailMenusid = sessionStorage.getItem("detailMenusid");
            item.menuItemUnitList.forEach(function(val){
                if(val.fidefault){
                    _this.unitList = val;
                }
            })
            this.$store.commit('SAVE_TITLE','菜品详情');
            this.$router.push({path:"/dishDetail"})
        },
        closeDishDetial(){
            if(this.$route.name=="menus" || this.$route.name=="menusScan"){
                this.showDetail = false;
                this.$store.commit('SAVE_TITLE','百味云点餐系统');
            }
        },
        closeSearchBox(){
            this.showSearchDish = !this.showSearchDish;
        },
        drop(target){
            this.$refs.balls.drop(target);
            this.nowDishesId = target.dishesId;
            this.nowItemUnit = target.itemUnit;
            //改变相同类别的数量
            this.goods.forEach(function(good){
                good.menuItemList.forEach((food)=>{
                    if(food.fsitemid == target.dishesId){//与当前选中的商品相同
                        if(food.isShowUnit ==0){//有规格
                            food.menuItemUnitList.forEach((attr)=>{
                                if(attr.fsorderunit==target.itemUnit){
                                    if(attr.count){
                                        attr.count = target.count;
                                    }else{
                                        Vue.set(attr,"count",target.count);
                                    }
                                }
                            })
                        }else{
                            if(food.count){
                                food.count = target.count;
                            }else{
                                Vue.set(food,"count",target.count);
                            }
                        }
                    }
                })
            });
        },
        closeKeyboard(){
            this.showKeyboard = !this.showKeyboard;
        },
        openKeyboard(obj){
            this.showKeyboard = !this.showKeyboard;
            this.keyboardItem = obj;
            console.log(obj)
        },
        confirmKeyboard(inputVal){
            if(!inputVal) return;
            let _keyboardItem = this.keyboardItem;
            this.showKeyboard = !this.showKeyboard;
            this.keyboardItem.count = parseInt(inputVal);
            // Vue.set(this.keyboardItem,'count',parseInt(inputVal));
            //改变相同类别的数量
            this.goods.forEach(function(good){
                good.menuItemList.forEach((food)=>{
                    if(food.fsitemid == _keyboardItem.dishesId){//与当前选中的商品相同
                        if(food.isShowUnit ==0){//有规格
                            food.menuItemUnitList.forEach((attr)=>{
                                if(attr.fsorderunit==_keyboardItem.itemUnit){
                                    attr.count = parseInt(inputVal);
                                }
                            })
                        }else{
                            food.count = parseInt(inputVal);
                        }
                    }
                })
            });
        }
    }
}

export default menuMixin