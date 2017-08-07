package domain;

/**
 * Created by ooopic on 2017/4/14.
 */
public class WxData{
    private String url;
    private String imgUrl;
    private String title;
    private String tag;
    private Long datetime;

    public WxData() {}

    public WxData(String url, String imgUrl, String title, String tag, Long datetime) {
        this.url = url;
        this.imgUrl = imgUrl;
        this.title = title;
        this.tag = tag;
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

}
