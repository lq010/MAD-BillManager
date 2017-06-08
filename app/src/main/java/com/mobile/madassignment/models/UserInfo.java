package com.mobile.madassignment.models;

import static com.mobile.madassignment.util.Constants.Default_Photo;

/**
 * Created by lq on 08/06/2017.
 */

public class UserInfo {
    protected String id;
    protected String name;
    protected String telNumber;
    protected String email;
    protected String profilePhoto;

    public UserInfo(){

    }
    public UserInfo(String id, String name, String email, String Photo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePhoto = Photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profile) {
        this.profilePhoto = profile;
    }
    public boolean isPhotoExist(){
        if(!this.profilePhoto.matches(Default_Photo))
            return true;
        else
            return  false;
    }
}
