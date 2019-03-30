package com.app.usageblockapp.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import com.app.usageblockapp.R;
import com.app.usageblockapp.config.Config;
import com.app.usageblockapp.models.App;

import java.util.List;

public class AppsListAdapter extends RecyclerView.Adapter<AppsListAdapter.ViewHolder> {


    private Context context;
    private static List<App> appList;

    public AppsListAdapter(Context context, List<App> appList) {
        this.context = context;
        this.appList = appList;
        if(!Config.getAppList(context).isEmpty())
            this.appList = Config.getAppList(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.app_item_layout, parent, false);
        return new AppsListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final App app = appList.get(position);

        holder.txtAppName.setText(app.getName());
        holder.swActivate.setChecked(app.isActivated());
        try {
            holder.imgIcon.setImageDrawable(getAppIcon(app.getPackageName()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        holder.swActivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                app.setActivated(isChecked);
                appList.set(appList.indexOf(app), app);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static List<App> getAppList() {
        return appList;
    }
    @Override
    public int getItemCount() {
        return appList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtAppName;
        private ImageView imgIcon;
        private Switch swActivate;

        public ViewHolder(View itemView) {
            super(itemView);

            txtAppName = itemView.findViewById(R.id.txtAppName);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            swActivate = itemView.findViewById(R.id.swActivate);
        }
    }

    private Drawable getAppIcon(String packageName) throws PackageManager.NameNotFoundException{
        PackageManager pm = context.getPackageManager();
        Drawable icon = pm.getApplicationIcon(packageName);
        return icon;
    }
}
