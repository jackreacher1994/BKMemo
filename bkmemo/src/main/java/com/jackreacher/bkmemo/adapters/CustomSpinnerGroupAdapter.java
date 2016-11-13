package com.jackreacher.bkmemo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.jackreacher.bkmemo.MainActivity;
import com.jackreacher.bkmemo.R;
import com.jackreacher.bkmemo.models.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackreacher on 11/13/16.
 */

public class CustomSpinnerGroupAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private List<Group> asr;

    public CustomSpinnerGroupAdapter(Context context, List<Group> asr) {
        this.asr = asr;
        this.activity = context;
    }

    public int getCount() {
        return asr.size();
    }

    public Group getItem(int i) {
        return asr.get(i);
    }

    public long getItemId(int i) {
        return (long) asr.get(i).getId();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity);
        txt.setText(asr.get(position).getName());
        return txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(activity);
        txt.setText(asr.get(i).getName());
        return txt;
    }

}
