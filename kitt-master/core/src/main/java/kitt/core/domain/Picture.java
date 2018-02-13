package kitt.core.domain;

/**
 * Created by liuxinjie on 15/11/18.
 */
public class Picture {
    private Integer width;          //图片的宽
    private Integer height;         //图片的长
    private String url;             //图片的路径
    boolean suitable;   //是否合适

    public Picture() {
    }

    public Picture(Integer width, Integer height, boolean suitable) {
        this.width = width;
        this.height = height;
        this.suitable = suitable;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSuitable() {
        return suitable;
    }

    public void setSuitable(boolean suitable) {
        this.suitable = suitable;
    }
}
