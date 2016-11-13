package com.jackreacher.bkmemo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackreacher.bkmemo.EventEditActivity;
import com.jackreacher.bkmemo.PlaceEditActivity;
import com.jackreacher.bkmemo.R;
import com.jackreacher.bkmemo.RecyclerItemClickListener;
import com.jackreacher.bkmemo.RecyclerViewEmptySupport;
import com.jackreacher.bkmemo.adapters.EventsAdapter;
import com.jackreacher.bkmemo.adapters.NotesAdapter;
import com.jackreacher.bkmemo.adapters.PlacesAdapter;
import com.jackreacher.bkmemo.models.Event;
import com.jackreacher.bkmemo.models.MyDatabase;
import com.jackreacher.bkmemo.models.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 * Favorite items fragment.
 */
public class EventFragment extends Fragment {
	private List<Integer> IDList;
	private EventsAdapter mAdapter;
	private RecyclerViewEmptySupport recyclerView;
	private MyDatabase mDatabase;

	public static EventFragment newInstance() {
		return new EventFragment();
	}

	@LayoutRes
	protected int getLayoutResId() {
		return R.layout.fragment_event;
	}

	protected int getNumColumns() {
		return 1;
	}

	protected int getNumItems() {
		return mDatabase.getEventsCount();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(getLayoutResId(), container, false);

		mDatabase = new MyDatabase(getActivity());
		List<Event> mArray = mDatabase.getAllEvents();
		IDList = new ArrayList<>();
		for (int i = 0; i < mArray.size(); i++) {
			IDList.add(mArray.get(i).getId());
		}

		// Setup list
		RecyclerViewEmptySupport recyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.notes_list);
		recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getNumColumns(),
				StaggeredGridLayoutManager.VERTICAL));
		recyclerView.setEmptyView(view.findViewById(R.id.empty_list));
		mAdapter = new EventsAdapter(getActivity(), getNumItems());
		recyclerView.setAdapter(mAdapter);
		recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
				new RecyclerItemClickListener.OnItemClickListener() {
					@Override
					public void onItemClick(View view, int position) {
						// TODO Handle item click
						editEvent(IDList.get(position));
					}
				}));

		return view;
	}

	// On clicking a place item
	private void editEvent(int mClickID) {
		String mStringClickID = Integer.toString(mClickID);

		// Create intent to edit the place
		// Put place id as extra
		Intent i = new Intent(getActivity(), EventEditActivity.class);
		i.putExtra(EventEditActivity.EXTRA_EVENT_ID, mStringClickID);
		startActivityForResult(i, 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResume(){
		super.onResume();
		mAdapter.updateList(getActivity(), getNumItems());
	}
}
