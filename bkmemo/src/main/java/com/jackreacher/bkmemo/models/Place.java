package com.jackreacher.bkmemo.models;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.jackreacher.bkmemo.R;

/**
 * Created by JackReacher on 04/10/2016.
 */
public class Place {
    private int id;
    private String name;
    private String description;
    private String latitude;
    private String longitude;
    private int groupId;
    @DrawableRes
    private int color;

    public Place(){}

    public Place(int id, String name, String description, String latitude, String longitude, int groupId){
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.groupId = groupId;
    }

    public Place(String name, String description, String latitude, String longitude, int groupId){
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static int getRandomColor(Context context) {
        int[] colors;
        if (Math.random() >= 0.6) {
            colors = context.getResources().getIntArray(R.array.note_accent_colors);
        } else {
            colors = context.getResources().getIntArray(R.array.note_neutral_colors);
        }
        return colors[((int) (Math.random() * colors.length))];
    }
}
