package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.mobrembski.kmeviewer.SerialFrames.KMEFrame;

public class RegistrationPlateChangeDialog extends Dialog {
    private String actualRegistration;

    public RegistrationPlateChangeDialog(Activity a,
                                         String actualRegistration) {
        super(a);
        this.actualRegistration = actualRegistration;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regplate_change_dialog);
        final EditText regEdit = (EditText) findViewById(R.id.registrationplateedit);
        regEdit.setText(this.actualRegistration);
        Button cancelBtn = (Button) this.findViewById(R.id.RegPlateDialogCancelBtn);
        Button okBtn = (Button) this.findViewById(R.id.RegPlateDialogOkBtn);
        final Dialog thisDialog = this;
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thisDialog.dismiss();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeRegistrationPlate(regEdit.getText().toString());
            }
        });
    }

    private void changeRegistrationPlate(String newPlate) {
        char tab[] = newPlate.toCharArray();
        for (int i = 0; i < tab.length; i++) {
            byte[] frameByte = new byte[4];
            frameByte[0] = 0x65;
            frameByte[1] = (byte) (0x2B + i);
            frameByte[2] = (byte) tab[i];
            frameByte[3] = 0;
            frameByte[3] = (byte) BluetoothController.getCRC(frameByte);
            try {
                KMEFrame tmp = new KMEFrame(frameByte);
                BluetoothController.getInstance().askForFrame(tmp);
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.dismiss();
            }
        }
        this.dismiss();
    }
}