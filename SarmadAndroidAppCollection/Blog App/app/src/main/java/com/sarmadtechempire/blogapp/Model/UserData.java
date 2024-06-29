package com.sarmadtechempire.blogapp.Model;

public class UserData {

    private String name;
    private String email; // Added email field
//    private String password;

    public UserData(String name, String email) {
        this.name = name;
        this.email = email;
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

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
}
