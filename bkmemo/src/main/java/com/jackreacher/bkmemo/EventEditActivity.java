package com.jackreacher.bkmemo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jackreacher.bkmemo.adapters.MainPagerAdapter;
import com.jackreacher.bkmemo.models.Event;
import com.jackreacher.bkmemo.models.MyDatabase;
import com.jackreacher.bkmemo.models.Place;

import java.util.Calendar;
import java.util.List;

/**
 * Created by JackReacher on 20/10/2016.
 */
public class EventEditActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText etName;
    private EditText etDescription;
    private String mName;
    private String mDescription;
    private int mReceivedID;
    private MyDatabase mDatabase;
    private Event mReceivedEvent;
    private int mPlaceId;

    private Spinner spPlace;
    private Button btDate;
    private Button btTime;
    private TextView tvDate;
    private TextView tvTime;
    private Calendar cal;
    private String mDate;
    private String mTime;
    private TextInputLayout inputLayoutName;

    // Constant Intent String
    public static final String EXTRA_EVENT_ID = "Event_ID";

    // Values for orientation change
    private static final String KEY_NAME = "name_key";
    private static final String KEY_DESCRIPTION = "description_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Initialize Views
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        etName = (EditText) findViewById(R.id.etEventName);
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutEventName);
        etDescription = (EditText) findViewById(R.id.etEventDescription);
        spPlace = (Spinner) findViewById(R.id.spPlace);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        btDate = (Button) findViewById(R.id.btDate);
        btTime = (Button) findViewById(R.id.btTime);
        btDate.setOnClickListener(new MyButtonEvent());
        btTime.setOnClickListener(new MyButtonEvent());

        // Setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_edit_event);
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

        // Get place id from intent
        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_EVENT_ID));

        // Get place using place id
        mDatabase = new MyDatabase(this);
        mReceivedEvent = mDatabase.getEvent(mReceivedID);

        // Get values from place
        mName = mReceivedEvent.getName();
        mDescription = mReceivedEvent.getDescription();
        String timeStr = mReceivedEvent.getTime();
        String[] arrStr = timeStr.split(" ");
        mDate = arrStr[1];
        mTime = arrStr[0];

        etName.setText(mName);
        etDescription.setText(mDescription);
        etName.setSelection(etName.length());
        tvDate.setText(mDate);
        tvTime.setText(mTime);
        getDefaultInfor();

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedName = savedInstanceState.getString(KEY_NAME);
            etName.setText(savedName);
            mName = savedName;

            String savedDescription = savedInstanceState.getString(KEY_DESCRIPTION);
            etDescription.setText(savedDescription);
            mDescription = savedDescription;
        }

        final List<Place> places = mDatabase.getAllPlaces();
        String[] placeNames = new String[mDatabase.getPlacesCount()];
        for(int i = 0; i < placeNames.length; i++){
            placeNames[i] = places.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, placeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlace.setAdapter(adapter);
        for (int position = 0; position < adapter.getCount(); position++) {
            if(places.get(position).getId() == mReceivedEvent.getPlaceId())
                spPlace.setSelection(position);
        }
        spPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPlaceId = places.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getDefaultInfor() {
        cal = Calendar.getInstance();
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
    public void updateEvent(){
        // Set new values in the place
        mReceivedEvent.setName(mName);
        mReceivedEvent.setDescription(mDescription);
        mReceivedEvent.setPlaceId(mPlaceId);
        mReceivedEvent.setTime(tvTime.getText() + " " + tvDate.getText());

        // Update place
        mDatabase.updateEvent(mReceivedEvent);

        //onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("numTab", MainPagerAdapter.EVENT_POS);
        bundle.putInt("updateEventResult", 1);
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
        bundle.putInt("numTab", MainPagerAdapter.EVENT_POS);
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
                else if(mDatabase.getEventsCountByName(mReceivedEvent, mName) == 0)
                    updateEvent();
                else {
                    inputLayoutName.setError(getString(R.string.item_existed));
                    etName.setSelection(etName.length());
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyButtonEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btDate:
                    showDatePickerDialog();
                    break;
                case R.id.btTime:
                    showTimePickerDialog();
                    break;
            }
        }
    }

    /**
     * Hàm hiển thị DatePicker dialog
     */
    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                //Mỗi lần thay đổi ngày tháng năm thì cập nhật lại TextView Date
                tvDate.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);
                cal.set(year, monthOfYear, dayOfMonth);
            }
        };

        //Các lệnh dưới này xử lý ngày giờ trong DatePickerDialog
        //sẽ giống với trên TextView khi mở nó lên
        String s = tvDate.getText() + "";
        String strArrtmp[] = s.split("/");
        int ngay = Integer.parseInt(strArrtmp[0]);
        int thang = Integer.parseInt(strArrtmp[1]) - 1;
        int nam = Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic = new DatePickerDialog(
                this,
                callback, nam, thang, ngay);
        pic.setTitle(R.string.select_date_event);
        pic.show();
    }

    /**
     * Hàm hiển thị TimePickerDialog
     */
    public void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view,
                                  int hourOfDay, int minute) {
                String s = hourOfDay + ":" + minute;
                tvTime.setText(s);
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
            }
        };

        String s = tvTime.getText() + "";
        String strArr[] = s.split(":");
        int gio = Integer.parseInt(strArr[0]);
        int phut = Integer.parseInt(strArr[1]);
        TimePickerDialog time = new TimePickerDialog(this, callback, gio, phut, true);
        time.setTitle(R.string.select_time_event);
        time.show();
    }
}
