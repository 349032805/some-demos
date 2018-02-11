<template>
    <div class="orderInfo">
        <div class="inner1">
            <span>订单信息</span>
            <ul>
                <li>
                    <span>订单编号：</span>
                    <span>{{list.orderNo}}</span>
                </li>
                <li>
                    <span>下单时间：</span>
                    <span>{{list.payConfigTimeStr}}</span>
                </li>
                <li>
                    <span>支付方式：</span>
                    <span>{{payType}}</span>
                </li>
                <li>
                    <span v-if='invoiceType'>发票形式：</span>
                    <span>{{invoiceType}}</span>
                </li>
                <li v-if='orderTicketOutVo.ticketHeader'>
                    <span>发票抬头：</span>
                    <span>{{orderTicketOutVo.ticketHeader}}</span>
                </li>
                <li v-if='list.remark'>
                    <span>订单备注：</span>
                    <span>{{list.remark}}</span>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
export default {
    props : {
       list : Object,
       orderTicketOutVo : Object
    },
    computed: {
        invoiceType () {
            let invoiceType = ""
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



<style lang='less' scoped>
.orderInfo {
    background: #FFFFFF;
    padding: .5rem 0 .5rem .5rem;
    margin-top: .5rem;
    .inner1 {
        >span {
            font-size: .66rem;
            display: block;
            padding-bottom: .5rem;
            box-shadow: inset 0 -1px 0 0 #EBECF1;
        }
        ul {
            li {
                margin-top: .45rem;
                color: #666666;
                font-size: .59rem;
            }
        }
    }
}
</style>
