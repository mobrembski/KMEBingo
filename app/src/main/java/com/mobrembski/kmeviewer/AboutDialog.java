package com.mobrembski.kmeviewer;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/*
* This class is made only for compatibility.
* AlertDialog.Builder doesn't allow to set custom
* content with API level less than 21. Currently,
* we're using 11 so this small class is required.
* */
public class AboutDialog extends Dialog {

    public AboutDialog(Context context) {
        super(context, R.style.AboutTheme);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        setContentView(R.layout.about_dialog);
        final ImageView logo = (ImageView)findViewById(R.id.about_logo);
        final Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.about_logo);
        final TextView version = (TextView)findViewById(R.id.about_version);
        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(
                    getContext().getPackageName(), 0);
            String versionStr = pInfo.versionName;
            version.setText("v" + versionStr);
        } catch (Exception e) {
            version.setText("");
        }

        logo.startAnimation(anim);
    }
}
