package com.jackreacher.bkmemo.fragments;

import com.jackreacher.bkmemo.R;
import com.jackreacher.bkmemo.models.MyDatabase;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 * Favorite items fragment.
 */
public class EventFragment extends NotesListFragment {

	public static EventFragment newInstance() {
		return new EventFragment();
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.fragment_event;
	}

	@Override
	protected int getNumColumns() {
		return 1;
	}

	@Override
	protected int getNumItems() {
		MyDatabase mDatabase = new MyDatabase(getActivity());
		return mDatabase.getEventsCount();
	}
}
