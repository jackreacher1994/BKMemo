package com.jackreacher.bkmemo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jackreacher.bkmemo.R;
import com.jackreacher.bkmemo.fragments.PlaceFragment;
import com.jackreacher.bkmemo.fragments.EventFragment;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 * Pager adapter for main activity.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

	public static final int NUM_ITEMS = 2;
	public static final int PLACE_POS = 0;
	public static final int EVENT_POS = 1;

	private Context context;

	public MainPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case PLACE_POS:
			return PlaceFragment.newInstance();
		case EVENT_POS:
			return EventFragment.newInstance();
		default:
			return null;
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case PLACE_POS:
			return context.getString(R.string.place);
		case EVENT_POS:
			return context.getString(R.string.event);
		default:
			return "";
		}
	}

	@Override
	public int getCount() {
		return NUM_ITEMS;
	}
}
