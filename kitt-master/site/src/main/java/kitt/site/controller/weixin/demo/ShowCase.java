package kitt.site.controller.weixin.demo;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongpf on 15/4/23.
 */
public class ShowCase {

    static WxMpInMemoryConfigStorage config ;
    static String  toUser = "omJl7t7o3d9FFlrrgUU_rHeu5crg" ;

    public static void man(String []  args ) throws WxErrorException, IOException {
        config = new WxMpInMemoryConfigStorage();
        config.setAppId("Wx6abf1fd4bc589d0a"); // 设置微信公众号的appid
        config.setSecret("69713d835931b4597df89b3a838a5601"); // 设置微信公众号的app corpSecret
        config.setToken("111"); // 设置微信公众号的token
        config.setAesKey("bKdT43K1gmBuKxynasE0ep1TOH3Uyn7ZufZmMAuXxlJ"); // 设置微信公众号的EncodingAESKey

        WxMpServiceImpl wxService = new WxMpServiceImpl();
        wxService.setWxMpConfigStorage(config);

     // 用户的openid在下面地址获得
     // https://mp.weixin.qq.com/debug/cgi-bin/apiinfo?t=index&type=用户管理&form=获取关注者列表接口%20/user/get
     // String openid = "omJl7t7o3d9FFlrrgUU_rHeu5crg";
     // WxMpCustomMessage message = WxMpCustomMessage.TEXT().toUser(openid).content("Hello World").build();
     // wxService.customMessageSend(message);
        ShowCase m =   new ShowCase()  ;
        //m.menuget(wxService) ;
          m.menuCreate(wxService);
        //m.erweima(wxService);
        //m.shortUrl(wxService);
        //m.voice(wxService);
        //m.mp4(wxService);
        //m.picture(wxService);
        m.menuGet(wxService);
    }


    public void menuGet(WxMpServiceImpl wxMpService) throws WxErrorException {
        WxMenu wxMenu  =   wxMpService.menuGet();
        System.out.println(wxMenu);
    }

    public void userinfo(WxMpServiceImpl wxMpService) throws WxErrorException {
        WxMpUser u =   wxMpService.userInfo(toUser, null) ;
        System.out.println(u);
    }


    public void OAuth2(WxMpServiceImpl wxMpService){
        wxMpService.oauth2buildAuthorizationUrl(WxConsts.OAUTH2_SCOPE_USER_INFO, null) ;
    }

    public void picture (WxMpServiceImpl wxMpService ) throws WxErrorException, IOException {

        WxMediaUploadResult wxMediaUploadResult = wxMpService
                .mediaUpload(WxConsts.MEDIA_IMAGE, WxConsts.FILE_JPG,
                        ClassLoader.getSystemResourceAsStream("mm.jpeg"));

        WxMpCustomMessage m =
                WxMpCustomMessage.IMAGE()
                        .mediaId(wxMediaUploadResult.getMediaId())
                        .toUser(toUser)
                        .build();

        wxMpService.customMessageSend(m);
    }

    public void mp4 (WxMpServiceImpl wxMpService ) throws WxErrorException, IOException {

        WxMediaUploadResult wxMediaUploadResult = wxMpService
                .mediaUpload(WxConsts.MEDIA_VIDEO, WxConsts.FILE_MP4,
                        ClassLoader.getSystemResourceAsStream("mm.mp4"));

        WxMpCustomMessage m =
                WxMpCustomMessage.VIDEO()
                        .mediaId(wxMediaUploadResult.getMediaId())
                        .toUser("omJl7t7o3d9FFlrrgUU_rHeu5crg")
                        .build();

        wxMpService.customMessageSend(m);
    }



    public void voice (WxMpServiceImpl wxMpService ) throws WxErrorException, IOException {

        WxMediaUploadResult wxMediaUploadResult = wxMpService
                .mediaUpload(WxConsts.MEDIA_VOICE, WxConsts.FILE_MP3,
                        ClassLoader.getSystemResourceAsStream("mm.mp3"));

        WxMpCustomMessage m =
            WxMpCustomMessage.VOICE()
                .mediaId(wxMediaUploadResult.getMediaId())
                    .toUser("omJl7t7o3d9FFlrrgUU_rHeu5crg")
                .build();
        wxMpService.customMessageSend(m);
    }




    public void shortUrl (WxMpServiceImpl wxMpService ) throws WxErrorException {
        String stringshortUrl = wxMpService.shortUrl("www.qq.com");
        WxMpCustomMessage m =
        WxMpCustomMessage
                .TEXT()
                .toUser("omJl7t7o3d9FFlrrgUU_rHeu5crg")
                .content("www.baidu.com")
                .build();
        wxMpService.customMessageSend(m);

    }



    public void erweima(WxMpServiceImpl wxMpService ) throws WxErrorException {
        WxMpQrCodeTicket ticket =   wxMpService.qrCodeCreateLastTicket(1) ;
        File file = wxMpService.qrCodePicture(ticket);
        //   InputStream inputStream = ...;
        //  File file = ...;
        //WxMediaUploadResult res = wxMpService.mediaUpload(mediaType, fileType, inputStream);
        // 或者
        WxMediaUploadResult res = wxMpService.mediaUpload(WxConsts.MEDIA_IMAGE, file);
        res.getType();
        res.getCreatedAt();
        res.getMediaId();
        res.getThumbMediaId();


        WxMpCustomMessage m
                = WxMpCustomMessage
                .IMAGE()
                .mediaId(res.getMediaId())
                .toUser("omJl7t7o3d9FFlrrgUU_rHeu5crg")
                .build();
        wxMpService.customMessageSend(m);

    }

    public void menuCreate( WxMpServiceImpl wxMpService ) throws WxErrorException {
        List<WxMenu.WxMenuButton > butts  =  new ArrayList<WxMenu.WxMenuButton>() ;

        String prefix = "http://www.yimei180.com/";
        WxMenu wxMenu = new WxMenu();

        WxMenu.WxMenuButton  one =  new WxMenu.WxMenuButton();
        one.setType("click");
        one.setName("交易专区");
        one.setKey("jiaoyizhuanqu");

        WxMenu.WxMenuButton  bt11 =  new WxMenu.WxMenuButton();
        bt11.setName("商城");
        bt11.setKey("shangcheng");
        bt11.setType(WxConsts.BUTTON_VIEW);
        bt11.setUrl(prefix+"m/mall");


        WxMenu.WxMenuButton  bt12 =  new WxMenu.WxMenuButton();
        bt12.setName("现货搜索");
        bt12.setKey("xianhuosousuo");
        bt12.setType(WxConsts.BUTTON_VIEW);
        bt12.setUrl(prefix+"m/supply");


        WxMenu.WxMenuButton  bt13 =  new WxMenu.WxMenuButton();
        bt13.setName("报价区");
        bt13.setKey("baojiaqu");
        bt13.setType(WxConsts.BUTTON_VIEW);
        bt13.setUrl(prefix+"m/sell");


        //WxMenu.WxMenuButton  bt14 =  new WxMenu.WxMenuButton();
        //bt14.setName("团购");
        //bt14.setKey("baojiaqu");
        //bt14.setType(WxConsts.BUTTON_VIEW);
        //bt14.setUrl(prefix+"m/demand");

        List<WxMenu.WxMenuButton > butts1  =  new ArrayList<WxMenu.WxMenuButton>() ;
        butts1.add(bt11) ;
        butts1.add(bt12) ;
        butts1.add(bt13) ;
        //butts1.add(bt14) ;

        one.setSubButtons(butts1);



        WxMenu.WxMenuButton  two =  new WxMenu.WxMenuButton();
        two.setType("click");
        two.setName("账户中心");
        two.setKey("two");


        WxMenu.WxMenuButton  bt21 =  new WxMenu.WxMenuButton();
        bt21.setName("登陆");
        bt21.setKey("denglu");
        bt21.setType(WxConsts.BUTTON_VIEW);
      //  bt21.setUrl(prefix+"m/login");
        bt21.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=Wx6abf1fd4bc589d0a&redirect_uri=http%3A%2F%2Fwww.yimei180.com%2Fm%2Flogin&response_type=code&scope=snsapi_base#wechat_redirect");


        WxMenu.WxMenuButton  bt22 =  new WxMenu.WxMenuButton();
        bt22.setName("注册");
        bt22.setKey("zhuce");
        bt22.setType(WxConsts.BUTTON_VIEW);
        bt22.setUrl(prefix+"m/reg");

        List<WxMenu.WxMenuButton > butts2  =  new ArrayList<WxMenu.WxMenuButton>() ;
        butts2.add(bt21) ;
        butts2.add(bt22);
        two.setSubButtons(butts2);


        WxMenu.WxMenuButton  bt31 =  new WxMenu.WxMenuButton();
        bt31.setName("公司简介");
        bt31.setKey("gongsijianjie");
        bt31.setType(WxConsts.BUTTON_VIEW);
        bt31.setUrl(prefix+"m/cpProfile");


        WxMenu.WxMenuButton  bt32 =  new WxMenu.WxMenuButton();
        bt32.setName("最新动态");
        bt32.setKey("xianhuosousuo");
        bt32.setType(WxConsts.BUTTON_VIEW);
        bt32.setUrl(prefix+"m/webNews");


        WxMenu.WxMenuButton  bt33 =  new WxMenu.WxMenuButton();
        bt33.setName("网站公告");
        bt33.setKey("baojiaqu");
        bt33.setType(WxConsts.BUTTON_VIEW);
        bt33.setUrl(prefix+"m/webNotice");


        WxMenu.WxMenuButton  bt34 =  new WxMenu.WxMenuButton();
        bt34.setName("联系我们");
        bt34.setKey("lianxiwomen");
        bt34.setType(WxConsts.BUTTON_VIEW);
        bt34.setUrl(prefix+"m/contactUs");


        WxMenu.WxMenuButton  three =  new WxMenu.WxMenuButton();
        three.setType("click");
        three.setName("关于易煤");
        three.setKey("guanyuyimei");


        List<WxMenu.WxMenuButton > butts3  =  new ArrayList<WxMenu.WxMenuButton>() ;
        butts3.add(bt31) ;
        butts3.add(bt32) ;
        butts3.add(bt33) ;
        butts3.add(bt34) ;

        three.setSubButtons(butts3);


        butts.add(one);
        butts.add(two);
        butts.add(three);

        wxMenu.setButtons(butts);

        wxMpService.menuCreate(wxMenu);
        System.out.println("创建成功");
    }

    public void menuget ( WxMpServiceImpl wxMpService ) throws WxErrorException {
        WxMenu wxMenu = wxMpService.menuGet() ;
        System.out.println(wxMenu.toString());
        //WxMenu{buttons=[WxMenuButton{type='click', name='今日歌曲', key='V1001_TODAY_MUSIC', url='null', subButtons=[]}, WxMenuButton{type='null', name='菜单', key='null', url='null', subButtons=[WxMenuButton{type='view', name='搜索', key='null', url='http://www.soso.com/', subButtons=[]}, WxMenuButton{type='view', name='视频', key='null', url='http://v.qq.com/', subButtons=[]}, WxMenuButton{type='click', name='赞一下我们', key='V1001_GOOD', url='null', subButtons=[]}]}]}

        //https://api.weixin.qq.com/sns/oauth2/access_token?appid=Wx6abf1fd4bc589d0a&secret=69713d835931b4597df89b3a838a5601&code=021bb8e43de625b26dd85d9f92ea43bR&grant_type=authorization_code
        //https://api.weixin.qq.com/sns/oauth2/access_token?appid=Wx6abf1fd4bc589d0a&secret=69713d835931b4597df89b3a838a5601&code=03131d5cfb341c66632b083351db231O&grant_type=authorization_code
    }
}
