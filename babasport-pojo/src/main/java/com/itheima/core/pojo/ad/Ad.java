package com.itheima.core.pojo.ad;

import java.io.Serializable;

public class Ad implements Serializable {
    /**
     * 广告ID
     */
    private Long id;

    /**
     * 广告位置ID
     */
    private Long positionId;

    /**
     * 广告标题
     */
    private String title;

    /**
     * 点击图片时触发的链接
     */
    private String url;

    /**
     * 图片绝对路径
     */
    private String picture;

    /**
     * 广告图片的高
     */
    private Integer height;

    /**
     * 广告图片的宽
     */
    private Integer width;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture == null ? null : picture.trim();
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", positionId=").append(positionId);
        sb.append(", title=").append(title);
        sb.append(", url=").append(url);
        sb.append(", picture=").append(picture);
        sb.append(", height=").append(height);
        sb.append(", width=").append(width);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}