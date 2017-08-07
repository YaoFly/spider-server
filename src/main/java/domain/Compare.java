package domain;

import javax.persistence.*;

/**
 * Created by ooopic on 2017/7/20.
 */
@Entity
public class Compare {
    @Id
    @GeneratedValue
    private Long id;
    private String targetFile;
    @Column(unique = true)
    private String targetImgUrl;
    private String score;
    private String sourceFile;
    private String sourceUrl;
    private String targetUrl;
    private String sourceTitle;
    private Long datetime;
    private String tag;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceTitle() {
        return sourceTitle;
    }

    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public String getTargetImgUrl() {
        return targetImgUrl;
    }

    public void setTargetImgUrl(String targetImgUrl) {
        this.targetImgUrl = targetImgUrl;
    }
}
