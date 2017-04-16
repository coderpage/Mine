package com.coderpage.framework;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author abner-l. 2017-04-09
 */

public interface Model<Q extends QueryEnum, UA extends UserActionEnum> {
    Q[] getQueries();

    UA[] getUserActions();

    void deliverUserAction(UA action, @Nullable Bundle args, UserActionCallback callback);

    void requestData(Q query, DataQueryCallback callback);

    void cleanUp();

    interface DataQueryCallback<M extends Model, Q extends QueryEnum> {
        void onModelUpdated(M model, Q query);

        void onError(Q query);
    }

    interface UserActionCallback<M extends Model, UA extends UserActionEnum> {
        void onModelUpdated(M model, UA userAction);

        void onError(UA userAction);
    }
}
