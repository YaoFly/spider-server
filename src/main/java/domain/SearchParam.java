package domain;

public class SearchParam {
    private String image="";
    private String imagef="";
    private String id="";
    private int shard=-1;
    private String tags="";
    private String url="";
    private String keyword="";

    public void setImage(String base64){image=base64;}
    public String getImage(){return image;}

    public void setImagef(String base64){imagef=base64;}
    public String getImagef(){return imagef;}

    public void setId(String i){id=i;}
    public String getId(){return id;}

    public void setShard(int i){shard=i;}
    public int getShard(){return shard;}

    public void setTags(String t){tags=t;}
    public String getTags(){return tags;}

    public void setUrl(String u){url=u;}
    public String getUrl(){return url;}

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
