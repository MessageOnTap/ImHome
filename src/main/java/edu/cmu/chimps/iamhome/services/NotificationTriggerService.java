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

package edu.cmu.chimps.iamhome.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import edu.cmu.chimps.iamhome.MyApplication;


public class NotificationTriggerService extends IntentService{
    public static final String ACTION_SEND = "ACTION_SEND";
    public NotificationTriggerService(String name) {
        super(name);
    }
    public NotificationTriggerService(){
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            /*
             * cancel send message notification
             */
            NotificationManager notificationManager =
                   (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(3);
        if(action != null){
            if (action.equals(ACTION_SEND)) {

                notificationManager.cancel(3);
                //// TODO: 7/21/17 send message to yuser
                Intent closeNotificationDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                this.sendBroadcast(closeNotificationDrawer);
                Intent startSessionIntent = new Intent("Session On Start");
                LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(startSessionIntent);
            }
        }

    }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3);
}
}
