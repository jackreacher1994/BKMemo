package com.jackreacher.bkmemo;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.jackreacher.bkmemo.adapters.MainPagerAdapter;
import com.jackreacher.bkmemo.models.Group;
import com.jackreacher.bkmemo.models.MyDatabase;
import com.jackreacher.bkmemo.models.Place;

import java.util.List;

/**
 * Created by JackReacher on 20/10/2016.
 */
public class PlaceAddActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Toolbar mToolbar;
    private EditText etName;
    private EditText etDescription;
    private String mName;
    private String mDescription;
    private Spinner spGroup;
    private MyDatabase mDatabase;
    private int mGroupId;
    private double mLatitude;
    private double mLongitude;
    private TextView tvAddress;
    private ProgressBar mProgressbar;
    private TextInputLayout inputLayoutName;

    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;
    private static final String TAG = "place-add-activity";
    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    private boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    private String mAddressOutput;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

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
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutPlaceName);
        etDescription = (EditText) findViewById(R.id.etPlaceDescription);
        spGroup = (Spinner) findViewById(R.id.spGroup);
        tvAddress = (TextView) findViewById(R.id.tvPlaceAddress);
        mProgressbar = (ProgressBar) findViewById(R.id.progress_bar);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mDatabase = new MyDatabase(this);
        final List<Group> groups = mDatabase.getAllGroups();
        String[] groupNames = new String[mDatabase.getGroupsCount()];
        for(int i = 0; i < groupNames.length; i++){
            groupNames[i] = groups.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGroup.setAdapter(adapter);
        spGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mGroupId = groups.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_add_place);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Setup Place Name EditText
        etName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mName = s.toString().trim();
                inputLayoutName.setError(null);
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
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mAddressRequested = false;
        mAddressOutput = "";
        mLatitude = 0;
        mLongitude = 0;
        updateValuesFromBundle(savedInstanceState);
        updateUIWidgets();
        buildGoogleApiClient();

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle == null){
            fetchAddressButtonHandler();
        } else {
            mLatitude = bundle.getDouble("latitude");
            mLongitude = bundle.getDouble("longitude");
            mAddressOutput = bundle.getString("address");
            displayAddressOutput();
        }
    }

    // To save state on device rotation
    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState) {
        savedInstanceState.putCharSequence(KEY_NAME, etName.getText());
        savedInstanceState.putCharSequence(KEY_DESCRIPTION, etDescription.getText());

        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String savedName = savedInstanceState.getString(KEY_NAME);
            etName.setText(savedName);
            mName = savedName;

            String savedDescription = savedInstanceState.getString(KEY_DESCRIPTION);
            etDescription.setText(savedDescription);
            mDescription = savedDescription;

            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    // On clicking the save button
    public void savePlace(){
        // Creating Place
        int ID = mDatabase.addPlace(new Place(mName, mDescription, String.valueOf(mLatitude), String.valueOf(mLongitude), mAddressOutput, mGroupId));

        //onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("numTab", MainPagerAdapter.PLACE_POS);
        bundle.putInt("addPlaceResult", 1);
        i.putExtra("bundle", bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("numTab", MainPagerAdapter.PLACE_POS);
        i.putExtra("bundle", bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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
                    inputLayoutName.setError(getString(R.string.required_field));
                else if(mDatabase.getPlacesCountByName(mName) == 1) {
                    inputLayoutName.setError(getString(R.string.item_existed));
                    etName.setSelection(etName.length());
                }
                else
                    savePlace();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Runs when user clicks the Fetch Address button. Starts the service to fetch the address if
     * GoogleApiClient is connected.
     */
    public void fetchAddressButtonHandler() {
        // We only start the service to fetch the address if GoogleApiClient is connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        // If GoogleApiClient isn't connected, we process the user's request by setting
        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
        // fetch the address. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
            // It is possible that the user presses the button to get the address before the
            // GoogleApiClient object successfully connects. In such a case, mAddressRequested
            // is set to true, but no attempt is made to fetch the address (see
            // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
            // user has requested an address, since we now have a connection to GoogleApiClient.
            if (mAddressRequested) {
                startIntentService();
            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        tvAddress.setText(mAddressOutput);
    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        if (mAddressRequested)
            mProgressbar.setVisibility(ProgressBar.VISIBLE);
        else
            mProgressbar.setVisibility(ProgressBar.GONE);
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            mLatitude = resultData.getDouble(Constants.LOCATION_LATITUDE);
            mLongitude = resultData.getDouble(Constants.LOCATION_LONGITUDE);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }
}

