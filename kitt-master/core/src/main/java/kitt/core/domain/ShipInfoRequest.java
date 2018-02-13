package kitt.core.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by lich on 16/1/13.
 */
public class ShipInfoRequest {
    @NotNull
    private int cdId;
    @NotBlank
    private  String Info;
    @NotBlank
    private String ProcessTime;
    @NotNull
    private int InfoId;
    @NotBlank
    private String Sign;
    @NotNull
    private int Sort;
    @NotBlank
    private String No;

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public String getProcessTime() {
        return ProcessTime;
    }

    public void setProcessTime(String processTime) {
        ProcessTime = processTime;
    }

    public int getInfoId() {
        return InfoId;
    }

    public void setInfoId(int infoId) {
        InfoId = infoId;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public int getSort() {
        return Sort;
    }

    public void setSort(int sort) {
        Sort = sort;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public int getCdId() {
        return cdId;
    }

    public void setCdId(int cdId) {
        this.cdId = cdId;
    }
}
