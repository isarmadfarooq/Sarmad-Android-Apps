package com.sarmadtechempire.blogapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class BlogItemModel implements Parcelable{

    private String userId;
    private String imageUrl;
    private String heading;
    private String post;
    private String date;
    private String userName;
    private int likeCount;

    // No-argument constructor
    public BlogItemModel() {
        // Required for Firebase deserialization
    }

    public BlogItemModel(String userId, String imageUrl, String heading, String post, String date, String userName) {
        this.userId = userId != null ? userId : "";
        this.imageUrl = imageUrl != null ? imageUrl : "";
        this.heading = heading != null ? heading : "";
        this.post = post != null ? post : "";
        this.date = date != null ? date : "";
        this.userName = userName != null ? userName : "";
        this.likeCount = 0; // Default value for likeCount
    }

    protected BlogItemModel(Parcel in)
    {
        userId = in.readString();
        imageUrl = in.readString();
        heading = in.readString();
        post = in.readString();
        date = in.readString();
        userName = in.readString();
        likeCount = in.readInt();
    }

    public static final Parcelable.Creator<BlogItemModel> CREATOR = new Parcelable.Creator<BlogItemModel>() {
        @Override
        public BlogItemModel createFromParcel(Parcel in) {
            return new BlogItemModel(in);
        }

        @Override
        public BlogItemModel[] newArray(int size) {
            return new BlogItemModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(imageUrl);
        dest.writeString(heading);
        dest.writeString(post);
        dest.writeString(date);
        dest.writeString(userName);
        dest.writeInt(likeCount);
    }


        // Getters
    public String getUserId() {
        return userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getHeading() {
        return heading;
    }

    public String getPost() {
        return post;
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public int getLikeCount() {
        return likeCount;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
