package edu.cmu.chimps.iamhome.utils;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AutoSelectUtils {

    public void autoLaunch(Context context, String message, String packageName) {
        Intent autoLaunchIntent = new Intent();
        autoLaunchIntent.setAction(Intent.ACTION_SEND);
        autoLaunchIntent.setType("text/plain");
        autoLaunchIntent.putExtra(Intent.EXTRA_TEXT, message);
        autoLaunchIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        if (packageName != null) { autoLaunchIntent.setPackage(packageName); }
        context.startActivity(autoLaunchIntent);
    }

    public static boolean autoSelect(String[] inputNameList, AccessibilityNodeInfo selectingView) {

        boolean clicked = false;
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
            }
            else {
                Log.e("Warning", "No matched");
            }
        }
        return clicked;
    }

}
