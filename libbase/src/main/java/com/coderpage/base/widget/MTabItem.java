package com.coderpage.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.View;

import com.coderpage.base.R;


/**
 * @author : liuchao
 *         created on 2018/8/7 上午10:51
 *         description : {@link MTabLayout}
 */
public class MTabItem extends View {
    final CharSequence mText;
    final Drawable mIcon;
    final int mCustomLayout;

    public MTabItem(Context context) {
        this(context, null);
    }

    public MTabItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabItem);
        mText = typedArray.getText(R.styleable.MTabItem_android_text);
        mIcon = getDrawable(context, typedArray, R.styleable.MTabItem_android_icon);
        mCustomLayout = typedArray.getResourceId(R.styleable.MTabItem_android_layout, 0);

        typedArray.recycle();
    }

    public Drawable getDrawable(Context context, TypedArray typedArray, int index) {
        if (typedArray.hasValue(index)) {
            final int resourceId = typedArray.getResourceId(index, 0);
            if (resourceId != 0) {
                return AppCompatResources.getDrawable(context, resourceId);
            }
        }
        return typedArray.getDrawable(index);
    }
}
