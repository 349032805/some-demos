<template>
   <div class="my-order" v-infinite-scroll="_loadList" infinite-scroll-disabled="loading" infinite-scroll-distance="10">
      <div class="item" v-for="(item,index) in orderList" :key="index">
        <div class="store bottom-line" @click="goShopMenu(item.orderId)">
          <p class="store-name">{{item.fsshopname}}</p>
          <p class="order-status" v-if="item.status == 4">已支付</p>
          <p class="order-status" v-if="item.status == 5 || item.status == 6 || item.status==7">已取消</p>
        </div>
        <div class="detail" @click="goDetail(item.orderId)">
            <p class="clearfix"><span class="pull-right">{{item.eatNumber}}</span><span class="text">下单时间</span><span class="val">{{item.createTimeStr}}</span></p>
            <p v-if="item.eatStyle == 3 || item.eatStyle == 4"><span class="text">到店时间</span><span class="val">{{item.eatTimeStr}}</span></p>
            <p v-if="item.eatStyle == 3 || item.eatStyle == 4"><span class="text">点餐方式</span><span class="val">提前点餐</span></p>
            <p><span class="text">消费金额</span><span class="val">¥{{item.paidMoney}}</span></p>
        </div>
      </div>
      <load-more type='flower' :spinnerTipstyles="spinnerTipstyles" v-show='loadingTip'></load-more>
      <!-- 提示没有更多了 -->
      <div class="no-more" v-if="showNoMore && orderList.length>0">没有更多记录了 ~</div>
      <div class="no-record" v-if="req == true && orderList.length == 0">
          <img src="../../assets/no_order.png">
          <p>您还没有订单记录</p>
      </div>
   </div>
</template>

<script>
  import {LoadMore} from '@/crib-zk';
  import { forbidWXshare } from "@/utils/forbidWXshare.js";
  const SUCCESS = 200;
  export default {
    components : {
      LoadMore
    },
    data () {
      return {
        orderList: [],
        req: false, //是否请求过了
        pageNum: 0, // 当前第几页
        pageSize: 10, //一页的长度
        total: 0, //数据总数
        showNoMore: false,
        loading: true,
        loadingTip : false,
        spinnerTipstyles : {
          fontSize:'14px'
        }
      }
    },
    created () {
      this.$store.commit('SAVE_TITLE','我的订单');
      forbidWXshare();
      let token = this.$route.query.token;
      if(token){
        localStorage.token = token;
      }
      // 请求订单列表
      this._loadList();
    },
    methods : {
        _loadList(){
          let _this = this;
          this.loading = true;
          this.$http("get",this.baseUrl + '/getUserOrder',{
            pageNum: _this.pageNum,
            pageSize: _this.pageSize
          }).then((response) => {
            if(response.code==SUCCESS){
              let _moreData = response.model;
              _this.orderList = [..._this.orderList,..._moreData]
              // _this.orderList = _this.orderList.concat(response.model);
              _this.req = true;
              if(_moreData.length >= _this.pageSize){
                _this.pageNum += 1;
                _this.loading= false;
                _this.loadingTip = true;
              }else{
                _this.showNoMore = true;
                _this.loading = true;
                _this.loadingTip = false
              }
            }
          });
      },
      goDetail(orderId){
        this.$router.push({path:`/orderDetail/${orderId}`,query:{from:'1',sign:this.$sign}})
      },
      goShopMenu(orderId){
        this.$router.push({ path: `/menus/${orderId}/${localStorage.token}`, query: {sign:this.$sign} })
      }
    }
  }
</script>
<style lang="less">
  @import './myOrder.less';
</style>
