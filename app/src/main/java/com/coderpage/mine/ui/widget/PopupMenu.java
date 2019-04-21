package com.coderpage.mine.ui.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2019-04-20 18:17
 * @since 0.6.0
 */


public class PopupMenu {
    private int mOffsetX;
    private int mOffsetY;

    private Activity mActivity;
    private OnItemClickListener mItemClickListener;
    private List<MenuItem> mMenuList = new ArrayList<>();
    private OffsetInterceptor mOffsetInterceptor = new DefaultOffsetInterceptor();

    public PopupMenu(Activity activity) {
        mActivity = activity;
        mOffsetX = 0;
        mOffsetY = UIUtils.dp2px(activity, 12);
    }

    public PopupMenu setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
        return this;
    }

    public PopupMenu addMenu(int id, @DrawableRes int iconRes, String text) {
        MenuItem item = new MenuItem();
        item.setId(id);
        item.setIcon(iconRes != 0 ? ResUtils.getDrawable(mActivity, iconRes) : null);
        item.setText(text);
        mMenuList.add(item);
        return this;
    }

    public PopupMenu setOffsetInterceptor(OffsetInterceptor interceptor) {
        mOffsetInterceptor = interceptor;
        return this;
    }

    public void show(View anchor) {
        final PopupWindow popupWindow = new PopupWindow(mActivity);

        int itemHeight = UIUtils.dp2px(mActivity, 45);
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        LinearLayout containerLy = (LinearLayout) inflater.inflate(R.layout.layout_popup_menu, null);
        for (int i = 0; i < mMenuList.size(); i++) {
            View itemView = inflater.inflate(R.layout.layout_popup_menu_item, null);
            ImageView iconIv = itemView.findViewById(R.id.iv_icon);
            TextView textTv = itemView.findViewById(R.id.tv_text);
            View divider = itemView.findViewById(R.id.view_divider_line);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
            containerLy.addView(itemView, lp);

            final MenuItem item = mMenuList.get(i);
            textTv.setText(item.getText());
            if (item.getIcon() != null) {
                iconIv.setImageDrawable(item.getIcon());
            } else {
                iconIv.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onClick(popupWindow, item);
                }
            });

            if (i == mMenuList.size() - 1) {
                divider.setVisibility(View.GONE);
            }

        }

        containerLy.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int anchorWidth = anchor.getWidth();
        int anchorHeight = anchor.getHeight();
        int popWidth = containerLy.getMeasuredWidth() + 20;
        int popHeight = containerLy.getMeasuredHeight();

        popWidth = Math.max(popWidth, UIUtils.dp2px(mActivity, 100) + 20);

        int xOff = mOffsetInterceptor.offsetX(popWidth, popHeight, anchorWidth, anchorHeight);
        int yOff = mOffsetInterceptor.offsetY(popWidth, popHeight, anchorWidth, anchorHeight);

        popupWindow.setWidth(popWidth);
        popupWindow.setHeight(popHeight);
        popupWindow.setContentView(containerLy);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(anchor, xOff, yOff);
    }

    private class DefaultOffsetInterceptor extends OffsetInterceptor {
    }

    public static abstract class OffsetInterceptor {

        /**
         * 返回 x 方向偏移
         *
         * @param popWidth     弹出框宽度
         * @param popHeight    弹出框高度
         * @param anchorWidth  锚点view宽
         * @param anchorHeight 锚点view高
         * @return x方向偏移
         */
        public int offsetX(int popWidth, int popHeight, int anchorWidth, int anchorHeight) {
            // 10 阴影宽度
            return -popWidth + anchorWidth - 10;
        }

        /**
         * 返回 y 方向偏移
         *
         * @param popWidth     弹出框宽度
         * @param popHeight    弹出框高度
         * @param anchorWidth  锚点view宽
         * @param anchorHeight 锚点view高
         * @return y方向偏移
         */
        public int offsetY(int popWidth, int popHeight, int anchorWidth, int anchorHeight) {
            return 0;
        }
    }

    public interface OnItemClickListener {
        /**
         * 菜单 ITEM 点击回调
         *
         * @param popupWindow popup window
         * @param item        item
         */
        void onClick(PopupWindow popupWindow, MenuItem item);
    }

    public class MenuItem {
        private int id;
        private Drawable icon;
        private String text;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
