package kitt.admin.controller;


import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xiangyang on 15/9/23.
 */
@RestController
public class WechatController {

  @Autowired
  private WxMpService wxMpService;

  @RequestMapping(value = "/loadWeixinMenu", method = RequestMethod.GET)
  public Object loadMenu() throws WxErrorException {
    return wxMpService.menuGet();
  }
  @RequestMapping(value = "/createWeixinMenu",method = RequestMethod.POST)
  public void createMenu(@RequestBody WxMenu wxMenu) throws WxErrorException {
      wxMpService.menuCreate(wxMenu);
  }
}
