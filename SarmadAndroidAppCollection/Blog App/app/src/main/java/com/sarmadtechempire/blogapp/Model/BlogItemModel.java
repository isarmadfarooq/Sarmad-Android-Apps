package com.sarmadtechempire.blogapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogItemModel implements Parcelable {

    private String userId;
    private String imageUrl;
    private String heading;
    private String post;
    private String date;
    private String userName;
    private Boolean isSaved;
    private int likeCount;
    private String postId;
    private MutableLiveData<List<String>> likesBy;

    // No-argument constructor

    // No-argument constructor required for Firebase deserialization
    public BlogItemModel() {
        this.userId = "";
        this.imageUrl = "";
        this.heading = "";
        this.post = "";
        this.date = "";
        this.userName = "";
        this.isSaved = false;
        this.likeCount = 0;
        this.postId = "";
        this.likesBy = new MutableLiveData<>(new ArrayList<>());
    }

    public BlogItemModel(String userId, String userImageUrlFromDb, String title, String description, String currentDate, String userNameFromDb) {
        // Required for Firebase deserialization
        this.likesBy = new MutableLiveData<>(new ArrayList<>()); // Initialize here
    }

    public BlogItemModel(String userId, String imageUrl, String heading, String post, String date, String userName, String isSaved) {
        this.userId = userId != null ? userId : "";
        this.imageUrl = imageUrl != null ? imageUrl : "";
        this.heading = heading != null ? heading : "";
        this.post = post != null ? post : "";
        this.date = date != null ? date : "";
        this.userName = userName != null ? userName : "";
        this.likeCount = 0;
        this.postId = "";
        this.isSaved = Boolean.valueOf(isSaved != null ? isSaved : "");
        this.likesBy = new MutableLiveData<>(new ArrayList<>()); // Initialize here
    }

    protected BlogItemModel(Parcel in) {
        userId = in.readString();
        imageUrl = in.readString();
        heading = in.readString();
        post = in.readString();
        date = in.readString();
        userName = in.readString();
        likeCount = in.readInt();
        postId = in.readString();
        List<String> likesList = new ArrayList<>();
        in.readStringList(likesList);
        isSaved = in.readByte() != 0;
        likesBy = new MutableLiveData<>(likesList);
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
        dest.writeString(postId);
        dest.writeByte((byte) (isSaved != null && isSaved ? 1 : 0));
        // Write the list to Parcel
        List<String> likesList = likesBy.getValue() != null ? likesBy.getValue() : new ArrayList<>();
        dest.writeStringList(likesList);
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

    public String getPostId() {
        return postId;
    }

    public List<String> getLikesBy() {
        return likesBy.getValue() != null ? new ArrayList<>(likesBy.getValue()) : new ArrayList<>();
    }
    public Boolean getIsSaved() {
        return isSaved;
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

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setLikesBy(MutableLiveData<List<String>> likesBy) {
        this.likesBy = likesBy != null ? likesBy : new MutableLiveData<>(new ArrayList<>()); // Initialize if null
    }
    public void setIsSaved(Boolean isSaved) {
        this.isSaved = isSaved;
    }

    // toMap method
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("imageUrl", imageUrl);
        result.put("heading", heading);
        result.put("post", post);
        result.put("date", date);
        result.put("userName", userName);
        result.put("likeCount", likeCount);
        result.put("postId", postId);
        result.put("isSaved", isSaved);
        result.put("likesBy", likesBy.getValue() != null ? new ArrayList<>(likesBy.getValue()) : new ArrayList<>());
        return result;
    }
}
