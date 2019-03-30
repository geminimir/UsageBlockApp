package com.app.usageblockapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.usageblockapp.BuildConfig;
import com.app.usageblockapp.R;
import com.app.usageblockapp.config.Config;
import com.app.usageblockapp.receivers.AdminClassReceiver;
import com.app.usageblockapp.receivers.DailyBroadcastReceiver;
import com.app.usageblockapp.services.ForegroundProcessService;
import com.crashlytics.android.Crashlytics;

import java.util.Calendar;

import io.fabric.sdk.android.Fabric;
import mobi.upod.timedurationpicker.TimeDurationPickerPreference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(needPermissionForBlocking(getApplicationContext())) {
            showSettingsDialog();
        } else {
            startService(new Intent(MainActivity.this, ForegroundProcessService.class));
            Context context = getApplicationContext();
            Calendar midnightCalendar = Calendar.getInstance();

            //set the time to midnight tonight
            midnightCalendar.set(Calendar.HOUR_OF_DAY, 0);
            midnightCalendar.set(Calendar.MINUTE, 0);
            midnightCalendar.set(Calendar.SECOND, 0);

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            //create a pending intent to be called at midnight
            PendingIntent midnightPI = PendingIntent.getService(this, 0, new Intent("com.app.usageblockapp.receivers.DailyBroadcastReceiver"), PendingIntent.FLAG_UPDATE_CURRENT);
            //schedule time for pending intent, and set the interval to day so that this event will repeat at the selected time every day
            am.setRepeating(AlarmManager.RTC_WAKEUP, midnightCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, midnightPI);

            Toast.makeText(context, "Alarm set", Toast.LENGTH_SHORT).show();
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction tx = manager.beginTransaction();
        tx.replace(R.id.frameLayout, new MainFragment());
        tx.commit();

    }

    public static boolean needPermissionForBlocking(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode != AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    private void showSettingsDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Usage Access Permission");
        builder.setCancelable(false);
        builder.setMessage("In order to detect settings changes, MomCensor needs the permission \"Usage Access\" to be activated.");
        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("ValidFragment")
    public static class MainFragment extends PreferenceFragment {
        private PreferenceScreen uninstall, selectApps, hideApp;
        private TimeDurationPickerPreference timePreference;

        private Button btnGo;
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(resultCode == RESULT_OK && requestCode == 0) {
                //startActivity(new Intent(getActivity(), ChangeKeyboardActivity.class));
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            if(uninstall != null) {
                if (AdminClassReceiver.isDeviceAdmin(getActivity())) {
                    uninstall.setTitle("Allow Uninstall");
                    uninstall.setSummary("Make app uninstallable again.");
                } else {
                    uninstall.setTitle("Disallow uninstall");
                    uninstall.setSummary("Prevent app from being uninstalled");
                }
            }

        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.ime_preferences);

            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);

            timePreference = (TimeDurationPickerPreference) findPreference("time");
            selectApps = (PreferenceScreen)findPreference("selectApps");
            uninstall = (PreferenceScreen)findPreference("uninstall");
            hideApp = (PreferenceScreen)findPreference("hide");

            timePreference.setDuration(sharedPreferences.getLong("time", 0));


            if(AdminClassReceiver.isDeviceAdmin(getActivity())) {
                uninstall.setTitle("Allow Uninstall");
                uninstall.setSummary("Make app uninstallable again.");
            } else {
                uninstall.setTitle("Disallow uninstall");
                uninstall.setSummary("Prevent app from being uninstalled");
            }

            selectApps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), SelectAppsActivity.class));
                    return false;
                }
            });

            /*if(!Config.isProVersion(getActivity()))
                general.removePreference(customWord);
            else
                customWord.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        showCustomWordDialog();
                        return false;
                    }
                });*/
            uninstall.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showUninstallDialog();
                    if(!AdminClassReceiver.isDeviceAdmin(getActivity())) {
                        ComponentName compName = new ComponentName(getActivity(), AdminClassReceiver.class);
                        Intent deviceIntent = new Intent(DevicePolicyManager
                                .ACTION_ADD_DEVICE_ADMIN);
                        deviceIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                compName);
                        startActivityForResult(deviceIntent, 0);
                    } else {
                        ComponentName compName = new ComponentName(getActivity(), AdminClassReceiver.class);
                        DevicePolicyManager mDPM = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
                        mDPM.removeActiveAdmin(compName);
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        startActivity(intent);
                    }
                    return false;
                }
            });


            hideApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[] {
                                Manifest.permission.PROCESS_OUTGOING_CALLS
                        }, Config.CALLS_PERMISSION_REQUEST_CODE);
                    }
                    else {
                        showHideAppIconDialog();
                    }
                    return false;
                }
            });


            timePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Long seconds = ((Long)newValue) / 1000;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("time", seconds);
                    editor.commit();

                    return false;
                }
            });
        }

        private void showUninstallDialog() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setMessage("To uninstall app you need to enter password and deactivate administator mode.");
            alertDialog.setTitle(uninstall.getTitle());

            final EditText editText = new EditText(getActivity());

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
            final String password = sharedPreferences.getString("password", "1234");

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            editText.setLayoutParams(lp);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            alertDialog.setView(editText);
            alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(editText.getText().toString().equals(password)) {
                        if(!AdminClassReceiver.isDeviceAdmin(getActivity())) {
                            ComponentName compName = new ComponentName(getActivity(), AdminClassReceiver.class);
                            Intent deviceIntent = new Intent(DevicePolicyManager
                                    .ACTION_ADD_DEVICE_ADMIN);
                            deviceIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                    compName);
                            startActivityForResult(deviceIntent, 0);
                        } else {
                            ComponentName compName = new ComponentName(getActivity(), AdminClassReceiver.class);
                            DevicePolicyManager mDPM = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
                            mDPM.removeActiveAdmin(compName);
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Password incorrect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }

        private void showHideAppIconDialog() {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
            final String password = sharedPreferences.getString("password", "1234");

            alertDialog.setMessage("The app icon will now become invisible. To make it reappear again dial #" +
                    password + "# on your dial pad.");

            alertDialog.setTitle("Hide App");
            alertDialog.setPositiveButton("MAKE APP ICON INVISIBLE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Config.hideAppIcon(getActivity());
                }
            });
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        /*private void showReadyDialog() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setMessage("Almost Done! You only have to choose a password to use in hiding and uninstalling the app.");
            alertDialog.setTitle("Almost Done!");
            final EditText editText = new EditText(getActivity());

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            editText.setLayoutParams(lp);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            alertDialog.setView(editText);
            editText.setText("1234");
            alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", editText.getText().toString());
                    editor.apply();
                }
            });

            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }*/

    }
}
