package com.example.root.citizenforumapp;

/**
 * Created by hudixt on 11/28/2016.
 */
public class ItemData {


    private String title;
    private String imageUrl;
    private String Problem;
    public ItemData(String title, String imageUrl, String pb){

        this.title = title;
        this.imageUrl = imageUrl;
        this.Problem  = pb;
    }
    public String getTitle(){
        return this.title;
    }
    public String getImageUrl(){
        return this.imageUrl;
    }
    public String getProblemDescription(){ return this.Problem; }
    // getters & setters
}