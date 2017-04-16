package com.coderpage.framework;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author abner-l. 2017-04-12
 * @since 0.1.0
 */

public interface UpdatableView<M, Q extends QueryEnum, UA extends UserActionEnum> {

    void displayData(M model, Q query);

    void displayErrorMessage(Q query);

    void displayUserActionResult(M model, Bundle args, UA userAction, boolean success);

    Uri getDataUri(Q query);

    Context getContext();

    void addListener(UserActionListener listener);

    interface UserActionListener<UA extends UserActionEnum> {

        public void onUserAction(UA action, @Nullable Bundle args);
    }
}
