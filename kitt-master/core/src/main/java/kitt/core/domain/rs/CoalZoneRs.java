package kitt.core.domain.rs;

import kitt.core.domain.SellInfo;
import kitt.core.domain.Shop;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xiangyang on 16/1/12.
 */
public class CoalZoneRs {

    private Integer shopId;
    private Integer supplyId;
    private BigDecimal price01;
    private BigDecimal price02;
    private String coaltype;
    private String deliveryDate;
    private BigDecimal price;
    private String deliverymode;
    private Integer availQuantity;
    private BigDecimal supplyQuantity;
    private String deliveryplace;
    private Integer supplyCount;
    private String location;
    private Integer NCV01;
    private Integer NCV02;
    private BigDecimal TM01;
    private BigDecimal TM02;
    //收到基硫分
    private BigDecimal RS01;
    private BigDecimal RS02;
    //空干基挥发分
    private BigDecimal ADV01;
    private BigDecimal ADV02;
    private String logo;
    //口号
    private String slogan;
    private String paymode;
    private  int pnameCount;
    //展示类型
    public enum Type {
        //企业类型 展示店铺
        enterprise,
        //商品    展示店铺商品
        product;
    }


    private String  provinceId;
    private List<Shop> shops;
    private List<SellInfo> sellInfos;

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Integer supplyId) {
        this.supplyId = supplyId;
    }

    public BigDecimal getPrice01() {
        return price01;
    }

    public void setPrice01(BigDecimal price01) {
        this.price01 = price01;
    }

    public BigDecimal getPrice02() {
        return price02;
    }

    public void setPrice02(BigDecimal price02) {
        this.price02 = price02;
    }

    public String getCoaltype() {
        return coaltype;
    }

    public void setCoaltype(String coaltype) {
        this.coaltype = coaltype;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(String deliverymode) {
        this.deliverymode = deliverymode;
    }

    public Integer getAvailQuantity() {
        return availQuantity;
    }

    public void setAvailQuantity(Integer availQuantity) {
        this.availQuantity = availQuantity;
    }

    public BigDecimal getSupplyQuantity() {
        return supplyQuantity;
    }

    public void setSupplyQuantity(BigDecimal supplyQuantity) {
        this.supplyQuantity = supplyQuantity;
    }

    public String getDeliveryplace() {
        return deliveryplace;
    }

    public void setDeliveryplace(String deliveryplace) {
        this.deliveryplace = deliveryplace;
    }

    public Integer getSupplyCount() {
        return supplyCount;
    }

    public void setSupplyCount(Integer supplyCount) {
        this.supplyCount = supplyCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getNCV01() {
        return NCV01;
    }

    public void setNCV01(Integer NCV01) {
        this.NCV01 = NCV01;
    }

    public Integer getNCV02() {
        return NCV02;
    }

    public void setNCV02(Integer NCV02) {
        this.NCV02 = NCV02;
    }

    public BigDecimal getTM01() {
        return TM01;
    }

    public void setTM01(BigDecimal TM01) {
        this.TM01 = TM01;
    }

    public BigDecimal getTM02() {
        return TM02;
    }

    public void setTM02(BigDecimal TM02) {
        this.TM02 = TM02;
    }

    public BigDecimal getRS01() {
        return RS01;
    }

    public void setRS01(BigDecimal RS01) {
        this.RS01 = RS01;
    }

    public BigDecimal getRS02() {
        return RS02;
    }

    public void setRS02(BigDecimal RS02) {
        this.RS02 = RS02;
    }

    public BigDecimal getADV01() {
        return ADV01;
    }

    public void setADV01(BigDecimal ADV01) {
        this.ADV01 = ADV01;
    }

    public BigDecimal getADV02() {
        return ADV02;
    }

    public void setADV02(BigDecimal ADV02) {
        this.ADV02 = ADV02;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getPaymode() {
        return paymode;
    }

    public int getPnameCount() {
        return pnameCount;
    }

    public void setPnameCount(int pnameCount) {
        this.pnameCount = pnameCount;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = StringUtils.isEmpty(provinceId)==true?null:provinceId;
    }

    public List<Shop> getShops() {
        return shops;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public List<SellInfo> getSellInfos() {
        return sellInfos;
    }

    public void setSellInfos(List<SellInfo> sellInfos) {
        this.sellInfos = sellInfos;
    }

    public void setPaymode(String paymode) {
        this.paymode = paymode;
    }
    public static class CoalZoneRsPage<T>{
        private int page = 1;
        private int pagesize = 12;
        // 总记录数
        private Integer totalCount;
        // 总页数
        private Integer totalPage;

        private List<T> list;
        private int indexNum;


        public int getPage() {
            return this.page;
        }

        public int getIndexNum() {
            this.indexNum = (getPage() - 1) * this.pagesize;
            return indexNum;
        }

        public void setPage(int page) {
            this.page = page < 1 ? 1 : page;
        }

        public int getPagesize() {
            return pagesize;
        }

        public void setPagesize(int pagesize) {
            this.pagesize = pagesize;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(Integer totalPage) {
            this.totalPage = totalPage;
        }

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

        public void setIndexNum(int indexNum) {
            this.indexNum = indexNum;
        }
    }

}
