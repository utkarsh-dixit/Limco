package com.hudixt.limco;

/**
 * Created by hudixt on 11/28/2016.
 */
public class ItemData {


    private String title;
    private String imageUrl;
    private String Problem;
    private String unique_id;
    public ItemData(String title, String imageUrl, String pb,String unqiue_id){

        this.title = title;
        this.imageUrl = imageUrl;
        this.Problem  = pb;
        this.unique_id  = unqiue_id;

    }
    public String getTitle(){
        return this.title;
    }
    public String getImageUrl(){
        return this.imageUrl;
    }
    public String getProblemDescription(){ return this.Problem; }
    public String returnUniqueId(){ return this.unique_id;}
    // getters & setters
}