package kitt.site.service;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import kitt.core.service.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.zxing.LuminanceSource;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成器
 * Created by zhangbolun on 15/10/17.
 */
@Service
public class QRCodeBuilderService {

    @Autowired
    FileStore fileStore;

    /**
     * 获取二维码图片
     * @param urlcontent url
     * @param size 边长尺寸
     * @param resp
     */
    public void getQRCode(String urlcontent,int size, HttpServletResponse resp) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = multiFormatWriter.encode(urlcontent, BarcodeFormat.QR_CODE, size, size,hints);
            //设置二维码图片边框
            bitMatrix= updateBit(bitMatrix,0);
            BufferedImage buffImg =  MatrixToImageWriter.toBufferedImage(bitMatrix);
            //根据size放大、缩小生成的二维码
            buffImg = zoomInImage(buffImg,size,size);

            // 禁止图像缓存。
            resp.setHeader("Pragma", "no-cache");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setDateHeader("Expires", 0);
            resp.setContentType("image/jpeg");
            // 将图像输出到Servlet输出流中。
            ServletOutputStream sos = resp.getOutputStream();
            ImageIO.write(buffImg, "jpeg", sos);
            sos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static BufferedImage  zoomInImage(BufferedImage  originalImage, int width, int height){
        BufferedImage newImage = new BufferedImage(width,height,originalImage.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0,0,width,height,null);
        g.dispose();
        return newImage;
    }

    private BitMatrix updateBit(BitMatrix matrix, int margin){
        int tempM = margin*2;
        int[] rec = matrix.getEnclosingRectangle();   //获取二维码图案的属性
        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
        resMatrix.clear();
        for(int i= margin; i < resWidth- margin; i++){   //循环，将二维码图案绘制到新的bitMatrix中
            for(int j=margin; j < resHeight-margin; j++){
                if(matrix.get(i-margin + rec[0], j-margin + rec[1])){
                    resMatrix.set(i,j);
                }
            }
        }
        return resMatrix;
    }

    /**
     * 解析二维码
     */
    public void analysisQRCode(){
        try {
            MultiFormatReader formatReader = new MultiFormatReader();
            File file = fileStore.getFileByFilePath("/files/upload/QRCode.jpg");
            BufferedImage image = ImageIO.read(file);
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            Result result = formatReader.decode(binaryBitmap,hints);
//            System.out.println("result = "+ result.toString());
//            System.out.println("resultFormat = "+ result.getBarcodeFormat());
//            System.out.println("resultText = "+ result.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
