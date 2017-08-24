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


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import edu.cmu.chimps.iamhome.MyApplication;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AutoSelectUtils {

    public void autoLaunch(Context context, String message, String packageName) {
        Intent autoLaunchIntent = new Intent();
        autoLaunchIntent.setAction(Intent.ACTION_SEND);
        autoLaunchIntent.setType("text/plain");
        autoLaunchIntent.putExtra(Intent.EXTRA_TEXT, message);
        autoLaunchIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        if (packageName != null) {
            autoLaunchIntent.setPackage(packageName);
        }
        context.startActivity(autoLaunchIntent);
    }

    public static boolean autoSelect(String[] inputNameList, AccessibilityNodeInfo selectingView) {

        boolean clicked = false;

        Log.e("hi", Integer.toString(inputNameList.length));
        for (String name : inputNameList) {
            List<AccessibilityNodeInfo> matchedList = selectingView.findAccessibilityNodeInfosByText(name);

            if (!matchedList.isEmpty()) {
                AccessibilityNodeInfo ro = matchedList.get(0);
                do {
                    ro = ro.getParent();
                    if (ro.isClickable()) {
                        ro.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.e(name, "Clicked");
                        clicked = true;
                    }
                } while (!ro.isClickable());
            } else {
                Log.e("Warning", "No matched");
            }
        }
        if (inputNameList.length == 1) {
            Log.e("try", "finding button");
            List<AccessibilityNodeInfo> sendButtons = selectingView.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
            Log.e("founded", "founded button");
            AccessibilityNodeInfo bu = sendButtons.get(0);
            bu.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return clicked;
    }

    public static boolean checkPermission(Context context) {
        Activity currentActivity = ((MyApplication)context.getApplicationContext()).getCurrentActivity();
        Integer permissionCode = ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.BIND_ACCESSIBILITY_SERVICE);
        Log.e("Code", Integer.toString(permissionCode));
        return permissionCode == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    /*
    public static boolean hasPermission(Context context) {
        String pkgName = context.getPackageName();
        int permissionCode = context.getPackageManager().checkPermission(Manifest.permission.BIND_ACCESSIBILITY_SERVICE, pkgName);
        Log.e("Code", Integer.toString(permissionCode));
        return permissionCode == PackageManager.PERMISSION_DENIED;
    }
    */
/*
    public static boolean hasPermission(Context context) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.BIND_ACCESSIBILITY_SERVICE)
                != PackageManager.PERMISSION_GRANTED;
    }
*/
}
