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

import com.github.privacystreams.core.exceptions.PSException;

import edu.cmu.chimps.iamhome.AlarmReceiver;
import edu.cmu.chimps.iamhome.utils.StatusToastsUtils;
import edu.cmu.chimps.iamhome.utils.AlarmUtils;
import edu.cmu.chimps.iamhome.utils.WifiUtils;


public class SaveHomeWifiService extends IntentService{
    public static final String ACTION_SAVE = "ACTION_SAVE";

    public SaveHomeWifiService(String name) {
        super(name);
    }
    public SaveHomeWifiService(){
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(AlarmReceiver.getNotificationId());
            if(action!=null){
              if (action.equals(ACTION_SAVE)) {
                //the user press yes and confirm he is at home.
                // We store the current wifi BSSIDs;
                try {
                    WifiUtils.storeUsersHomeWifi(this);
                    AlarmUtils.cancelAlarm();
                    //user has selected contacts, create a noti to let user send message
                    StatusToastsUtils.createAthomeNoti(this);
                }
                catch (PSException e) {
                    e.printStackTrace();
                }
              }
           }
        }
    }
}
