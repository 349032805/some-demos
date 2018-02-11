export function forbidWXshare() {
    console.log("进入禁用微信分享")
    WeixinJSBridge.call('hideOptionMenu');
    // document.addEventListener('WeixinJSBridgeReady', function onBridgeReady() {
    //     console.log("开始禁用微信分享")
    //     WeixinJSBridge.call('hideOptionMenu');
    // });
};