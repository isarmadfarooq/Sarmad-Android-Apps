package com.sarmadtechempire.blogapp.Model;

public class BlogItemModel {

    private String heading;
    private String userName;
    private String date;
    private String post;
    private int likeCount;
    private String imageUrl;

    public BlogItemModel(String heading, String userName, String date, String post, int likeCount, String imageUrl)
    {
        this.heading = heading;
        this.userName = userName;
        this.date = date;
        this.post = post;
        this.likeCount = likeCount;
        this.imageUrl = imageUrl;
    }

    public String getHeading() {
        return heading;
    }
    public String getUserName() {
        return userName;
    }
    public String getDate() {
        return date;
    }
    public String getPost() {
        return post;
    }
    public int getLikeCount() {
        return likeCount;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public  void setHeading(String heading) {
        this.heading = heading;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setPost(String post) {
        this.post = post;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}

