package com.example.imageclassificationapp.model;

public class Photo {
    public String highestScore;
    public String secondHighestScore;
    public String thirdHighestScore;
    public String userName;
    public String photoName;

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(String highestScore) {
        this.highestScore = highestScore;
    }

    public String getSecondHighestScore() {
        return secondHighestScore;
    }

    public void setSecondHighestScore(String secondHighestScore) {
        this.secondHighestScore = secondHighestScore;
    }

    public String getThirdHighestScore() {
        return thirdHighestScore;
    }

    public void setThirdHighestScore(String thirdHighestScore) {
        this.thirdHighestScore = thirdHighestScore;
    }
}
