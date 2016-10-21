package com.jackreacher.bkmemo;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jackreacher.bkmemo.adapters.GroupsAdapter;
import com.jackreacher.bkmemo.adapters.NotesAdapter;
import com.jackreacher.bkmemo.models.MyDatabase;

/**
 * Created by JackReacher on 20/10/2016.
 */
public class GroupActivity extends AppCompatActivity {
    protected int getNumColumns() {
        return 2;
    }

    protected int getNumItems() {
        MyDatabase mDatabase = new MyDatabase(this);
        return mDatabase.getGroupsCount();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_group);
        setContentView(R.layout.activity_groups);
        setupActionBar();
        setupList();
    }

    /**
     * Sets up the action bar.
     */
    private void setupActionBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Sets up group list.
     */
    private void setupList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.groups_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getNumColumns(),
                StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new GroupsAdapter(this, getNumItems()));
    }

    // To save state on device rotation
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Creating the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    // On clicking menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // On clicking the back arrow
            // Discard any changes
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.new_group:
                //showAddGroupDialogBox();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
