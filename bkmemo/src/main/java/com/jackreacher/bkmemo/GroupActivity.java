package com.jackreacher.bkmemo;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.support.v7.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jackreacher.bkmemo.adapters.GroupsAdapter;
import com.jackreacher.bkmemo.adapters.NotesAdapter;
import com.jackreacher.bkmemo.models.Group;
import com.jackreacher.bkmemo.models.MyDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JackReacher on 20/10/2016.
 */
public class GroupActivity extends AppCompatActivity implements ActionMode.Callback {
    private MyDatabase mDatabase;
    private List<Integer> IDList;
    private GroupsAdapter mAdapter;
    private RecyclerViewEmptySupport recyclerView;
    private ActionMode actionMode;

    protected int getNumColumns() {
        return 2;
    }

    protected int getNumItems() {
        return mDatabase.getGroupsCount();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_group);
        setContentView(R.layout.activity_groups);
        mDatabase = new MyDatabase(this);
        List<Group> mArray = mDatabase.getAllGroups();
        IDList = new ArrayList<>();
        for (int i = 0; i < mArray.size(); i++) {
            IDList.add(mArray.get(i).getId());
        }
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
        recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.groups_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getNumColumns(),
                StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setEmptyView(findViewById(R.id.empty_list));
        mAdapter = new GroupsAdapter(this, getNumItems());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        editGroup(IDList.get(position), position);
                    }
                }));
    }

    // To save state on device rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
                showAddGroupDialogBox();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddGroupDialogBox() {
        View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        final EditText etGroupName = (EditText) view.findViewById(R.id.etGroupName);
        final AlertDialog customDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(getResources().getString(R.string.action_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int groupId = mDatabase.addGroup(new Group(etGroupName.getText().toString()));
                                if (groupId > 0) {
                                    IDList.add(groupId);
                                    mAdapter.updateListAfterAdd(getApplicationContext(), getNumItems(), recyclerView.getChildCount()+1);
                                } else
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.item_existed), Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.action_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                .create();
        customDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        customDialog.show();
        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        etGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(etGroupName.getText().length() > 0);
            }
        });
    }

    private void editGroup(int mClickID, int position) {
        final int mPosition = position;
        View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        final EditText etGroupName = (EditText) view.findViewById(R.id.etGroupName);
        final Group group = mDatabase.getGroup(mClickID);
        etGroupName.setText(group.getName());
        etGroupName.setSelection(etGroupName.length());
        final AlertDialog customDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(getResources().getString(R.string.action_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                group.setName(etGroupName.getText().toString());
                                int groupId = mDatabase.updateGroup(group);
                                if (groupId > 0)
                                    mAdapter.updateListAfterUpdate(getApplicationContext(), getNumItems(), mPosition);
                                else
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.item_existed), Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.action_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                .create();
        customDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        customDialog.show();
        etGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(etGroupName.getText().length() > 0);
            }
        });
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                actionMode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.actionMode = null;
    }
}
