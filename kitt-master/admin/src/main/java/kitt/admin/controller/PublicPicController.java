package kitt.admin.controller;

import kitt.admin.service.FileService;
import kitt.core.domain.PublicPic;
import kitt.core.persistence.PublicPicMapper;
import kitt.core.service.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanjun on 15-5-6.
 */
@RestController
public class PublicPicController {

    @Autowired
    protected FileService fileService;

    @Autowired
    protected PublicPicMapper publicPicMapper;

    @Autowired
    protected FileStore fileStore;

    //上传图片
    @RequestMapping("/publicPic/uploadPic")
    public Object uploadPic(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception{
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        if(file.getSize() / 1000 / 1000 <= 10) {
            response.setContentType("text/html");
            String picOriginName = file.getOriginalFilename();
            map.put("filePath", fileService.uploadPublicPicture(file, picOriginName));

            int i = publicPicMapper.countPicByName(picOriginName);
            if(i == 0) {
                //上传后保存记录
                PublicPic p = new PublicPic();
                p.setName(picOriginName);
                p.setPath("/files/public/" + picOriginName);
                p.setCreatetime(LocalDateTime.now());
                publicPicMapper.addPublicPic(p);
            }else{
                publicPicMapper.modifyCreatetimeByName(picOriginName);
            }

            success = true;
        }
        map.put("success", success);
        return map;
    }

    //获取所有图片(从数据库获取)
    @RequestMapping("/publicPic/getAllPictures")
    public Object getAllPictures(){
        List<PublicPic> picList = publicPicMapper.getAllPublicPic();
        Map<String, Object> map = new HashMap<>();
        map.put("picList",picList);
        return map;
    }

    //修改图片备注
    @RequestMapping("/publicPic/modifyCommnet")
    public Object modifyCommnet(int id,String comment){
        publicPicMapper.modifyCommentById(id,comment);
        Map<String, Object> map = new HashMap<>();
        boolean success = true;
        map.put("success",success);
        return map;
    }

    //删除图片
    @RequestMapping("/publicPic/deletePic")
    public Object deletePic(int id) throws Exception{
        PublicPic publicPic = publicPicMapper.getPicById(id);
        //根据路径删除图片
        fileStore.deletePublicPic(publicPic.getPath());
        //删除数据库记录
        publicPicMapper.deleteOneById(id);
        Map<String, Object> map = new HashMap<>();
        boolean success = true;
        map.put("success",success);
        return map;
    }

    //判断图片是否重复
    @RequestMapping("/publicPic/checkIsExist")
    public Object checkIsExist(String picName){
        int picCount = publicPicMapper.countPicByName(picName);
        Map<String, Object> map = new HashMap<>();
        map.put("picCount",picCount);
        return map;
    }
}
