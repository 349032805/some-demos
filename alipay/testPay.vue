
<template>
    <div class="alipay">支付宝支付</div>
</template>
<script src="https://as.alipayobjects.com/g/component/antbridge/1.1.1/antbridge.min.js"></script>
<script>
export default {
  created() {
    let tradeNO = "XY123";
    this.tradePay(tradeNO);
  },
  methods: {
    // 由于js的载入是异步的，所以可以通过该方法，当AlipayJSBridgeReady事件发生后，再执行callback方法
    ready(callback) {
      if (window.AlipayJSBridge) {
        callback && callback();
      } else {
        document.addEventListener("AlipayJSBridgeReady", callback, false);
      }
    },
    tradePay(tradeNO) {
      let _this = this;
      _this.ready(function() {
        // 通过传入交易号唤起快捷调用方式(注意tradeNO大小写严格)
        AlipayJSBridge.call(
          "tradePay",
          {
            tradeNO: tradeNO
          },
          function(data) {
            console.log("json: " + JSON.stringify(data));
            if ("9000" == data.resultCode) {
              console.log("支付成功!");
            } else if (data.resultCode == "4000" || data.resultCode == "6001") {
              //4000支付失败,6001 	//用户中途取消
              console.log("支付失败,用户中途取消!");
            } else {
              //关闭支付页面
              AlipayJSBridge.call("closeWebview");
            }
          }
        );
      });
    }
  }
};
</script>