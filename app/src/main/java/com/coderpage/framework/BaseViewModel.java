package com.coderpage.framework;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.widget.Toast;

/**
 * @author lc. 2018-09-22 23:18
 * @since 0.6.0
 */

public class BaseViewModel extends AndroidViewModel {

    public BaseViewModel(Application application) {
        super(application);
    }

    protected void showTostShort(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showTostLong(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
    }
}
