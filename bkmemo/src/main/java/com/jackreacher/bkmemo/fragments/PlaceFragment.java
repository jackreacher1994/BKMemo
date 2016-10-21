package com.jackreacher.bkmemo.fragments;

import com.jackreacher.bkmemo.R;
import com.jackreacher.bkmemo.models.MyDatabase;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 * All items fragment.
 */
public class PlaceFragment extends NotesListFragment {

	public static PlaceFragment newInstance() {
		return new PlaceFragment();
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.fragment_place;
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}

	@Override
	protected int getNumItems() {
		MyDatabase mDatabase = new MyDatabase(getActivity());
		return mDatabase.getPlacesCount();
	}
}
