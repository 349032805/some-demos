package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.service.BeanValidators;
import kitt.admin.service.FileService;
import kitt.admin.service.FocusImageService;
import kitt.admin.service.Session;
import kitt.core.domain.AuthenticationRole;
import kitt.core.domain.Focusimage;
import kitt.core.service.FileStore;
import kitt.core.util.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lich on 16/2/16.
 */
@RestController
@RequestMapping("/focuspic")
public class FocusImageController{
    @Autowired
    private FocusImageService focusImageService;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private FileService fileService;
    @Autowired
    private Session session;
    @RequestMapping("/list")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object getAdverList(@RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return new Object(){
            public Pager<Focusimage> focuspicList = focusImageService.getFocusImage(page, 10);
        };
    }


    @RequestMapping(value="/add", method= RequestMethod.POST)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object uploadAdvPic(@RequestParam(value="addfile" ,required = false) MultipartFile addfile,
                               @RequestParam(value="updatefile" ,required = false) MultipartFile updatefile,
                               @RequestParam(value = "pictitle", required = true, defaultValue = "")String pictitle,
                               @RequestParam(value = "articletitle", required = true, defaultValue = "")String articletitle,
                               @RequestParam(value = "articlelink", required = true, defaultValue = "")String articlelink,
                               @RequestParam(value = "summary", required = false, defaultValue = "")String summary,
                               @RequestParam(value = "focusId", required = false, defaultValue = "0")int focusId
                               ) throws Exception{
        Map map=new HashMap<String,Object>();
        List<String> picTypeList = Arrays.asList(new String[]{"jpg", "bmp", "png", "gif"});
        String fileType=null;
        String filePath=null;
        Focusimage focus = new Focusimage();
        focus.setLasteditman("工号：" + session.getAdmin().getJobnum() + ", 姓名：" + session.getAdmin().getName());
        focus.setArticlelink(articlelink);
        focus.setPictitle(pictitle);
        focus.setArticletitle(articletitle);
        focus.setSummary(summary);

        if(focusId!=0){
            //修改焦点图片
            focus.setId(focusId);
            int j=0;
            if(updatefile!=null) {
                fileType = fileStore.getFileType(updatefile);
                if (picTypeList.contains(fileType)) {
                    if (updatefile.getSize() / 1000 / 1000 <= 10) {
                        filePath = fileService.uploadFocusPic(updatefile);
                        focus.setPicaddress(filePath);
                        BeanValidators.validateWithException(focus);
                        //修改焦点图片
                        j = focusImageService.updateFocusPic(focus);
                    }else{
                        throw new BusinessException("上传的图片不能超过10M！");
                    }

                }else{
                    throw new BusinessException("请选择 jpg, bmp, png, gif 格式的图片上传！");
                }
            }else{
                j = focusImageService.updateFocusPic(focus);
            }
            if (j != 0) {
                map.put("filePath", filePath);
                map.put("success", true);
                return map;
            } else {
                throw new BusinessException("添加焦点图片失败!");
            }

        }else{
            //新增焦点图片
            fileType = fileStore.getFileType(addfile);
            if(picTypeList.contains(fileType)) {
                if (addfile.getSize() / 1000 / 1000 <= 10) {
                     filePath = fileService.uploadFocusPic(addfile);
                       focus.setPicaddress(filePath);
                    BeanValidators.validateWithException(focus);
                        //添加焦点图片
                        int j=  focusImageService.addFocusPic(focus);
                        if(j!=0) {
                            map.put("filePath",filePath);
                            map.put("success",true);
                            return map;
                        }else{
                            throw new BusinessException("添加焦点图片失败!");
                        }

                }else {
                    throw new BusinessException("上传的图片不能超过10M！");
                }
            }else{
                throw new BusinessException("请选择 jpg, bmp, png, gif 格式的图片上传！");
            }

        }


    }
    /**
     * 删除焦点图片
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeleteArticleAdver(@RequestParam(value = "id", required = true)int id,@RequestParam(value = "ftype", required = true)int ftype){
        return  focusImageService.doDeleteFocusImage(id,ftype);

    }



    @RequestMapping("/findByPictitle")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean findByPictitle(@RequestParam(value = "pictitle", required = true)String pictitle){
        return  focusImageService.findByPicTitle(pictitle);

    }


}
