package com.app.usageblockapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.app.usageblockapp.activities.DialogPasswordActivity;

public class LaunchPasswordService extends Service {
    public LaunchPasswordService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showPasswordScreen();
    }

    private void showPasswordScreen() {
        startActivity(new Intent(LaunchPasswordService.this, DialogPasswordActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
