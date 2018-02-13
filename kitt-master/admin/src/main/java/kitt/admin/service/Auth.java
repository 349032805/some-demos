package kitt.admin.service;

import kitt.admin.basic.exception.BusinessException;
import kitt.core.domain.*;
import kitt.core.persistence.AdminMapper;
import kitt.core.persistence.DealerMapper;
import kitt.core.persistence.UserRoleMapper;
import kitt.core.service.FileStore;
import kitt.core.util.check.CheckPictureInfo;
import kitt.ext.WithLogger;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joe on 11/2/14.
 */
@Service
public class Auth implements WithLogger {
    @Autowired
    private Session session;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
	@Autowired
	private DealerMapper dealerMapper;
    @Autowired
    private CheckPictureInfo checkPictureInfo;
    @Autowired
    private FileService fileService;

	//登陆
    public boolean login(String username, String password){
        if(username != null) {
            Admin admin = adminMapper.getByUsername(username);
            if (admin != null && admin.getPassword().equals(DigestUtils.md5Hex(password)) && admin.isIsactive()) {
                session.login(admin, userRoleMapper.getRoleListByUserid(admin.getId()));
                return true;
            }
        }
        return false;
    }

	//输出错误日志
	public void doOutputErrorInfo(String info){
		logger().info("ERROR---ERROR");
		for(int i=0; i<15; i++) {
			logger().info(info);
		}
	}

	/**
     * 获取交易员列表
	 */
	public List<Admin> getDealerList(){
		return dealerMapper.findyiMeiAllDealer();
	}


    /**
     * 上传图片,文件公共方法
     * @param fileModule            文件所属模块
     * @param fileType              文件类型
     * @param file                  文件
     * @param width                 宽(图片)
     * @param height                高(图片)
     */
    public Map<String, Object> uploadPicMethod(String fileModule, String fileType, MultipartFile file, Integer width, Integer height) throws IOException, FileStore.UnsupportedContentType {
        if (fileType.equals(EnumFileType.IMG.toString()) && !checkPictureInfo.doCheckPictureType(file)){
            throw new BusinessException("请选择 .jpg, .bmp, .png, .jpeg 格式的图片上传！");
        } else if (fileType.equals(EnumFileType.ATTACHMENT.toString()) && !checkPictureInfo.doCheckATTACHMENTFileType(file)) {
            throw new BusinessException("请选择 .doc, .docx, .pdf, .zip, .rar 格式的文件上传！");
        } else if (fileType.equals(EnumFileType.EXCEL.toString()) && !checkPictureInfo.doCheckExcelFileType(file)) {
            throw new BusinessException("请选择 .xls, .xlsx 格式的文件上传! ");
        } else if(fileType.equals(EnumFileType.IMG.toString()) && !checkPictureInfo.doCheckPictureSize(file)){
            throw new BusinessException("上传的图片不能超过10M！");
        } else {
            Picture picture = new Picture();
            boolean success = true;
            if (fileType.equals(EnumFileType.IMG.toString())) {
                try {
                    picture = checkPictureInfo.doCheckPictueLengthWigth(file, width, height);
                    success = picture.isSuitable();
                } catch (Exception e) {
                    throw new BusinessException("该文件可能已经损坏,请更换重新上传!");
                }
            }
            if (success){
                Map<String, Object> map = new HashMap<>();
                String filePath = "";
                if (fileModule.equals(EnumFileType.File_Article.toString())) {
                    filePath = fileService.uploadArticlePicture(file);
                } else if (fileModule.equals(EnumFileType.File_Meeting.toString())) {
                    filePath = fileService.uploadMeetingPicture(file);
                } else if (fileModule.equals(EnumFileType.File_Tender.toString())) {
                    filePath = fileService.uploadTenderPicture(file);
                } else if (fileModule.equals(EnumFileType.File_DataCenter.toString())) {
                    filePath = fileService.uploadExcelData(file);
                } else if (fileModule.equals(EnumFileType.File_PartnerLogo.toString())) {
                    filePath = fileService.uploadPartnerPicture(file);
                } else if (fileModule.equals(EnumFileType.File_ShopCoal.toString())) {
                    filePath = fileService.doUploadShopPicture(file);
                } else if (fileModule.equals(EnumFileType.File_UserCompany.toString())) {
                    filePath = fileService.uploadPicture(file);
                } else if (fileModule.equals(EnumFileType.File_IndexBanner.toString())) {
                    filePath = fileService.uploadIndexBannerPicture(file);
                } else {
                    filePath = fileService.uploadArticlePicture(file);
                }
                map.put("filePath", filePath);
                map.put("success", true);
                return map;
            } else{
                throw new BusinessException("图片尺寸不能超过 " + width + "×" + height + " 　(本图片大小：" + picture.getWidth() + "×" + picture.getHeight() + " )");
            }
        }
    }

    /**
     * 根据id判断该用户是否是超级管理员
     */
    public boolean checkIFSuperAdminById() {
        List<Role> roleList = userRoleMapper.getRoleListByUserid(session.getAdmin().getId());
        for (Role role : roleList) {
            if (role.getRolecode().equals(AuthenticationRole.Admin.toString())) return true;
        }
        return false;
    }

}
