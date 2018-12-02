package com.coderpage.framework;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.Looper;
import android.widget.Toast;

import com.coderpage.concurrency.MineExecutors;

/**
 * @author lc. 2018-09-22 23:18
 * @since 0.6.0
 */

public class BaseViewModel extends AndroidViewModel {

    public BaseViewModel(Application application) {
        super(application);
    }

    protected void showToastShort(String message) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            MineExecutors.executeOnUiThread(() ->
                    Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showToastLong(String message) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            MineExecutors.executeOnUiThread(() ->
                    Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
        }
    }
}
