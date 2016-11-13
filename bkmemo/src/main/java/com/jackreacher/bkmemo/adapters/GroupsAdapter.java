package com.jackreacher.bkmemo.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jackreacher.bkmemo.R;
import com.jackreacher.bkmemo.models.Group;
import com.jackreacher.bkmemo.models.MyDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JackReacher on 20/10/2016.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    private Group[] groups;
    private SparseBooleanArray selectedItems;

    public GroupsAdapter(Context context, int numGroups) {
        groups = generateGroups(context, numGroups);
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public GroupsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group, parent,
                false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group groupModel = groups[position];
        String name = groupModel.getName();
        int color = groupModel.getColor();

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
        return groups.length;
    }

    private Group[] generateGroups(Context context, int numGroups) {
        Group[] groups = new Group[numGroups];
        MyDatabase mDatabase = new MyDatabase(context);
        List<Group> mGroups = mDatabase.getAllGroups();
        for (int i = 0; i < mGroups.size(); i++) {
            groups[i] = mGroups.get(i);
            groups[i].setColor(Group.getRandomColor(context));
        }
        return groups;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.group_name);
        }
    }

    public void updateListAfterAdd(Context context, int numGroups, int position) {
        groups = generateGroups(context, numGroups);
        notifyItemInserted(position);
    }

    public void updateListAfterUpdate(Context context, int numGroups, int position) {
        groups = generateGroups(context, numGroups);
        notifyItemChanged(position);
    }
}
