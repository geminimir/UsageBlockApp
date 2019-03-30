package com.app.usageblockapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.usageblockapp.activities.MainActivity;
import com.app.usageblockapp.config.Config;


public class DialPadBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i("receiver", "launched");
        String phoneNumber = getResultData();
        if (phoneNumber == null) {
            // No reformatted number, use the original
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            String password = sharedPreferences.getString("password", "1234");
            if (phoneNumber.equals("#" + password + "#")) {
                setResultData(null);
                Config.showAppIcon(context);
                // Start my app
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else abortBroadcast();
        }
    }
}
