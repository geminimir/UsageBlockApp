package com.app.usageblockapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.app.usageblockapp.R;
import com.app.usageblockapp.adapters.AppsListAdapter;
import com.app.usageblockapp.config.Config;
import com.app.usageblockapp.models.App;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class SelectAppsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<App> appList  = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_select_apps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.select_apps_title));

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        appList = Config.getAllInstalledApps(getApplicationContext());

        AppsListAdapter appsListAdapter = new AppsListAdapter(getApplicationContext(), appList);
        recyclerView.setAdapter(appsListAdapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_selectapps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_confirm)
            confirmSelections();

        return super.onOptionsItemSelected(item);
    }

    private void confirmSelections() {
        Config.setNotFirstTime(getApplicationContext());
        Config.setAppList(getApplicationContext(), AppsListAdapter.getAppList());
        startActivity(new Intent(SelectAppsActivity.this, MainActivity.class));
        finish();
    }
}
