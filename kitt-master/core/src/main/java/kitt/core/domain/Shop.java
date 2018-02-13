package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 15/9/9.
 * 煤矿店铺（旗舰店）
 */
public class Shop implements Serializable {
    private Integer id;                         //店铺id
    private int userid;                         //客户users表对应 id
    private String companyname;                 //客户公司名称
    private String shopid;                      //店铺编号 编号规则 MK00001
    private String name;                        //店铺名称
    private int level;                          //店铺级别，类型
    private int sequence;                       //煤矿在前台宣传区排列的顺序
    private int status;                         //1-已上架 2-待上架 0-空
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;           //店铺创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;       //最后一次更新时间
    private String adverpic;                    //店铺宣传图片
    private String logo;                        //店铺logo url
    private String featurepic001;               //店铺特色图片001 url
    private String featurepic002;               //店铺特色图片002 url
    private String featurepic003;               //店铺特色图片003 url
    private String featurepic004;               //店铺特色图片004 url
    private String featuretext;                 //店铺特色文字描述
    private String partnerpic001;               //合作伙伴图片001 url
    private String partnerpic002;               //合作伙伴图片002 url
    private String partnertext;                 //战略合作内容
    private String location;                    //所在地区
    private String provinceId;
    private String cityId;
    private String slogan;                      //口号

    //首页 煤炭专区列表属性
    private String  mainproduct;               //主售产品
    private BigDecimal productprice;               //价格
    private Integer totalsupply;              //累计供应量

    public Shop() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public String getAdverpic() {
        return adverpic;
    }

    public void setAdverpic(String adverpic) {
        this.adverpic = adverpic;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getFeaturepic001() {
        return featurepic001;
    }

    public void setFeaturepic001(String featurepic001) {
        this.featurepic001 = featurepic001;
    }

    public String getFeaturepic002() {
        return featurepic002;
    }

    public void setFeaturepic002(String featurepic002) {
        this.featurepic002 = featurepic002;
    }

    public String getFeaturepic003() {
        return featurepic003;
    }

    public void setFeaturepic003(String featurepic003) {
        this.featurepic003 = featurepic003;
    }

    public String getFeaturepic004() {
        return featurepic004;
    }

    public void setFeaturepic004(String featurepic004) {
        this.featurepic004 = featurepic004;
    }

    public String getFeaturetext() {
        return featuretext;
    }

    public void setFeaturetext(String featuretext) {
        this.featuretext = featuretext;
    }

    public String getPartnerpic001() {
        return partnerpic001;
    }

    public void setPartnerpic001(String partnerpic001) {
        this.partnerpic001 = partnerpic001;
    }

    public String getPartnerpic002() {
        return partnerpic002;
    }

    public void setPartnerpic002(String partnerpic002) {
        this.partnerpic002 = partnerpic002;
    }

    public String getPartnertext() {
        return partnertext;
    }

    public void setPartnertext(String partnertext) {
        this.partnertext = partnertext;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getMainproduct() {
        return mainproduct;
    }

    public void setMainproduct(String mainproduct) {
        this.mainproduct = mainproduct;
    }

    public BigDecimal getProductprice() {
        return productprice;
    }

    public void setProductprice(BigDecimal productprice) {
        this.productprice = productprice;
    }

    public Integer getTotalsupply() {
        return totalsupply;
    }

    public void setTotalsupply(Integer totalsupply) {
        this.totalsupply = totalsupply;
    }
}
