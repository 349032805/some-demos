package kitt.core.service;


import kitt.ext.WithLogger;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by fanjun on 14-11-17
 */
public class FileStore implements WithLogger {
    protected final String filePath;
    protected final File fileStoreDir;
    protected final File uploadDir;
    protected final File tenderDir;

    protected final File downloadDir;
    protected final File shopDir;                       //存放店铺图片
    protected final File appDir;                        //app文件夹建android文件夹,为如果有苹果app预留空间
    protected final File androidAppDir;                 //用于存放安卓app正式发布版本
    protected final File historyDir;                    //用于存放app历史版本
    protected final File publicDir;                     //用于存放网页和手机端公用图片供调用
    protected final File articleDir;                    //存放文章图片
    protected final File articleTempDir;                //存放文章临时图片
    protected final File articleVideoDir;               //存放宣传视频
    protected final File adverPicDir;                   //存放广告宣传图片
    protected final File focusPicDir;                   //存放焦点图片
    protected final File tempDir;                       //用于临时存放前端客户上传的图片
    protected final File excelData;                     //存放Excel 数据文件
    protected final File meetingDir;                    //存放会议文件
    protected final File customerPartner;               //合作伙伴文件路径
    protected final File articleVideoBannerDir;         //视频海报图片url
    protected final File indexBannerDir;                //网站,微信banner图片

    public FileStore(String filePath) {
        this.filePath = filePath;
        fileStoreDir = new File(filePath);
        if (!fileStoreDir.exists())
            fileStoreDir.mkdirs();

        uploadDir = new File(fileStoreDir, "upload");
        if (!uploadDir.exists())
            uploadDir.mkdir();

        tenderDir = new File(fileStoreDir, "tender");
        if (!tenderDir.exists())
            tenderDir.mkdir();

        downloadDir = new File(fileStoreDir, "download");
        if (!downloadDir.exists())
            downloadDir.mkdir();

        appDir = new File(fileStoreDir, "app");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        androidAppDir = new File(appDir, "androidApp");
        if (!androidAppDir.exists()) {
            androidAppDir.mkdir();
        }

        historyDir = new File(appDir, "history");
        if (!historyDir.exists()) {
            historyDir.mkdir();
        }

        publicDir = new File(fileStoreDir, "public");
        if (!publicDir.exists()) {
            publicDir.mkdir();
        }

        articleDir = new File(fileStoreDir, "article");
        if (!articleDir.exists()) {
            articleDir.mkdir();
        }

        articleTempDir = new File(fileStoreDir, "articletemp");
        if (!articleTempDir.exists()) {
            articleTempDir.mkdir();
        }

        articleVideoDir = new File(fileStoreDir, "article/video");
        if (!articleVideoDir.exists()) {
            articleVideoDir.mkdir();
        }

        adverPicDir = new File(fileStoreDir, "article/adverpic");
        if (!adverPicDir.exists()) {
            adverPicDir.mkdir();
        }


        focusPicDir = new File(fileStoreDir, "article/focusimage");
        if(!focusPicDir.exists()){
            focusPicDir.mkdir();
        }
        excelData = new File(fileStoreDir, "exceldata");
        if (!excelData.exists()) {
            excelData.mkdir();
        }

        meetingDir = new File(fileStoreDir, "meeting");
        if (!meetingDir.exists()) {
            meetingDir.mkdir();
        }

        tempDir = new File(fileStoreDir, "temp");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        shopDir = new File(fileStoreDir, "shop");
        if (!shopDir.exists()) {
            shopDir.mkdir();
        }

        customerPartner = new File(fileStoreDir, "customer/partner");
        if (!customerPartner.exists()) {
            customerPartner.mkdir();
        }

        articleVideoBannerDir = new File(fileStoreDir, "article/video");
        if (!articleVideoBannerDir.exists()) {
            articleVideoBannerDir.mkdir();
        }

        indexBannerDir = new File(fileStoreDir, "banner");
        if (!indexBannerDir.exists()) {
            indexBannerDir.mkdir();
        }
    }


    public String getFilePath() {
        return filePath;
    }

    public String uploadBy(MultipartFile file, String suffix) throws IOException {
        String filename = Hex.encodeHexString(DigestUtils.md5(file.getInputStream())) + "." + suffix;
       /* String filePath="/files/upload/"+filename;
        File target=new File(uploadDir, filename);*/

        //先保存在临时文件夹temp
        String filePath = "/files/temp/" + filename;
        File target = new File(tempDir, filename);
        if (!target.exists()) {
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        }
        return filePath;
    }

    /**
     * 上传招标图片
     */
    public String uploadTenderBy(MultipartFile file, String suffix) throws IOException {
        String filename = Hex.encodeHexString(DigestUtils.md5(file.getInputStream())) + "." + suffix;
        String filePath = "/files/tender/" + filename;
        File target = new File(tenderDir, filename);
        if (!target.exists()) {
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        }
        return filePath;
    }

    public String uploadPaymentBy(MultipartFile file, String suffix) throws IOException {
        String filename = Hex.encodeHexString(DigestUtils.md5(file.getInputStream())) + "." + suffix;
        String filePath = "/files/upload/" + filename;
        File target = new File(uploadDir, filename);
        if (!target.exists()) {
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        }
        return filePath;
    }

    protected String getSuffixByContentType(String contentType) {
        //兼容IE8文件上传
        if ("image/pjpeg".equals(contentType)) {
            contentType = "image/jpeg";
        } else if ("image/x-png".equals(contentType)) {
            contentType = "image/png";
        }
        try {
            MimeType type = MimeTypes.getDefaultMimeTypes().getRegisteredMimeType(contentType);
            if (type != null)
                return type.getExtension();
        } catch (MimeTypeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFileType(MultipartFile file) {
        String suffix = getSuffixByContentType(file.getContentType());
        if (suffix != null) suffix = suffix.substring(1);
        return suffix;
    }

    public String getFileTypeByFileOriginName(MultipartFile file) {
        return file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length());
    }

    public static class UnsupportedContentType extends Exception {
        public UnsupportedContentType(String message) {
            super(message);
        }
    }

    protected static List<String> pictureSuffixes = Arrays.asList(new String[]{"png", "jpg", "jpeg", "bmp", "gif", "pdf", "xls", "xlsx", "zip", "docx", "ppt", "pptx", "doc"});

    public String getPictureSuffix(MultipartFile file) throws UnsupportedContentType {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
//        String suffix=getSuffixByContentType(file.getContentType());
//        if(suffix!=null)
//            suffix = suffix.substring(1);
        if (suffix == null || !contains(pictureSuffixes, suffix)) {
            throw new UnsupportedContentType(file.getOriginalFilename());
        }
        return suffix;
    }

    private boolean contains(List<String> pictureSuffixes, String s) {
        for (String a : pictureSuffixes) {
            if (a.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public String getPictureSuffixNoType(MultipartFile file) throws UnsupportedContentType {
        String suffix = getSuffixByContentType(file.getContentType());
        if (suffix != null)
            suffix = suffix.substring(1);
        if (suffix == null)
            throw new UnsupportedContentType(file.getOriginalFilename());
        return suffix;
    }

    /**
     * 定义的视频文件格式
     */
    protected static List<String> videosSuffixes = Arrays.asList(new String[]{"flv", "mp4", "ogg", "avi", "wmv"});

    /**
     * 获取视频文件的格式
     *
     * @param file 视频文件
     * @return 视频文件格式
     * @throws UnsupportedContentType
     */
    public String getArticleVideoSuffix(MultipartFile file) throws UnsupportedContentType {
        String suffix = getSuffixByContentType(file.getContentType());
        if (suffix != null) suffix = suffix.substring(1);
        if (suffix == null || !contains(videosSuffixes, suffix))
            throw new UnsupportedContentType(file.getOriginalFilename());
        return suffix;
    }

    public File getFileByFilePath(String filePath) throws Exception {
        if (filePath != null && filePath.startsWith("/files/")) {
            return new File(fileStoreDir, filePath.substring("/files/".length()));
        }
        throw new Exception("file path is not right" + filePath);
    }

    public String getDownloadFilePathByFileName(String filename) {
        return "/files/download/" + filename;
    }

    public File getDownloadFileByFileName(String filename) throws Exception {
        return getFileByFilePath(getDownloadFilePathByFileName(filename));
    }

    public String getAndroidAppFilePath(String filename) {
        return "/files/app/androidApp" + filename;
    }

    public File getAndroidAppFileByFileName(String filename) throws Exception {
        return getFileByFilePath(getAndroidAppFilePath(filename));
    }

    public File copyToDownload(File file, String filename) throws Exception {
        File target = new File(downloadDir, filename);
        if (target.exists())
            throw new Exception("file exists");
        FileCopyUtils.copy(file, target);
        return target;
    }

    /**
     * 网站,微信首页图片
     */
    public String uploadIndexBannerPicBy(MultipartFile file, String suffix) throws IOException {
        String filename = Hex.encodeHexString(DigestUtils.md5(file.getInputStream())) + "." + suffix;
        String filePath = "/files/banner/" + filename;
        File target = new File(indexBannerDir, filename);
        if (!target.exists()) {
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        }
        return filePath;
    }

    //上传app文件.app文件夹建android文件夹,为如果有苹果app预留空间
    public String uploadAppBy(MultipartFile file, String versionName) throws IOException {
        //直接放入history文件夹
        String filename = "yimeiwang_" + LocalDate.now() + "_" + versionName + ".apk";
        String filePath = "/files/history/" + filename;
        File target = new File(historyDir, filename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        return filePath;
    }

    //上传网站和手机端公共图片
    public String uploadPublicPicBy(MultipartFile file, String fileOriginName) throws Exception {
        String filename = fileOriginName;
        File picFile = getFileByFilePath("/files/public/" + filename);
        if (picFile.isFile()) {
            picFile.delete();
        }

        String filePath = "/files/public/" + filename;
        File target = new File(publicDir, filename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        return filePath;
    }

    //删除public文件夹下某张图片
    public void deletePublicPic(String path) throws Exception {
        File picFile = getFileByFilePath(path);
        if (picFile.isFile()) {
            picFile.delete();
        }
    }

    /**
     * 上传文章图片
     *
     * @param file
     * @param suffix
     * @return
     * @throws IOException
     */
    public String uploadArticlePicBy(MultipartFile file, String suffix) throws IOException {
        String filename = new Date().getTime() + "." + suffix;
        String filePath = "/files/article/" + filename;
        File target = new File(articleDir, filename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        return filePath;
    }

    //上传店铺图片
    public String uploadShopPicBy(MultipartFile file, String suffix) throws IOException {
        String filename = Hex.encodeHexString(DigestUtils.md5(file.getInputStream())) + "." + suffix;
        String filePath = "/files/shop/" + filename;
        File target = new File(shopDir, filename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        return filePath;
    }

    /**
     * 上传合作伙伴图片
     *
     * @param file
     * @param suffix
     * @return
     * @throws IOException
     */
    public String uploadPartnerPicBy(MultipartFile file, String suffix) throws IOException {
        String filename = Hex.encodeHexString(DigestUtils.md5(file.getInputStream())) + "." + suffix;
        String filePath = "/files/customer/partner/" + filename;
        File target = new File(customerPartner, filename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        return filePath;
    }

    /**
     * 上传广告图片
     */
    public String uploadAdverPic(MultipartFile file, String suffix) throws IOException {
        String filename = Hex.encodeHexString(DigestUtils.md5(LocalDateTime.now().toString())) + "." + suffix;
        String filePath = "/files/article/adverpic/" + filename;
        File target = new File(adverPicDir, filename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        return filePath;
    }


    /**
     * 上传焦点图片
     */
    public String uploadFocusPic(MultipartFile file, String suffix) throws IOException {
        String filename = Hex.encodeHexString(DigestUtils.md5(LocalDateTime.now().toString()))+"." + suffix;
        String filePath="/files/article/focusimage/" + filename;
        File target=new File(focusPicDir, filename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        return filePath;
    }


    //上传 Excel 文件
    public String uploadExcelFileBy(MultipartFile file, String suffix) throws IOException {
        String filename = LocalDateTime.now().toString() + "." + suffix;
        String filePath = "/files/exceldata/" + filename;
        File target = new File(excelData, filename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        return filePath;
    }

    /**
     * 上传 会议 图片
     */
    public String uploadMeetingPic(MultipartFile file, String suffix) throws IOException {
        String filename = LocalDateTime.now().toString() + Hex.encodeHexString(DigestUtils.md5(file.getInputStream())) + "." + suffix;
        String filePath = "/files/meeting/" + filename;
        File target = new File(meetingDir, filename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        return filePath;
    }

    //启用app版本
    public void useAppVersionByName(String appName) throws Exception {
        //先删除androidApp文件夹下的已有版本
        File appFile = getFileByFilePath("/files/app/androidApp/yimeiwang.apk");
        if (appFile.isFile()) {
            appFile.delete();
        }

        //将启用的版本放进去
        File target = new File(androidAppDir, "yimeiwang.apk");
        File historyFile = getFileByFilePath("/files/app/history/" + appName);
        FileCopyUtils.copy(historyFile, target);
    }

    //复制图片到upload文件夹
    public void copyFileToUploadDir(String picPath) throws Exception {
        String picName = picPath.substring(picPath.lastIndexOf("/") + 1);
        File target = new File(uploadDir, picName);
        File tempPicFile = getFileByFilePath(picPath);
        if (!target.exists()) {
            FileCopyUtils.copy(tempPicFile, target);
        }

    }

    //删除temp文件夹图片
    public void deleteTempPic(String picPath) throws Exception {
        File picFile = getFileByFilePath(picPath);
        if (picFile.isFile()) {
            picFile.delete();
        }
    }

    //复制图片到upload文件夹
    public void copyFileToUploadDir(File file) throws Exception {
        File target = new File(uploadDir, file.getName());
        if (!target.exists()) {
            FileCopyUtils.copy(file, target);
        }
    }

    //微信端图片直接上传到upload文件夹
    public String uploadToUploadDir(MultipartFile file, String suffix) throws IOException {
        String filename = Hex.encodeHexString(DigestUtils.md5(file.getInputStream())) + "." + suffix;
        String filePath = "/files/upload/" + filename;
        File target = new File(uploadDir, filename);
        if (!target.exists()) {
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(target));
        }
        return filePath;
    }
}



