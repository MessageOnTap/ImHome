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

package edu.cmu.chimps.iamhome.sharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

public class FirstTimeStorage {

    private static final String IDENTIFIER = "firt_time";
    private static final String IDENTIFIER_CONTACT_ACTIVITY_INDICATOR_SPECIAL = "send";

    public static void setFirst(Context context, Boolean input) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IDENTIFIER, input);
        editor.apply();
    }

    public static void setFirst(Context context, Boolean input, String stage) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IDENTIFIER + stage, input);
        editor.apply();
    }

    public static boolean getFirst(Context context) {
        SharedPreferences isFirstTime = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE);
        return isFirstTime.getBoolean(IDENTIFIER, true);
    }

    public static void setContactActivityIndicatorSend(Context context, Boolean input) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IDENTIFIER_CONTACT_ACTIVITY_INDICATOR_SPECIAL, input);
        editor.apply();
    }

    public static boolean getIndicator(Context context) {
        SharedPreferences isSpecialActivity = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE);
        return isSpecialActivity.getBoolean(IDENTIFIER_CONTACT_ACTIVITY_INDICATOR_SPECIAL, false);
    }
}
