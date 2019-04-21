package com.coderpage.mine.app.tally.module.setting;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coderpage.base.common.IError;
import com.coderpage.base.utils.FileUtils;
import com.coderpage.base.utils.ResUtils;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.framework.Presenter;
import com.coderpage.framework.PresenterImpl;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.router.TallyRouter;
import com.coderpage.mine.app.tally.module.backup.Backup;
import com.coderpage.mine.app.tally.module.backup.BackupModelMetadata;
import com.coderpage.mine.ui.BaseActivity;
import com.joker.annotation.PermissionsDenied;
import com.joker.annotation.PermissionsGranted;
import com.joker.api.Permissions4M;

import java.io.File;
import java.util.Date;
import java.util.List;

import static com.coderpage.base.utils.LogUtils.LOGE;
import static com.coderpage.base.utils.LogUtils.makeLogTag;
import static com.coderpage.mine.app.tally.module.setting.SettingModel.ACTION_CODE_FINISH;
import static com.coderpage.mine.app.tally.module.setting.SettingModel.EXTRA_ACTION_CODE;
import static com.coderpage.mine.app.tally.module.setting.SettingModel.EXTRA_FILE_PATH;
import static com.coderpage.mine.app.tally.module.setting.SettingModel.EXTRA_MESSAGE;
import static com.coderpage.mine.app.tally.module.setting.SettingModel.SettingQueryEnum;
import static com.coderpage.mine.app.tally.module.setting.SettingModel.SettingUserActionEnum;

/**
 * @author abner-l. 2017-06-01
 */

@Route(path = TallyRouter.SETTING)
public class SettingActivity extends BaseActivity
        implements UpdatableView<SettingModel, SettingQueryEnum, SettingUserActionEnum, IError> {

    private static final String TAG = makeLogTag(SettingActivity.class);

    /** 申请写文件权限 CODE */
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    /** 申请读文件权限 CODE */
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;

    private Dialog mBackupProgressDialog;

    private Presenter mPresenter;
    private SettingModel mModel;
    private UserActionListener mUserActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        initPresenter();
    }

    private void initView() {
        findViewById(R.id.lyDataExport).setOnClickListener(mOnClickListener);
        findViewById(R.id.lyDataImport).setOnClickListener(mOnClickListener);
    }

    private void initPresenter() {
        mModel = new SettingModel(getContext());
        mPresenter = new PresenterImpl<>(
                mModel,
                this, SettingUserActionEnum.values(),
                SettingQueryEnum.values());
        mPresenter.loadInitialQueries();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsBack(view -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String path = FileUtils.getPath(this, uri);
            onBackupFileSelectedFromFileSystem(path);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Permissions4M.onRequestPermissionsResult(this, requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mModel.cleanUp();
    }

    @Override
    public void displayData(SettingModel model, SettingQueryEnum query) {

    }

    @Override
    public void displayErrorMessage(SettingQueryEnum query, IError error) {

    }

    @Override
    public void displayUserActionResult(SettingModel model,
                                        Bundle args,
                                        SettingUserActionEnum userAction,
                                        boolean success,
                                        IError error) {
        switch (userAction) {
            case BACKUP_TO_JSON_FILE:
                if (success) {
                    int actionCode = args.getInt(EXTRA_ACTION_CODE, 0);
                    if (actionCode != ACTION_CODE_FINISH) {
                        updateBackupProgressMessage(args.getString(EXTRA_MESSAGE, ""));
                    } else {
                        updateBackupProgressMessage(ResUtils.getString(
                                getContext(), R.string.tally_alert_backup_success));
                        dismissBackupProgressDialog();
                        UIUtils.showToastShort(
                                getContext(), R.string.tally_alert_backup_success);
                    }
                } else {
                    updateBackupProgressMessage(ResUtils.getString(
                            getContext(), R.string.tally_alert_backup_failure));
                    dismissBackupProgressDialog();
                    UIUtils.showToastShort(
                            getContext(), R.string.tally_alert_backup_failure);
                }
                break;

            case READ_BACKUP_JSON_FILE:
                if (success) {
                    int actionCode = args.getInt(EXTRA_ACTION_CODE, 0);
                    LOGE(TAG, "READ_BACKUP_JSON_FILE SUCCESS CODE=" + actionCode + " message=" + args.getString(EXTRA_MESSAGE));
                    if (actionCode != ACTION_CODE_FINISH) {
                        updateBackupProgressMessage(args.getString(EXTRA_MESSAGE, ""));
                    } else {
                        updateBackupProgressMessage(ResUtils.getString(
                                getContext(), R.string.tally_alert_read_data_success));
                        dismissBackupProgressDialog();
                        LOGE(TAG, "READ BACKUP FILE SUCCESS");
                        showRestoreDataConfirmDialog(model);
                    }
                } else {
                    updateBackupProgressMessage(ResUtils.getString(
                            getContext(), R.string.tally_alert_read_data_failure));
                    dismissBackupProgressDialog();
                    UIUtils.showToastShort(
                            getContext(), R.string.tally_alert_read_data_failure);
                }
                break;

            case RESTORE_TO_DB_WITH_BACKUP_MODEL:
                if (success) {
                    int actionCode = args.getInt(EXTRA_ACTION_CODE, 0);
                    if (actionCode != ACTION_CODE_FINISH) {
                        updateBackupProgressMessage(args.getString(EXTRA_MESSAGE, ""));
                    } else {
                        updateBackupProgressMessage(ResUtils.getString(
                                getContext(), R.string.tally_alert_restore_data_success));
                        dismissBackupProgressDialog();
                        UIUtils.showToastShort(
                                getContext(), R.string.tally_alert_restore_data_success);
                    }
                } else {
                    updateBackupProgressMessage(ResUtils.getString(
                            getContext(), R.string.tally_alert_restore_data_failure));
                    dismissBackupProgressDialog();
                    UIUtils.showToastShort(
                            getContext(), R.string.tally_alert_restore_data_failure);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public Uri getDataUri(SettingQueryEnum query) {
        return null;
    }

    @Override
    public void addListener(UserActionListener listener) {
        mUserActionListener = listener;
    }

    @Override
    public Context getContext() {
        return this;
    }

    /****** 权限申请 ******/
    @PermissionsGranted({REQUEST_CODE_READ_EXTERNAL_STORAGE, REQUEST_CODE_WRITE_EXTERNAL_STORAGE})
    public void onPermissionsGranted(int code) {
        switch (code) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE:
                showBackupFileSelectDialog();
                break;
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                showBackupProgressDialog(ResUtils.getString(
                        getContext(), R.string.tally_alert_backup));
                mUserActionListener.onUserAction(
                        SettingUserActionEnum.BACKUP_TO_JSON_FILE, new Bundle());
                break;
            default:
                break;
        }
    }

    @PermissionsDenied({REQUEST_CODE_READ_EXTERNAL_STORAGE, REQUEST_CODE_WRITE_EXTERNAL_STORAGE})
    public void onPermissionsDenied(int code) {
        switch (code) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE:
                UIUtils.showToastShort(SettingActivity.this,
                        R.string.permission_request_failed_read_external_storage);
                break;
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                UIUtils.showToastShort(SettingActivity.this,
                        R.string.permission_request_failed_write_external_storage);
                break;
            default:
                break;
        }
    }

    private View.OnClickListener mOnClickListener = (view) -> {
        int id = view.getId();
        switch (id) {
            case R.id.lyDataExport:
                Permissions4M.get(SettingActivity.this)
                        .requestForce(true)
                        .requestUnderM(false)
                        .requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .requestCodes(REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
                        .request();
                break;
            case R.id.lyDataImport:
                Permissions4M.get(SettingActivity.this)
                        .requestForce(true)
                        .requestUnderM(false)
                        .requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .requestCodes(REQUEST_CODE_READ_EXTERNAL_STORAGE)
                        .request();
                break;
            default:
                break;
        }
    };

    private void showBackupProgressDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_simple_progress);
        mBackupProgressDialog = builder.create();
        mBackupProgressDialog.setCanceledOnTouchOutside(false);
        mBackupProgressDialog.show();
        updateBackupProgressMessage(message);
    }

    private void updateBackupProgressMessage(String message) {
        if (mBackupProgressDialog != null && mBackupProgressDialog.isShowing()) {
            TextView messageTv = (TextView) mBackupProgressDialog.findViewById(R.id.tvText);
            messageTv.setText(message);
        }
    }

    private void dismissBackupProgressDialog() {
        if (mBackupProgressDialog != null && mBackupProgressDialog.isShowing()) {
            mBackupProgressDialog.dismiss();
        }
    }

    /**
     * 处理从文件管理器选择的备份文件。
     *
     * @param filePath 文件路径。
     */
    private void onBackupFileSelectedFromFileSystem(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(this, R.string.tally_toast_illegal_path, Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            Toast.makeText(this, R.string.tally_toast_illegal_path, Toast.LENGTH_SHORT).show();
            return;
        }

        showBackupProgressDialog(ResUtils.getString(
                getContext(), R.string.tally_alert_reading_backup_file));
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_FILE_PATH, filePath);
        mUserActionListener.onUserAction(SettingUserActionEnum.READ_BACKUP_JSON_FILE, bundle);
    }

    private void showBackupFileSelectDialog() {
        List<File> fileList = Backup.listBackupFiles(getBaseContext());

        String[] fileItems = new String[fileList.size()];
        for (int i = 0; i < fileItems.length; i++) {
            fileItems[i] = fileList.get(i).getName();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setItems(fileItems, (dialog, which) -> {
            dialog.dismiss();
            showBackupProgressDialog(ResUtils.getString(
                    getContext(), R.string.tally_alert_reading_backup_file));
            Bundle args = new Bundle();
            args.putString(EXTRA_FILE_PATH, fileList.get(which).getAbsolutePath());
            mUserActionListener.onUserAction(SettingUserActionEnum.READ_BACKUP_JSON_FILE, args);
        });
        builder.setPositiveButton(
                R.string.dialog_btn_choose_local_file, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 1);
                });
        builder.setNegativeButton(R.string.dialog_btn_cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void showRestoreDataConfirmDialog(SettingModel model) {
        BackupModelMetadata metadata = model.getBackupModel().getMetadata();
        String backupDate = new Date(metadata.getBackupDate()).toLocaleString();
        String backupDeviceName = metadata.getDeviceName();
        String backupExpenseCount = String.valueOf(metadata.getExpenseNumber());
        String backupVersion = metadata.getClientVersion() + "(" + metadata.getClientVersionCode() + ")";

        View view = getLayoutInflater().inflate(R.layout.dialog_tally_restore_data_confirm, null);
        ((TextView) view.findViewById(R.id.tvBackupDate)).setText(backupDate);
        ((TextView) view.findViewById(R.id.tvBackupDeviceName)).setText(backupDeviceName);
        ((TextView) view.findViewById(R.id.tvBackupVersion)).setText(backupVersion);
        ((TextView) view.findViewById(R.id.tvBackupExpenseCount)).setText(backupExpenseCount);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tally_alert_restore_data)
                .setView(view)
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    mModel.cleanUp();
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    dialog.dismiss();
                    showBackupProgressDialog(ResUtils.getString(
                            getContext(), R.string.tally_alert_restore_data));
                    mUserActionListener.onUserAction(
                            SettingUserActionEnum.RESTORE_TO_DB_WITH_BACKUP_MODEL, new Bundle());
                });
        AlertDialog confirmDialog = builder.create();
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.show();
    }


}
