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

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.privacystreams.core.exceptions.PSException;

import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.iamhome.listeners.IconChangeListener;
import edu.cmu.chimps.iamhome.sharedprefs.ContactStorage;
import edu.cmu.chimps.iamhome.sharedprefs.FirstTimeStorage;
import edu.cmu.chimps.iamhome.views.Contact;
import edu.cmu.chimps.iamhome.views.ContactAdapter;


public class SelectContactActivity extends AppCompatActivity {
    protected MyApplication mAPP;
    private int mBackPressedCount;
    private Toast updatableToast;
    public static IconChangeListener iconChangeListener;

    public static void setIconChangeListener(IconChangeListener icl) {
        iconChangeListener = icl;
    }

    @Override
    public void onBackPressed() {

        if (mBackPressedCount == 0) {
            if (updatableToast != null) {
                updatableToast.cancel();
            }
            updatableToast = Toast.makeText(SelectContactActivity.this, "Click again to cancel the change", Toast.LENGTH_SHORT);
            updatableToast.show();
            mBackPressedCount++;
        } else if (mBackPressedCount == 1) {
            if (updatableToast != null) {
                updatableToast.cancel();
            }
            updatableToast = Toast.makeText(SelectContactActivity.this, "Change canceled", Toast.LENGTH_SHORT);
            updatableToast.show();
            super.onBackPressed();
        } else {
            Set<String> set = new HashSet<>(Contact.getSavedContactList());
            ContactStorage.storeSendUsers(SelectContactActivity.this, set, ContactStorage.KEY_STORAGE);
            if (updatableToast != null) {
                updatableToast.cancel();
            }
            updatableToast = Toast.makeText(SelectContactActivity.this, "Contacts saved", Toast.LENGTH_SHORT);
            updatableToast.show();
            super.onBackPressed();
        }

    }

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPP = (MyApplication) this.getApplicationContext();
        mBackPressedCount = 0;

        try {
            Contact.contactList = Contact.getWhatsAppContacts(this);
        } catch (PSException e) {
            e.printStackTrace();
        }
        Contact.initFlag(this, ContactStorage.KEY_STORAGE);

        //Initialize UI
        setContentView(R.layout.activity_contact_select);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary), true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setTitle("Select Contacts");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorwhite));
        toolbar.setSubtitle(" " + Contact.selectedItemCount() + " selected");
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorwhite));
        toolbar.inflateMenu(R.menu.selectall);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getBaseContext(), "Contacts Saved" , Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        setIconChangeListener(new IconChangeListener() {
            public void onChange(Boolean wantChange) {
                Log.e("test", "Listener recieved");
                MenuItem i = toolbar.getMenu().getItem(0);
                if (wantChange) {
                    i.setIcon(getDrawable(R.drawable.ic_delete_sweep_black_24dp));
                } else {
                    i.setIcon(getDrawable(R.drawable.ic_action_selectall));
                }
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                switch (menuItemId) {
                    case R.id.selectAll:
                        if (Contact.selectedItemCount() == Contact.contactList.size()) {
                            item.setIcon(getDrawable(R.drawable.ic_action_selectall));
                            ContactAdapter.SetAllSelection(false, recyclerView);
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.recyclerview), "Deselect All", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            item.setIcon(getDrawable(R.drawable.ic_delete_sweep_black_24dp));
                            Set<String> set = new HashSet<>(Contact.getSavedContactList());
                            ContactStorage.storeSendUsers(getBaseContext(), set, ContactStorage.KEY_ALL_SELECT_STORAGE);
                            ContactAdapter.SetAllSelection(true, recyclerView);
                            final MenuItem itemP = item;
                            Snackbar undoSnackbar = Snackbar
                                    .make(findViewById(R.id.recyclerview), "Select All", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            itemP.setIcon(getDrawable(R.drawable.ic_action_selectall));
                                            Contact.initFlag(SelectContactActivity.this, ContactStorage.KEY_ALL_SELECT_STORAGE);
                                            ContactAdapter.SetAllSavedSelection(recyclerView);
                                            toolbar.setSubtitle(" " + Contact.selectedItemCount() + " selected");
                                        }
                                    });

                            undoSnackbar.show();
                        }
                        toolbar.setSubtitle(" " + Contact.selectedItemCount() + " selected");
                        break;
                        //Toast.makeText(getBaseContext(), "Select All" , Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        ContactAdapter adapter = new ContactAdapter(Contact.contactList, toolbar);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        FloatingActionButton floatingUndefinedButton = findViewById(R.id.floatingUndefinedAction);
        if (FirstTimeStorage.getIndicator(MyApplication.getContext())) {
            floatingUndefinedButton.setImageResource(R.drawable.ic_action_send);
            floatingUndefinedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBackPressedCount = 2;
                    onBackPressed();
                    Intent startSessionIntent = new Intent("Session On Start");
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(startSessionIntent);
                }
            });
        } else {
            floatingUndefinedButton.setImageResource(R.drawable.ic_action_check);
            floatingUndefinedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBackPressedCount = 2;
                    onBackPressed();
                }
            });
        }

        FirstTimeStorage.setContactActivityIndicatorSend(MyApplication.getContext(), false);

        startService(new Intent(this, IAmHomePlugin.class));
    }
    protected void onResume() {
        super.onResume();
        mAPP.setCurrentActivity(this);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = mAPP.getCurrentActivity();
        if (this.equals(currActivity))
            mAPP.setCurrentActivity(null);
    }
}




