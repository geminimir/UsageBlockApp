package com.app.usageblockapp.services;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


import com.app.usageblockapp.config.Config;
import com.app.usageblockapp.models.App;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ForegroundProcessService extends Service {
    public ForegroundProcessService() {
    }

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    public static long elapsedTime = 0;
    private Handler mHandler;
    private long selectedTime;
    private SharedPreferences sharedPreferences;

    private List<String> activatedPackageNames = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("tagged", "Service started");
        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        for(App app : Config.getActivatedAppList(getApplicationContext()))
            activatedPackageNames.add(app.getPackageName());
        mHandler = new Handler();
        startRepeatingTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                selectedTime = sharedPreferences.getLong("time", 60);
                Log.i("tagged", (elapsedTime * 5) + "   " +  selectedTime  + "    "  + getForegroundProcess(getApplicationContext()));
                //TODO check the selected apps list
                //TODO calculate time spent on each app.
                Date date = Calendar.getInstance().getTime();
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String today = formatter.format(date);
                if(!sharedPreferences.getBoolean("elapsed", false) && sharedPreferences.getString("dailyElapsed", "").equals(today)) {
                    if (getForegroundProcess(getApplicationContext()).contains("com.android.settings")) {
                        if (!Config.isRunning())
                            startService(new Intent(ForegroundProcessService.this, LaunchPasswordService.class));
                    } else if (activatedPackageNames.contains(getForegroundProcess(getApplicationContext()))) {
                        if (elapsedTime * 5 >= selectedTime) {
                            sharedPreferences.edit().putBoolean("elapsed", true).apply();
                            stopRepeatingTask();
                            startService(new Intent(ForegroundProcessService.this, LaunchPasswordService.class));
                            Log.i("tagged", "elapsed time");

                            //TODO start next day
                        } else {
                            elapsedTime++;
                        }
                    } else {
                        Config.setRunning(false);
                    }
                } else if(!sharedPreferences.getString("dailyElapsed", "").equals(today)) {
                    Log.i("tagged", "new Day");
                    sharedPreferences.edit().putBoolean("elapsed", false).commit();
                    sharedPreferences.edit().putString("dailyElapsed", today).commit();
                    elapsedTime = 0;
                }/*else {
                    if(activatedPackageNames.contains(getForegroundProcess(getApplicationContext())))
                        startService(new Intent(ForegroundProcessService.this, LaunchPasswordService.class));
                }*/
            } catch (NullPointerException e) {

            }
            finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    boolean stop;

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
        stop = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static String getForegroundProcess(Context context) {

        String topPackageName = null;
        UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
        if (stats != null) {
            SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                runningTask.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (runningTask.isEmpty()) {
                return null;
            }
            topPackageName = runningTask.get(runningTask.lastKey()).getPackageName();
        }
        if (topPackageName == null) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            context.startActivity(intent);
        }

        return topPackageName;
    }


}
