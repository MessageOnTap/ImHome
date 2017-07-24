package edu.cmu.chimps.iamhome;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.LinearLayout;

import android.widget.Toast;
import com.github.privacystreams.communication.Contact;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.exceptions.PSException;
import com.github.privacystreams.core.purposes.Purpose;
import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;
import java.util.Timer;
import java.util.TimerTask;
import edu.cmu.chimps.iamhome.SharedPrefs.FirstTimeStorage;
import edu.cmu.chimps.iamhome.SharedPrefs.StringStorage;
import edu.cmu.chimps.iamhome.services.ShareMessageService;
import edu.cmu.chimps.iamhome.utils.WifiUtils;

public class IAmHomeSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private String sentText;
    public String username;
    IAmHomePlugin userstatus = new IAmHomePlugin();

    Intent circleIntent = new Intent();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UQI uqi = new UQI(this);
        uqi.getData(Contact.getAll(), Purpose.UTILITY("test")).debug();
        /**
         * set user wifi status
         */
        setContentView(R.layout.welcome_page);



        //Callback when the view is ready
        final LinearLayout welcomePage = (LinearLayout) findViewById(R.id.welcome_page);
        ViewTreeObserver vto = welcomePage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                welcomePage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                /**
                 * Tutorial
                 */
                View homeView = findViewById(R.id.imageView);
                int[] imageLocation = new int[2];
                homeView.getLocationOnScreen(imageLocation);
                float imageX = imageLocation[0] + homeView.getWidth() / 2f;
                float imageY = imageLocation[1] + homeView.getHeight() / 2f;
                // make an target
                SimpleTarget firstTarget = new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(imageX, imageY)
                        .setRadius(250f)
                        .setTitle("Image")
                        .setDescription("Show whether you are at home")
                        .build();

                View two = findViewById(R.id.circleMenu);
                int[] twoLocation = new int[2];
                two.getLocationInWindow(twoLocation);
                PointF point =
                        new PointF(twoLocation[0] + two.getWidth() / 2f, twoLocation[1] + two.getHeight() / 2f);
                // make an target
                SimpleTarget secondTarget = new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(point)
                        .setRadius(160f)
                        .setTitle("Menu")
                        .setDescription("The Main Menu")
                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                            @Override
                            public void onStarted(SimpleTarget target) {

                            }

                            @Override
                            public void onEnded(SimpleTarget target) {

                            }
                        })
                        .build();

                SimpleTarget thirdTarget =
                        new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(findViewById(R.id.textView3))
                                .setRadius(200f)
                                .setTitle("Title")
                                .setDescription("Showing your connected wifi")
                                .build();

                Spotlight.with(IAmHomeSettingsActivity.this)
                        .setDuration(1000L)
                        .setAnimation(new DecelerateInterpolator(2f))
                        .setTargets(firstTarget, thirdTarget, secondTarget)
                        .setOnSpotlightStartedListener(new OnSpotlightStartedListener() {
                            @Override
                            public void onStarted() {
                                Toast.makeText(IAmHomeSettingsActivity.this, "Lets learn how to use this masterpiece", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                        .setOnSpotlightEndedListener(new OnSpotlightEndedListener() {
                            @Override
                            public void onEnded() {

                            }
                        })
                        .start();
            }
        });


        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        /**
         * Tood listen user wifi
         */
        if (userstatus.isAtHome()) {
            Drawable drawable = getDrawable(R.drawable.ic_work_black_24dp);
            imageView.setImageDrawable(drawable);
        } else {
            Drawable drawable = getDrawable(R.drawable.ic_home_white_24px);
            imageView.setImageDrawable(drawable);
        }

        final CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);

        if (FirstTimeStorage.getFirst(MyApplication.getContext())) {
            Toast.makeText(MyApplication.getContext(), "This is I AM HOME Plugin", Toast.LENGTH_SHORT).show();
            StringStorage.storeMessage(MyApplication.getContext(), "", true);
        }

        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                /**
                 * Four buttons for actions;
                 */

                if (menuButton == menuButton.findViewById(R.id.search)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(IAmHomeSettingsActivity.this, R.style.myDialog));
                    dialog.setTitle("Reset Home Wifi");
                    dialog.setMessage("Saved wifi will be replaced by the connected wifi");
                    dialog.setPositiveButton("RESET TO CURRENT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Reset Wifi code here
                            new LongOperation().execute(" ");
                        }
                    });
                    dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent closeNotificationDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                            sendBroadcast(closeNotificationDrawer);
                        }
                    });
                    dialog.show();
                }
                if (menuButton == menuButton.findViewById(R.id.favorite)) {
                    circleIntent = new Intent(MyApplication.getContext(), SelectContactActivity.class);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(circleIntent);
                        }
                    }, 1170);
                }
                if (menuButton == menuButton.findViewById(R.id.edit)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(IAmHomeSettingsActivity.this, R.style.myDialog));
                    builder.setTitle("Set message to send");
                    final EditText input = new EditText(MyApplication.getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setHint(StringStorage.getMessage(getBaseContext()));

                    input.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            input.setHint(null);
                        }
                    });
                    builder.setView(input);
                    builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sentText = input.getText().toString();
                            StringStorage.storeMessage(IAmHomeSettingsActivity.this, sentText, false);
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    Log.i("diaglog", "ok");
                }
                if (menuButton == menuButton.findViewById(R.id.explorer)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(IAmHomeSettingsActivity.this, R.style.myDialog));
                    dialog.setTitle("Send message");
                    dialog.setMessage("Send your message to your friends!" + "\n\nCurrent message:\n\"" + StringStorage.getMessage(MyApplication.getContext()) + "\"\n");
                    dialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (FirstTimeStorage.getFirst(MyApplication.getContext())) {
                                Toast.makeText(MyApplication.getContext(), "Since it's your first time, please set up the sending list", Toast.LENGTH_LONG).show();
                                Intent launchActivity = new Intent(MyApplication.getContext(), SelectContactActivity.class);
                                MyApplication.getContext().startActivity(launchActivity);
                            } else {
                                Intent launchService = new Intent(MyApplication.getContext(), ShareMessageService.class);
                                startService(launchService);
                            }
                        }
                    });
                    dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent closeNotificationDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                            MyApplication.getContext().sendBroadcast(closeNotificationDrawer);
                        }
                    });
                    dialog.show();
                }
            }


        });


        circleMenu.setStateUpdateListener(new CircleMenu.OnStateUpdateListener() {
            @Override
            public void onMenuExpanded() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                                   @Override
                                   public void run() {
                                       runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               View reset= findViewById(R.id.search);
                                               int[] imageLocation = new int[2];
                                               reset.getLocationOnScreen(imageLocation);
                                               float imageX = imageLocation[0] + reset.getWidth() / 2f;
                                               float imageY = imageLocation[1] + reset.getHeight() / 2f;

                                               // make an target
                                               SimpleTarget firstTarget = new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(imageX, imageY)
                                                       .setRadius(200f)
                                                       .setTitle("Rest")
                                                       .setDescription("Use this to reset your home WIFI")
                                                       .build();

                                               View two = findViewById(R.id.edit);
                                               int[] twoLocation = new int[2];
                                               two.getLocationInWindow(twoLocation);
                                               PointF point =
                                                       new PointF(twoLocation[0] + two.getWidth() / 2f, twoLocation[1] + two.getHeight() / 2f);
                                               // make an target
                                               SimpleTarget secondTarget = new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(point)
                                                       .setRadius(200f)
                                                       .setTitle("Edit")
                                                       .setDescription("Customize your at home message")
                                                       .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                                                           @Override
                                                           public void onStarted(SimpleTarget target) {

                                                           }

                                                           @Override
                                                           public void onEnded(SimpleTarget target) {

                                                           }
                                                       })
                                                       .build();

                                               SimpleTarget thirdTarget =
                                                       new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(findViewById(R.id.favorite))
                                                               .setRadius(200f)
                                                               .setTitle("Contacts")
                                                               .setDescription("Check your sending contact list")
                                                               .build();
                                               SimpleTarget fourthTarget =
                                                       new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(findViewById(R.id.explorer))
                                                               .setRadius(200f)
                                                               .setTitle("Launch!")
                                                               .setDescription("One tap to tell your friends you are home")
                                                               .build();

                                               Spotlight.with(IAmHomeSettingsActivity.this)
                                                       .setDuration(1000L)
                                                       .setAnimation(new DecelerateInterpolator(2f))
                                                       .setTargets(firstTarget, secondTarget, thirdTarget,fourthTarget)
                                                       .setOnSpotlightStartedListener(new OnSpotlightStartedListener() {
                                                           @Override
                                                           public void onStarted() {
                                                           }
                                                       })
                                                       .setOnSpotlightEndedListener(new OnSpotlightEndedListener() {
                                                           @Override
                                                           public void onEnded() {
                                                               Toast.makeText(IAmHomeSettingsActivity.this, "You have learned how to use this masterpiece", Toast.LENGTH_SHORT)
                                                                       .show();
                                                           }
                                                       })
                                                       .start();

                                           }
                                       });


                                   }
                               },600);


            }

            @Override
            public void onMenuCollapsed() {
                Log.i("collapsed", "circle menu collapsed");

            }

        });
        startService(new Intent(this, IAmHomePlugin.class));
    }
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                WifiUtils.storeUsersHomeWifi(MyApplication.getContext());
                Log.i("test", "stored");
                Log.i("get", String.valueOf(WifiUtils.getUsersHomeWifiList(MyApplication.getContext())));
            } catch (PSException e) {
                e.printStackTrace();
            }

            return "Executed";
        }
    }

    @Override
    public void onClick(View view) {

    }
}

