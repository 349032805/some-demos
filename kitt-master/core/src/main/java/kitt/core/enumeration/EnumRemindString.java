package kitt.core.enumeration;

import java.io.Serializable;

/**
 * Created by liuxinjie on 16/2/26.
 */
public enum  EnumRemindString implements Serializable {
    //admin模块
    Admin_System_Error("系统出错,请联系技术人员"),
    Admin_Refresh_Again("请刷新页面重试"),
    Admin_ImportExcel_BadTemplate("上传的Excel不是标准模板格式,请按照标准模板格式排版,再上传"),
    Admin_Excel_File_NULL("上传的Excel表是空文件"),
    Admin_Excel_Invalid_NotTemplate("上传的Excel表无效,按照标准模板上传"),

    //前台
    Client_System_Error("系统出错了,已自动通知我们的团队,我们会尽快修复!");

    public String value;

    EnumRemindString(String value) {
        this.value=value;
    }
    EnumRemindString(){}

    public String value() {
        return this.value;
    }
}