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

    public EventsAdapter(Context context, int numEvents) {
        events = generateEvents(context, numEvents);
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
        int color = eventModel.getColor();

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
        return events.length;
    }

    private Event[] generateEvents(Context context, int numEvents) {
        Place[] Events = new Place[numEvents];
        MyDatabase mDatabase = new MyDatabase(context);
        List<Event> mEvents = mDatabase.getAllEvents();
        for (int i = 0; i < mEvents.size(); i++) {
            events[i] = mEvents.get(i);
            events[i].setColor(Group.getRandomColor(context));
        }
        return events;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.group_name);
        }
    }
}
