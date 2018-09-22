package com.coderpage.framework;

/**
 * @author abner-l. 2017-04-09
 */

public interface Presenter<Q extends QueryEnum, UA extends UserActionEnum> {

    void loadInitialQueries();
}
