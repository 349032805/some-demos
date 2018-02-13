package kitt.site.service;

/**
 * 生成二维码
 * Created by zhangbolun on 15/10/17.
 */
import com.google.zxing.common.BitMatrix;
import java.io.*;
import java.awt.image.BufferedImage;


public final class MatrixToImageWriter {
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private MatrixToImageWriter() {}

    public static BufferedImage toBufferedImage(BitMatrix matrix) throws IOException {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }
}