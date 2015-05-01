package com.mobrembski.kmeviewer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

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
    }
}
