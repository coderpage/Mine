package com.coderpage.mine.app.tally.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.CalendarView;

import com.coderpage.mine.R;

/**
 * @author abner-l. 2017-03-11
 */

public class DatePickUtils {

    public static void showDatePickDialog(Activity activity, final OnDatePickListener listener) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        CalendarView calendar = (CalendarView) inflater.inflate(R.layout.dialog_tally_date_pick, null);
        calendar.setMaxDate(System.currentTimeMillis());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog dialog = builder.setTitle("")
                .setView(calendar)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onCancelClick(dialog);
                        }
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onConfirmClick(dialog);
                        }
                    }
                })
                .create();
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if (listener != null) {
                    listener.onDatePick(dialog, view, year, month, dayOfMonth);
                }
            }
        });
        dialog.show();
    }

    public static class OnDatePickListener {
        public void onDatePick(DialogInterface dialog, @NonNull CalendarView view, int year, int month, int dayOfMonth) {
        }

        public void onCancelClick(DialogInterface dialog) {
        }

        public void onConfirmClick(DialogInterface dialog) {
        }
    }
}
