package kitt.core.editor;

import java.io.Serializable;

/**
 * Created by liuxinjie on 16/1/13.
 */
public enum EnumActionType implements Serializable {
    config(0),                      //编辑器初始化
    uploadimage(1),                 //上传图片
    uploadscrawl(2),                //上传涂鸦图片
    uploadvideo(3),                 //上传视频文件
    uploadfile(4),                  //上传附件文件
    catchimage(5),                  //上传远程图片
    listfile(6),                    //列出所有附件文件
    listimage(7);                   //列出所有的图片

    public int value;

    EnumActionType(int value) {
        this.value = value;
    }

    EnumActionType() {}

    public int value() {
        return this.value;
    }

}
