package com.jackreacher.bkmemo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.jackreacher.bkmemo.adapters.CustomSpinnerGroupAdapter;
import com.jackreacher.bkmemo.adapters.CustomSpinnerPlaceAdapter;
import com.jackreacher.bkmemo.models.Event;
import com.jackreacher.bkmemo.models.Group;
import com.jackreacher.bkmemo.models.MyDatabase;
import com.jackreacher.bkmemo.models.Place;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by JackReacher on 20/10/2016.
 */
public class EventAddActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText etName;
    private EditText etDescription;
    private String mName;
    private String mDescription;
    private Spinner spPlace;
    private MyDatabase mDatabase;
    private int mPlaceId;

    private Button btDate;
    private Button btTime;
    private TextView tvDate;
    private TextView tvTime;
    private Calendar cal;
    private Date dateFinish;
    private Date hourFinish;
    private Switch swAlarm;

    // Values for orientation change
    private static final String KEY_NAME = "name_key";
    private static final String KEY_DESCRIPTION = "description_key";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        // Initialize Views
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        etName = (EditText) findViewById(R.id.etEventName);
        etDescription = (EditText) findViewById(R.id.etEventDescription);
        spPlace = (Spinner) findViewById(R.id.spPlace);
        mDatabase = new MyDatabase(this);
        List<Place> places = mDatabase.getAllPlaces();
        final CustomSpinnerPlaceAdapter adapter = new CustomSpinnerPlaceAdapter(this, places);
        spPlace.setAdapter(adapter);
        spPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPlaceId = (int) adapter.getItemId(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btDate = (Button) findViewById(R.id.btDate);
        btTime = (Button) findViewById(R.id.btTime);
        btDate.setOnClickListener(new MyButtonEvent());
        btTime.setOnClickListener(new MyButtonEvent());
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        getDefaultInfor();
        swAlarm = (Switch) findViewById(R.id.swAlarm);
        swAlarm.setChecked(false);
        swAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                } else{

                }
            }
        });

        // Setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_add_event);
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

        updateValuesFromBundle(savedInstanceState);
    }

    /**
     * Hàm lấy các thông số mặc định khi lần đầu tiền chạy ứng dụng
     */
    public void getDefaultInfor() {
        //Lấy ngày hiện tại của hệ thống
        cal = Calendar.getInstance();
        SimpleDateFormat dft = null;
        dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String strDate = dft.format(cal.getTime());
        tvDate.setText(strDate);
        //Định dạng giờ phút am/pm
        dft = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String strTime = dft.format(cal.getTime());
        tvTime.setText(strTime);
        //Lấy giờ theo 24
        dft = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvTime.setTag(dft.format(cal.getTime()));

        //Gán cal.getTime() cho ngày và giờ
        dateFinish = cal.getTime();
        hourFinish = cal.getTime();
    }

    // To save state on device rotation
    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState) {
        savedInstanceState.putCharSequence(KEY_NAME, etName.getText());
        savedInstanceState.putCharSequence(KEY_DESCRIPTION, etDescription.getText());

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
        }
    }

    // On clicking the save button
    public void saveEvent(){
        // Creating Place
        int ID = mDatabase.addEvent(new Event(mName, mDescription,
                tvTime.getText() + " " + tvDate.getText(), mPlaceId));

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
                    etName.setError(getString(R.string.required_field));
                else
                    saveEvent();

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
                //Lưu vết lại biến ngày
                cal.set(year, monthOfYear, dayOfMonth);
                dateFinish = cal.getTime();
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
                hourFinish = cal.getTime();
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
