package kitt.core.domain;

import java.io.Serializable;

/**
 * Created by liuxinjie on 15/12/29.
 */
public enum EnumFileType implements Serializable {

    //文件所属模块
    File_Article("文章文件"),
    File_Tender("招标文件"),
    File_Meeting("会议文件"),
    File_DataCenter("数据中心文件"),
    File_PartnerLogo("合作伙伴logo文件"),
    File_ShopCoal("煤矿专区文件"),
    File_UserCompany("用户公司资质图片"),
    File_IndexBanner("网站,微信等Banner图片"),



    //文件类型分类
    IMG("图片文件类型"),
    ATTACHMENT("附件文件格式"),
    EXCEL("Excel格式文件"),
    OFFICE("办公文件类型");

    public String value;

    EnumFileType(String value) {
        this.value = value;
    }
    EnumFileType(){}

    public String value() {
        return this.value;
    }
}
