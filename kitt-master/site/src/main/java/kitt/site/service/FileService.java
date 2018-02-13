package kitt.site.service;


import kitt.core.domain.FileRecord;
import kitt.core.domain.UploadFileByUser;
import kitt.core.persistence.FileRecordMapper;
import kitt.core.persistence.UploadFileByUserMapper;
import kitt.core.service.FileStore;
import kitt.site.basic.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by joe on 2/8/15.
 */
@Component
public class FileService {
    @Autowired
    protected FileStore fileStore;
    @Autowired
    protected Session session;
    @Autowired
    protected UploadFileByUserMapper uploadFileByUserMapper;
    @Autowired
    protected FileRecordMapper fileRecordMapper;

    @Transactional
    public String uploadPicture(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByUser upload = new UploadFileByUser(session.getUser().getId(), fileStore.uploadBy(file, fileStore.getPictureSuffix(file)));
            uploadFileByUserMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(), UploadFileByUser.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    @Transactional
    public String uploadPictureNoType(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByUser upload = new UploadFileByUser(session.getUser().getId(), fileStore.uploadBy(file, fileStore.getPictureSuffixNoType(file)));
            uploadFileByUserMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(), UploadFileByUser.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }


    @Transactional
    public String uploadTenderPicture(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByUser upload = new UploadFileByUser(session.getUser().getId(), fileStore.uploadTenderBy(file, fileStore.getPictureSuffix(file)));
            uploadFileByUserMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(), UploadFileByUser.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

    public File getDownloadFileByFileName(String filename) throws Exception {
        return fileStore.getDownloadFileByFileName(filename);
    }

    public File copyToDownload(File file, String filename) throws Exception {
        return fileStore.copyToDownload(file, filename);
    }

    //微信端,直接上传图片到upload文件夹
    public String uploadPictureToUploadDir(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        if(session.isLogined()) {
            UploadFileByUser upload=new UploadFileByUser(session.getUser().getId(), fileStore.uploadToUploadDir(file, fileStore.getPictureSuffix(file)));
            uploadFileByUserMapper.insertUpload(upload);
            fileRecordMapper.insertRecord(new FileRecord(upload.getFilepath(), UploadFileByUser.tablename, upload.getId()));
            return upload.getFilepath();
        }
        throw new UnauthorizedException();
    }

}
