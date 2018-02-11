<template>
  <section class="order-text">
    <div class="title-box" @click="listShow=!listShow">
        <span class="pull-right pre-order" v-show="list.eatTimeStr">{{splitTime}}到店</span>
        订单信息
        <span class="right-icon" :class="{'rotate':listShow}"><img src="../../../assets/arrow.png"></span>
    </div>
    <div class="order-content" v-show="listShow">
        <div class="clearfix" v-if='list.eatStyle==3 || list.eatStyle ==4'><span>到店时间：</span><p>{{list.eatTimeStr}}</p></div>
        <div class="clearfix" v-if="list.phone"><span>取餐电话：</span><p>{{list.phone}}</p></div>
        <div class="clearfix"><span>订单编号：</span><p>{{list.orderNo}}</p></div>
        <div class="clearfix"><span>下单时间：</span><p>{{list.payConfigTimeStr}}</p></div>
        <div class="clearfix"><span>支付方式：</span><p>{{payType}}</p></div>
        <div class="clearfix" v-if='orderTicketOutVo&&orderTicketOutVo.ticketHeader'><span>发票抬头：</span><p>{{orderTicketOutVo.ticketHeader}}</p></div>
        <div class="clearfix" v-if="invoiceType"><span>发票形式：</span><p>{{invoiceType}}</p></div>
        <div class="clearfix" v-if="orderTicketOutVo&&orderTicketOutVo.ticketRatepayNumber"><span>纳税人识别号：</span><p>{{orderTicketOutVo.ticketRatepayNumber}}</p></div>
        <div class="clearfix" v-if='remark'><span>订单备注：</span><p>{{remark}}</p></div>
    </div>
</section>
</template>

<script>
    export default {
        props: {
            list : Object,
            orderTicketOutVo : Object,
        },
        data(){
            return {
                listShow:false
            }
        },
        computed: {
            splitTime(){
                if(this.list.eatTimeStr){
                    return (this.list.eatTimeStr).split(" ")[1];
                }
                return ""
            },
            remark () {
                let _remark = this.list.remark;
                if(_remark) {
                    _remark = _remark.replace(/(^\s*)|(\s*$)/g, "");//去除两边的空格
                    return _remark.replace("!@#","")
                }else{
                    return ""
                }
            },
            invoiceType () {
                let invoiceType = ""
                if(this.orderTicketOutVo){
                    switch (this.orderTicketOutVo.invoiceType) {
                        case 0:
                            invoiceType = "不需要发票";
                            break;
                        case 1:
                            invoiceType = "个人纸质 "
                            break;
                        case 2:
                            invoiceType = "个人电子";
                            break;
                        case 3:
                            invoiceType = "公司纸质";
                            break
                        case 4:
                            invoiceType = "公司电子";
                            break;
                    }
                }
                return invoiceType
            },
            payType() {
                let payType = ""
                switch (this.list.payType) {
                    case 1:
                        payType = "微信支付"
                        break;
                    case 2:
                        payType = "线下支付";
                        break;
                    case 3:
                        payType = "支付宝支付";
                        break
                }
                return payType
            },
        }
    }
</script>

<style lang="less">
   .order-content{
    padding: .3rem;
    background-color :#fff;
    border-top:.01rem solid #e5e5e5;
    .clearfix{
        margin-bottom: .2rem;
        color: #666;
        vertical-align: middle;
        span{
            float: left;
        }
        p{
            overflow: hidden;
        }
    }
}
</style>
