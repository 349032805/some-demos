<template>
   <div class="order-confirm" :style="[styleObj]">
      <div class="layer">
        <div class="price-ban">
          <p class="store-name">{{storeName}}</p>
          <p class="total-price">¥<span>{{nowPrice}}</span></p>
          <p class="origin-price">原价: ¥<span> {{originPrice}}</span></p>
        </div>

        <!-- 点餐方式 -->
        <div class="order-mode">
          <div class="row">
            <div class="two-select">
                <div class="mode-change right-line" @click="displayMode()">
                  <span>{{text}}</span>
                  <span class="triangle" v-if="orderType==2"></span>
                </div>

                <div class="eat-mode">
                  <button type="button" :class="{'active':eatMode == 0}" @click="changeEatMode(0)">堂吃</button>
                  <button type="button" :class="{'active':eatMode == 1}" @click="changeEatMode(1)">打包</button>
                </div>
            </div>
          </div>

          <div class="two-rows" v-show="showOrderMode">
            <div class="row top-line" @click="selectOrderMode(0)">
              <p class="float-left" :class="{'active-row':orderMode == 0}">现场点餐</p>
              <div class="tick" v-show="orderMode==0">
                <img src="../../assets/tick_icon.png">
              </div>
            </div>
             <div class="row top-line" @click="selectOrderMode(1)">
              <p class="float-left" :class="{'active-row':orderMode == 1}">预先点餐</p>
              <div class="tick" v-show="orderMode==1">
                <img src="../../assets/tick_icon.png">
              </div>
            </div>
          </div>

        </div>

      </div>

      <!-- 打包的预约时间和联系方式 -->
       <div class="time-phone" v-show="orderMode == 1">
         <div class="row bottom-line" @click="chooseTime">
           <p class="float-left">预约时间:</p>
           <div class="float-right">
             <span class="col999" v-if="!eatTime">请选择预约时间</span>
             <span class="col666" v-if="dueDate">{{dueDate | formatDate}}</span>
             <span class="col666" v-if="eatTime">{{eatTime}}</span>
             <img src="../../assets/arrow.png" class="arrow">
           </div>
         </div>

         <div class="row" @click="writePhone">
           <p class="float-left">联系方式:</p>
           <div class="float-right">
             <span class="col999" v-if="!rightPhone">请填写手机号码</span>
             <span class="col666" v-if="rightPhone">{{rightPhone}}</span>
             <img src="../../assets/arrow.png" class="arrow">
           </div>
         </div>
       </div>


       <!-- 背景遮罩 --> 
       <div class="bg-layer" v-show="showOrderMode==true" @click="showOrderMode=false"></div>
       <div class="bg-layer" v-show="showPhoneModal==true" style="z-index: 100"></div>

      <!-- 选择时间弹出层 -->
      <mt-popup v-model="popupVisible" position="bottom">
        <div class="time-picker">
          <!--<div class="date-select">
            <ul>
              <li><span>{{new Date() | formatDate}}</span> &nbsp; <span>今天</span></li>
            </ul>
          </div>-->
          <div class="time-select" >
            <ul ref='rightList'>
              <li @click="selectTime(item,index)"  v-for="(item,index) in timeArr" :class="{'active':currentTime==index}">
                <span>{{item.date | showNormalDate}}&nbsp;&nbsp;{{item.time}}</span>
                  <div class="tick" v-show="currentTime == index">
                    <img src="../../assets/tick_icon.png">
                  </div>
              </li>
            </ul>
          </div>
        </div>
      </mt-popup>

      <!-- 填写手机号模态框 -->
      <div class="phone-modal" v-show="showPhoneModal==true">
        <div class="title">
          <span>请绑定手机号</span>
          <div class="close" @click="showPhoneModal=false">
            <img src="../../assets/error.png">
          </div>
        </div>
        <div class='content'>
          <div class="row">
            <input type="tel" placeholder="输入手机号" maxlength="11" v-model="telephone" :disabled="canWrite">
            <countDownBtn v-on:send="getPhone" ref="countDownBtn"></countDownBtn>
            <img src="../../assets/close_icon.png" class="clear" @click="clear('phone')" v-show="showClearPhone">
          </div>
          <div class="row">
            <input type="tel" placeholder="填写验证码" maxlength="4" v-model="verifyCode">
            <img src="../../assets/close_icon.png" class="clear" @click="clear('code')" v-show="showClearVerifyCode" style="right:15px">
          </div>

          <button type="button" class="sure-btn" :class="completePhone" @click="bindPhone()" :disabled="isDisabled">
            <img src="../../assets/loading.gif" class="loading-icon" v-if="showLoading"> 确认
          </button>
        </div>
      </div>

      <!-- 购买清单和优惠清单 -->
      <div class="orderlist-discount">
        <p>待支付：¥{{nowPrice}} 共{{totalCount}}份</p>

        <!-- 调用组件!!!!!!!!!!!!!!!!!!!!!! -->
        <div class="cart-activity">
          <cartActivity  :dishList='newDishList' :serviceList='serviceList' :activityList='activityList' :showService='showServiceRow' :totalCount='totalCount' :serviceTotalPrice='serviceTotalPrice'></cartActivity>
        </div>
      </div>

      <!-- 备注 -->
    <div class="remark" @click="switchRemark()">
        <div class="remark-title">备注</div>
        <div class="content overflow" v-show="displayRemark==false">{{remarkAllStr}}</div>
        <div class="float-right">
          <img src="../../assets/display.png" class="display" :class="{'convert':displayRemark == true}">
        </div>
    </div>

      <div class="remark-detail" v-show="displayRemark == true">
        <div class="split-line"><div class="top-line"></div></div>
        <div class="taste-options">
            <ul class="clearfix">
              <li v-for="(item,index) in demandList"><button type="button" @click="demand(item,index)" :class="{'current-select':item.flag == true}">{{item.fsaskname}}</button></li>
            </ul>
          </div>

          <div class="other-demand">
            <input type="text" placeholder="其他偏好或要求" maxlength="50" v-model="remarkInput">
          </div>
      </div>

      <!-- 开具发票 -->
      <div class="invoice" v-if="showInvoice== true || showInvoice== 'true'">
        <div class="invoice-switch">
          <p class="float-left">开具发票</p>
          <div class="float-right" style="padding-top:10px;">
            <mt-switch v-model="showInvoiceWrite"></mt-switch>
          </div>
        </div>

        <!-- 填写发票信息 -->
        <div class="invoice-write top-line" v-show="showInvoiceWrite">
          <div class="invoice-type">
            <span style="margin-right:30px;">抬头: </span>
            <div @click="selectTitle()" style="margin-right:30px;"><div class="option" :class="{'type-tick': isCom,'type-circle':!isCom}"></div> &nbsp;<span>公司发票</span></div>
            <div @click="selectTitle2()"><div class="option" :class="{'type-tick': isPerson,'type-circle':!isPerson}"></div> &nbsp;<span>个人发票</span></div>
          </div>

          <input type="text" placeholder="请输入发票抬头" maxlength="30" v-model="invoiceHeader" v-show="isCom == true">
        </div>
      </div>

      <!-- 支付按钮 -->
      <button type="button" class="pay-btn" @click="gotoPay()">
        <span style="font-size: 18px;">微信支付 </span> <span style="font-size: 25px;">¥{{nowPrice}}</span>
      </button>

      <!-- 点餐完成后的提示框 -->
      <div class="bg-layer" v-show="showRemindModal==true" style="z-index: 100"></div>
      <div class="remind-modal" v-show="showRemindModal==true">
        <div class="title">支付完成后凭取餐号取餐</div>
        <img src="../../assets/remind.png" class="remind">
        <div class="no-next bottom-line">
          <div @click="cancelTip()">
            <div class="box"><img src="../../assets/tick_icon.png" v-show="showTip==false"></div> <span>下次不再提示</span>
          </div>
        </div>
        <div class="isee" @click="isee()">知道了</div>
      </div>

      <!-- 提示售罄 -->
     <!--  <confirm :dialog.sync="showConfirm" title="提示" :content="confirmText" confirmText="继续下单" @confirm="confirm()" cancelText="返回继续点餐"></confirm> -->

   </div>
</template>

<script>
 
  import cartActivity from '@/vistitorComponents/cartActivity/cartActivity';
  import countDownBtn from '@/components/countDownBtn/countDownBtn';
  import {demo} from './confirm.js';
  import {splitArray} from './hour.js';
  import {getSys} from '@/utils/system.js';

  const SUCCESS = 200;
  export default {
    filters: {
      formatDate(date) {
        let date2 = new Date(date);
        let year = date2.getFullYear();
        let month = (date2.getMonth()+1);
        let day = date2.getDate();
        return month+"月"+day+"日";
      },
      showNormalDate(dateStr){
        if(dateStr =="today"){
          let d = new Date();
          let year = d.getFullYear();
          let month = (d.getMonth()+1);
          let day = d.getDate();
          return month+"月"+day+"日";
        }else{
          let d = new Date();
          let year = d.getFullYear();
          let month = (d.getMonth()+1);
          let day = d.getDate()+1;
          return month+"月"+day+"日";
        }
      }
    },
    data () {
      return {
        storeId:'',
        storeName: '',
        tableId: '',
        businessTimeList:[],
        eatMode: 0, // 0堂食 1打包
        orderMode: 0, //0 现场点餐 1预先点餐
        showOrderMode: false,
        popupVisible: false,
        showRegisterPhone: false,
        showPhoneModal: false,
        text: '现场点餐',
        remarkInput:"",
        remarkArr: [],
        remarkAllStr:"",
        displayRemark: false,
        telephone: "",
        eatTime: "",
        rightPhone:"",
        verifyCode: "",
        isDisabled: true,
        showLoading: false,
        showRemindModal: false,
        showTip: false,
        showInvoice: false,
        showInvoiceWrite: false,
        isCom: true,
        isPerson: false,
        invoiceHeader: "",
        demandList:[],
        timeArr: [],
        timeList: [],
        dishList: [],
        newDishList: [],
        activityList: [],
        discountList: [],
        originPrice: 0,
        nowPrice: 0,
        serviceList: [],
        noDiscountTotal: 0,
        serviceTotalPrice: 0,
        currentTime: 0,
        showServiceRow: false,
        canWrite : false,
        totalCount: 0,
        orderType: 1,
        serviceType: 0,
        serviceUnitPrice: 0,
        dueDate: ""
      }
    },
    created () {
      this.$store.commit('SAVE_TITLE','订单确认');

      // 确认提示框  
      // this.$crib.confirm.show({
      //   title : " 提示",
      //   headStyle : {fontSize:'18px'},
      //   content : "您点的【香葱吴山烤鸡】刚刚售罄，请再看看其他菜品吧！",
      //   confirmText : "继续下单",
      //   cancelText : "返回继续点餐",
      //   cancelStyle : {color:'#06C1AE'},
      //   confirmStyle : {color:'#333'},
      //   // onCancel : function(){
          
      //   // },
      //   onConfirm:function(){
      //     alert(2)
      //   }
      // });

      //从localStorage获取shopId,shopName,tableId,营业时间段,是否支持发票,点餐方式
      this.storeId = localStorage.getItem('shopId');
      this.storeName = localStorage.getItem('shopName');
      let tabId = localStorage.getItem('tableId');
      if(tabId != undefined && tabId != "" && tabId != 'null' && tabId != null){
        this.tableId = tabId;
      }
      let businessHoursList = localStorage.getItem('businessHoursList');
      businessHoursList = JSON.parse(businessHoursList);
      console.log("-----------------------营业时间原始时间--------------");
      console.log(businessHoursList);
      console.log("-----------------------营业时间原始时间--------------");

      // let businessHoursList = ["09:00:00-02:59:59"];
      // console.log("-----------------------营业时间测试时间--------------");
      // console.log(businessHoursList);
      // console.log("-----------------------营业时间测试时间--------------");


      this.timeArr = splitArray(businessHoursList);
      console.log("-----------------------营业时间处理过后--------------");
      console.log(this.timeArr)
      console.log("-----------------------营业时间处理过后--------------");
      this.showInvoice = localStorage.getItem('invoiceFlag');

      this.orderType = Number(localStorage.orderType);


      //从localStorage获取菜品数组并进行处理
      this._getDishListAndBuild();
      console.log("-----------------------处理过的菜品集合--------------");
      console.log(this.newDishList);
      console.log("-----------------------处理过的菜品集合--------------");

      //从localStorage获取活动并计算活动
      //newDishList可能已经改变,所以重新取出来
      this._dealActivity();
   

      //调用服务费和备注的接口,
      this._getServiceChargeAndRemark();

      // 从localStorage取发票抬头
      this._getTitle();

      //获取用户的资料,拿到手机号
      this._getUser();

    },
    methods : {
      writePhone(){
        this.telephone = "";
        this.verifyCode = "";
        this.showPhoneModal = true;
      },
      changeEatMode(num){
          if(this.eatMode !=num){
            this.eatMode = num;
            this.showServiceRow = !this.showServiceRow;
            if(num == 0){
              this.nowPrice = parseFloat(this.nowPrice )- parseFloat(this.serviceTotalPrice);
              this.originPrice = parseFloat(this.originPrice) -parseFloat(this.serviceTotalPrice);
            }else{
              this.nowPrice = parseFloat(this.nowPrice) + parseFloat(this.serviceTotalPrice);
              this.originPrice = parseFloat(this.originPrice) + parseFloat(this.serviceTotalPrice);
            }
            this.nowPrice = Number(this.nowPrice).toFixed(2);
            this.originPrice = Number(this.originPrice).toFixed(2);
          }
      },
      _dealActivity(){ //处理活动
        let shopActivityList = localStorage.getItem('shopActivityList');

        //如果没有活动的情况
        if(shopActivityList != "" && shopActivityList != 'null'){
          shopActivityList = JSON.parse(shopActivityList);
          //活动集合,原总价,已点菜品集合
          this.activityList= demo(shopActivityList,this.noDiscountTotal,this.newDishList);
          console.log("----------------------- 最终活动集合--------------");
          console.log(this.activityList);
          console.log("-----------------------最终活动集合--------------");

           let newDishList = localStorage.getItem('newDishList');
            this.newDishList = JSON.parse(newDishList);

            //计算原总价和现总价
            this.newDishList.forEach(item=>{
                if(item.originTotalPrice){
                  this.originPrice += item.originTotalPrice;
                }else{
                  this.originPrice += item.totalPrice;
                }
            })
            this.originPrice = Number(this.originPrice).toFixed(2);

            //从localStoage里拿到计算活动后的总价finalPriceNoService
            let finalPriceNoService = localStorage.getItem('finalPriceNoService');
            finalPriceNoService = Number(finalPriceNoService);
            this.nowPrice = finalPriceNoService.toFixed(2);

            }else{
              this.nowPrice = this.noDiscountTotal;
              this.newDishList.forEach(item=>{
                  if(item.originTotalPrice){
                    this.originPrice += item.originTotalPrice;
                  }else{
                    this.originPrice += item.totalPrice;
                  }
                })
              this.originPrice = Number(this.originPrice).toFixed(2);
            }
      },
      _getDishListAndBuild(){
        let cartFoods = localStorage.getItem('cartFoods');
        this.dishList = JSON.parse(cartFoods);
        // console.log("-----------------------已点的菜品--------------");
        // console.log(this.dishList);
        // console.log("-----------------------已点的菜品--------------");

        //可能存在同一个菜品多个规格,所以将菜品拆分多个对象
        this.dishList.forEach(item=>{
          if(item.isShowUnit == 0){
            item.menuItemUnitList.forEach(attr=>{
            if(attr.count){
              let dish = {};

              let doIdList = []; //做法id
              let doNameList = []; //做法名称
              let doPrice = 0;

              if(attr.askList.length>0){
                attr.askList.forEach(doMode=>{
                  doMode.menuItemAskOutVoList.forEach(doStyle=>{
                    if(doStyle.checked == true){
                      doIdList.push(doStyle.fsaskid);
                      doNameList.push(doStyle.fsaskname);
                      doPrice += doStyle.fdaddprice;
                    }
                  });

                });
              }

              dish.doIdList = doIdList;
              dish.doNameList = doNameList;
              dish.doNameStr = doNameList.join("/");

              dish.fsitemid = item.fsitemid;
              dish.dishname = item.fsitemname;
              dish.count = attr.count;

              //加入总份数
              this.totalCount += dish.count;

              if(attr.bargainPrice){ //bargainPrice 现价  fdsaleprice 原价
                 dish.nowPrice = attr.bargainPrice + doPrice;
                  dish.originPrice = attr.fdsaleprice + doPrice;
                  dish.originTotalPrice = parseFloat((dish.originPrice*dish.count).toFixed(2));
               }else{
                dish.nowPrice = attr.fdsaleprice + doPrice;
               }

              dish.unit = attr.fsorderunit;
              dish.totalPrice = parseFloat((dish.nowPrice*dish.count).toFixed(2));
              this.newDishList.push(dish);
            }

            });
          }else{
            let dish = {};
            dish.fsitemid = item.fsitemid;
            dish.dishname = item.fsitemname;

            dish.count = item.count;

             //加入总份数
              this.totalCount += dish.count;

              if(item.menuItemUnitList[0].bargainPrice){ //bargainPrice 现价  fdsaleprice 原价
                 dish.nowPrice = item.menuItemUnitList[0].bargainPrice;
                  dish.originPrice = item.menuItemUnitList[0].fdsaleprice;
                  dish.originTotalPrice = parseFloat((dish.originPrice*dish.count).toFixed(2));
               }else{
                dish.nowPrice = item.menuItemUnitList[0].fdsaleprice;
               }

            dish.unit = item.menuItemUnitList[0].fsorderunit;
            dish.totalPrice = parseFloat((dish.nowPrice*dish.count).toFixed(2));
            this.newDishList.push(dish);
          }
        });

        this.newDishList.forEach(item=>{
          this.noDiscountTotal += parseFloat(item.totalPrice);
        });

        this.noDiscountTotal = this.noDiscountTotal.toFixed(2);

        console.log("现价总和(不包括活动):"+this.noDiscountTotal);

        window.localStorage.setItem('newDishList', JSON.stringify(this.newDishList));

      },
      chooseTime () {
        this.popupVisible = true;
      },
      selectTime(timeObj,index){
       this.currentTime = index;
       this.popupVisible = false;
       this.eatTime = timeObj.time;
       console.log("预约日期:"+timeObj.date);
       console.log("eatTime:"+this.eatTime);

       if(timeObj.date =="today"){
          this.dueDate = new Date();
       }else{ //tomorrow
          let d = new Date();
          let nextDate = new Date(d.getFullYear(),d.getMonth(),d.getDate()+1);
          console.log("预约日期为明天:"+nextDate);
          this.dueDate = nextDate;
       }
      },
     
      displayMode(){
        if(this.orderType ==2){
          this.showOrderMode = !this.showOrderMode;
        }
      },
      selectOrderMode(mode){
        this.orderMode = mode;
        this.showOrderMode = false;
        if(this.orderMode == 0){
          this.text = "现场点餐";
        }else{
          this.text = "预先点餐";
        }
      },
    
      clear(obj){
        if(obj == "phone"){
          this.telephone = "";
        }else{
          this.verifyCode = "";
        }
      },
      getPhone(){
        let flag = this._verifyPhone();
        if(flag){
          //按钮倒计时
          this.$refs.countDownBtn.start = true;
          this.$emit('send');

          //发送短信
          this._sendMessage();
        }
      },
      bindPhone(){
        let flag = this._verifyPhone();
        if(!flag){
          return;
        }
        if(!this.verifyCode){
          this.$toast("请输入验证码");
          return;
        }
        //验证 验证码
        this._commitPhone();
      },
      _verifyPhone(){
        let telephone = this.telephone;
        let flag = true;
        if(telephone){
           if(!/^1\d{10}$/gi.test(telephone)){
              this.$toast("手机号不合法");
              flag = false;
            }
        }else{
          this.$toast("请输入手机号");
          flag = false;
        }

        if(this.rightPhone == telephone){
          flag = false;
          this.$toast("手机号已存在");
        }

        return flag;
      },
      _sendMessage(){
        console.log("发送短信");
        this.$http("get",this.baseUrl + '/phoneCodes',{phone: this.telephone});

      },
      _commitPhone(){
        console.log("验证验证码");
        this.showLoading = true;
        this.$http("post",this.baseUrl + '/checkSms',{phone: this.telephone,validateCode:this.verifyCode}).then((response) => {
           this.showLoading = false;
          if(response.code==SUCCESS){
            this.showPhoneModal = false;
             //请求后赋值
            this.rightPhone = this.telephone;
          }else{
            this.$toast(response.message);
          }
        });

      },
      cancelTip(){
        this.showTip = !this.showTip;
        //将结果保存在localStorage
        localStorage.setItem("bwy_showTip",this.showTip);
      },
      gotoPay(){
        //判断是否需要提醒
        let remind = localStorage.getItem('bwy_showTip');
        if(remind == undefined){
          this.showRemindModal = true;
        }


        if(remind == true || remind =='true'){
          this.showRemindModal = true;
        }

        if(remind == false || remind =='false'){
          this._commitForm();
        }
      
      },
      isee(){
        localStorage.setItem("bwy_showTip",this.showTip);
        this.showRemindModal = false;
        this._commitForm()
      },
      selectTitle(){
        this.isCom =true;
        this.isPerson =false;
      },
      selectTitle2(){
        this.isPerson =true;
        this.isCom =false;
      },
      demand(option,index){
        this.demandList[index].flag = !this.demandList[index].flag;
        if(option.flag){
          this.remarkArr.push(option.fsaskname);
        }else{
          this.remarkArr.forEach((item,index)=>{
            if(option.fsaskname == item){
              this.remarkArr.splice(index,1);
            }
          });
        }
        this.remarkAllStr = this.remarkArr.join(",");
        console.log("选择的备注:"+this.remarkAllStr);
      },
      switchRemark(){
        //将需求输入的字符放入数组
        //判断依据:1.非空,2.不存在目标数组
        if(this.remarkInput){
          let isExist = false;
          this.remarkArr.forEach((item,index)=>{
            if(item == this.remarkInput){
              isExist = true;
            }
          });
          if(!isExist){
            this.remarkArr.push(this.remarkInput);
          }
        }else{
          this.remarkArr = [];
          this.demandList.forEach(item=>{
            if(item.flag){
              this.remarkArr.push(item.fsaskname);
            }
          });
        }
        this.remarkAllStr = this.remarkArr.join(",");
        this.displayRemark = !this.displayRemark;
        console.log("折起选择的备注:"+this.remarkAllStr);
      },
      _getServiceChargeAndRemark(){
        let storeId = localStorage.getItem('shopId');
        this.$http("get",this.baseUrl+'/getOrderExtraInfo',{shopId:storeId}).then((response) => {
          if(response.code==SUCCESS){
            this.demandList = response.model.menuItemAskOutVoList;
            this.demandList.forEach(item=>{
              this.$set(item,'flag',false);
            });
            if(response.model.orderServiceFeeOutVoList){
              this.serviceList = response.model.orderServiceFeeOutVoList;
              if(this.serviceList.length>0){
                this.serviceList.forEach(item=>{
                  this.serviceType = item.type;

                  //1,没有服务费
                  if(item.type == 1 || item.type == '1'){
                    this.serviceList = [];
                  }

                  //2,固定费用
                  if(item.type == 2 || item.type == '2'){
                    this.serviceTotalPrice=item.unitPrice;
                    this.serviceUnitPrice = item.unitPrice;
                  }

                  //3,乘法费用
                  if(item.type == 3 || item.type=='3'){
                    this.serviceTotalPrice=(item.unitPrice*this.totalCount).toFixed(2);
                    this.serviceTotalPrice = parseFloat(this.serviceTotalPrice);
                  }
                })

              }
            }
          }
        });
      },
      _getTitle(){
        let invoiceHeader = localStorage.getItem('bwy_invoiceHeader');
        if(invoiceHeader){
          this.invoiceHeader = invoiceHeader;
        }
       
      },
      _getUser(){
        this.$http("get",this.baseUrl + '/getUserInfo').then((response) => {
          if(response.code==SUCCESS){
            if(response.model.phone){
              this.rightPhone = response.model.phone;
            }
          }
        });
      },
      formatToFullDate(date){
        let year = date.getFullYear();
        let month = (date.getMonth()+1);
        if(month<10){
          month = "0"+month;
        }
        let day = date.getDate();
        if(day <10){
          day = "0"+day;
        }
        return year+"-"+month+"-"+day;
      },
      _commitForm(){
        //看是堂食还是打包,如果是打包,判断是否选择了预约时间和电话
        if(this.orderMode == 1){
          if(!this.eatTime){
            this.$toast("请选择预约时间");
            return;
          }

          if(!this.rightPhone){
            this.$toast("请填写联系方式");
            return;
          }
        }

        //判断是否支持发票
        //看发票开关是否开着
        //如果开着,如果是公司发票,判断是否填写了抬头
        if(this.showInvoice == true || this.showInvoice =='true'){
          if(this.showInvoiceWrite){
            //公司纸质
            if(this.isCom){
              if(!this.invoiceHeader){
                this.$toast("请填写抬头");
                return;
              }
            }

          }
        }

        //**********************************


        //查看备注
        let optionArr = [];
       this.demandList.forEach(item=>{
          if(item.flag){
            optionArr.push(item.fsaskname);
          }
        });

       let optionStr = optionArr.join(",");
       if(this.remarkInput){
        optionStr = optionStr+"!@#"+this.remarkInput;
       }
       console.log("最终的备注选项字符:"+optionStr);
      //**********************************

      //就餐类型 1.现点堂吃 2.现点打包 3.预点堂吃 4.预点打包
      let eatStyle = 0;
      if(this.orderMode == 0 && this.eatMode == 0){
        eatStyle = 1;
      }
      if(this.orderMode == 0 && this.eatMode == 1){
        eatStyle = 2;
      }

      if(this.orderMode == 1 && this.eatMode == 0){
        eatStyle = 3;
      }

      if(this.orderMode == 1 && this.eatMode == 1){
        eatStyle = 4;
      }

      let orderMenuitemInVoList = [];
      this.newDishList.forEach((item,index)=>{
       if(item.doIdList){
         if(item.doIdList.length>0){
              item.doIdList.forEach((doStr,index)=>{
              let obj = {};
              obj.fsAskId = doStr;
              obj.fsitemid = item.fsitemid;
              obj.number = item.count;
              obj.price = item.nowPrice;
              obj.unit = item.unit;
              orderMenuitemInVoList.push(obj);
            })
         }else{
          let obj = {};
          obj.fsAskId = "";
          obj.fsitemid = item.fsitemid;
          obj.number = item.count;
          obj.price = item.nowPrice;
          obj.unit = item.unit;
          orderMenuitemInVoList.push(obj);
         }
       }else{
         let obj = {};
          obj.fsAskId = "";
          obj.fsitemid = item.fsitemid;
          obj.number = item.count;
          obj.price = item.nowPrice;
          obj.unit = item.unit;
          orderMenuitemInVoList.push(obj);
       }
      });

      let orderTicketInVo = {};

      if(this.showInvoice == true || this.showInvoice =='true'){
        if(this.showInvoiceWrite){
          if(this.isCom){
            orderTicketInVo.invoiceType = 3;
          }
           if(this.isPerson){
            orderTicketInVo.invoiceType = 1;
          }
        }else{
          orderTicketInVo.invoiceType = 0;
        }
      }else{
        orderTicketInVo.invoiceType = 0;
      }

      orderTicketInVo.ticketHeader = this.invoiceHeader;

      let fromatTime = "";
      if(this.orderMode == 1){
        fromatTime = this.formatToFullDate(this.dueDate) + ' '+this.eatTime+':00';
      }

      let orderInVo = {};
      orderInVo.eatStyle = eatStyle;
      orderInVo.eatTime = fromatTime;
      orderInVo.fsShopGUID = this.storeId;
      orderInVo.fsmtableid = this.tableId;
      orderInVo.orderMenuitemInVoList = orderMenuitemInVoList;
      orderInVo.orderTicketInVo = orderTicketInVo;
      orderInVo.remark = optionStr;

      //手机号
      orderInVo.phone = this.rightPhone;

      //传给后台原价和现价
      orderInVo.paidMoney = this.nowPrice;
      orderInVo.totalprice = this.originPrice;
         
      console.log("*************orderInVo**************");
      console.log(orderInVo);
      console.log("************orderInVo***************");

      const _this = this;
     this.$http("post",this.baseUrl+'/createOrder',orderInVo,'json').then((response) => {
        if(response.code==SUCCESS){

          //判断如果现价等于0,直接跳详情页面,否则跳支付
          if(this.nowPrice ==0 ||this.nowPrice=="0" || this.nowPrice=="0.00"){
            let orderId = response.model.orderId;
            this.$router.push({name:'orderDetail',params:{orderId:orderId}});
          }else{
            let payUrl = response.extra.url;
            window.location.href=payUrl;
          }

          //清除已点菜品
          sessionStorage.removeItem('sessionfoods');
   
        }

        if(response.code==500){ //有估清菜品
            if(response.errorCode==5000058){
               this.$crib.confirm.show({
                  title : " 提示",
                  headStyle : {fontSize:'18px'},
                  content : response.message,
                  confirmText : "继续下单",
                  cancelText : "返回继续点餐",
                  cancelStyle : {color:'#06C1AE'},
                  confirmStyle : {color:'#333'},
                  onCancel : function(){
                    //返回点菜页
                     _this.$router.go(-1);
                  },
                  onConfirm:function(){
                    //关闭弹窗,并重新展示菜品列表,和计算价格

                    //获取估清的菜品集合
                    let emptyDishList = response.extra.item;
                    console.log("------------估清的菜品集合-----------");
                    console.log(emptyDishList);
                    console.log("------------估清的菜品集合-----------");
                    _this.newDishList.forEach((item,index)=>{
                       emptyDishList.forEach(emptyObj=>{
                        if(item.fsitemid == emptyObj.fsItemId && item.unit == emptyObj.fsItemUnit){
                          _this.newDishList.splice(index,1);
                        }
                      });
                    });


                    console.log("------------处理估清的菜品之后的菜品列表-----------");
                    console.log(_this.newDishList);
                    console.log("------------处理估清的菜品之后的菜品列表-----------");

                    _this.noDiscountTotal = 0;
                    _this.totalCount = 0;
                    _this.nowPrice = 0;
                    _this.originPrice = 0;
                    _this.serviceTotalPrice = 0;
                    _this.newDishList.forEach(item=>{
                      _this.noDiscountTotal += parseFloat(item.totalPrice);
                      _this.totalCount += item.count;
                    });

                    if(_this.serviceType == 2 || _this.serviceType=='2'){
                      _this.serviceTotalPrice = _this.serviceUnitPrice;
                    }

                     if(_this.serviceType == 3 || _this.serviceType=='3'){
                      _this.serviceTotalPrice = (_this.serviceUnitPrice*_this.totalCount).toFixed(2);
                    }

                    _this.noDiscountTotal = _this.noDiscountTotal.toFixed(2);

                    console.log("------------处理估清的菜品之后的菜品价格总和-----------");
                    console.log(_this.noDiscountTotal);
                    console.log("------------处理估清的菜品之后的菜品价格总和-----------");

                    window.localStorage.setItem('newDishList', JSON.stringify(_this.newDishList));

                    //重新处理一遍活动
                    _this._dealActivity();

                  }
                });
            }else{
              _this.$toast(response.message);
            }
        }

      });


      }
    },
    computed: {
      showClearPhone(){
        // if(this.telephone.length > 0){
        //   return true;
        // }else{
        //   return false;
        // }

        if(this.telephone.length > 0 && this.telephone.length<11){
          return true;
        }else{
          return false;
        }
      },
      showClearVerifyCode(){
        // if(this.verifyCode.length > 0){
        //   return true;
        // }else{
        //   return false;
        // }

        if(this.verifyCode.length > 0 && this.verifyCode.length <4){
          return true;
        }else{
          return false;
        }
      },
      completePhone(){
        // if(this.telephone.length == 11){
        //   if(/^1\d{10}$/gi.test(this.telephone)){
        //     this.isDisabled = false;
        //     return "active-btn";
        //   }
        // }else{
        //   return "";
        // }

        if(this.telephone.length > 0){
          this.isDisabled = false;
          return "active-btn";
        }else{
          return "";
        }


      },
      styleObj () {
        if(getSys() == "android"){
          return {paddingTop : '158px'}
        }else{
          return {paddingTop : '167px'}
        }
      }
    },
    components: {
      cartActivity,
      countDownBtn
    }
  }
</script>

<style lang="less">
  @import './confirm.less';
</style>
