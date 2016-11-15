package com.jackreacher.bkmemo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jackreacher.bkmemo.PlaceEditActivity;
import com.jackreacher.bkmemo.R;
import com.jackreacher.bkmemo.RecyclerViewEmptySupport;
import com.jackreacher.bkmemo.adapters.PlacesAdapter;
import com.jackreacher.bkmemo.models.MyDatabase;
import com.jackreacher.bkmemo.models.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 * All items fragment.
 */
public class PlaceFragment extends Fragment {
	private List<Integer> IDList;
	private PlacesAdapter mAdapter;
	private RecyclerViewEmptySupport recyclerView;
	private MyDatabase mDatabase;

	public static PlaceFragment newInstance() {
		return new PlaceFragment();
	}

	@LayoutRes
	protected int getLayoutResId() {
		return R.layout.fragment_place;
	}

	protected int getNumColumns() {
		return 2;
	}

	protected int getNumItems() {
		return mDatabase.getPlacesCount();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(getLayoutResId(), container, false);

		mDatabase = new MyDatabase(getActivity());
		List<Place> mArray = mDatabase.getAllPlaces();
		IDList = new ArrayList<>();
		for (int i = 0; i < mArray.size(); i++) {
			IDList.add(mArray.get(i).getId());
		}

		// Setup list
		recyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.places_list);
		recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getNumColumns(),
				StaggeredGridLayoutManager.VERTICAL));
		recyclerView.setEmptyView(view.findViewById(R.id.empty_list));
		mAdapter = new PlacesAdapter(getActivity(), getNumItems());
		mAdapter.setOnItemClickListener(new PlacesAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				editPlace(IDList.get(position));
			}
		});
		recyclerView.setAdapter(mAdapter);

		return view;
	}

	// On clicking a place item
	private void editPlace(int mClickID) {
		String mStringClickID = Integer.toString(mClickID);

		// Create intent to edit the place
		// Put place id as extra
		Intent i = new Intent(getActivity(), PlaceEditActivity.class);
		i.putExtra(PlaceEditActivity.EXTRA_PLACE_ID, mStringClickID);
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
	}
}
