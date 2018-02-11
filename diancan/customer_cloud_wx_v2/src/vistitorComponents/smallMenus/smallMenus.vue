<template>
    <div :class="{'small-menus':true,'hide-title':isHide}">
      <div class="loading" v-show="showLoading">
          <div class="loading-box">
            <img src="../../assets/loading2.gif">
            <p>加载中...</p>
          </div>
      </div>
      <span class="search-box" v-if="goods.length>0" @click.stop.prevent="showSearchDish=!showSearchDish"><span class="icon-search"></span>搜索</span>
      <div class="menu-sidebar" ref="menuWrapper">
        <ul>
            <li v-for="(menu,index) in goods" @click="selectMenu(index,$event)" :class="{'current':currentIndex==index,'menu-list-hook':true}" :key='index'>
                {{menu.fsmenuclsname}}
                <span class="num" v-show="menu.total>0&&menu.fsmenuclsid">{{menu.total}}</span>
            </li>
        </ul>
      </div>
      <div class="menu-lists" ref="foodsWrapper" @touchstart="replaceCuIndex=false">
        <ul>
            <li v-for="(menu,menuIndex) in goods" class="menu-list-hook" :key='menuIndex'>
                <h2>{{menu.fsmenuclsname}}</h2>
                <ul class="list-item">
                    <li v-for="(item,itemIndex) in menu.menuItemList" :key='itemIndex'>
                        <div v-for="(subItem,subIndex) in item.menuItemUnitList" :key="subIndex" v-if="subItem.fidefault" >
                            <dish :menusid="menu.fsmenuclsid" :item="item" :itemExtra="subItem" @showDishDetial="showDishDetial" @showAttribute="showAttribute" @drop="drop($event)" @openKeyboard="openKeyboard"></dish>
                        </div>
                    </li>
                </ul>
            </li>
            <!--<li class="menu-list-hook" v-show="goods.length>0"><h2>图片仅供参考 菜品请以实物为准</h2></li>-->
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
  import BScroll from 'better-scroll'
  import axios from 'axios'
  import cart from '@/vistitorComponents/cart/cart.vue'
  import foodAttribute from '@/vistitorComponents/foodAttribute/foodAttribute.vue'
  import dishDetail from '@/pages/dishDetail/dishDetail.vue'
  import dish from '@/vistitorComponents/dish/dish.vue'
  import searchDishes from '@/vistitorComponents/searchDishes/searchDishes.vue'
  import balls from '@/vistitorComponents/balls/balls.vue'
  import keyboard from '@/vistitorComponents/keyboard/keyboard.vue'
  import mixin from '@/config/menuCommon.js'
  export default {
      name: 'smallMenus',
      mixins: [mixin],
      data(){
          return {
              shopId:'',
              showLoading:true,
              listHeight:[],//存储列表的高度
              sumMenuList:[]//存储菜单的高度
          }
      },
      components : {
        foodAttribute,
        cart,
        dishDetail,
        dish,
        searchDishes,
        keyboard,
        balls
      },
      created () {
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
                    this.menusid = _sessionFoods[0].fsmenuclsid;
                    let _sumTotal = JSON.parse(sessionStorage.getItem('sumTotal'));
                    this.total = _sumTotal;
                }else{
                    this.goods=res.model;
                    this.commonDishesList = res.model[0].menuItemList;
                    this.menusid = res.model[0].fsmenuclsid;
                }
                this.$nextTick(() => {
                    this.initScroll(); // 初始化scroll
                    this._calculateHeight(); // 初始化列表高度列表
                    this.showLoading = false;
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
      methods:{
        initScroll(){
          //初始化侧边栏滚动
          this.menuScroll = new BScroll(this.$refs.menuWrapper,{
              click:true//打开点击事件
          });
          //初始化菜单列表滚动
          this.foodsScroll = new BScroll(this.$refs.foodsWrapper,{
              click:true,
              probeType:3
          });
          let _this = this;
          this.foodsScroll.on('scroll',(pos)=>{
              _this.scrollY=Math.abs(Math.round(pos.y))
              for(let i=0;i<_this.listHeight.length;i++){
                  let height1=_this.listHeight[i];
                  let height2=_this.listHeight[i+1];
                  if(!height2 || (_this.scrollY>=height1 && _this.scrollY<height2)){
                      if(_this.replaceCuIndex){//点击左边菜单
                          return _this.currentIndex;
                      }else{
                          let liList = _this.$refs.menuWrapper.querySelectorAll('li');
                          let el = liList[i-1];
                          if(i!=0){//判断是否滚动了一屏
                              _this.menuScroll.scrollToElement(el,300);
                              this.isHide = true;
                          }else{
                              _this.menuScroll.scrollTo(0,0);
                              this.isHide = false;
                          }
                          return _this.currentIndex = i;
                      }
                  }
              }
          })
        },
        //计算每个分类的高度
        _calculateHeight(){
          let foodList =this.$refs.foodsWrapper.getElementsByClassName('menu-list-hook')
          let height = 0;
          this.listHeight.push(height);
          for(let i=0;i<foodList.length;i++){
            let item = foodList[i];
            height += item.clientHeight;
            this.listHeight.push(height)
          }
        },
        //选择菜单
        selectMenu(index,enent){
            this.currentIndex = index;
            if(!this.replaceCuIndex){
                this.replaceCuIndex = true;
            }
            let foodList = this.$refs.foodsWrapper.getElementsByClassName('menu-list-hook')
            let el = foodList[index];
            //获取列表dom，并且滚动到对应位置 
            this.foodsScroll.refresh();
            this.foodsScroll.scrollToElement(el,300)
        },
        //初始化购物车列表
        _initScroll() {
            this.foodlistScroll = new BScroll(this.$refs.foodlist, {
                click: true
            });
        }
      }
  }
</script>
<style lang="less">
  @import "./smallMenus.less";
</style>
