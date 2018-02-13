package kitt.admin.service;

import kitt.admin.basic.exception.UnauthorizedException;
import kitt.core.domain.FileRecord;
import kitt.core.domain.UploadFileByAdmin;
import kitt.core.persistence.FileRecordMapper;
import kitt.core.persistence.UploadFileByAdminMapper;
import kitt.core.service.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by joe on 2/8/15.
 */
@Component
public class FileService {
    @Autowired
    protected FileStore fileStore;
    @Autowired
    protected UploadFileByAdminMapper uploadFileByAdminMapper;
    @Autowired
    protected FileRecordMapper fileRecordMapper;
    @Autowired
    protected Session session;

    public String uploadPicture(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByAdmin upload=new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadBy(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    //上传手机app文件
    public String uploadApp(MultipartFile file,String versionName) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByAdmin upload=new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadAppBy(file, versionName));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    //上传网站和手机端公共图片
    public String uploadPublicPicture(MultipartFile file, String fileOriginName) throws Exception{
        if(session.isLogined()) {
            UploadFileByAdmin upload=new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadPublicPicBy(file, fileOriginName));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    //上传首页banner图
    public String uploadIndexBannerPicture(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByAdmin upload = new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadIndexBannerPicBy(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    /**
     * 上传文章图片
     * @param file
     * @return
     * @throws FileStore.UnsupportedContentType
     * @throws IOException
     */
    public String uploadArticlePicture(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByAdmin upload = new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadArticlePicBy(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    /**
     * 上传店铺图片
     * @param file             店铺图片文件
     * @return
     * @throws FileStore.UnsupportedContentType
     * @throws IOException
     */
    public String doUploadShopPicture(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByAdmin upload = new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadShopPicBy(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    /**
     * 上传视频海报图片
     * @param file
     * @return
     * @throws FileStore.UnsupportedContentType
     * @throws IOException
     */
    public String uploadVideoBannerPicture(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByAdmin upload = new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadArticlePicBy(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    /**
     * 上传合作伙伴图片
     * @param file
     * @return
     * @throws FileStore.UnsupportedContentType
     * @throws IOException
     */
    @Transactional
    public String uploadPartnerPicture(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByAdmin upload = new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadArticlePicBy(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    //上传广告图片
    @Transactional
    public String uploadAdverPic(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()){
            UploadFileByAdmin upload = new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadAdverPic(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    //上传焦点图片
    @Transactional
    public String uploadFocusPic(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()){
            UploadFileByAdmin upload = new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadFocusPic(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    /**
     * 上传Excel 导入数据
     */
    public String uploadExcelData(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByAdmin upload = new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadExcelFileBy(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    /**
     * 上传会议图片
     */
    @Transactional
    public String uploadMeetingPicture(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByAdmin upload = new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadMeetingPic(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    /**
     * 上传招标图片
     */
    public String uploadTenderPicture(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()){
            UploadFileByAdmin upload = new UploadFileByAdmin(session.getAdmin().getId(), fileStore.uploadTenderBy(file, fileStore.getPictureSuffix(file)));
            uploadFileByAdminMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(),UploadFileByAdmin.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }
}


