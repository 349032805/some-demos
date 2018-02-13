package kitt.core.editor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuxinjie on 15/11/11.
 */
public enum EditorAppInfo implements Serializable {
    SUCCESS("SUCCESS"),
    MAX_SIZE("文件大小超出限制"),
    PERMISSION_DENIED("权限不足"),
    FAILED_CREATE_FILE("创建文件失败"),
    IO_ERROR("IO错误"),
    NOT_MULTIPART_CONTENT("上传表单不是multipart/form-data类型"),
    PARSE_REQUEST_ERROR("解析上传表单错误"),
    NOTFOUND_UPLOAD_DATA("未找到上传数据"),
    NOT_ALLOW_FILE_TYPE("不允许的文件类型"),
    INVALID_ACTION("无效的Action"),
    CONFIG_ERROR("配置文件初始化失败"),
    PREVENT_HOST("抓取远程图片失败"),
    CONNECTION_ERROR("被阻止的远程主机"),
    REMOTE_FAIL("远程连接出错"),
    NOT_DIRECTORY("指定路径不是目录"),
    NOT_EXIST("指定路径并不存在"),
    ILLEGAL("Callback参数名不合法"),
    IMAGE_MAX_SIZE("图片超过了最大尺寸");

    public static Map<Integer, String> info = new HashMap() {
        {
            this.put(Integer.valueOf(0), "SUCCESS");
            this.put(Integer.valueOf(101), "无效的Action");
            this.put(Integer.valueOf(102), "配置文件初始化失败");
            this.put(Integer.valueOf(203), "抓取远程图片失败");
            this.put(Integer.valueOf(201), "被阻止的远程主机");
            this.put(Integer.valueOf(202), "远程连接出错");
            this.put(Integer.valueOf(1), "文件大小超出限制");
            this.put(Integer.valueOf(2), "权限不足");
            this.put(Integer.valueOf(3), "创建文件失败");
            this.put(Integer.valueOf(4), "IO错误");
            this.put(Integer.valueOf(5), "上传表单不是multipart/form-data类型");
            this.put(Integer.valueOf(6), "解析上传表单错误");
            this.put(Integer.valueOf(7), "未找到上传数据");
            this.put(Integer.valueOf(8), "不允许的文件类型");
            this.put(Integer.valueOf(301), "指定路径不是目录");
            this.put(Integer.valueOf(302), "指定路径并不存在");
            this.put(Integer.valueOf(401), "Callback参数名不合法");
            this.put(Integer.valueOf(606), "图片超过了最大尺寸");
        }
    };


    public static String getStateInfo(int key) {
        return (String)info.get(Integer.valueOf(key));
    }

    public String value;

    EditorAppInfo(String value) {
        this.value = value;
    }

    EditorAppInfo() {}

    public String value() {
        return this.value;
    }

}
