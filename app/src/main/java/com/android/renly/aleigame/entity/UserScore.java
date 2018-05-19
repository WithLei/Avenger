package com.android.renly.aleigame.entity;

public class UserScore {
    private String name;
    private int score;

    public UserScore(String userName, int score) {
        this.name = userName;
        this.score = score;
    }

    public String getUserName() {
        return name;
    }

    public void setUserName(String userName) {
        this.name = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
