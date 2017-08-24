/*
  Copyright 2017 CHIMPS Lab, Carnegie Mellon University

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package edu.cmu.chimps.iamhome.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import edu.cmu.chimps.iamhome.R;
import edu.cmu.chimps.iamhome.services.NotificationTriggerService;

public class StatusToastsUtils {

    public static void atHomeToast(Context context){
        CharSequence text = "You are at home";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public static void leaveHomeToast(Context context){
        CharSequence text = "You have left home";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static void wifiConnectedToast(Context context){
        CharSequence text = "Wifi Connected";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public static void wifiDisconnectedToast(Context context){
        CharSequence text = "Wifi Disconnected";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static void saveHomeToast(Context context){
        CharSequence text = "save home success";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * send message noti
     * @param context
     */
    public static void createAthomeNoti(Context context){
        //setting yes action
        Intent sendMessageServiceIntent= new Intent(context, NotificationTriggerService.class);
        sendMessageServiceIntent.setAction(NotificationTriggerService.ACTION_SEND);
        PendingIntent yesPendingIntent = PendingIntent
                .getService(context.getApplicationContext(), 0, sendMessageServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //setting no action
        sendMessageServiceIntent = new Intent(context, NotificationTriggerService.class);
        PendingIntent noPendingIntent = PendingIntent
                .getService(context.getApplicationContext(), 0, sendMessageServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_home_white_24px)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText("Do you want to send At Home Messgage to your selected friend")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .addAction(0, "Yes", yesPendingIntent)
                        .addAction(0, "No", noPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(3, mBuilder.build());
    }
}
