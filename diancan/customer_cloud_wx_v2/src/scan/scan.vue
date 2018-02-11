<template>
  <div class="scan">
    
  </div>
</template>

<script type="text/ecmascript-6">
 const SUCCESS = 200;
  export default {
    created () {
        // https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141115
        //从后台获取调用微信接口需要的参数值
        this._getScanParams();
    },
    methods: {
        _getScanParams(){
            let shareUrl = location.origin + '/#/scan';
            this.$http("get",this.baseUrl + '/getShareSign',{notifyUrl: shareUrl}).then((response) => {
                if(response.code==SUCCESS){
                    //拿到参数值后调用扫一扫接口
                    let model = response.model;
                    this.initScan(model);
                }
            });
        },
        initScan(wxAuth){
            wx.config({
                debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                appId: wxAuth.appId, // 必填，公众号的唯一标识
                timestamp: wxAuth.timestamp, // 必填，生成签名的时间戳
                nonceStr: wxAuth.nonceStr, // 必填，生成签名的随机串
                signature: wxAuth.signature,// 必填，签名，见附录1
                jsApiList: ['scanQRCode'] // 必填，需要使用的JS接口列表
            });
            wx.ready(function() {
                //扫一扫
                wx.scanQRCode({
                    needResult: 0, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
                    scanType: ["qrCode"], // 可以指定扫二维码还是一维码，默认二者都有
                    success: function (res) {
                         var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
                    }
                });

            });
        }
    }
  };
</script>
