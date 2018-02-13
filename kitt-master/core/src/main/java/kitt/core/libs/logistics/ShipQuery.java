package kitt.core.libs.logistics;

/**
 * Created by zhangbolun on 16/1/9.
 */
public class ShipQuery {
    private Integer Page=1;           //当前第几页
    private Integer PageSize;       //每页显示多少条数据
    private String LoadPortName="";    //目的港，空载港
    private String ShipName="";        //船名
    private String LowerTon="";        //下限吨数,传值方式：可传null,不限传null值
    private String UpperTon="";        //上限吨数,传值方式：可传null,不限传null值
    private String BeginDate="";       //空船开始日期
    private String EndDate="";         //空船结束日期
    private Integer SortNum=0;        //排序字段（默认0-日期　1-吨位　2－推荐 3-等级
    private Integer SortIdent=0;      //排序标识（0降序des　1升序Asc）
    public ShipQuery() {}



    public Integer getPage() {
        return Page;
    }

    public void setPage(Integer page) {
        Page = page;
    }

    public Integer getPageSize() {
        return PageSize;
    }

    public void setPageSize(Integer pageSize) {
        PageSize = pageSize;
    }

    public String getLoadPortName() {
        return LoadPortName;
    }

    public void setLoadPortName(String loadPortName) {
        LoadPortName = loadPortName;
    }

    public String getShipName() {
        return ShipName;
    }

    public void setShipName(String shipName) {
        ShipName = shipName;
    }

    public String getLowerTon() {
        return LowerTon;
    }

    public void setLowerTon(String lowerTon) {
        LowerTon = lowerTon;
    }

    public String getUpperTon() {
        return UpperTon;
    }

    public void setUpperTon(String upperTon) {
        UpperTon = upperTon;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getBeginDate() {
        return BeginDate;
    }

    public void setBeginDate(String beginDate) {
        BeginDate = beginDate;
    }

    public Integer getSortNum() {
        return SortNum;
    }

    public void setSortNum(Integer sortNum) {
        SortNum = sortNum;
    }

    public Integer getSortIdent() {
        return SortIdent;
    }

    public void setSortIdent(Integer sortIdent) {
        SortIdent = sortIdent;
    }


    @Override
    public String toString() {
        return "{" +
                "Page:" + Page +
                ", PageSize:" + PageSize +
                ", LoadPortName:'" + LoadPortName + '\'' +
                ", ShipName:'" + ShipName + '\'' +
                ", LowerTon:" + LowerTon +
                ", UpperTon:" + UpperTon +
                ", BeginDate:'" + BeginDate + '\'' +
                ", EndDate:'" + EndDate + '\'' +
                ", SortNum:" + SortNum +
                ", SortIdent:" + SortIdent +
                '}';
    }
}
