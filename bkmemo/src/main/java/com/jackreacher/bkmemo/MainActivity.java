package com.jackreacher.bkmemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.jackreacher.bkmemo.adapters.MainPagerAdapter;
import com.jackreacher.bkmemo.fragments.PlaceFragment;
import com.jackreacher.bkmemo.models.Group;
import com.jackreacher.bkmemo.models.MyDatabase;
import com.jackreacher.bkmemo.models.Place;
import com.jackreacher.bkmemo.adapters.PlacesAdapter;

import java.util.List;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private MaterialSheetFab materialSheetFab;
	private int statusBarColor;
	private MyDatabase mDatabase;
	private FloatingActionButton fab2;
	private ViewPager viewpager;
	private MainPagerAdapter pagerAdapter;
	private RelativeLayout layoutMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_MaterialSheetFab);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//  A preference is stored to mark the activity as started before
		//  Declare a new thread to do a preference check
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				//  Initialize SharedPreferences
				SharedPreferences getPrefs = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				//  Create a new boolean and preference and set it to true
				boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
				//  If the activity has never started before...
				if (isFirstStart) {
					//  Launch app intro
					Intent i = new Intent(MainActivity.this, IntroActivity.class);
					startActivity(i);
					//  Make a new preferences editor
					SharedPreferences.Editor e = getPrefs.edit();
					//  Edit preference to make it false because we don't want this to run again
					e.putBoolean("firstStart", false);
					//  Apply changes
					e.apply();
				}
			}
		});
		// Start the thread
		t.start();

		setupActionBar();
		setupDrawer();
		setupFab();
		setupTabs();

		layoutMain = (RelativeLayout) findViewById(R.id.layoutMain);

		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("bundle");
		if(bundle != null) {
			viewpager.setCurrentItem(bundle.getInt("numTab"));
			if(bundle.getInt("updateEventResult") == 1)
				Snackbar.make(layoutMain, R.string.event_updated, Snackbar.LENGTH_SHORT).show();
			if(bundle.getInt("addEventResult") == 1)
				Snackbar.make(layoutMain, R.string.event_added, Snackbar.LENGTH_SHORT).show();
			if(bundle.getInt("updatePlaceResult") == 1)
				Snackbar.make(layoutMain, R.string.place_updated, Snackbar.LENGTH_SHORT).show();
			if(bundle.getInt("addPlaceResult") == 1)
				Snackbar.make(layoutMain, R.string.place_added, Snackbar.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onBackPressed() {
		if (materialSheetFab.isSheetVisible()) {
			materialSheetFab.hideSheet();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * Sets up the action bar.
	 */
	private void setupActionBar() {
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Sets up the navigation drawer.
	 */
	private void setupDrawer() {
		mDatabase = new MyDatabase(this);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.opendrawer,
				R.string.closedrawer);
		drawerLayout.setDrawerListener(drawerToggle);

		NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
		navigationView.setNavigationItemSelectedListener(this);

		Menu menu = navigationView.getMenu();
		List<Group> groups = mDatabase.getAllGroups();
		MenuItem menuItem;
		TextView tv;
		tv = (TextView) menu.getItem(0).getActionView().findViewById(R.id.tvGroupItem);
		tv.setText(Integer.toString(mDatabase.getPlacesCount()));
		menu.add(R.id.nav_first_group, Menu.NONE, Menu.NONE, R.string.group)
				.setActionView(R.layout.action_view_title_group).setEnabled(false);
		for (int i=0; i<groups.size(); i++){
			menuItem = menu.add(R.id.nav_first_group, groups.get(i).getId(), Menu.NONE, groups.get(i).getName());
			menuItem.setIcon(R.drawable.nav_group_item);
			menuItem.setActionView(R.layout.action_view_group);
			tv = (TextView) menuItem.getActionView().findViewById(R.id.tvGroupItem);
			tv.setText(Integer.toString(mDatabase.getPlacesCountByGroup(groups.get(i))));
		}
		menu.setGroupCheckable(R.id.nav_first_group, true, true);

		navigationView.setCheckedItem(R.id.nav_all);
	}

	/**
	 * Sets up the tabs.
	 */
	private void setupTabs() {
		// Setup view pager
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		pagerAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
		viewpager.setAdapter(pagerAdapter);
		viewpager.setOffscreenPageLimit(MainPagerAdapter.NUM_ITEMS);
		updatePage(viewpager.getCurrentItem());

		// Setup tab layout
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewpager);
		viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {
			}

			@Override
			public void onPageSelected(int i) {
				updatePage(i);
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});
	}

	/**
	 * Sets up the Floating action button.
	 */
	private void setupFab() {

		Fab fab = (Fab) findViewById(R.id.fab);
		fab2 = (FloatingActionButton) findViewById(R.id.fab2);
		View sheetView = findViewById(R.id.fab_sheet);
		View overlay = findViewById(R.id.overlay);
		int sheetColor = getResources().getColor(R.color.background_card);
		int fabColor = getResources().getColor(R.color.theme_accent);

		// Create material sheet FAB
		materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

		// Set material sheet event listener
		materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
			@Override
			public void onShowSheet() {
				// Save current status bar color
				statusBarColor = getStatusBarColor();
				// Set darker status bar color to match the dim overlay
				setStatusBarColor(getResources().getColor(R.color.theme_primary_dark2));
			}

			@Override
			public void onHideSheet() {
				// Restore status bar color
				setStatusBarColor(statusBarColor);
			}
		});

		// Set material sheet item click listeners
		findViewById(R.id.fab_sheet_item_location_auto).setOnClickListener(this);
		findViewById(R.id.fab_sheet_item_location_hand).setOnClickListener(this);
		fab2.setOnClickListener(this);
	}

	/**
	 * Called when the selected page changes.
	 *
	 * @param selectedPage selected page
	 */
	private void updatePage(int selectedPage) {
		updateFab(selectedPage);
	}

	/**
	 * Updates the FAB based on the selected page
	 * 
	 * @param selectedPage selected page
	 */
	private void updateFab(int selectedPage) {
		switch (selectedPage) {
		case MainPagerAdapter.PLACE_POS:
			fab2.setVisibility(View.INVISIBLE);
			materialSheetFab.showFab();
			break;
		case MainPagerAdapter.EVENT_POS:
			fab2.setVisibility(View.VISIBLE);
			materialSheetFab.hideSheetThenFab();
			break;
		default:
			//materialSheetFab.hideSheetThenFab();
			break;
		}
	}

	/**
	 * Toggles opening/closing the drawer.
	 */
	private void toggleDrawer() {
		if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START);
		} else {
			drawerLayout.openDrawer(GravityCompat.START);
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.fab_sheet_item_location_auto) {
			if(mDatabase.getGroupsCount() == 0)
				Snackbar.make(layoutMain, R.string.no_group, Snackbar.LENGTH_SHORT).show();
			else {
				Intent intent = new Intent(v.getContext(), PlaceAddActivity.class);
				startActivity(intent);
				materialSheetFab.hideSheet();
			}
		} else if(v.getId()==R.id.fab2) {
			if(mDatabase.getPlacesCount() == 0)
				Snackbar.make(layoutMain, R.string.no_place, Snackbar.LENGTH_SHORT).show();
			else {
				Intent intent = new Intent(v.getContext(), EventAddActivity.class);
				startActivity(intent);
			}
		} else if(v.getId()==R.id.fab_sheet_item_location_hand) {
			if(mDatabase.getPlacesCount() == 0)
				Snackbar.make(layoutMain, R.string.no_group, Snackbar.LENGTH_SHORT).show();
			else {
				Intent intent = new Intent(v.getContext(), MapActivity.class);
				startActivity(intent);
				materialSheetFab.hideSheet();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggleDrawer();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private int getStatusBarColor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return getWindow().getStatusBarColor();
		}
		return 0;
	}

	private void setStatusBarColor(int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(color);
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here
		int id = item.getItemId();

		RecyclerViewEmptySupport recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.places_list);
		PlacesAdapter placesAdapter = (PlacesAdapter) recyclerView.getAdapter();

		if (id == R.id.nav_all) {
			placesAdapter.updateList(this, mDatabase.getPlacesCount());
		} else if (id == R.id.nav_setting) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		} else {
			placesAdapter.updateListByGroup(this, mDatabase.getPlacesCountByGroup(mDatabase.getGroup(id)), id);
		}
		toggleDrawer();
		return true;
	}

	public void clickTitle(View v){
		Intent intent = new Intent(v.getContext(), GroupActivity.class);
		startActivity(intent);
		toggleDrawer();
	}
}
