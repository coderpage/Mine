package com.coderpage.mine.app.tally.ui.dialog;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.v4.app.DialogFragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coderpage.base.utils.ResUtils;
import com.coderpage.framework.BaseViewModel;
import com.coderpage.framework.ViewReliedTask;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.router.TallyRouter;
import com.coderpage.mine.app.tally.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2019-04-07 09:15
 * @since 0.6.0
 */

public class MenuDialogViewModel extends BaseViewModel {

    /** 菜单列表 */
    private MutableLiveData<List<MenuDialogItem>> mMenuList = new MutableLiveData<>();
    private MutableLiveData<ViewReliedTask<DialogFragment>> mViewReliedTask = new MutableLiveData<>();

    public MenuDialogViewModel(Application application) {
        super(application);
        init();
    }

    LiveData<List<MenuDialogItem>> getMenuList() {
        return mMenuList;
    }

    LiveData<ViewReliedTask<DialogFragment>> getViewReliedTask() {
        return mViewReliedTask;
    }

    /** 菜单点击 */
    public void onMenuClick(MenuDialogItem item) {
        if (item == null) {
            return;
        }
        mViewReliedTask.setValue(dialog -> {
            if (dialog.getActivity() == null) {
                return;
            }
            dialog.dismiss();

            switch (item.getPath()) {

                case TallyRouter.CHART:
                case TallyRouter.RECORDS:
                    SecurityUtils.executeAfterFingerprintAuth(dialog.getActivity(), () -> {
                        ARouter.getInstance().build(item.getPath()).navigation(dialog.getActivity());
                    });
                    break;

                default:
                    ARouter.getInstance().build(item.getPath()).navigation(dialog.getActivity());
                    break;
            }
        });
    }

    /** 关闭点击 */
    public void onCloseClick() {
        mViewReliedTask.setValue(DialogFragment::dismiss);
    }

    private void init() {
        List<MenuDialogItem> menuList = new ArrayList<>();
        // 关于
        menuList.add(new MenuDialogItem(
                ResUtils.getString(getApplication(), R.string.menu_tall_about),
                TallyRouter.ABOUT,
                ResUtils.getDrawable(getApplication(), R.drawable.ic_about)));
        // 设置
        menuList.add(new MenuDialogItem(
                ResUtils.getString(getApplication(), R.string.menu_tally_setting),
                TallyRouter.SETTING,
                ResUtils.getDrawable(getApplication(), R.drawable.ic_settings)));
        // 账单记录
        menuList.add(new MenuDialogItem(
                ResUtils.getString(getApplication(), R.string.menu_tally_records),
                TallyRouter.RECORDS,
                ResUtils.getDrawable(getApplication(), R.drawable.ic_list)));
        // 图表
        menuList.add(new MenuDialogItem(
                ResUtils.getString(getApplication(), R.string.menu_tally_chart),
                TallyRouter.CHART,
                ResUtils.getDrawable(getApplication(), R.drawable.ic_chart)));
        mMenuList.setValue(menuList);
    }
}
