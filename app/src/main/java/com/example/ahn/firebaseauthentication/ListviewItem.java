package com.example.ahn.firebaseauthentication;

/**
 * Created by Ahn on 2017-02-15.
 */

public class ListviewItem {
    String name, chatContent;

    ListviewItem(){

    }
    ListviewItem(String name, String chatContent){
        this.name = name;
        this.chatContent = chatContent;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setChatContent(String chatContent){
        this.chatContent = chatContent;
    }

    public String getChatContent(){
        return this.chatContent;
    }
}
