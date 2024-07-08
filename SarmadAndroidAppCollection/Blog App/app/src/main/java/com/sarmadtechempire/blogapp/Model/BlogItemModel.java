package com.sarmadtechempire.blogapp.Model;

public class BlogItemModel {

    private String heading;
    private String userName;
    private String date;
    private String post;
    private int likeCount;
    private String imageUrl;

    // No-argument constructor
    public BlogItemModel() {
        // Required for Firebase deserialization
    }

    public BlogItemModel(String heading, String userName, String date, String post, int likeCount, String imageUrl) {
        this.heading = heading != null ? heading : "";
        this.userName = userName != null ? userName : "";
        this.date = date != null ? date : "";
        this.post = post != null ? post : "";
        this.likeCount = likeCount; // no need to handle int, defaults to 0 if uninitialized
        this.imageUrl = imageUrl != null ? imageUrl : "";
    }

    // Getters
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

    // Setters
    public void setHeading(String heading) {
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
