package kitt.site.controller;

import kitt.site.service.QRCodeBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhangbolun on 15/10/19.
 */
@Controller
public class QrCodeController {
    @Autowired
    private QRCodeBuilderService qrCodeBuilderService;

    @RequestMapping(value="getqrcode",method= RequestMethod.GET)
    public void getqrcode(@RequestParam(value = "word", required = true)String word,
                          @RequestParam(value = "picSize", required = true)int picSize,
                          HttpServletResponse resp){
        qrCodeBuilderService.getQRCode(word,picSize,resp);
    }

}
