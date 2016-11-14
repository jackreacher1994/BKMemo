package com.jackreacher.bkmemo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jackreacher.bkmemo.adapters.CustomSpinnerPlaceAdapter;
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
        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_EVENT_ID));

        // Get place using place id
        mDatabase = new MyDatabase(this);
        mReceivedEvent = mDatabase.getEvent(mReceivedID);

        // Get values from place
        mName = mReceivedEvent.getName();
        mDescription = mReceivedEvent.getDescription();
        String timeStr = mReceivedEvent.getTime();
        String[] arrStr = timeStr.split(" ", 2);
        mDate = arrStr[0];
        mTime = arrStr[1];

        etName.setText(mName);
        etDescription.setText(mDescription);
        etName.setSelection(etName.length());
        tvDate.setText(mDate);
        tvTime.setText(mTime);

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedName = savedInstanceState.getString(KEY_NAME);
            etName.setText(savedName);
            mName = savedName;

            String savedDescription = savedInstanceState.getString(KEY_DESCRIPTION);
            etDescription.setText(savedDescription);
            mDescription = savedDescription;
        }

        List<Place> places = mDatabase.getAllPlaces();
        final CustomSpinnerPlaceAdapter adapter = new CustomSpinnerPlaceAdapter(this, places);
        spPlace.setAdapter(adapter);
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItemId(position) == mReceivedEvent.getPlaceId()) {
                spPlace.setSelection(position);
            }
        }
        spPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPlaceId = (int) adapter.getItemId(position);
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
    public void updateEvent(){
        // Set new values in the place
        mReceivedEvent.setName(mName);
        mReceivedEvent.setDescription(mDescription);
        mReceivedEvent.setPlaceId(mPlaceId);

        // Update place
        mDatabase.updateEvent(mReceivedEvent);

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
                    updateEvent();

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
                //Xử lý lưu giờ và AM, PM
                String s = hourOfDay + ":" + minute;
                int hourTam = hourOfDay;
                if (hourTam > 12)
                    hourTam = hourTam - 12;
                tvTime.setText(hourTam + ":" + minute + (hourOfDay > 12 ? " PM" : " AM"));
                //Lưu giờ thực vào tag
                tvTime.setTag(s);
                //Lưu vết lại giờ vào hourFinish
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
            }
        };

        //Các lệnh dưới này xử lý ngày giờ trong TimePickerDialog
        //sẽ giống với trên TextView khi mở nó lên
        String s = tvTime.getTag() + "";
        String strArr[] = s.split(":");
        int gio = Integer.parseInt(strArr[0]);
        int phut = Integer.parseInt(strArr[1]);
        TimePickerDialog time = new TimePickerDialog(
                this,
                callback, gio, phut, true);
        time.setTitle(R.string.select_time_event);
        time.show();
    }
}
