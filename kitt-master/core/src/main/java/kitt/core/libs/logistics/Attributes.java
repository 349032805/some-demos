package kitt.core.libs.logistics;

/**
* Created by zhangbolun on 15/12/14.
*/
public class Attributes {
    private int databodysize;

    public int getDatabodysize() {
        return databodysize;
    }

    public void setDatabodysize(int databodysize) {
        this.databodysize = databodysize;
    }

    public Attributes() {}

    public Attributes(int databodysize) {
        this.databodysize = databodysize;
    }

    @Override
    public String toString() {
        return "Attributes{" +
                "databodysize=" + databodysize +
                '}';
    }
}
