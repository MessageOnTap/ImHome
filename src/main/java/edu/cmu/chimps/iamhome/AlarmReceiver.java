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

package edu.cmu.chimps.iamhome;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import edu.cmu.chimps.iamhome.services.SaveHomeWifiService;

public class AlarmReceiver extends BroadcastReceiver{
    private static final int NOTIFICATION_ID = 1;
    private static final String YES_TITLE = "Yes";
    private static final String NO_TITLE = "No";

    @Override
    public void onReceive(Context context, Intent intent) {
        //trigger notification

         createNotification(context);
    }

    public static int getNotificationId(){
        return NOTIFICATION_ID;
    }

    /**
     * at home noti
     * @param context
     */
    public void createNotification(Context context){
        //setting yes action
        Intent saveHomeWifiServiceIntent = new Intent(context, SaveHomeWifiService.class);
        saveHomeWifiServiceIntent.setAction(SaveHomeWifiService.ACTION_SAVE);
        PendingIntent yesPendingIntent = PendingIntent
                .getService(context.getApplicationContext(), 0, saveHomeWifiServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //setting no action
        saveHomeWifiServiceIntent = new Intent(context, SaveHomeWifiService.class);
        PendingIntent noPendingIntent = PendingIntent
                .getService(context.getApplicationContext(), 0, saveHomeWifiServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_home_white_24px)
                        //.setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText(context.getResources().getString(R.string.are_you_at_home))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .addAction(0, YES_TITLE, yesPendingIntent)
                        .addAction(0, NO_TITLE, noPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


    }


}
