package com.jackreacher.bkmemo.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.jackreacher.bkmemo.models.Group;
import com.jackreacher.bkmemo.models.Place;

import java.util.List;

/**
 * Created by jackreacher on 11/13/16.
 */

public class CustomSpinnerPlaceAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private List<Place> asr;

    public CustomSpinnerPlaceAdapter(Context context, List<Place> asr) {
        this.asr = asr;
        this.activity = context;
    }

    public int getCount() {
        return asr.size();
    }

    public Place getItem(int i) {
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
