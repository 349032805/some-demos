package kitt.site.controller;

import kitt.core.service.FileStore;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;

/**
 * Created by fanjun on 15/11/3.
 */
@Controller
public class DownloadPicController {
    @Autowired
    private FileStore fileStore;
    //下载图片
    @RequestMapping("/downloadPic")
    public HttpEntity<byte[]> doDownloadCertification(@RequestParam(value = "picPath", required = true)String picPath) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",picPath);
        return new HttpEntity<byte[]>(FileUtils.readFileToByteArray(fileStore.getFileByFilePath(picPath)), headers);
    }
}
