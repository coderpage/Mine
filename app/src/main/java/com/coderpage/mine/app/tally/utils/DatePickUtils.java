package com.coderpage.mine.app.tally.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.CalendarView;

import com.coderpage.mine.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

/**
 * @author abner-l. 2017-03-11
 */

public class DatePickUtils {

    public static void showDatePickDialog(Activity activity, final OnDatePickListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            showDatePickDialog_Lollipop(activity, listener);
        } else {
            showDatePickDialog_Low(activity, listener);
        }
    }

    private static void showDatePickDialog_Low(Activity activity, final OnDatePickListener listener) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        MaterialCalendarView materialCalendar = ((MaterialCalendarView) inflater.inflate(
                R.layout.dialog_tally_date_pick, null, false));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog dialog = builder.setTitle("")
                .setView(materialCalendar)
                .setNegativeButton(R.string.cancel, (DialogInterface d, int which) -> {
                    if (listener != null) {
                        listener.onCancelClick(d);
                    }
                })
                .setPositiveButton(R.string.confirm, (DialogInterface dialogInterface, int which) -> {
                    if (listener != null) {
                        listener.onConfirmClick(dialogInterface);
                    }
                })
                .create();

        final String titleDateFormat = activity.getString(R.string.tally_calendar_title_format);
        materialCalendar.setTitleFormatter(
                (CalendarDay day) -> String.format(titleDateFormat, day.getYear(), day.getMonth() + 1));
        materialCalendar.state().edit().setMaximumDate(Calendar.getInstance()).commit();
        materialCalendar.setOnDateChangedListener(
                (@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) -> {
                    if (listener != null) {
                        listener.onDatePick(dialog, date.getYear(), date.getMonth(), date.getDay());
                    }
                });
        dialog.show();
    }

    private static void showDatePickDialog_Lollipop(Activity activity,
                                                    final OnDatePickListener listener) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        CalendarView calendarView = ((CalendarView) inflater.inflate(
                R.layout.dialog_tally_date_pick, null, false));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog dialog = builder.setTitle("")
                .setView(calendarView)
                .setNegativeButton(R.string.cancel, (DialogInterface d, int which) -> {
                    if (listener != null) {
                        listener.onCancelClick(d);
                    }
                })
                .setPositiveButton(R.string.confirm, (DialogInterface dialogInterface, int which) -> {
                    if (listener != null) {
                        listener.onConfirmClick(dialogInterface);
                    }
                })
                .create();

        calendarView.setMaxDate(System.currentTimeMillis());
        calendarView.setOnDateChangeListener(
                (@NonNull CalendarView view, int year, int month, int dayOfMonth) -> {
                    if (listener != null) {
                        listener.onDatePick(dialog, year, month, dayOfMonth);
                    }
                });
        dialog.show();
    }

    public static class OnDatePickListener {
        public void onDatePick(DialogInterface dialog, int year, int month, int dayOfMonth) {
        }

        public void onCancelClick(DialogInterface dialog) {
        }

        public void onConfirmClick(DialogInterface dialog) {
        }
    }
}
