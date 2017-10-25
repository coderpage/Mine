package com.coderpage.mine.app.tally.utils;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.R;

import java.util.List;

/**
 * @author lc. 2017-10-18 22:01
 * @since 0.5.1
 */

public class PopupUtils {

    public static ListPopupWindow createPopupMenuWindow(Context context,
                                                        View anchor,
                                                        List<String> titles) {
        ListPopupWindow popupWindow = new ListPopupWindow(context);

        PopupListAdapter popupListAdapter = new PopupListAdapter(context, titles);

        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(popupListAdapter);
        popupWindow.setContentWidth(
                measureIndividualMenuWidth(
                        popupListAdapter, null, context, UIUtils.getWindowSize(context).x / 2));
        popupWindow.setModal(true);

        return popupWindow;
    }

    public interface PopupMenuItemOnclickListener {
        void onItemClick(ListPopupWindow popupWindow, View view, String text, int position);
    }

    public interface InternalPopupMenuItemOnclickListener {
        void onItemClick(View view, String text, int position);
    }

    private static int measureIndividualMenuWidth(ListAdapter adapter, ViewGroup parent,
                                                  Context context, int maxAllowedWidth) {
        // Menus don't tend to be long, so this is more sane than it looks.
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (parent == null) {
                parent = new FrameLayout(context);
            }

            itemView = adapter.getView(i, itemView, parent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();
            if (itemWidth >= maxAllowedWidth) {
                return maxAllowedWidth;
            } else if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }

    private static class PopupListAdapter extends BaseAdapter {

        private List<String> titleList;
        private LayoutInflater inflater;

        private PopupListAdapter(Context context, List<String> titleList) {
            this.titleList = titleList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }

        @Override
        public Object getItem(int position) {
            return titleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            VH vh;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_popup_menu, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.tvText);
                vh = new VH(textView);
                convertView.setTag(vh);
            } else {
                vh = (VH) convertView.getTag();
            }

            vh.textView.setText(titleList.get(position));

            return convertView;
        }

        private class VH {
            private TextView textView;

            private VH(TextView textView) {
                this.textView = textView;
            }
        }

    }
}
