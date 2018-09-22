package com.coderpage.mine.app.tally.module.main;

import com.coderpage.framework.PresenterImpl;

/**
 * @author abner-l. 2017-04-16
 */

class MainPresenter extends PresenterImpl {

    MainPresenter(MainModel model, MainActivity view,
                  MainModel.MainUserActionEnum[] validUserActions,
                  MainModel.MainQueryEnum[] initialQueries) {
        super(model, view, validUserActions, initialQueries);
    }
}
