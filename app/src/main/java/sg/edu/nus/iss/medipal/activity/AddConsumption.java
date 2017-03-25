package sg.edu.nus.iss.medipal.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import sg.edu.nus.iss.medipal.R;
import sg.edu.nus.iss.medipal.dao.ConsumptionDAO;
import sg.edu.nus.iss.medipal.manager.ConsumptionManager;
import sg.edu.nus.iss.medipal.manager.PreferenceManager;
import sg.edu.nus.iss.medipal.pojo.HealthManager;
import sg.edu.nus.iss.medipal.pojo.Medicine;
import sg.edu.nus.iss.medipal.utils.MediPalUtility;

import static java.lang.Integer.valueOf;

public class AddConsumption extends AppCompatActivity implements View.OnClickListener {

    private Spinner spnMedicine;
    private EditText etDate, etTime;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private Spinner spinner;
    private TextView view_Medicine;
    private EditText view_Quantity;
    HealthManager healthmanager = new HealthManager();
    Medicine medicine;
    private boolean isEdit = true;
    private PreferenceManager consumptionPreference;
    private int quantity;
    private int medicine_id;



    Calendar currentCal = Calendar.getInstance();
    Calendar selectedDate = Calendar.getInstance();

    //get Medicine and get Dosage
    List<String> medinceList = null;
    List<Integer> dosageList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String medicine_name;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_consumption);

        //initial a medicine

        consumptionPreference = new PreferenceManager(this);
        etDate = (EditText)findViewById(R.id.et_select_date);
        etTime = (EditText)findViewById(R.id.et_select_time);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        view_Medicine = (TextView)findViewById(R.id.view_medicine);
        view_Quantity = (EditText) findViewById(R.id.Dosage);
        etDate.setOnClickListener(this);
        etTime.setOnClickListener(this);

        ;

        Bundle bundleMedicine = getIntent().getExtras();
        medicine_id = bundleMedicine.getInt("medicine_id");

        Intent transferMedicineId = new Intent(getApplicationContext(),ConsumptionDetail.class);
        transferMedicineId.putExtra("medicine_id",medicine_id);

        Intent transferMedicineIdtoUnconsumption = new Intent(getApplicationContext(),UncomsumedActivity.class);
        transferMedicineIdtoUnconsumption.putExtra("medicine_id",medicine_id);
        medicine_name = healthmanager.getMedicine(medicine_id,getApplicationContext()).getMedicine_name();
        quantity = healthmanager.getMedicine(medicine_id,getApplicationContext()).getConsumequantity();




        view_Medicine.setText(medicine_name);
        view_Quantity.setText(Integer.toString(quantity));

        //Log.v("MT","------------------------Cquantity="+medicine.getConsumequantity());

        Log.v("mateng","***************************name"+medicine_name);
        Log.v("mateng","***************************quantity"+quantity);
        Log.v("mateng","***************************bundle"+bundleMedicine);

        //view_Quantity.setText(Integer.toString(medicine.getConsumequantity()));
    }

    public void onClick(View v) {
        final Calendar calender;
        int day,month,year,hour,minute;
        if (v == etDate) {
            etDate.setError(null);
            calender = Calendar.getInstance();
            day = calender.get(Calendar.DAY_OF_MONTH);
            month = calender.get(Calendar.MONTH);
            year = calender.get(Calendar.YEAR);

            DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    etDate.setText(dayOfMonth+"-"+(month+1)+"-"+year);
                }
            }, day, month, year);
            datePicker.updateDate(year, month, day);
            datePicker.show();
        }
        else if(v == etTime)
        {
            etTime.setError(null);
            calender = Calendar.getInstance();
            hour = calender.get(Calendar.HOUR_OF_DAY);
            minute = calender.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    int hour = hourOfDay % 12;
                    String period;

                    if(hour == 0)
                        hour = 12;
                    if(hourOfDay < 12)
                        period = "AM";
                    else
                        period = "PM";

                    etTime.setText(String.format("%02d:%02d %s",hour, minute, period));
                }
            }, hour,minute,false);
            timePicker.updateTime(hour,minute);
            timePicker.show();
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_action_items, menu);
        MenuItem menuItem;
        menuItem = menu.findItem(R.id.action_close);
        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_close) {
            finish();

        }
        if (id == R.id.action_done) {
            saveConsumption();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveConsumption() {


        //int quantity = Integer.valueOf(view_Quantity.getText().toString().trim());
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        int threshold = healthmanager.getMedicine(medicine_id,getApplicationContext()).getThreshold();


        Log.v("MATENG ADDCONSUMPTION","_+_+_++_+_+_+_+_+_+_+_+_+_+_+_+"+medicine_id);

        if (input_validate(quantity,date,time)) {

            String date_time = date + " " + time;
            /*
                select * from consumptionTable where medicineid = getmedicineid,

            */
            Intent transferDate_time = new Intent(getApplicationContext(),ConsumptionDetail.class);
            transferDate_time.putExtra("ConsumedOn",date_time);

            ConsumptionDAO consumptionDAO = new ConsumptionDAO(this);

            Calendar c = Calendar.getInstance();
            int day, month, year;
            day = c.get(Calendar.DAY_OF_MONTH);
            month = c.get(Calendar.MONTH);
            year = c.get(Calendar.YEAR);
            String consumedDate = day + "-" + (month + 1) + "-" + year;


            ConsumptionManager consumptionManager = new ConsumptionManager(quantity,date_time,this);


            Log.d("consumed Date", consumedDate);
            int consumptionCount = consumptionDAO.getConsumptionCount(Integer.toString(medicine_id), consumedDate);
            //int MinConsumptionId = consumptionDAO.getMinConsumptionId(Integer.toString(medicine_id),consumedDate);
            int ReminderId = healthmanager.getMedicine(medicine_id,getApplicationContext()).getReminderId();
            int FrequentNum = healthmanager.getReminder(ReminderId,getApplicationContext()).getFrequency();
            if (consumptionCount == 0) {
                for (int i =0; i < FrequentNum; i++) {
                    consumptionManager.addConsumption(0,medicine_id,0,date_time,this);
                }
            }
            int MinConsumptionId = consumptionDAO.getMinConsumptionId(Integer.toString(medicine_id),consumedDate);
            consumptionManager.updateConsumption(MinConsumptionId,medicine_id,quantity,date_time,this);

            Toast toast = Toast.makeText(AddConsumption.this,"Add Consumption Successfully!",Toast.LENGTH_SHORT);
            toast.show();


            if (healthmanager.getMedicine(medicine_id,getApplicationContext()).getQuantity() - quantity <= threshold) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("Notice");
                dlg.setMessage("Dear, you need replenish the medicine");
                dlg.setPositiveButton("OK",null);
                dlg.show();
            }
            Intent i = new Intent(getApplicationContext(),ConsumptionActivity.class);
            i.putExtra("medicine_id",medicine_id);
            i.putExtra("ConsumedOn",date_time);
            startActivity(i);
        }


    }

    public boolean input_validate(int quantity,String date,String time) {
        boolean validate_status = true;
        if (view_Quantity.getText().length() == 0)
        {
            view_Quantity.setError("please input a quantity");
            validate_status = false;
        }
        if (date.isEmpty()) {
           etDate.setError("please select a date");
            validate_status = false;
        } else if (!MediPalUtility.isValidDate(date)) {
            etDate.setError("please select a right date");
            validate_status = false;
        }
        if (time.isEmpty()) {
            etTime.setError("please select a time");
            validate_status = false;
        } else if (MediPalUtility.isValidTime(date,time)) {
            etTime.setError("please select a right time");
            validate_status = false;
        }
        return validate_status;
    }


}
