package com.jackreacher.bkmemo.models;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.jackreacher.bkmemo.R;

/**
 * Created by JackReacher on 04/10/2016.
 */
public class Group {
    private int id;
    private String name;
    @DrawableRes
    private int color;

    public Group(){}

    public Group(int id, String name){
        this.id = id;
        this.name = name;
    }

    public Group(String name){
        this.name = name;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static int getRandomColor(Context context) {
        /*int[] colors;
        if (Math.random() >= 0.6) {
            colors = context.getResources().getIntArray(R.array.note_accent_colors);
        } else {
            colors = context.getResources().getIntArray(R.array.note_neutral_colors);
        }
        return colors[((int) (Math.random() * colors.length))];*/
        return context.getResources().getIntArray(R.array.group_colors)[0];
    }
}
