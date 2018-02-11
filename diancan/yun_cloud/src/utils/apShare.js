export function apShare(storeId,store,userId) {
    console.log("调用支付宝分享开始");
    const shareUrl = location.origin + '/#/shareStore?storeId='+storeId+'&userId='+userId+'&sign=ap';;
    let imgUrl = "http://cdn.51eparty.com/static/images/default_store.png";
    if(store.fsLogo){
        if(store.fsLogo != null && store.fsLogo != ""){
            imgUrl = store.fsLogo;
        }
    }

    console.log("imgUrl:"+imgUrl);

    let option = {
        title: store.fsShopName+'-百味云人气餐厅',
        content: '千帆过尽皆不是，唯有这家合我胃，现在我把她推荐给你',
        url: shareUrl,
        image: imgUrl
    };
    ap.share(option,function(result){
        if(result.shareResult){
            console.log("分享成功");
        }
    });
  
};