package com.jackreacher.bkmemo.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jackreacher.bkmemo.BasicMapActivity;
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
    private MyDatabase mDatabase;
    private Context context;

    public PlacesAdapter(Context context, int numPlaces) {
        this.context = context;
        this.places = generatePlaces(context, numPlaces);
        this.mDatabase = new MyDatabase(context);
    }

    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_place, parent,
                false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Place placeModel = places[position];
        String name = placeModel.getName();
        String description = placeModel.getDescription();
        String address = placeModel.getAddress();
        String groupName = mDatabase.getGroup(placeModel.getGroupId()).getName();
        int color = placeModel.getColor();

        // Set name
        holder.nameTextView.setText(name);
        holder.descriptionTextView.setText(description);
        holder.groupNameTextView.setText(groupName);
        holder.addressTextView.setText(address);

        // Set visibilities
        holder.nameTextView.setVisibility(TextUtils.isEmpty(name) ? View.GONE : View.VISIBLE);
        holder.descriptionTextView.setVisibility(TextUtils.isEmpty(description) ? View.GONE : View.VISIBLE);
        holder.groupNameTextView.setVisibility(TextUtils.isEmpty(groupName) ? View.GONE : View.VISIBLE);
        holder.addressTextView.setVisibility(TextUtils.isEmpty(address) ? View.GONE : View.VISIBLE);

        // Set padding
        int paddingTop = (holder.nameTextView.getVisibility() != View.VISIBLE) ? 0
                : holder.itemView.getContext().getResources()
                .getDimensionPixelSize(R.dimen.note_content_spacing);
        holder.descriptionTextView.setPadding(holder.descriptionTextView.getPaddingLeft(), paddingTop,
                holder.descriptionTextView.getPaddingRight(), holder.descriptionTextView.getPaddingBottom());

        // Set background color
        ((CardView) holder.itemView).setCardBackgroundColor(color);

        /*holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawGoogleMap(placeModel.getLatitude(), placeModel.getLongitude(), placeModel.getAddress());
            }
        });*/
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
            places[i].setColor(Place.getRandomColor(context));
        }
        return places;
    }

    /***** Creating OnItemClickListener *****/

    // Define listener member variable
    private static OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView groupNameTextView;
        public TextView addressTextView;
        public ImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.place_name);
            descriptionTextView = (TextView) itemView.findViewById(R.id.place_description);
            groupNameTextView = (TextView) itemView.findViewById(R.id.place_group_name);
            addressTextView = (TextView) itemView.findViewById(R.id.place_address);
            imageView = (ImageView) itemView.findViewById(R.id.place_image);

            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }

    public void updateList(Context context, int numPlaces) {
        places = generatePlaces(context, numPlaces);
        notifyDataSetChanged();
    }

    /*public void drawGoogleMap(String latitude, String longitude, String address) {
        Bundle bundle = new Bundle();
        Intent intentViewMap = new Intent(context, BasicMapActivity.class);
        bundle.putDouble("latitude", Double.valueOf(latitude));
        bundle.putDouble("longitude", Double.valueOf(longitude));
        bundle.putString("address", address);
        intentViewMap.putExtra("bundle", bundle);
        context.startActivity(intentViewMap);
    }*/
}