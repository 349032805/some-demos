<template>
    <div :class="{'bigMenus':true,'hide-title':isHide}">
        <div class="loading" v-show="showLoading">
          <div class="loading-box">
            <img src="../../assets/loading2.gif">
            <p>加载中...</p>
          </div>
        </div>
        <div class="menu-box">
            <div class="menu-header">
                <span class="pull-left search-box" v-if="goods.length>0" @click="showSearchDish=!showSearchDish"><span class="icon-search"></span>搜索</span>
                <div ref="menuHeaderWrapper">
                    <ul :style="{'width':menuHeaderWidth}">
                        <li v-for="(menu,index) in goods" @click="selectMenu(index)" :class="{'current':currentIndex==index}" :key="index">
                            {{menu.fsmenuclsname}}
                            <span class="num" v-show="menu.total>0&&menu.fsmenuclsid">{{menu.total}}</span>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="menu-list" ref="menuListWrapper" @touchstart="replaceCuIndex=false">
            <ul class="menu-list-item">
                <li v-for="(menu,menuIndex) in goods" class="menu-list-hook" :key="menuIndex">
                    <h4>{{menu.fsmenuclsname}}</h4>
                    <ul class="item-list">
                        <li v-for="(item,itemIndex) in menu.menuItemList" :key="itemIndex">
                            <div v-for="(subItem,subIndex) in item.menuItemUnitList" :key="subIndex" v-if="subItem.fidefault" >
                                <div class="dish-img" @click.stop.prevent="itemShowDishDetial(item,menu.fsmenuclsid)">
                                    <img v-if="item.fsimageurl" :src="cutImg(item.fsimageurl,350,350)">
                                    <img v-else src="../../assets/nodish-big.png">
                                    <div class="youhui" v-if="item.menuItemUnitList[0].menuItemActivity">{{item.menuItemUnitList[0].menuItemActivity.bargainName}}</div>
                                </div>
                                <h3>{{item.fsitemname}}</h3>
                                <p class="clearfix"><span class="count pull-right">已售{{item.soldNum}}{{subItem.fsorderunit}}</span><span class="price">¥{{subItem.bargainPrice?subItem.bargainPrice:subItem.fdsaleprice}}/{{subItem.fsorderunit}}</span><span class="now-price" v-if="subItem.bargainPrice>0">¥{{subItem.fdsaleprice}}</span></p>
                                <div class="btn-box">
                                    <a href="javascript:;" class="btn-add" @click.stop.prevent="itemShowAttribute(item,menu.fsmenuclsid)" v-if="item.isShowUnit==0">选规格</a>
                                    <numberControl :item="item" :itemExtra="subItem" v-if="item.isShowUnit==1" @drop="drop($event)" @openKeyboard="openKeyboard"></numberControl>
                                    <span class="num" v-show="item.attrCount>0">{{item.attrCount}}</span>
                                </div>
                            </div>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
        <cart :total="total" :activityPrice="activityPrice" @drop="drop($event)" @clearCart="clearCart()" @openKeyboard="openKeyboard"></cart>
        <foodAttribute ref="attrItem" :food="attrifoods" v-show="showAttributeBox" @closeAttributeBox="showAttributeBox=false" @drop="drop($event)" @openKeyboard="openKeyboard" @selectAsk="selectAsk"></foodAttribute>
        <transition name="slideLeft">
            <dishDetail :dishDetail="dishDetail" :unitList="unitList" :detailMenusid="detailMenusid" v-show="showDetail" @showAttribute="showAttribute" @drop="drop($event)" @openKeyboard="openKeyboard"></dishDetail>
        </transition>
        <searchDishes @closeSearchBox="closeSearchBox" @showAttribute="showAttribute" :lists="goods" :menusid="menusid" :commonDishesList="commonDishesList" v-show="showSearchDish" @drop="drop($event)" @openKeyboard="openKeyboard"></searchDishes>
        <balls ref="balls"></balls>
        <keyboard v-show="showKeyboard" @closeKeyboard="closeKeyboard" @confirmKeyboard="confirmKeyboard"></keyboard>
    </div>
</template>
<script>
    import Vue from 'vue'
    import cutImg from '@/config/cutImg.js'
    import BScroll from 'better-scroll'
    import numberControl from '@/vistitorComponents/numControl/numControl.vue'
    import foodAttribute from '@/vistitorComponents/foodAttribute/foodAttribute.vue'
    import cart from '@/vistitorComponents/cart/cart.vue'
    import dishDetail from '@/pages/dishDetail/dishDetail.vue'
    import searchDishes from '@/vistitorComponents/searchDishes/searchDishes.vue'
    import balls from '@/vistitorComponents/balls/balls.vue'
    import keyboard from '@/vistitorComponents/keyboard/keyboard.vue'
    import mixin from '@/config/menuCommon.js'
    export default {
        name: 'bigMenus',
        mixins: [mixin,cutImg],
        data() {
            return {
                shopId:'',
                showLoading:true,
                menuListHeight:[],//存储菜单高度
                menuHeaderWidth:0//存储菜单栏宽度
            }
        },
        created() {
            if(!this.$route.params.token){
                let _fullPath = sessionStorage.getItem('menusLink');
                if(_fullPath&&this.$route.fullPath!=_fullPath){
                    sessionStorage.removeItem('sessionfoods')
                }
                // this.$router.push({name:'menus'});
                sessionStorage.setItem('menusLink',this.$route.fullPath);
            }else{
                // this.$router.push({name:'menusScan'});
            }
            this.shopId = this.$route.params.shopId;
            if(!this.shopId){
                this.shopId = localStorage.getItem("shopId");
            }
            this.$http('get',this.baseUrl+'/getShopMenu',
            {
                shopId : this.shopId,
                tableId : sessionStorage.getItem("tableId")
            }
            ).then((res) => {  
                let data=res;
                if(data.code==200){
                    let _sessionFoods = JSON.parse(sessionStorage.getItem('sessionfoods'));
                    if(_sessionFoods){
                        this.goods=_sessionFoods;
                        this.commonDishesList = _sessionFoods[0].menuItemList;
                        let _sumTotal = JSON.parse(sessionStorage.getItem('sumTotal'));
                        this.menusid = _sessionFoods[0].fsmenuclsid;
                        this.total = _sumTotal;
                    }else{
                        this.goods=res.model;
                        this.commonDishesList = res.model[0].menuItemList;
                        this.menusid = res.model[0].fsmenuclsid;
                    }
                    // this.goods=res.model;
                    // this.commonDishesList = res.model[0].menuItemList;
                    this.$nextTick(() => {
                        this.headerWidth();
                        this.initMenuListHeight();// 初始化列表高度列表
                        this.$nextTick(() => {
                            this.initScroll(); // 初始化scroll
                            this.showLoading = false;
                        })
                    });
                    let sellOutList = JSON.parse(localStorage.getItem("sellOutList"));//售罄菜品
                    //判断是否有估清菜品
                    if(sellOutList&&sellOutList.length>0){
                        this.goods.forEach(function(menus){
                            menus.menuItemList.forEach(function(food){
                                sellOutList.forEach((sell) => {
                                    if(food.fsitemid == sell.fsItemId){
                                        food.menuItemUnitList.forEach(function(unit,index){
                                            if(unit.fsorderunit == sell.fsItemUnit){
                                                Vue.set(unit,"count",0);
                                                Vue.set(unit,"fistatusUnit",2);
                                                Vue.set(food,"count",0);
                                                Vue.set(food,"attrCount",0);
                                                if(food.menuItemUnitList.length<2){
                                                    Vue.set(food,"fiiscout",1);
                                                }
                                            }
                                        })
                                    }
                                })
                            })
                        })
                    }
                }else{
                    alert(data.message);
                    this.showLoading = false;
                }
            });
            //获取店铺活动
            this.getShopActivityList();
        },
        components : {
            numberControl,
            foodAttribute,
            cart,
            dishDetail,
            balls,
            keyboard,
            searchDishes
        },
        watch:{
            '$route':'closeDishDetial',//监听路由变化，改变菜品详情状态
            goods: {
                handler: function (val, oldVal) {
                    val.forEach(function(menu) {
                        let menuTotal = 0;
                        menu.menuItemList.forEach(function(food) {
                             //设置有规格商品的总价
                            if(food.isShowUnit == 0){
                                let totalPrice=0;
                                let attrCount=0;
                                let totalBargainPrice = 0;//特价
                                food.menuItemUnitList.forEach(function(attr){
                                    if(attr.count&&attr.count>0){
                                        totalPrice+=attr.fdsaleprice*attr.count;
                                        if(attr.bargainPrice){
                                            totalBargainPrice+=attr.bargainPrice*attr.count;//特价总价
                                        }else{
                                            totalBargainPrice+=attr.fdsaleprice*attr.count;//特价总价
                                        }
                                        //有价格的做法
                                        if(attr.askList){
                                            attr.askList.forEach(function(ask){
                                                ask.menuItemAskOutVoList.forEach(function(subAsk){
                                                    if(subAsk.checked){
                                                        totalPrice += (subAsk.fdaddprice*attr.count);
                                                        totalBargainPrice += (subAsk.fdaddprice*attr.count);
                                                    }
                                                })
                                            })
                                        }
                                        attrCount+=attr.count;
                                    }
                                });
                                // food['total'] = totalPrice;
                                // food['totalBargainPrice'] = totalBargainPrice;
                                Vue.set(food,'total',totalPrice);
                                Vue.set(food,'totalBargainPrice',totalBargainPrice);
                                Vue.set(food,'attrCount',attrCount);
                                menuTotal += attrCount;
                            };
                            if(food.count && food.isShowUnit == 1){
                                menuTotal += food.count;
                            }
                        });
                        Vue.set(menu,'total',menuTotal);
                    });
                    this.total = {
                        totalPrice:this.totalPrice,
                        totalCount:this.totalCount,
                        cartFoods:this.selectFoods
                    }
                    sessionStorage.setItem('sessionfoods',JSON.stringify(this.goods));
                    sessionStorage.setItem('sumTotal',JSON.stringify(this.total));
                },
                deep: true
            }
        },
        methods : {
            itemShowDishDetial(item,menusid){
                sessionStorage.setItem("detailMenusid",menusid);
                this.showDishDetial(item);
            },
            itemShowAttribute(item,menusid){
                sessionStorage.setItem("attrMenusid",menusid);
                this.showAttribute(item);
            },
            initScroll() {
                //初始化侧边栏滚动
                this.headerScroll = new BScroll(this.$refs.menuHeaderWrapper,{
                    click: true,//打开点击事件
                    scrollX: true
                });

                //初始化菜单列表滚动
                this.menusScroll = new BScroll(this.$refs.menuListWrapper,{
                    click:true,
                    probeType:3
                });

                //初始化滚动条
                let _this = this;
                _this.menusScroll.on('scroll',(pos)=>{
                    _this.scrollY=Math.abs(Math.round(pos.y))
                    for(let i=0;i<_this.menuListHeight.length;i++){
                        let height1=_this.menuListHeight[i]
                        let height2=_this.menuListHeight[i+1]
                        if(!height2 || (_this.scrollY>=height1 && _this.scrollY<height2)){
                            if(_this.replaceCuIndex){//点击左边菜单
                                return _this.currentIndex;
                            }else{
                                let liList = _this.$refs.menuHeaderWrapper.querySelectorAll('li');
                                if(i!=0){
                                    this.isHide = true;
                                    let el = liList[i-1];
                                    _this.headerScroll.scrollToElement(el,300);
                                }else{
                                    this.isHide = false;
                                    _this.headerScroll.scrollTo(0,0);
                                }
                                return _this.currentIndex = i;
                            }
                        }
                    }
                })
            },
            headerWidth() { //初始化菜品分类宽度
                let liList = this.$refs.menuHeaderWrapper.querySelectorAll('li');
                let liWidth = liList[0].clientWidth + 35;
                for(let i=0;i<liList.length;i++){
                    liWidth = liList[i].clientWidth + 35;
                    this.menuHeaderWidth += liWidth;
                }
                this.menuHeaderWidth += 80;
                this.menuHeaderWidth = this.menuHeaderWidth +'px';
                // this.menuHeaderWidth = liList.length * liWidth + 'px';
            },
            initMenuListHeight() {  //初始化菜品分类高度
                let menuList = this.$refs.menuListWrapper.querySelectorAll(".menu-list-hook");
                let height = 0;
                this.menuListHeight.push(height);
                for(let i=0;i<menuList.length;i++){
                    let item = menuList[i];
                    height += item.clientHeight;
                    this.menuListHeight.push(height)
                }
            },
            selectMenu(index) { //选中菜品分类事件
                this.currentIndex = index;
                if(!this.replaceCuIndex){
                    this.replaceCuIndex = true;
                }
                let menuList = this.$refs.menuListWrapper.querySelectorAll(".menu-list-hook");
                let el = menuList[index];
                this.menusScroll.refresh();
                this.menusScroll.scrollToElement(el,300)
            }
        }
    }
</script>
<style lang="less">
    @import "./bigMenus.less";
</style>