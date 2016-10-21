package com.jackreacher.bkmemo.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jackreacher.bkmemo.R;
import com.jackreacher.bkmemo.models.Group;
import com.jackreacher.bkmemo.models.MyDatabase;
import com.jackreacher.bkmemo.models.Place;

import java.util.List;

/**
 * Created by JackReacher on 20/10/2016.
 */
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {
    private Place[] places;

    public PlacesAdapter(Context context, int numPlaces) {
        places = generatePlaces(context, numPlaces);
    }

    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_place, parent,
                false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place placeModel = places[position];
        String name = placeModel.getName();
        int color = placeModel.getColor();

        // Set name
        holder.nameTextView.setText(name);

        // Set visibilities
        holder.nameTextView.setVisibility(TextUtils.isEmpty(name) ? View.GONE : View.VISIBLE);

        // Set padding
        int paddingTop = (holder.nameTextView.getVisibility() != View.VISIBLE) ? 0
                : holder.itemView.getContext().getResources()
                .getDimensionPixelSize(R.dimen.note_content_spacing);

        // Set background color
        ((CardView) holder.itemView).setCardBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return places.length;
    }

    private Place[] generatePlaces(Context context, int numPlaces) {
        Place[] places = new Place[numPlaces];
        MyDatabase mDatabase = new MyDatabase(context);
        List<Place> mPlaces = mDatabase.getAllPlaces();
        for (int i = 0; i < mPlaces.size(); i++) {
            places[i] = mPlaces.get(i);
            places[i].setColor(Group.getRandomColor(context));
        }
        return places;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.group_name);
        }
    }
}