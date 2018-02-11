<template>
   <div class="my-order" v-infinite-scroll="_loadList"
  infinite-scroll-disabled="loading" infinite-scroll-distance="10">

      <div class="item" v-for="(item,index) in orderList" @click="goDetail(item.orderId)">
        <div class="store bottom-line">
          <p class="store-name">{{item.fsshopname}}</p>
          <p class="order-status" v-if="item.status == 4">已支付</p>
          <p class="order-status" style="color: #646464" v-if="item.status == 5 || item.status == 6 || item.status==7">已取消</p>
        </div>

        <!-- {{item.createTime | formatDate}} -->
        <div class="detail">
            <p v-if="item.eatStyle == 3 || item.eatStyle == 4">{{item.createTimeStr}}</p>
            <p>
              <span>{{isWhichTimeWord(item.eatStyle,item.isSupportDelivery)}}时间</span>&nbsp;&nbsp;<span>{{isWhichTime(item.eatStyle,item.createTimeStr,item.eatTimeStr)}}</span>
            </p>
            <!-- 7.25日下午16:30和产品确认,这里只显示就餐号 -->
            <p><span>就餐号</span>&nbsp;&nbsp;<span>{{item.eatNumber}}</span></p>
            <p>消费金额&nbsp;&nbsp;<span>¥{{item.paidMoney}}</span></p>

            <div class="pre-order" v-if="item.eatStyle == 3 || item.eatStyle == 4">
              <div class="box">预点</div>
            </div>
        </div>
      </div>

       <load-more type='flower' :spinnerTipstyles="spinnerTipstyles" v-show='loadingTip'></load-more>
      <!-- 提示没有更多了 -->
      <div class="no-more" v-if="showNoMore && orderList.length>0">没有更多记录了 ~</div>

      <!-- 无记录提示 -->
      <div class="no-record" v-if="req == true && orderList.length == 0">
        <img src="../../assets/no_order.png">
        <p>您还没有订单记录</p>
      </div>

   </div>
</template>

<script>
  // import {formatDate} from '@/utils/date.js';
  import {LoadMore} from '@/crib-zk';
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
      let token = this.$route.query.token;
      if(token){
        console.log("从地址栏获取的token:"+token);
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
          _this.orderList = _this.orderList.concat(response.model);
          _this.req = true;
          if(response.model.length >= _this.pageSize){
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
       this.$router.push({path:`/orderDetail/${orderId}`,query:{from:'1'}});
     },
     isWhichTimeWord(eatStyle,isSupportDelivery){
      if(eatStyle == 1 || eatStyle ==2){
        return "下单"
      }else{
        if(isSupportDelivery ==1){ //送餐
          if(eatStyle ==3){
            return "就餐"
          }else{ // eatStyle=4
            return "取餐"
          }
        }else{ //isSupportDelivery =2 取餐
          return "取餐"
        }
      }

     },
     isWhichTime(eatStyle,createTimeStr,eatTimeStr){
        if(eatStyle == 1|| eatStyle ==2){
          return createTimeStr
        }else{
          return eatTimeStr;
        }
     }
    },
    // filters: {
    //   formatDate(time) {
    //     let date = new Date(time);
    //     return formatDate(date, 'yyyy-MM-dd hh:mm');
    //   }
    // },
  }
</script>

<style lang="less">
  @import './myOrder.less';
</style>
