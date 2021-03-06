package com.coinbase.zxing.client.android.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.coinbase.zxing.client.android.PreferencesActivity;
import com.coinbase.zxing.client.android.common.executor.AsyncTaskExecInterface;
import com.coinbase.zxing.client.android.common.executor.AsyncTaskExecManager;
import java.util.ArrayList;
import java.util.Collection;

final class AutoFocusManager implements AutoFocusCallback {
    private static final long AUTO_FOCUS_INTERVAL_MS = 2000;
    private static final Collection<String> FOCUS_MODES_CALLING_AF = new ArrayList(2);
    private static final String TAG = AutoFocusManager.class.getSimpleName();
    private boolean active;
    private final Camera camera;
    private AutoFocusTask outstandingTask;
    private final AsyncTaskExecInterface taskExec = ((AsyncTaskExecInterface) new AsyncTaskExecManager().build());
    private final boolean useAutoFocus;

    private final class AutoFocusTask extends AsyncTask<Object, Object, Object> {
        private AutoFocusTask() {
        }

        protected Object doInBackground(Object... voids) {
            try {
                Thread.sleep(AutoFocusManager.AUTO_FOCUS_INTERVAL_MS);
            } catch (InterruptedException e) {
            }
            synchronized (AutoFocusManager.this) {
                if (AutoFocusManager.this.active) {
                    AutoFocusManager.this.start();
                }
            }
            return null;
        }
    }

    static {
        FOCUS_MODES_CALLING_AF.add("auto");
        FOCUS_MODES_CALLING_AF.add("macro");
    }

    AutoFocusManager(Context context, Camera camera) {
        this.camera = camera;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String currentFocusMode = camera.getParameters().getFocusMode();
        boolean z = sharedPrefs.getBoolean(PreferencesActivity.KEY_AUTO_FOCUS, true) && FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
        this.useAutoFocus = z;
        Log.i(TAG, "Current focus mode '" + currentFocusMode + "'; use auto focus? " + this.useAutoFocus);
        start();
    }

    public synchronized void onAutoFocus(boolean success, Camera theCamera) {
        if (this.active) {
            this.outstandingTask = new AutoFocusTask();
            this.taskExec.execute(this.outstandingTask, new Object[0]);
        }
    }

    synchronized void start() {
        if (this.useAutoFocus) {
            this.active = true;
            try {
                this.camera.autoFocus(this);
            } catch (RuntimeException re) {
                Log.w(TAG, "Unexpected exception while focusing", re);
            }
        }
    }

    synchronized void stop() {
        if (this.useAutoFocus) {
            try {
                this.camera.cancelAutoFocus();
            } catch (RuntimeException re) {
                Log.w(TAG, "Unexpected exception while cancelling focusing", re);
            }
        }
        if (this.outstandingTask != null) {
            this.outstandingTask.cancel(true);
            this.outstandingTask = null;
        }
        this.active = false;
    }
}
