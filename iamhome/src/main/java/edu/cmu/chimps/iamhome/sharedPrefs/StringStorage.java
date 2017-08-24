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
import android.widget.Toast;

public class StringStorage {

    private static final String APP_DEFAULT_MESSAGE = "Hey! I just arrived home!";
    private static final String POSITION = "IAmHomeDefaultMessage";

    public static void storeMessage(Context context, String inputText, Boolean mute) {
        SharedPreferences.Editor editor = context.getSharedPreferences("message", Context.MODE_PRIVATE).edit();
        if (inputText.replaceAll(" ", "").equals("")) {
            if(!mute) {
                Toast.makeText(context, "Message has been reset to default", Toast.LENGTH_SHORT).show();
            }
            editor.putString(POSITION, APP_DEFAULT_MESSAGE);
            editor.apply();
        } else {
            editor.putString(POSITION, inputText);
            editor.apply();
            if(!mute) {
                Toast.makeText(context, "Successfully save", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getMessage(Context context) {
        SharedPreferences msg = context.getSharedPreferences("message", Context.MODE_PRIVATE);
        return msg.getString(POSITION, "");
    }
}
