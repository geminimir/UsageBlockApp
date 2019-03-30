package com.app.usageblockapp.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.usageblockapp.R;

public class LegalitiesActivity extends AppCompatActivity {

    private TextView txtWhatItDoes, txtTerms, txtPrivacy;
    private Button btnSubmit;
    private CheckBox chbxTerms, chbxPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legalities);

        txtTerms = findViewById(R.id.txtTerms);
        txtPrivacy = findViewById(R.id.txtPrivacy);
        btnSubmit = findViewById(R.id.btnSubmit);
        chbxTerms = findViewById(R.id.chbxTerms);
        chbxPrivacy = findViewById(R.id.chbxPrivacy);

        txtTerms.setPaintFlags(txtTerms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtPrivacy.setPaintFlags(txtPrivacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        txtPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://momcensor.com/privacy"));
                startActivity(browser);
            }
        });

        txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://momcensor.com/terms"));
                startActivity(browser);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chbxTerms.isChecked() && chbxPrivacy.isChecked()) {

                    Intent mainActivity = new Intent(LegalitiesActivity.this, SetPasswordActivity.class);
                    startActivity(mainActivity);
                    finish();
                }
            }
        });
    }
}
