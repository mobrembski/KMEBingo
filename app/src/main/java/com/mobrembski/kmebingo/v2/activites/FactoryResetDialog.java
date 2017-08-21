package com.mobrembski.kmebingo.v2.activites;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.util.TypedValue;

import com.mobrembski.kmebingo.v2.BitUtils;
import com.mobrembski.kmebingo.v2.R;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.v2.SerialFrames.KMESetDataFrame;
import com.mobrembski.kmebingo.v2.bluetoothmanager.ISerialConnectionManager;

public class FactoryResetDialog extends AppCompatDialog {

    private Activity myView;
    private OnDismissListener listener;
    private OnResetFinishedInterface onResetFinished;
    private ISerialConnectionManager btManager;

    public FactoryResetDialog(Activity parent, final ISerialConnectionManager btManager) {
        super(parent);
        // TODO: Why this is needed? Need to verify.
        setOwnerActivity(parent);
        myView = parent;
        final FactoryResetDialog parentDialog = this;
        onResetFinished = new OnResetFinishedInterface() {
            @Override
            public void emitOnDismiss() {
                parentDialog.emitOnDismiss();
                btManager.postNewRequest(new KMEDataActual(), 1);
                btManager.postNewRequest(new KMEDataSettings(), 1);
                btManager.postNewRequest(new KMEDataConfig(), 1);
            }
        };
        this.btManager = btManager;
    }

    @Override
    public void show() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.dialog_style, typedValue, true);
        new AlertDialog.Builder(myView, typedValue.resourceId)
                .setTitle(R.string.configuration_reset)
                .setMessage(R.string.sure_to_reset)
                .setPositiveButton(R.string.reset_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tryToResetConfiguration();
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        emitOnDismiss();
                    }
                })
                .show();
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        listener = onDismissListener;
    }

    public void emitOnDismiss() {
        if (listener != null)
            listener.onDismiss(this);
    }

    private interface OnResetFinishedInterface {
        void emitOnDismiss();
    }

    private void tryToResetConfiguration() {
        if (!checkIfBTManagerIsConnected())
            return;
        KMEDataActual actual = btManager.runRequestNow(new KMEDataActual());
        if (actual.Ignition) {
            new AlertDialog.Builder(myView)
                    .setTitle(R.string.error)
                    .setMessage(R.string.cannot_reset_on_ignition)
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }
        new resetConfigurationTask().execute(onResetFinished);
    }

    private boolean checkIfBTManagerIsConnected() {
        if (btManager == null || !btManager.isConnected()) {
            new AlertDialog.Builder(myView)
                    .setTitle(R.string.error)
                    .setMessage(R.string.cannot_reset_without_connection)
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return false;
        }
        return true;
    }

    private class resetConfigurationTask extends AsyncTask<OnResetFinishedInterface, Integer, Void> {
        ProgressDialog waitDialog;
        OnResetFinishedInterface listenerToFire;
        KMESetDataFrame framesToSend[] = {
                // region Default values
                new KMESetDataFrame(BitUtils.packFrame(0x1F, 0x33), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x06, 0x6A), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x07, 0x00), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x08, 0x17), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x09, 0x01), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x0A, 0x88), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x0B, 0x0B), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x0C, 0x0B), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x0D, 0xA9), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x0E, 0x64), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x0F, 0x11), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x10, 0xA1), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x11, 0x32), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x12, 0x32), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x13, 0x73), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x14, 0xC8), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x15, 0x1E), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x16, 0x10), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x17, 0x27), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x18, 0x07), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x19, 0x10), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x1A, 0x27), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x1B, 0x24), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x1C, 0xC4), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x1D, 0x09), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x1E, 0x01), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x20, 0x37), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x21, 0x2D), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x22, 0x28), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x23, 0x5F), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x24, 0x89), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x25, 0xCD), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x26, 0x32), 2),
                new KMESetDataFrame(BitUtils.packFrame(0x32, 0x29), 2),
                // endregion
        };

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.progress_dialog_style, typedValue, true);
            waitDialog = new ProgressDialog(myView, typedValue.resourceId);
            waitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitDialog.setMax(framesToSend.length);
            waitDialog.setMessage(getContext().getString(R.string.resetting_please_wait));
            waitDialog.setTitle(R.string.factory_reset);
            waitDialog.show();
        }

        @Override
        protected Void doInBackground(OnResetFinishedInterface... params) {
            listenerToFire = params[0];
            for (int i = 0; i < framesToSend.length; i++) {
                btManager.runRequestNow(framesToSend[i]);
                publishProgress(i + 1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void notUsed) {
            super.onPostExecute(notUsed);
            waitDialog.dismiss();
            if (listenerToFire != null)
                listenerToFire.emitOnDismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            this.waitDialog.setProgress(progress[0]);
        }
    }
}
