package kitt.core.util.check;

import com.mysql.jdbc.StringUtils;
import kitt.core.domain.Picture;
import kitt.core.service.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liushengbin on 15/11/5.
 */
@Service
public class CheckPictureInfo {
    @Autowired
    FileStore fileStore;

    /**
     * 检查图片类型
     * @param file            图片文件对象
     * @return
     */
    public boolean doCheckPictureType(MultipartFile file) {
        String fileType = fileStore.getFileTypeByFileOriginName(file);
        List<String> fileTypeList = Arrays.asList(new String[]{"jpg", "bmp", "png", "jpeg"});
        return checkFileTypeMethod(fileTypeList, fileType);
    }

    public boolean doCheckPictureTypeIncludeNullFile(MultipartFile file) {
        String fileType = fileStore.getFileTypeByFileOriginName(file);
        List<String> fileTypeList = Arrays.asList(new String[]{"jpg", "bmp", "png", "jpeg"});
        if(file == null || StringUtils.isNullOrEmpty(file.getOriginalFilename()) || fileTypeList.contains(fileType)) {
            return true;
        } else{
            return false;
        }
    }

    /**
     * 检查图片大小
     * @param file            图片文件对象
     * @return
     */
    public boolean doCheckPictureSize(MultipartFile file){
        if (file.getSize() / 1000 / 1000 <= 10) {
            return true;
        } else{
            return false;
        }
    }

    /**
     * 检查图片的长宽
     * @param file            图片文件对象
     * @param width           要求图片的最大宽度
     * @param height          要求图片的最大长度
     */
    public Picture doCheckPictueLengthWigth(MultipartFile file, Integer width, Integer height) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        Picture picture = new Picture();
        if (width == null || height == null) {
            picture.setSuitable(true);
        } else {
            picture.setSuitable(bufferedImage.getWidth() <= width && bufferedImage.getHeight() <= height);
        }
        picture.setWidth(bufferedImage.getWidth());
        picture.setHeight(bufferedImage.getHeight());
        return picture;
    }

    /**
     * 检查办公文件类型
     * @param file            文件对象
     */
    public boolean doCheckOfficeFileType(MultipartFile file) {
        String fileType = fileStore.getFileTypeByFileOriginName(file);
        List<String> fileTypeList = Arrays.asList(new String[]{"doc", "docx", "xls", "xlsx", "pdf"});
        return checkFileTypeMethod(fileTypeList, fileType);
    }

    /**
     * 检查附件文件类型
     */
    public boolean doCheckATTACHMENTFileType(MultipartFile file) {
        String fileType = fileStore.getFileTypeByFileOriginName(file);
        List<String> fileTypeList = Arrays.asList(new String[]{"doc", "docx", "pdf", "zip", "rar"});
        return checkFileTypeMethod(fileTypeList, fileType);
    }

    /**
     * 检查Excel格式的问题就
     */
    public boolean doCheckExcelFileType(MultipartFile file) {
        String fileType = fileStore.getFileTypeByFileOriginName(file);
        List<String> fileTypeList = Arrays.asList(new String[]{"xls", "xlsx"});
        return checkFileTypeMethod(fileTypeList, fileType);
    }

    private boolean checkFileTypeMethod(List<String> fileTypeList, String fileType) {
        if(fileTypeList.contains(fileType)) {
            return true;
        } else{
            return false;
        }
    }

}
