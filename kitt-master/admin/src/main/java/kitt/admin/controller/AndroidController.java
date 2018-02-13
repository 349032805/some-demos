package kitt.admin.controller;

import kitt.admin.service.FileService;
import kitt.core.domain.AppVersion;
import kitt.core.domain.Dictionary;
import kitt.core.persistence.AppVersionMapper;
import kitt.core.persistence.AppdownloadMapper;
import kitt.core.persistence.AppstatisticsMapper;
import kitt.core.persistence.DictionaryMapper;
import kitt.core.service.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanjun on 15-4-10.
 */
@Controller
public class AndroidController {

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    protected FileService fileService;

    @Autowired
    protected AppVersionMapper appVersionMapper;

    @Autowired
    protected FileStore fileStore;

    @Autowired
    protected AppdownloadMapper appdownloadMapper;

    @Autowired
    protected AppstatisticsMapper appstatisticsMapper;

    //获取当前apk版本号和当前日期
    @RequestMapping("/android/getVersion")
    @ResponseBody
    public Object getVersion(int page) {
        AppVersion appVersion = appVersionMapper.getNewestVersionNum();
        String version = null;
        if(appVersion != null){
            version = appVersion.getVersion();
        }else{
            version = "待上传";
        }
        Map map = new HashMap<>();
        map.put("appstatisticsCount",appstatisticsMapper.getAllAppstatisticsb().size());
        map.put("version",version);
        map.put("today",LocalDate.now());
        map.put("appVersion",appVersionMapper.pageAllAppVersion(page,10));

        //统计下载量
        int androidDwonloadNum = appdownloadMapper.countDownloadNumByType("Android");
        int iosDownloadNum = appdownloadMapper.countDownloadNumByType("ios");
        map.put("androidDwonloadNum",androidDwonloadNum);
        map.put("iosDownloadNum",iosDownloadNum);
        return map;
    }

    //上传安卓app文件
    @RequestMapping("/android/uploadApp")
    @ResponseBody
    public Object uploadApp(@RequestParam("file") MultipartFile file,
                            @RequestParam("newVersion") String newVersion,
                            @RequestParam("versionNum") String versionNum) throws Exception{

        String uploadPath = fileService.uploadApp(file,newVersion);
        //上传后保存记录
        AppVersion appVersion = new AppVersion();
        appVersion.setType("android");
        appVersion.setName("yimeiwang_" + LocalDate.now() + "_" + newVersion + ".apk");
        appVersion.setPath(uploadPath);
        appVersion.setVersion(newVersion+";"+LocalDate.now());
        appVersion.setCreatetime(LocalDateTime.now());
        appVersion.setVersionnum(Integer.parseInt(versionNum));

        appVersionMapper.addAppVersion(appVersion);
        Map map = new HashMap<>();
        map.put("uploadPath",uploadPath);
        return map;
    }

    //判断版本号是否小于最高版本
    @RequestMapping("/android/getVersionNum")
    @ResponseBody
    public Object getVersionNum() {
       AppVersion appVersion = appVersionMapper.getNewestVersionNum();
        Map map = new HashMap<>();
        if(appVersion != null) {
            map.put("versionNum", appVersion.getVersionnum());
        }else{
            map.put("versionNum", 0);
        }
        return map;
    }

    //保存描述
    @RequestMapping("/android/saveComment")
    @ResponseBody
    public Object saveComment(String newVersion,String comment) {
        newVersion = newVersion+";"+LocalDate.now();
        appVersionMapper.modifyCommentByVersion(newVersion,comment);
        boolean success = true;
        Map map = new HashMap<>();
        map.put("success",success);
        return map;
    }

    //获取描述
    @RequestMapping("/android/getComment")
    @ResponseBody
    public Object getComment(int id) {
        AppVersion appVersion = appVersionMapper.getAppVersionById(id);
        Map map = new HashMap<>();
        map.put("comment",appVersion.getComment());
        return map;
    }

    //修改描述
    @RequestMapping("/android/modifyComment")
    @ResponseBody
    public Object modifyComment(int id,String comment) {
        appVersionMapper.modifyCommentById(id,comment);
        boolean success = true;
        Map map = new HashMap<>();
        map.put("success",success);
        return map;
    }

    //启用版本
    @RequestMapping("/android/useVersion")
    @ResponseBody
    public Object useVersion(int id) throws Exception{
        AppVersion appVersion = appVersionMapper.getAppVersionById(id);
        fileStore.useAppVersionByName(appVersion.getName());
        appVersionMapper.modifyVersionToNoUse();
        appVersionMapper.modifyStatusAndUsetimeById(id);
        //修改字典表版本号
        Dictionary d = dictionaryMapper.getAPKVersion();
        if(d == null){
            dictionaryMapper.addAPKVersion(appVersion.getVersion());
        }else{
            dictionaryMapper.moidyfAPKVersion(appVersion.getVersion());
        }
        boolean success = true;
        Map map = new HashMap<>();
        map.put("success",success);
        return map;
    }

    //根据时间段查询app下载总数
    @RequestMapping("/android/checkDownloadNum")
    @ResponseBody
    public Object checkDownloadNum(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)@RequestParam(value = "startDate", required = false)LocalDate startDate,
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)@RequestParam(value = "endDate", required = false)LocalDate endDate){
        //int androidDwonloadNum = appdownloadMapper.countDownloadNumByTypeAndDate("Android", startDate,endDate);
        int androidDwonloadNum = appstatisticsMapper.countDownloadNumByAndDate(startDate,endDate);
        int iosDownloadNum = appdownloadMapper.countDownloadNumByTypeAndDate("ios",startDate,endDate);
        Map map = new HashMap<>();
        map.put("androidDwonloadNum", androidDwonloadNum);
        map.put("iosDownloadNum", iosDownloadNum);
        return map;
    }

}
