package edu.cmu.chimps.iamhome.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;

import edu.cmu.chimps.iamhome.AlarmReceiver;
import edu.cmu.chimps.iamhome.MyApplication;

public class AlarmUtils {
    public static AlarmManager alarmManager;
    public static PendingIntent pendingIntent;

    public static void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) MyApplication.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setAlarm(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        Calendar right_now = Calendar.getInstance();

        int timeOffset = hour - right_now.get(Calendar.HOUR);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, hour - timeOffset);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        Intent intent = new Intent(MyApplication.getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(), 0, intent, 0);
        alarmManager = (AlarmManager) MyApplication.getContext().getSystemService(Context.ALARM_SERVICE);

        //set the alarm repeat one day
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }
}