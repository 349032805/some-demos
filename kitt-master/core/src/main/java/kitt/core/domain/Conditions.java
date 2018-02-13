package kitt.core.domain;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by lich on 16/1/25.
 */
public class Conditions {
    @NotBlank(message="装货地点不能为空")
    private String LoadPortName;    //目的港，空载港
    private double LowerTon;        //下限吨数,传值方式：可传null,不限传null值
    private double UpperTon;        //上限吨数,传值方式：可传null,不限传null值
    private String BeginDate;       //空船开始日期
    private String EndDate;         //空船结束日期

    public String getLoadPortName() {
        return LoadPortName;
    }

    public void setLoadPortName(String loadPortName) {
        LoadPortName = loadPortName;
    }

    public double getLowerTon() {
        return LowerTon;
    }

    public void setLowerTon(double lowerTon) {
        LowerTon = lowerTon;
    }

    public double getUpperTon() {
        return UpperTon;
    }

    public void setUpperTon(double upperTon) {
        UpperTon = upperTon;
    }

    public String getBeginDate() {
        return BeginDate;
    }

    public void setBeginDate(String beginDate) {
        BeginDate = beginDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }
}
