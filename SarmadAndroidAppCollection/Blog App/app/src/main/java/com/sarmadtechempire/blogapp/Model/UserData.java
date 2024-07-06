package com.sarmadtechempire.blogapp.Model;

public class UserData {

    private String name;
    private String email;
    private String profileImage;

    public UserData() {
        this.name = "";
        this.email = "";
        this.profileImage = null; // or initialize to ""
    }

    public UserData(String name, String email) {
        this.name = name != null ? name : "";
        this.email = email != null ? email : "";
        this.profileImage = null; // or initialize to ""
    }

    public UserData(String name, String email, String profileImage) {
        this.name = name != null ? name : "";
        this.email = email != null ? email : "";
        this.profileImage = profileImage; // profileImage can be null if not provided
    }

    // Getters and setters (optional)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
