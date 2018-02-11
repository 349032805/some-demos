<template>
   <div class="cartActivity">

     <!-- 购物车部分 -->
    <div class="cart">
      <ul class="clearfix" v-for='(item,index) in dishList' v-if="dishList.length>0">
        <li style="width:50%;color: #797979;">
          <p class="overflow">{{item.dishname}}</p>
          <p style="font-size:12px;color:#999;">
          (<span>{{item.unit}}</span><span v-if="item.doNameStr">/</span><span v-if="item.doNameStr">{{item.doNameStr}}</span>)
          </p>
          <p style="font-size:12px;color:#999;" v-if="item.sendName"><span class="dot"></span>&nbsp;&nbsp;<span style="color:#666;">送</span>&nbsp;&nbsp;{{item.sendName}}</p>
        </li>
        <li style="width:10%; color: #7c7c7c;" class="text-center">
          <p>x{{item.count}}</p>
          <p style="opacity:0">x</p>
          <p v-if="item.sendCount">x{{item.sendCount}}</p>
        </li>
        <li style="width:20%;color: #ccc;text-decoration:line-through" class="text-right" >
          <p style="color: #ccc;" v-if="item.originTotalPrice">¥{{item.originTotalPrice}}</p>
          <p style="opacity:0" v-if="!item.originTotalPrice">¥</p>
          <p style="opacity:0">¥</p>
          <p style="color: #ccc;" v-if="item.sendTotalPrice">¥{{item.sendTotalPrice}}</p>
        </li>
        <li style="width:20%;color: #7c7c7c;" class="text-right">
          <p>¥{{item.totalPrice}}</p>
          <p style="opacity:0">¥</p>
          <p style="color: #7c7c7c;" v-if="item.sendName">¥0</p>
        </li>
      </ul>
      <ul class="clearfix" v-for='(item,index) in serviceList' v-if="serviceList.length>0 && showService == true">
        <li style="width:50%;color: #797979;">
          <p class="overflow">{{item.serviceName}}</p>
        </li>
        <li style="width:50%;color: #7c7c7c;" class="text-right">
        ¥<span v-if="item.type=='2'">{{item.unitPrice}}</span><span v-if="item.type=='3'">{{serviceTotalPrice}}</span>
        </li>
      </ul>

<!--       <ul class="clearfix">
        <li style="width:50%;color: #797979;">
          <p class="overflow">香葱吴山烤鸡香葱吴山烤鸡香葱吴山烤鸡</p>
          <p style="font-size:12px;color:#999;">(大份/微辣)</p>
        </li>
        <li style="width:10%; color: #7c7c7c;" class="text-center">x1</li>
        <li style="width:20%;color: #ccc;text-decoration:line-through" class="text-right">¥20.00</li>
        <li style="width:20%;color: #7c7c7c;" class="text-right">¥18.00</li>
      </ul>

       <ul class="clearfix">
        <li style="width:50%;color: #797979;">
          <p class="overflow">我是一条鱼</p>
        </li>
        <li style="width:10%; color: #7c7c7c;" class="text-center">x1</li>
        <li style="width:40%;color: #7c7c7c;" class="text-right">¥18.00</li>
      </ul>

       <ul class="clearfix">
        <li style="width:50%;color: #797979;">
          <p class="overflow">吃货吃货吃货</p>
          <p style="font-size:12px;color:#999;"><span class="dot"></span>送 色拉</p>
        </li>
        <li style="width:10%; color: #7c7c7c;" class="text-center">
          <p>x1</p>
          <p>x1</p>
        </li>
        <li style="width:20%;color: #ccc;text-decoration:line-through" class="text-right">
          <p>¥0.00</p>
          <p>¥0.00</p>
        </li>
        <li style="width:20%;color: #7c7c7c;" class="text-right">
          <p>¥18.00</p>
          <p>¥0.00</p>
        </li>
      </ul>

      <ul class="clearfix">
        <li style="width:50%;color: #797979;">
          <p class="overflow">打包费</p>
        </li>
        <li style="width:50%;color: #7c7c7c;" class="text-right">¥18.00</li>
      </ul> -->
    </div>

    <!-- 活动部分 -->
    <!-- <div class="activity top-line">
        <ul class="row clearfix">
          <li style='width:80%'>
            <div class="outer">
              <div class="box" style="background:green">新</div>
            </div>
            <p class="activity-message overflow">新用户下单立减20元</p>
          </li>
          <li style='width:20%' class="discount-price">-¥18</li>
        </ul>

        <ul class="row clearfix">
          <li style='width:80%'>
            <div class="outer">
              <div class="box" style="background:green">新</div>
            </div>
            <p class="activity-message overflow">买一送一【紫甘蓝金枪鱼色拉金枪鱼色拉金枪鱼色拉】</p>
          </li>
          <li style='width:20%' class="discount-price">-¥18</li>
        </ul>

        <ul class="row clearfix">
          <li style='width:80%'>
            <div class="outer">
              <div class="box" style="background:green">新</div>
            </div>
            <p class="activity-message overflow">满150随机赠送【可乐】</p>
          </li>
          <li style='width:20%' class="discount-price">-¥18</li>
        </ul>
    </div> -->


    <div class="activity top-line" v-if="activityList.length>0">
        <ul class="row clearfix" v-for='(item,index) in activityList'>
          <li style='width:80%'>
            <div class="outer">
              <div class="box" :class="whichColor(item.word)">{{item.word}}</div>
            </div>
            <p class="activity-message overflow">{{item.name}}</p>
          </li>
          <li style='width:20%' class="discount-price"><span v-if="item.price!=0">-</span>¥{{item.price}}</li>
        </ul>
       
    </div>

   </div>
</template>

<script>
  export default {
    name : 'cartActivity',
    props : {
      dishList : Array,
      serviceList: Array,
      activityList: Array,
      showService: Boolean,
      totalCount: Number,
      serviceTotalPrice: Number
    },
    methods:{
      whichColor(word){
        if(word == '新'){
          return 'green'
        }

        if(word == '减' || word == '折' || word == '赠'){
          return 'orange'
        }

        if(word == '送' || word == '惠'){
          return 'red'
        }
        
      }
    }
  }
</script>

<style lang="less">
  @import './cartActivity.less';
</style>
