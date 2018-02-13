package kitt.core.domain;

/**
 * Created by zhangbolun on 15/12/2.
 */
public class MyInterestShop implements Comparable<MyInterestShop> {
    private int myinterestid;    //关注id
    private int shopid;          //店铺id
    private String shopname;     //店铺名
    private String location;     //店铺所在区域
    private int myinterestcount; //关注人数
    private int sellinfocount;   //在售产品数量

    private String sortfield;    //排序字段
    private boolean isdesc;      //降序 or 升序


    public MyInterestShop(){}

    public MyInterestShop(int shopid,int myinterestid,String shopname, String location,int sellinfocount, int myinterestcount,String sortfield,boolean isdesc) {
        this.sellinfocount = sellinfocount;
        this.myinterestid = myinterestid;
        this.shopname = shopname;
        this.location = location;
        this.myinterestcount = myinterestcount;
        this.shopid=shopid;
        this.sortfield=sortfield;
        this.isdesc=isdesc;
    }

    public int getMyinterestid() {
        return myinterestid;
    }

    public void setMyinterestid(int myinterestid) {
        this.myinterestid = myinterestid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMyinterestcount() {
        return myinterestcount;
    }

    public void setMyinterestcount(int myinterestcount) {
        this.myinterestcount = myinterestcount;
    }

    public int getSellinfocount() {
        return sellinfocount;
    }

    public void setSellinfocount(int sellinfocount) {
        this.sellinfocount = sellinfocount;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public String getSortfield() {
        return sortfield;
    }

    public void setSortfield(String sortfield) {
        this.sortfield = sortfield;
    }

    public boolean isIsdesc() {
        return isdesc;
    }

    public void setIsdesc(boolean isdesc) {
        this.isdesc = isdesc;
    }

    @Override
    public int compareTo(MyInterestShop myInterestShop){
        if(sortfield.equals("myinterestcount")){
            if(isdesc==true){//关注人数降序
                if(myinterestcount>myInterestShop.getMyinterestcount()) {
                    return -1;
                }else if(myinterestcount<myInterestShop.getMyinterestcount()) {
                    return 1;
                }else {
                    return 0;
                }
            }else {//关注人数升序
                if(myinterestcount>myInterestShop.getMyinterestcount()) {
                    return 1;
                }else if(myinterestcount<myInterestShop.getMyinterestcount()) {
                    return -1;
                }else {
                    return 0;
                }
            }
        }else {
            if(isdesc==true){//在售产品数量降序
                if(sellinfocount>myInterestShop.getSellinfocount()) {
                    return -1;
                }else if(sellinfocount<myInterestShop.getSellinfocount()) {
                    return 1;
                }else {
                    return 0;
                }
            }else {//在售产品数量升序
                if(sellinfocount>myInterestShop.getSellinfocount()) {
                    return 1;
                }else if(sellinfocount<myInterestShop.getSellinfocount()) {
                    return -1;
                }else {
                    return 0;
                }
            }
        }
    }



    @Override
    public String toString() {
        return "MyInterestShop{" +
                "myinterestid=" + myinterestid +
                ", shopid=" + shopid + '\'' +
                ", shopname='" + shopname + '\'' +
                ", location='" + location + '\'' +
                ", myinterestcount=" + myinterestcount +
                ", sellinfocount=" + sellinfocount +
                '}';
    }
}
