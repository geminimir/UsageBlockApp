package com.app.usageblockapp.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.app.usageblockapp.activities.MainActivity;
import com.app.usageblockapp.services.ForegroundProcessService;

import java.util.Calendar;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();

        if (action != null) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, ForegroundProcessService.class));
                } else
                    context.startService(new Intent(context, ForegroundProcessService.class));
                Log.i("booting", "boot complete");
                context.startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                PendingIntent pi = PendingIntent.getService(context, 0,
                        new Intent(context, DailyBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 1);

// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pi);

            }
        }
    }
}
