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
import com.jackreacher.bkmemo.models.Event;
import com.jackreacher.bkmemo.models.Group;
import com.jackreacher.bkmemo.models.MyDatabase;
import com.jackreacher.bkmemo.models.Place;

import java.util.List;

/**
 * Created by JackReacher on 20/10/2016.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private Event[] events;
    private MyDatabase mDatabase;
    private Context context;

    public EventsAdapter(Context context, int numEvents) {
        this.events = generateEvents(context, numEvents);
        this.mDatabase = new MyDatabase(context);
        this.context = context;
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event, parent,
                false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event eventModel = events[position];
        String name = eventModel.getName();
        String description = eventModel.getDescription();
        String address = mDatabase.getPlace(eventModel.getPlaceId()).getAddress();
        String time = eventModel.getTime();
        int color = eventModel.getColor();

        // Set name
        holder.nameTextView.setText(name);
        holder.descriptionTextView.setText(description);
        holder.addressTextView.setText(address);

        // Set visibilities
        holder.nameTextView.setVisibility(TextUtils.isEmpty(name) ? View.GONE : View.VISIBLE);
        holder.descriptionTextView.setVisibility(TextUtils.isEmpty(description) ? View.GONE : View.VISIBLE);
        holder.addressTextView.setVisibility(TextUtils.isEmpty(address) ? View.GONE : View.VISIBLE);

        // Set padding
        int paddingTop = (holder.nameTextView.getVisibility() != View.VISIBLE) ? 0
                : holder.itemView.getContext().getResources()
                .getDimensionPixelSize(R.dimen.note_content_spacing);
        holder.descriptionTextView.setPadding(holder.descriptionTextView.getPaddingLeft(), paddingTop,
                holder.descriptionTextView.getPaddingRight(), holder.descriptionTextView.getPaddingBottom());

        // Set background color
        ((CardView) holder.itemView).setCardBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return events.length;
    }

    private Event[] generateEvents(Context context, int numEvents) {
        Event[] events = new Event[numEvents];
        MyDatabase mDatabase = new MyDatabase(context);
        List<Event> mEvents = mDatabase.getAllEvents();
        for (int i = 0; i < mEvents.size(); i++) {
            events[i] = mEvents.get(i);
            events[i].setColor(Event.getRandomColor(context));
        }
        return events;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView addressTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.event_name);
            descriptionTextView = (TextView) itemView.findViewById(R.id.event_description);
            addressTextView = (TextView) itemView.findViewById(R.id.event_address);
        }
    }

    public void updateListAfterAdd(Context context, int numEvents, int position) {
        events = generateEvents(context, numEvents);
        notifyItemInserted(position);
    }

    public void updateListAfterUpdate(Context context, int numEvents, int position) {
        events = generateEvents(context, numEvents);
        notifyItemChanged(position);
    }

    public void updateList(Context context, int numEvents) {
        events = generateEvents(context, numEvents);
        notifyDataSetChanged();
    }
}
