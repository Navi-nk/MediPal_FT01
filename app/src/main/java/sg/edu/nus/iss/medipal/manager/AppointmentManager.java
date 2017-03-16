package sg.edu.nus.iss.medipal.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import sg.edu.nus.iss.medipal.service.RemindAlarmReceiver;
import sg.edu.nus.iss.medipal.dao.AppointmentDAO;
import sg.edu.nus.iss.medipal.pojo.Appointment;

/**
 * Created by : Navi on 15-03-2017.
 * Description : This is the manager class to for handling all operations concerning appointments
 * Modified by :
 * Reason for modification :
 */

public class AppointmentManager {
    private Appointment appointment;
    private AppointmentDAO appointmentDAO;
    private String appointmentTitle;
    private String remainderInterval;
    private Context context;

    PreferenceManager appointmentPreference;

    public AppointmentManager(String title, String location, String datetime, String description,String remainderInterval,Context context ) {
        this.context = context;
        this.remainderInterval = remainderInterval;
        this.appointmentTitle = title;
        appointment = new Appointment(null,location,datetime,description);
        appointmentDAO = new AppointmentDAO(context);
        appointmentPreference = new PreferenceManager(context);
    }

    public boolean addAppointment()
    {
        Long appointmentId;
        Calendar reminderDate;

        appointmentId = appointmentDAO.insert(appointment);
        if(appointmentId != -1)
        {
            reminderDate = determineReminderTime(remainderInterval,appointment.getAppointment());
            if(reminderDate == null)
            {
                return false;
            }
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent alertIntent = new Intent(context, RemindAlarmReceiver.class);
            alertIntent.putExtra("Id",Integer.toString(appointmentId.intValue()));

            PendingIntent broadcast = PendingIntent.getBroadcast(context,appointmentId.intValue(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //Log.d("check",Long.toString(date.getTime()));
            //Log.d("check",
            //      Long.toString(reminderDate.get(Calendar.DAY_OF_MONTH))+" "+Long.toString(reminderDate.get(Calendar.MONTH))+" "+Long.toString(reminderDate.get(Calendar.YEAR))+" "+Long.toString(reminderDate.get(Calendar.HOUR_OF_DAY))+" "+Long.toString(reminderDate.get(Calendar.MINUTE)));
            //Log.d("doublecheck",Long.toString(new GregorianCalendar().getTimeInMillis() + (60*1000)));
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderDate.getTimeInMillis(), broadcast);
            //alarmManager.set(AlarmManager.RTC_WAKEUP, new GregorianCalendar().getTimeInMillis() +(5*1000), broadcast);

            //store the appointment Id and title in shared preferences. will be used in displaying remainders and appointments
            appointmentPreference.storeAppointmentInfo(Long.toString(appointmentId),appointmentTitle);
        }
        else
        {
            return false;
        }

        return true;

    }

    /*
    method to get the date and time on which a remainder needs to be set
     */
    private Calendar determineReminderTime(String remainderInterval, String appointment)  {
        //Log.d("date:",appointment);
        Integer interval = decodeRemainderInterval(remainderInterval);
        //Log.d("interval:",interval.toString());

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

        try {

            Date date = df.parse(appointment);
            //Log.d("date", df.format(date));
            c.setTime(date);
            c.add(Calendar.MINUTE,-interval);
            //Log.d("c date", df.format(c.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c;
    }

    /*
    method to get the interval in minutes to be used to set a reminder
     */
    private Integer decodeRemainderInterval(String remainderInterval) {
        Integer retVal = 0;
        switch (remainderInterval){
            case "No Remainder":
                retVal = 0;
                break;
            case "15 Minutes Before":
                retVal = 15;
                break;
            case "30 Minutes Before":
                retVal = 30;
                break;
            case "1 Hour Before":
                retVal = 60;
                break;
            case "4 Hours Before":
                retVal = 240;
                break;
            case "12 Hours Before":
                retVal = 720;
                break;
            case "1 Day Before":
                retVal = 1440;
                break;
        }
        return retVal;
    }
}
