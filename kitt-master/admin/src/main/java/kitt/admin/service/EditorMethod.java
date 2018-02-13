package kitt.admin.service;

import kitt.core.domain.FileRecord;
import kitt.core.domain.UploadFileByAdmin;
import kitt.core.editor.*;
import kitt.core.persistence.FileRecordMapper;
import kitt.core.persistence.UploadFileByAdminMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by liuxinjie on 15/12/10.
 */
@Service
public class EditorMethod {
    @Autowired
    private UploadFileByAdminMapper uploadFileByAdminMapper;
    @Autowired
    private FileRecordMapper fileRecordMapper;
    @Value("${editorConfigPath}")
    private String editorConfigPath;
    @Value("${saveArticleFilePath}")
    private String saveArticleFilePath;
    @Autowired
    private Session session;

    /**
     * 读取配置文件内容,转化位JSON格式
     */
    public JSONEditorObject getConfigFileContentJSON() throws FileNotFoundException {
        String path = editorConfigPath + "config.json";
        StringBuilder builder = new StringBuilder();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String tmpContent = null;
            while((tmpContent = bufferedReader.readLine()) != null) {
                builder.append(tmpContent);
            }
            bufferedReader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONEditorObject(removeStringComment(builder.toString()));
    }

    /**
     * 去掉注释里面内容方法
     * @param str                    个数
     */
    public String removeStringComment(String str) {
        return str.replaceAll("/\\*[\\s\\S]*?\\*/", "");
    }

    /**
     * 在线管理,列出所有图片/文件
     * @param index                  起点,从第几个开始
     * @param allowFiles             允许的格式
     * @param count                  个数
     */
    public EditorState listFile(int index, String[] allowFiles, int count) {
        File dir = new File(saveArticleFilePath);
        if(!dir.exists()) {
            return new EditorBaseState(false, 302);
        } else if(!dir.isDirectory()) {
            return new EditorBaseState(false, 301);
        } else {
            Object state = null;
            Collection list = FileUtils.listFiles(dir, allowFiles, true);
            if(index >= 0 && index <= list.size()) {
                Object[] fileList = Arrays.copyOfRange(list.toArray(), index, index + count);
                state = getFileListState(fileList);
            } else {
                state = new EditorMultiState(true);
            }
            ((EditorState)state).putInfo("start", (long)index);
            ((EditorState)state).putInfo("total", (long)list.size());
            return (EditorState)state;
        }
    }

    /**
     * 获取文件列表状态,是否成功
     * @param files              文件集合
     */
    private EditorState getFileListState(Object[] files) {
        EditorMultiState editorMultiState = new EditorMultiState(true);
        EditorBaseState editorBaseState = null;
        EditorPathFormat editorPathFormat = new EditorPathFormat();
        for(int i=0; i<files.length; i++) {
            Object obj = files[i];
            if(obj == null) break;
            File file = (File)obj;
            editorBaseState = new EditorBaseState(true);
            String URL = editorPathFormat.format(file.getAbsolutePath());
            int startPlace = URL.indexOf("/files/article/");
            URL = URL.substring(startPlace, URL.length());
            editorBaseState.putInfo("url", URL);
            editorMultiState.addState(editorBaseState);
        }
        return editorMultiState;
    }

    /**
     * 捕获远程图片
     */
    public EditorState capture(int userid, String[] list) {
        EditorMultiState state = new EditorMultiState(true);
        int length = list.length;
        for(int i = 0; i < length; ++i) {
            String source = list[i];
            state.addState(captureRemoteData(userid, source));
        }
        return state;
    }

    /**
     * 捕获远程图片
     */
    public EditorState captureRemoteData(int userid, String urlStr) {
        HttpURLConnection connection = null;
        URL url = null;
        String suffix = null;
        try {
            url = new URL(urlStr);
            if(!validHost(url.getHost())) {
                return new EditorBaseState(false, 201);
            } else {
                connection = (HttpURLConnection)url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setUseCaches(true);
                if(connection.getResponseCode() != 200) {
                    return new EditorBaseState(false, 202);
                } else {
                    suffix = imageTypeList.get(connection.getContentType());
                    if(!Arrays.asList(EditorConfigContent.imageAllowFiles).contains(suffix)) {
                        return new EditorBaseState(false, 8);
                    } else if(EditorConfigContent.imageMaxSize < connection.getContentLength()) {
                        return new EditorBaseState(false, 1);
                    } else {
                        EditorPathFormat editorPathFormat = new EditorPathFormat();
                        String e = editorPathFormat.parse(saveArticleFilePath + suffix, EditorConfigContent.fileName);
                        String physicalPath = e;
                        EditorState state = saveFileByInputStream(userid, connection.getInputStream(), physicalPath);
                        if(state.isSuccess()) {
                            state.putInfo("url", editorPathFormat.format(e));
                            state.putInfo("source", urlStr);
                        }
                        return state;
                    }
                }
            }
        } catch (Exception var8) {
            return new EditorBaseState(false, 203);
        }
    }

    public final Map<String, String> imageTypeList = new HashMap() {
        {
            this.put("image/gif", ".gif");
            this.put("image/jpeg", ".jpg");
            this.put("image/jpg", ".jpg");
            this.put("image/png", ".png");
            this.put("image/bmp", ".bmp");
        }
    };

    private boolean validHost(String hostname) throws UnknownHostException {
        try {
            InetAddress e = InetAddress.getByName(hostname);
            if(e.isSiteLocalAddress()) {
                return false;
            }
        } catch (UnknownHostException var3) {
            return false;
        }
        return !EditorConfigContent.catcherLocalDomain.contains(hostname);
    }

    public EditorState saveFileByInputStream(int userid, InputStream inputStream, String path) {
        EditorState state = null;
        File tmpFile = getTempFileMethod();
        byte[] dataBuf = new byte[2048];
        BufferedInputStream bis = new BufferedInputStream(inputStream, 8192);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), 8192);
            int count1;
            while((count1 = bis.read(dataBuf)) != -1) {
                bos.write(dataBuf, 0, count1);
            }
            bos.flush();
            bos.close();
            state = saveTempFileMethod(userid, tmpFile, path);
            if(!state.isSuccess()) {
                tmpFile.delete();
            }
            return state;
        } catch (IOException var8) {
            return new EditorBaseState(false, 4);
        }
    }

    /**
     * 获取临时文件方法
     * @return
     */
    private File getTempFileMethod() {
        final File tempDir = new File(saveArticleFilePath);
        if(!tempDir.exists()) tempDir.mkdir();
        String tmpFileName = String.valueOf(Math.random() * 10000.0D).replace(".", "");
        return new File(tempDir, tmpFileName);
    }

    private EditorState saveTempFileMethod(int userid, File tmpFile, String path) {
        EditorBaseState editorBaseState = null;
        File targetFile = new File(path);
        File tempTargetFile = new File(path.substring(0, path.lastIndexOf("/")+1));
        if(!tempTargetFile.exists()) tempTargetFile.mkdir();
        if(targetFile.canWrite()) {
            return new EditorBaseState(false, 2);
        } else {
            try {
                FileCopyUtils.copy(new FileInputStream(tmpFile), new FileOutputStream(targetFile));
            } catch (IOException e) {
                e.printStackTrace();
                return new EditorBaseState(false, 4);
            }
            UploadFileByAdmin uploadFileByAdmin = new UploadFileByAdmin(userid, path);
            uploadFileByAdminMapper.insertUpload(uploadFileByAdmin);
            fileRecordMapper.insertRecord(new FileRecord(uploadFileByAdmin.getFilepath(), UploadFileByAdmin.tablename, uploadFileByAdmin.getId()));
            editorBaseState = new EditorBaseState(true);
            editorBaseState.putInfo("size", targetFile.length());
            editorBaseState.putInfo("title", targetFile.getName());
            return editorBaseState;
        }
    }

    private static EditorState valid(File file) {
        File parentPath = file.getParentFile();
        return !parentPath.exists() && !parentPath.mkdirs()?new EditorBaseState(false, 3):(!parentPath.canWrite()?new EditorBaseState(false, 2):new EditorBaseState(true));
    }

    /**
     * 是否是Base64格式文件
     * @param actionType              action请求类型
     * @param isBase64                是否是Base64格式文件
     */
    public final EditorState doUploadImageMethod(String actionType, boolean isBase64, HttpServletRequest request) throws FileUploadException, IOException {
        EditorState state;
        if(isBase64) {
            state = Base64UploaderSave(session.getAdmin().getId(), request.getParameter(EditorConfigContent.fileName));
        } else {
            state = BinaryUploaderSave(session.getAdmin().getId(), actionType, request);
        }
        return state;
    }

    /**
     * 上传isBase64 文件方法
     * @param content
     * @return
     */
    public final EditorState Base64UploaderSave(int userid, String content) throws IOException {
        byte[] data = Base64.decodeBase64(content);
        long maxSize = EditorConfigContent.scrawlMaxSize;
        if(!((long)data.length <= maxSize)) {
            return new EditorBaseState(false, 1);
        } else {
            String suffix = "jpg";
            String savePath = saveArticleFilePath + LocalDate.now() + File.separator + LocalDateTime.now().toString() + "." + suffix;
            EditorState storageState = saveBinaryFileMethod(data, savePath);
            if(storageState.isSuccess()) {
                storageState.putInfo("url", savePath);
                storageState.putInfo("type", suffix);
                storageState.putInfo("original", "");
            }
            return storageState;
        }
    }

    /**
     * 上传文件方法
     * @param userid
     * @param request
     * @return
     */
    public final EditorState BinaryUploaderSave(int userid, String actionType, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartRequest.getFile(EditorConfigContent.fileName);
            EditorState storageState = null;
            String originFileName = multipartFile.getOriginalFilename();
            String suffix = originFileName.substring(originFileName.lastIndexOf(".")).toLowerCase().replace(".", "");
            originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
            String savePath = saveArticleFilePath + LocalDate.now() + File.separator + LocalDateTime.now().getHour() + LocalDateTime.now().getMinute() + LocalDateTime.now().getSecond();
            long maxSize = 0;
            switch (actionType){
                case "uploadimage":
                case "catcherLocalDomain":
                    if (!Arrays.asList(EditorConfigContent.imageAllowFiles).contains(suffix)) {
                        return new EditorBaseState(false, EditorAppInfo.NOT_ALLOW_FILE_TYPE.toString());
                    }
                    maxSize = EditorConfigContent.imageMaxSize;
                    break;
                case "uploadscrawl":
                    maxSize = EditorConfigContent.scrawlMaxSize;
                    break;
                case "catchimage":
                    maxSize = EditorConfigContent.catcherMaxSize;
                    break;
                case "uploadvideo":
                    maxSize = EditorConfigContent.videoMaxSize;
                    if(!Arrays.asList(EditorConfigContent.videoAllowFiles).contains(suffix)) {
                        return new EditorBaseState(false, EditorAppInfo.NOT_ALLOW_FILE_TYPE.toString());
                    }
                    break;
                case "uploadfile":
                    maxSize = EditorConfigContent.fileMaxSize;
                    if(!Arrays.asList(EditorConfigContent.fileAllowFiles).contains(suffix)) {
                        return new EditorBaseState(false, EditorAppInfo.NOT_ALLOW_FILE_TYPE.toString());
                    }
                    break;
                default:
                    break;
            }
            savePath = savePath + multipartFile.getOriginalFilename();
            storageState = saveFileByInputStream(userid, multipartFile.getInputStream(), savePath, maxSize);
            int startPlace = savePath.indexOf("/files/");
            if (storageState.isSuccess()) {
                storageState.putInfo("url", savePath.substring(startPlace, savePath.length()));
                storageState.putInfo("type", suffix);
                storageState.putInfo("original", originFileName + suffix);
            }
            return storageState;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new EditorBaseState(false, EditorAppInfo.IO_ERROR.toString());
    }

    /**
     * 保存BinaryFile方法
     * @param data
     * @param path
     * @return
     */
    public EditorState saveBinaryFileMethod(byte[] data, String path) throws IOException {
        File file = new File(path);
        if(!file.getParentFile().exists()) file.getParentFile().mkdir();
        if(!file.exists()) file.createNewFile();
        EditorState editorState = valid(file);
        if(!editorState.isSuccess()) {
            return editorState;
        } else {
            try {
                BufferedOutputStream ioe = new BufferedOutputStream(new FileOutputStream(file));
                ioe.write(data);
                ioe.flush();
                ioe.close();
            } catch (IOException var5) {
                return new EditorBaseState(false, 4);
            }
            EditorBaseState baseEditorState = new EditorBaseState(true, file.getAbsolutePath());
            baseEditorState.putInfo("size", (long)data.length);
            baseEditorState.putInfo("title", file.getName());
            return baseEditorState;
        }
    }

    /**
     * 保存FileInputStream方法
     * @param userid
     * @param inputStream
     * @param path
     * @param maxSize
     * @return
     */
    public EditorState saveFileByInputStream(int userid, InputStream inputStream, String path, long maxSize) {
        EditorState editorState = null;
        File tmpFile = getTempFileMethod();
        byte[] dataBuf = new byte[2048];
        BufferedInputStream bis = new BufferedInputStream(inputStream, 8192);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), 8192);
            int count1;
            while((count1 = bis.read(dataBuf)) != -1) {
                bos.write(dataBuf, 0, count1);
            }
            bos.flush();
            bos.close();
            if(tmpFile.length() > maxSize) {
                tmpFile.delete();
                return new EditorBaseState(false, 1);
            } else {
                editorState = saveTempFileMethod(userid, tmpFile, path);
                if(!editorState.isSuccess()) {
                    tmpFile.delete();
                }
                return editorState;
            }
        } catch (IOException var10) {
            return new EditorBaseState(false, 4);
        }
    }

}
