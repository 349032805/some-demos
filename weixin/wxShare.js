import api from "@/api/api";
export function wxShare(baseUrl, succCallback) {
    console.log("调用微信分享开始");
    const SUCCESS = 200;

    // location.origin + '/#/shareStore';
    let shareUrl = location.origin;
    console.log("要分享的页面域名:" + shareUrl);
    //调用微信api分享
    api.getShareAuth(shareUrl).then(response => {
        if (response.code == SUCCESS) {
            let shareUrlFinal = location.origin + '/#/shareActivity';
            console.log("最终分享地址:" + shareUrlFinal);
            _initWXshare(response.model, shareUrlFinal, succCallback);
        }
    });

};

function _initWXshare(wxAuth, shareUrlFinal, succCallback) {
    console.log("调用微信分享接口");
    let shareTitle = '早起打卡,邀你来挑战赢奖金!';
    let imgUrl = "http://cdn.51eparty.com/static/images/default_store.png";
    console.log("imgUrl:" + imgUrl);

    wx.config({
        debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
        appId: wxAuth.appId, // 必填，公众号的唯一标识
        timestamp: wxAuth.timestamp, // 必填，生成签名的时间戳
        nonceStr: wxAuth.nonceStr, // 必填，生成签名的随机串
        signature: wxAuth.signature,// 必填，签名，见附录1
        jsApiList: ['onMenuShareAppMessage', "onMenuShareTimeline"] // 必填，需要使用的JS接口列表
    });
    wx.ready(function () {
        //分享到朋友圈
        wx.onMenuShareTimeline({
            title: shareTitle, // 分享标题
            link: shareUrlFinal, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
            imgUrl: imgUrl,
            success: function () {
                // 用户确认分享后执行的回调函数
                //关闭分享模态框并给出提示
                succCallback();
            },
            cancel: function () {
                // 用户取消分享后执行的回调函数
            },
            fail: function (res) {
                alert(JSON.stringify(res));
            }
        });

        //分享给朋友
        wx.onMenuShareAppMessage({
            title: shareTitle, // 分享标题
            desc: '早起打卡,邀你来挑战赢奖金!',
            link: shareUrlFinal, // 分享链接，该链接域名必须与当前企业的可信域名一致
            imgUrl: imgUrl,
            success: function () {
                // 用户确认分享后执行的回调函数
                succCallback();
            },
            cancel: function () {
                // 用户取消分享后执行的回调函数
            },
            fail: function (res) {
                alert(JSON.stringify(res));
            }
        });

    });
}