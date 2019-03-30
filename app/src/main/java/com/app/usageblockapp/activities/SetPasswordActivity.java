package com.app.usageblockapp.activities;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.usageblockapp.R;
import com.app.usageblockapp.receivers.AdminClassReceiver;


public class SetPasswordActivity extends AppCompatActivity {

    EditText edtPassword, edtRepeatPassword;
    Button btnConfirm;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        edtPassword = findViewById(R.id.edtPassword);
        edtRepeatPassword = findViewById(R.id.edtRepeatPassword);
        btnConfirm = findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edtPassword.getText().toString();
                String repeatPassword = edtRepeatPassword.getText().toString();

                if(password.equals(repeatPassword)) {
                    editor.putString("password", password);
                    editor.apply();
                    if(!AdminClassReceiver.isDeviceAdmin(getApplicationContext())) {
                        ComponentName compName = new ComponentName(getApplicationContext(), AdminClassReceiver.class);
                        Intent deviceIntent = new Intent(DevicePolicyManager
                                .ACTION_ADD_DEVICE_ADMIN);
                        deviceIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                compName);
                        startActivityForResult(deviceIntent, 0);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Passwords must match.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 0) {
            Intent main = new Intent(SetPasswordActivity.this, SelectAppsActivity.class);
            startActivity(main);
            finish();
        }
    }
}
