package com.jackreacher.bkmemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jackreacher.bkmemo.adapters.CustomSpinnerGroupAdapter;
import com.jackreacher.bkmemo.models.Group;
import com.jackreacher.bkmemo.models.MyDatabase;
import com.jackreacher.bkmemo.models.Place;

import java.util.List;

/**
 * Created by JackReacher on 20/10/2016.
 */
public class PlaceEditActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private EditText etName;
    private EditText etDescription;
    private String mName;
    private String mDescription;
    private int mReceivedID;
    private MyDatabase mDatabase;
    private Place mReceivedPlace;
    private int mGroupId;

    private Spinner spGroup;
    private double mLatitude;
    private double mLongitude;
    private TextView tvAddress;
    private String mAddress;
    private ProgressBar mProgressbar;

    // Constant Intent String
    public static final String EXTRA_PLACE_ID = "Place_ID";

    // Values for orientation change
    private static final String KEY_NAME = "name_key";
    private static final String KEY_DESCRIPTION = "description_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        // Initialize Views
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        etName = (EditText) findViewById(R.id.etPlaceName);
        etDescription = (EditText) findViewById(R.id.etPlaceDescription);
        spGroup = (Spinner) findViewById(R.id.spGroup);
        tvAddress = (TextView) findViewById(R.id.tvPlaceAddress);
        mProgressbar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressbar.setVisibility(ProgressBar.GONE);

        // Setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_edit_place);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Setup Place Name EditText
        etName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mName = s.toString().trim();
                etName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup Place Description EditText
        etDescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDescription = s.toString().trim();
                etDescription.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Get place id from intent
        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_PLACE_ID));

        // Get place using place id
        mDatabase = new MyDatabase(this);
        mReceivedPlace = mDatabase.getPlace(mReceivedID);

        // Get values from place
        mName = mReceivedPlace.getName();
        mDescription = mReceivedPlace.getDescription();
        mAddress = mReceivedPlace.getAddress();
        mLatitude = Double.valueOf(mReceivedPlace.getLatitude());
        mLongitude = Double.valueOf(mReceivedPlace.getLongitude());

        etName.setText(mName);
        etDescription.setText(mDescription);
        etName.setSelection(etName.length());
        tvAddress.setText(mAddress);

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedName = savedInstanceState.getString(KEY_NAME);
            etName.setText(savedName);
            mName = savedName;

            String savedDescription = savedInstanceState.getString(KEY_DESCRIPTION);
            etDescription.setText(savedDescription);
            mDescription = savedDescription;
        }

        List<Group> groups = mDatabase.getAllGroups();
        final CustomSpinnerGroupAdapter adapter = new CustomSpinnerGroupAdapter(this, groups);
        spGroup.setAdapter(adapter);
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItemId(position) == mReceivedPlace.getGroupId())
                spGroup.setSelection(position);
        }
        spGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGroupId = (int) adapter.getItemId(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // To save state on device rotation
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_NAME, etName.getText());
        outState.putCharSequence(KEY_DESCRIPTION, etDescription.getText());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    // On clicking the update button
    public void updatePlace(){
        // Set new values in the place
        mReceivedPlace.setName(mName);
        mReceivedPlace.setDescription(mDescription);
        mReceivedPlace.setGroupId(mGroupId);

        // Update place
        mDatabase.updatePlace(mReceivedPlace);

        onBackPressed();
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Creating the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_place, menu);
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

            case R.id.save_place:
                etName.setText(mName);
                etDescription.setText(mDescription);

                if (etName.getText().toString().length() == 0)
                    etName.setError("Place name cannot be blank!");
                else
                    updatePlace();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void drawGoogleMap(double latitude, double longitude, String address) {
        /*String myGeoCode = "geo:" + latitude + "," + longitude + "?z=15";
        Intent intentViewMap = new Intent(Intent.ACTION_VIEW, Uri.parse(myGeoCode));
        startActivity(intentViewMap);*/
        Bundle bundle = new Bundle();
        Intent intentViewMap = new Intent(this, BasicMapActivity.class);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        bundle.putString("address", address);
        intentViewMap.putExtra("bundle", bundle);
        startActivity(intentViewMap);
    }

    @Override
    public void onClick(View v) {
        /*if (v.getId() == R.id.btGetLocation) {
            fetchAddressButtonHandler();
        } */
        if (v.getId() == R.id.btDrawMap) {
            drawGoogleMap(mLatitude, mLongitude, mAddress);
        }
    }
}
