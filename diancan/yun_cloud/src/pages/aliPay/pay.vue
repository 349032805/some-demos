<template>
    <div class="alipay"></div>
</template>
<script src="https://as.alipayobjects.com/g/component/antbridge/1.1.1/antbridge.min.js"></script>
<script>
import {deleteCache} from '../menus/model/cache.js'
    export default {
        created() {
            let _this = this;
            let orderId = _this.$route.params.orderId;
            //调用后台给的接口，返回tradeNO,在支付的时候调用
            _this.$http('get',_this.baseUrl+'/pay',
	            {
	                orderId:orderId
	            }
            ).then((res) => {  
                // alert("调起支付页面")
                if(res.code==200){
                    //唤起支付页面
                    _this.tradePay(res.model.tradeNO,res.model.sign,orderId); 
                }else{
                   alert(res.message);
                }
            });
        },
        methods:{
            // 由于js的载入是异步的，所以可以通过该方法，当AlipayJSBridgeReady事件发生后，再执行callback方法
            ready(callback){
                if (window.AlipayJSBridge) {
                    callback && callback();
                } else {
                    document.addEventListener('AlipayJSBridgeReady', callback, false);
                }
            },
            tradePay(tradeNO,sign,orderId){
                 let token = localStorage.getItem("token");
                 let _this = this;
                 _this.ready(function(){
                    // 通过传入交易号唤起快捷调用方式(注意tradeNO大小写严格)
                    AlipayJSBridge.call("tradePay", {
                        tradeNO: tradeNO
                    }, function (data) {
                        console.log("json: "+JSON.stringify(data));
                        if ("9000" == data.resultCode) {    //支付成功
                        	_this.$http('get',_this.baseUrl+'/callbackByFront',
					            {
					                orderId:orderId,
                                    sign:sign
                            	}
                            ).then(function(resData){
                                if(resData.code == 200){
                                    // _this.$router.replace('/orderDetail/'+orderId);
                                    deleteCache()
                                    _this.$router.replace({path:`/orderDetail/${orderId}`,query:{sign:'pay'}})
                                }else{
                                    alert(resData.message);
                                }
                            });
                        }else if(data.resultCode == '4000' || data.resultCode == '6001'){    //4000支付失败,6001 	//用户中途取消
                            _this.$router.replace({name:'confirmOrderBack'});
                        }else{
                            //关闭支付页面
                             AlipayJSBridge.call('closeWebview');
                        }
                    });
                });
            },
            goConfirm(){
                this.$router.push('/confirmOrder');
            }
        }
    }
</script>