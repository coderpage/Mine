package com.coderpage.framework.plugin;

import android.graphics.Bitmap;

/**
 * @author abner-l. 2017-01-22
 */

public interface Plugin {

    String name();

    Bitmap icon();

    void open();
}
