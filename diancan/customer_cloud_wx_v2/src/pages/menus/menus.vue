<template>
     <div class="menus">
         <menu-header :shopInfo="shopInfo" :shopActivityCount="shopActivityCount"></menu-header>
         <div v-if='swiperList.length>0' class="menus-swiper">
            <swiper :type='2' :swiperList='swiperList'></swiper>
         </div>
         <components :is="currentView">

         </components>
     </div>
</template>
<script>
    import Vue from 'vue'
    import {MessageBox} from 'mint-ui';
    Vue.component(MessageBox.name, MessageBox);
    import MenuHeader from '@/vistitorComponents/menuHeader/menuHeader.vue'
    import SmallMenus from '@/vistitorComponents/smallMenus/smallMenus.vue'
    import BigMenus from '@/vistitorComponents/bigMenus/bigMenus.vue'
    import Swiper from '@/vistitorComponents/swiper/swiper.vue'
    export default {
        components : {
          MenuHeader,
          SmallMenus,
          BigMenus,
          Swiper
        },
        data () {
            return {
                currentView : '',
                openTime : '',
                shopId:'',
                shopInfo : {},
                shopActivityCount : 0,
                swiperList : {}
            }
        },
        created(){
            this.shopId = this.$route.params.shopId;
            if(!this.shopId){
                this.shopId = localStorage.getItem("shopId");
            }
            let tableId = this.$route.query.tableId;
            if(!tableId){
                tableId = sessionStorage.getItem("tableId");
            }
            let token = this.$route.params.token;
            if(!token){
                token = this.$route.query.token;
                if(!token){
                   token = localStorage.getItem("token");
                }
            }
            if(token){
                localStorage.setItem("shopId",this.shopId);
                sessionStorage.setItem("tableId",tableId);
                localStorage.setItem("token",token);
            }
            this.$store.commit('SAVE_TITLE','百味云点餐系统');
            //获取店铺详情
            this.getShopInfo();
            //获取未完成的订单
            this.getUserOrder();
            //判断
            this.$http('get',this.baseUrl+'/getMenusTypeSetting',{
                shopId : this.shopId
            }).then((res) =>{
                if(res.code = 200){
                    if(res.model==1){//1：左右排版，2：上下排版
                        this.currentView = SmallMenus;
                    }else{
                        this.currentView = BigMenus;
                    }
                }else{
                    alert(res.message);
                }
            })
        },
        methods : {
            getShopInfo(){
                // 获取店铺详情
                this.$http('get',this.baseUrl+'/getShopInfo',{
                    shopId : this.shopId
                }).then((res) =>{
                    if(res.code == 200){
                       this.openTime = res.model.openTime;
                       this.shopInfo = res.model;
                       this.shopActivityCount = 0;
                       if(res.model.shopActivityOutVoList){
                           this.shopActivityCount = res.model.shopActivityOutVoList.length;
                       }
                       localStorage.setItem('businessHoursList',JSON.stringify(res.model.businessHoursList));//营业时间
                       localStorage.setItem('shopName',res.model.fsShopName);//门店名称
                       localStorage.setItem('invoiceFlag',res.model.isSupportInvoice);//是否支持发票
                       localStorage.setItem('orderType',res.model.orderType);//订单类型
                       localStorage.setItem('isSupportDelivery',res.model.isSupportDelivery);//送餐模式
                       let _isRest = res.model.isRest;
                       if(_isRest==0||_isRest==2){//0餐厅休息，1，餐厅营业，2，停业时间
                           this.$store.commit('SAVE_SHOP_STATUS',res.model.isRest);
                           let closeTime = res.model.closingTimeInfo;
                           let timeRange = "";
                           if(closeTime){
                                timeRange = closeTime.startDate+"至"+closeTime.endDate;
                           }
                           let _text = "";
                           if(_isRest==0){
                                _text = "当前非营业时间，";
                           }else if(_isRest==2){
                               _text = '该餐厅在'+timeRange+'休息，';
                           }
                           MessageBox({
                                title: '提示',
                                message: _text +'暂不提供点餐服务，敬请见谅！',
                                showCancelButton: false
                           });
                       }
                    }else{
                        alert(res.message);
                    }
                })
            },
            getUserOrder(){
                this.$http('get',this.baseUrl+'/getUserShopOrder',{
                    shopId:this.shopId,
                    pageNum :0,
                    pageSize:100
                }).then(res=>{
                    if(res.code == 200){
                        this.swiperList = res.model;
                    }
                })
            }
        }
    }
</script>
<style lang="less">
  @import "./menus.less";
</style>
