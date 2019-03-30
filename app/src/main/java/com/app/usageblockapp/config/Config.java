package com.app.usageblockapp.config;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.app.usageblockapp.activities.SplashScreenActivity;
import com.app.usageblockapp.models.App;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static boolean running = false;

    public static final int CALLS_PERMISSION_REQUEST_CODE = 101;

    public static void hideAppIcon(Context context) {
        PackageManager p = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, SplashScreenActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public static void showAppIcon(Context context) {
        PackageManager p = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, SplashScreenActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void setNotFirstTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firsttime", false);
        editor.apply();
    }

    public static boolean isFirstTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("firsttime", true);
    }

    public static void setAppList(Context context, List<App> AppList){
        Gson gson = new Gson();
        String jsonApps = gson.toJson(AppList);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jsonApps", jsonApps).apply();
    }

    public static List<App> getAppList(Context context) {
        List<App> apps;
        SharedPreferences mPrefs = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("jsonApps", "");
        if (json.isEmpty()) {
            apps = new ArrayList<App>();
        } else {
            Type type = new TypeToken<List<App>>() {
            }.getType();
            apps = gson.fromJson(json, type);
        }
        return apps;
    }

    public static List<App> getActivatedAppList(Context context) {
        List<App> appList = new ArrayList<>();
        for(App app : getAppList(context)) {
            if(app.isActivated())
                appList.add(app);
        }
        return appList;
    }

    public static List<App> getAllInstalledApps(Context context) {

        List<PackageInfo> apps = context.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
        List<App> appList = new ArrayList<>();

        for (int i = 0; i < apps.size(); i++) {
            PackageInfo p = apps.get(i);
            if (!isSystemPackage(p) && !p.packageName.equals(context.getPackageName())) {
                String name = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
                String packageName = p.packageName;
                App app = new App(name, packageName, false);
                appList.add(app);
            }
        }
        return appList;
    }

    private static boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Config.running = running;
    }

}
