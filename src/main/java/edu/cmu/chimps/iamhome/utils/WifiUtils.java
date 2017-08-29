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


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.privacystreams.core.Callback;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.exceptions.PSException;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.device.WifiAp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.cmu.chimps.iamhome.MyApplication;


public class WifiUtils {

    public static final String KEY_WIFI_SENSING = "key_wifi_sensing";
    public static final String KEY_USER_HOME_BSSID_LIST  = "key_wifi_bssid_list";
    /**
     * Store all user's wifi BSSIDs.
     */

    public static void storeUsersHomeWifi(Context context) throws PSException {
        SharedPreferences preferences = context.getSharedPreferences(KEY_WIFI_SENSING, Context.MODE_PRIVATE);
        preferences.edit().putStringSet(KEY_USER_HOME_BSSID_LIST, new HashSet<>(getBSSIDList(context))).apply();
        Log.i("BSSID", String.valueOf(getUsersHomeWifiList(context)));
    }

    /**
     * Get all BSSIDs that are associated with user home wifi.
     * @return
     */
    public static Set<String> getUsersHomeWifiList(Context context){
        SharedPreferences preferences = context.getSharedPreferences(KEY_WIFI_SENSING, Context.MODE_PRIVATE);
        return preferences.getStringSet(KEY_USER_HOME_BSSID_LIST, new HashSet<String>());
    }

    //

    /**
     * Get the connected WiFi BSSID
     * @return the BSSID
     * @throws PSException
     */
    public String getConnectedWifiBSSID(Context context) throws PSException {
        UQI uqi = new UQI(context);
        Item wifiItem = uqi.getData(WifiAp.getScanResults(), Purpose.FEATURE("Get Connected Wifi BSSID"))
                .filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED)
                .limit(1).getFirst().asItem();
        if(wifiItem !=null){
            return wifiItem.getValueByField(WifiAp.BSSID).toString();
        }

        return null;
    }

    /**
     * Check whether user has connected to a wifi
     * @return
     * @throws PSException
     */
    public  static void isConnectedToWifi() throws PSException {
        UQI uqi = new UQI(MyApplication.getContext());
        uqi.getData(WifiAp.getUpdateStatus(), Purpose.UTILITY("listen")).filter(WifiAp.STATUS,WifiAp.STATUS_CONNECTED).forEach(new Callback<Item>() {
            @Override
            protected void onInput(Item input) {
                if (input != null){
                    try {
                        isConnectedToWifi(true);
                    } catch (PSException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    isConnectedToWifi(false);
                } catch (PSException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public static boolean isConnectedToWifi(Boolean result) throws PSException {
        isConnectedToWifi();
        return result;
    }

    /**
     * Get all related BSSIDs of the user connected wifi;
     * @throws PSException
     */

    public static List<String> getBSSIDList(Context context) throws PSException {

        UQI uqi = new UQI(context);
        String ssid = uqi.getData(WifiAp.getScanResults(), Purpose.FEATURE("Get access to the SSID of connected Wifi"))
                .filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED)
                .getFirst().getField(WifiAp.SSID).toString();

        return uqi.getData(WifiAp.getScanResults(), Purpose.FEATURE("Get access to all related BSSIDs of the connected Wifi"))
                .filter(WifiAp.SSID, ssid).asList(WifiAp.BSSID);
    }


    public static String getWifiName(Context context) throws PSException {
        UQI uqi = new UQI(context);
        String name  = uqi.getData(WifiAp.getScanResults(),Purpose.UTILITY("get wifi name")
                ).filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED).getFirst().getField(WifiAp.SSID).toString();
        return name;
    }
    public void isAtHome(Context context){
        UQI uqi = new UQI(context);

        uqi.getData(WifiAp.getUpdateStatus(), Purpose.UTILITY("listen for wifi changes ")).forEach(new Callback<Item>() {
            @Override
            protected void onInput(Item input) {
                Set<String> temp = WifiUtils.getUsersHomeWifiList(MyApplication.getContext());
                if(temp != null && temp.contains(input.getValueByField(WifiAp.BSSID))){
                        isAtHome(true);
                }
                isAtHome(false);
            }
        });
    }
    public static boolean isAtHome(boolean result){
        return result;
    }

    /**
     * check whter the user is connecting a wifi, true if the user is connecetd to a wifi
     * @return
     * @throws PSException
     */
    public static boolean checkWifiStatus() throws PSException {
        UQI uqi = new UQI(MyApplication.getContext());
        return !uqi.getData(WifiAp.getScanResults(), Purpose.UTILITY("check wifi stauts")).filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED).asList().isEmpty();
    }

}
