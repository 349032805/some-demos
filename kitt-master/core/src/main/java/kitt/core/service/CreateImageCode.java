package kitt.core.service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by liuxinjie on 16/1/20.
 */
@Service
public class CreateImageCode {
    private static final int ImageCode_Width = 100;                          //图片验证码的宽度
    private static final int ImageCode_Height = 30;                          //图片验证码的高度
    private static final int ImageCode_NumsCount = 4;                        //验证码字符个数
    private static int ImageCode_FontHeight;                                 //字体高度
    private static int x = 0;
    private static int codeY;

    public static char[] codeChars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    /**
     * 生成图片验证码
     * @param date              时间戳 - 时间转成Long型
     * @param type              类型: 1,2,3,4 整形
     * @param codes             图片验证码字符串
     */
    public static BufferedImage createImageCode(Long date, int type, String codes) {
        x = ImageCode_Width / (ImageCode_NumsCount + 1);
        ImageCode_FontHeight = ImageCode_Height - 2;
        codeY = ImageCode_Height - 4;
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(ImageCode_Width, ImageCode_Height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        Random random = new Random(date+type);
        g.setColor(Color.WHITE);                                  // 将图像填充为白色
        g.fillRect(0, 0, ImageCode_Width, ImageCode_Height);
        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.PLAIN, ImageCode_FontHeight);
        // 设置字体。
        g.setFont(font);
        g.setColor(Color.BLACK);
        //画10条干扰线
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(ImageCode_Width);
            int y = random.nextInt(ImageCode_Height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        for (int i = 0; i < ImageCode_NumsCount; i++) {
            //产生随机的颜色分量来构造颜色值, 这样输出的每位数字的颜色值都将不同, 用随机产生的颜色将验证码绘制到图像中。
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawString(codes.substring(i, i+1), (i + 1) * x, codeY);
        }
        return buffImg;
    }

    /**
     * 生成随机图片验证码字符串
     */
    public static StringBuffer getRandomImageCode() {
        Random random = new Random();
        StringBuffer codes = new StringBuffer();
        for (int i = 0; i < ImageCode_NumsCount; i++) {
            codes.append(String.valueOf(codeChars[random.nextInt(36)]));   //得到随机产生的验证码数字。
        }
        return codes;
    }


}
