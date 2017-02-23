package com.example.ahn.firebaseauthentication;

/**
 * Created by Ahn on 2017-02-13.
 */

public class ChatData {
    private String userName;
    private String message;
    private String testData;

    public ChatData() { }

    public ChatData(String userName, String message, String testData) {
        this.userName = userName;
        this.message = message;
        this.testData = testData;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTestData(){
        return testData;
    }

    public void setTestData(String testData){
        this.testData = testData;
    }
}