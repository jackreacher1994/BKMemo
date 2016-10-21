package com.jackreacher.bkmemo.models;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.jackreacher.bkmemo.R;

/**
 * Created by JackReacher on 04/10/2016.
 */
public class Event {
    private int id;
    private String name;
    private String description;
    private String time;
    private int placeId;
    @DrawableRes
    private int color;

    public Event(){}

    public Event(int id, String name, String description, String time, int placeId){
        this.id = id;
        this.name = name;
        this. description = description;
        this.time = time;
        this.placeId = placeId;
    }

    public Event(String name, String description, String time, int placeId){
        this.name = name;
        this. description = description;
        this.time = time;
        this.placeId = placeId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
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
