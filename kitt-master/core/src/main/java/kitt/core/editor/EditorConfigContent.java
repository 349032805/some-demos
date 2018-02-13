package kitt.core.editor;


import java.io.Serializable;

/**
 * Created by liuxinjie on 15/12/22.
 */
public class EditorConfigContent implements Serializable {
    /**
     * 公共变量
     */
    public static String fileName = "upfile";                                //上传文件名称
    public static String urlPrefix = "";                                     //文件访问路径前缀
    //允许上传的图片格式
    public static String[] imageAllowFiles = new String[]{"png", "jpg", "jpeg", "gif", "bmp"};
    //允许上传的视频格式
    public static String[] videoAllowFiles = new String[]{"flv", "swf", "mkv", "avi", "rm", "rmvb", "mpeg", "mpg",
            "ogg", "ogv", "mov", "wmv", "mp4", "webm", "mp3", "wav", "mid"};
    //允许上传的文件格式
    public static String[] fileAllowFiles = new String[]{"png", "jpg", "jpeg", "gif", "bmp",
            "flv", "swf", "mkv", "avi", "rm", "rmvb", "mpeg", "mpg",
            "ogg", "ogv", "mov", "wmv", "mp4", "webm", "mp3", "wav", "mid",
            "rar", "zip", "tar", "gz", "7z", "bz2", "cab", "iso",
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt", "md", "xml"};
    /**
     * 上传图片的配置项
     */
    public static String imageActionName = "uploadimage";                    //执行上传图片的action名称
    public static int imageMaxSize = 10240000;                               //上传图片大小限制,单位B
    public static boolean imageCompressEnable = false;                       //是否压缩图片,
    public static int imageCompressBorder = 1000;                            //图片压缩最长宽度限制
    public static String imageInsertAlign = "none";                          //图片的浮动方式

    /**
     * 涂鸦图片上传配置项
     */
    public static String scrawlActionName = "uploadscrawl";                  //执行上传涂鸦图片的action名称
    public static int scrawlMaxSize = 10240000;                              //上传涂鸦文件大小限制,单位B
    public static String scrawlInsertAlign = "none";                         //涂鸦图片的浮动方式

    /**
     * 截图工具上传配置项
     */
    public static String snapscreenActionName = "uploadimage";               //执行上传截图图片的action名称
    public static String snapscreenInsertAlign = "none";                     //插入的图片浮动方式

    /**
     * 抓取远程图片配置
     * 此功能暂时未开发
     */
    public static String catcherLocalDomain = "";                            //远程服务器ip,域名
    public static String catcherActionName = "catchimage";                   //执行抓取远程图片的action名称
    public static int catcherMaxSize = 10240000;                             //上传远程图片大小限制,单位B

    /**
     * 上传视频设置
     */
    public static String videoActionName = "uploadvideo";                    //执行上传视频的action名称
    public static int videoMaxSize = 102400000;                              //上传视频大小限制,单位B

    /**
     * 上传文件(附件)配置
     */
    public static String fileActionName = "uploadfile";                      //执行上传文件的action名称
    public static int fileMaxSize = 51200000;                                //上传文件大小限制,单位B

    /**
     * 列出指定目录下的图片
     */
    public static String imageManagerActionName = "listimage";               //执行图片管理的action名称
    public static int imageManagerListSize = 20;                             //每次列出文件数量;
    public static String imageManagerInsertAlign = "none";                   //插入的图片浮动方式

    /**
     * 列出指定目录下的文件
     */
    public static String fileManagerActionName = "listfile";                 //执行文件管理的action名称
    public static int fileManagerListSize = 20;                              //每次列出文件数量


}
