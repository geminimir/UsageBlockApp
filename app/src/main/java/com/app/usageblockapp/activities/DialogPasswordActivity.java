package com.app.usageblockapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.usageblockapp.R;
import com.app.usageblockapp.config.Config;
import com.app.usageblockapp.services.ForegroundProcessService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DialogPasswordActivity extends AppCompatActivity {

    private boolean launched = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_password);
        if(!launched)
            showDialog();
    }

    private void showDialog() {
        launched = true;
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.app_name));
        alertDialog.setMessage("You have exceeded the time limit for today. Please enter password to continue usage.");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setCancelable(false);
        final EditText edtPassword = new EditText(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        edtPassword.setLayoutParams(lp);
        edtPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialog.setView(edtPassword);

        final SharedPreferences sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        final String password = sharedPreferences.getString("password", "1234");

        alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edtPassword.getText().toString().equals(password)) {
                    ForegroundProcessService.elapsedTime = 0;
                    //startActivity(new Intent(DialogPasswordActivity.this, ForegroundProcessService.class));
                    Date date = Calendar.getInstance().getTime();
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String today = formatter.format(date);
                    Log.i("tagged", today);
                    sharedPreferences.edit().putString("dailyElapsed", today).apply();
                    DialogPasswordActivity.this.finish();
                } else
                    showDialog();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Config.setRunning(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Config.setRunning(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDialog();
    }
}
