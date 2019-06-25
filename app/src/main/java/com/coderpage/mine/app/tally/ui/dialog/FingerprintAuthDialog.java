package com.coderpage.mine.app.tally.ui.dialog;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.utils.CryptoObjectUtil;

/**
 * @author lc. 2019-06-25 19:51
 * @since 0.7.0
 */
public class FingerprintAuthDialog extends BaseDialog {

    private boolean mIsAuthing;
    private boolean mIsAuthSuccess;

    private Listener mListener;
    private AppCompatImageView mFingerPrintIv;
    private TextView mFingerPrintAuthTipTv;

    private FingerprintManagerCompat mFingerprintManager;
    private CancellationSignal mCancellationSignal;
    private CryptoObjectUtil mCryptoObjectHelper;

    public FingerprintAuthDialog(Activity activity) {
        super(activity, R.style.Widget_Dialog_BottomSheet);
        mFingerprintManager = FingerprintManagerCompat.from(activity);
        mCancellationSignal = new CancellationSignal();
        mCryptoObjectHelper = new CryptoObjectUtil();
    }

    public FingerprintAuthDialog setListener(Listener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public View initView(Activity activity) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_fingerprint_auth, null, false);
        contentView.findViewById(R.id.ivClose).setOnClickListener(v -> {
            dismiss();
        });
        mFingerPrintIv = contentView.findViewById(R.id.ivFingerprint);
        mFingerPrintAuthTipTv = contentView.findViewById(R.id.tvPleaseVerifyFingerprint);

        setOnDismissListener(dialog -> {
            if (mListener != null) {
                mListener.onAuthFinish(mIsAuthSuccess);
            }
        });
        return contentView;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        boolean hardwareDetected = mFingerprintManager.isHardwareDetected();
        if (!hardwareDetected) {
            mFingerPrintAuthTipTv.setText(R.string.fingerprint_auth_err_no_hardware);
            showErrorStyle();
            return;
        }

        KeyguardManager keyguardManager = (KeyguardManager) getContext().getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager == null || !keyguardManager.isKeyguardSecure()) {
            mFingerPrintAuthTipTv.setText(R.string.fingerprint_auth_err_no_screen_lock);
            showErrorStyle();
            return;
        }

        if (!mFingerprintManager.hasEnrolledFingerprints()) {
            mFingerPrintAuthTipTv.setText(R.string.fingerprint_auth_err_no_fingerprint);
            showErrorStyle();
            return;
        }

        mIsAuthing = true;
        mFingerprintManager.authenticate(mCryptoObjectHelper.buildCryptoObject(), 0, mCancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                super.onAuthenticationError(errMsgId, errString);
                mFingerPrintAuthTipTv.setText(R.string.fingerprint_auth_tip);
                showErrorStyle();
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                super.onAuthenticationHelp(helpMsgId, helpString);
                mFingerPrintAuthTipTv.setText(helpString);
                showErrorStyle();
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                mIsAuthSuccess = true;
                dismiss();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                mFingerPrintAuthTipTv.setText(R.string.fingerprint_auth_tip);
                showErrorStyle();
            }
        }, null);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mIsAuthing && !mCancellationSignal.isCanceled()) {
            mCancellationSignal.cancel();
        }
    }

    private void showErrorStyle() {
        mFingerPrintIv.setImageResource(R.drawable.ic_fingerprint_error);
        mFingerPrintAuthTipTv.setTextColor(ResUtils.getColor(getContext(), R.color.warning));
    }

    public interface Listener {
        /**
         * 验证通过回调
         *
         * @param success 是否验证成功
         */
        void onAuthFinish(boolean success);
    }
}
