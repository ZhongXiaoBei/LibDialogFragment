package com.rhino.dialog.picker;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.rhino.dialog.R;
import com.rhino.dialog.base.BaseSimpleDialogFragment;
import com.rhino.wheel.WheelView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * @author LuoLin
 * @since Create on 2018/10/26.
 */
public class DatePickerDialogFragment extends BaseSimpleDialogFragment {

    public static final long MILLISECONDS_PER_YEAR = 31536000000L;

    public static final int STYLE_YYYY_MM_DD_HH_MM_SS = 1;
    public static final int STYLE_YYYY_MM_DD_HH_MM = 2;
    public static final int STYLE_YYYY_MM_DD = 3;
    public static final int STYLE_HH_MM_SS = 4;
    public static final int STYLE_HH_MM = 5;
    public int mStyle = STYLE_YYYY_MM_DD_HH_MM_SS;

    public WheelView mWvYear, mWvMonth, mWvDay, mWvHour, mWvMinute, mWvSecond;
    public String[] yearArr;

    public DatePickerDialogFragment() {
        setTitle("Choose time");
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AnimationTranBottomDialog);
        setWindowGravity(Gravity.BOTTOM);
        setWindowWidth(WindowManager.LayoutParams.MATCH_PARENT);
//        setTitleVisibility(View.GONE);
    }

    @Override
    protected void setContent() {
        setContentView(R.layout.widget_date_picker_dialog);
    }

    @Override
    protected boolean initData() {
        return true;
    }

    @Override
    protected void initView() {
        mLlDialog.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mLlDialog.setBackgroundColor(Color.WHITE);
        mWvYear = findSubViewById(R.id.wv_year);
        mWvMonth = findSubViewById(R.id.wv_month);
        mWvDay = findSubViewById(R.id.wv_day);
        mWvHour = findSubViewById(R.id.wv_hour);
        mWvMinute = findSubViewById(R.id.wv_minute);
        mWvSecond = findSubViewById(R.id.wv_second);
        init();
    }

    public void init() {
        switch (mStyle) {
            case STYLE_YYYY_MM_DD_HH_MM_SS:
                initDefaultYear();
                initDefaultMoth();
                initDefaultDay();
                initDefaultHour();
                initDefaultMinute();
                initDefaultSecond();
                break;
            case STYLE_YYYY_MM_DD_HH_MM:
                mWvSecond.setVisibility(View.GONE);
                initDefaultYear();
                initDefaultMoth();
                initDefaultDay();
                initDefaultHour();
                initDefaultMinute();
                break;
            case STYLE_YYYY_MM_DD:
                mWvHour.setVisibility(View.GONE);
                mWvMinute.setVisibility(View.GONE);
                mWvSecond.setVisibility(View.GONE);
                initDefaultYear();
                initDefaultMoth();
                initDefaultDay();
                break;
            case STYLE_HH_MM_SS:
                mWvYear.setVisibility(View.GONE);
                mWvMonth.setVisibility(View.GONE);
                mWvDay.setVisibility(View.GONE);
                initDefaultHour();
                initDefaultMinute();
                initDefaultSecond();
                break;
            case STYLE_HH_MM:
                mWvYear.setVisibility(View.GONE);
                mWvMonth.setVisibility(View.GONE);
                mWvDay.setVisibility(View.GONE);
                mWvSecond.setVisibility(View.GONE);
                initDefaultHour();
                initDefaultMinute();
                initDefaultSecond();
                break;
            default:
                break;
        }
    }

    public void setStyle(int style) {
        this.mStyle = style;
        if (mWvYear != null) {
            init();
        }
    }

    public void initDefaultYear() {
        long start = System.currentTimeMillis() - 30 * MILLISECONDS_PER_YEAR;
        long end = System.currentTimeMillis() + 30 * MILLISECONDS_PER_YEAR;
        initYear(start, end);
    }

    public void initYear(long start, long end) {
        int currentValue = 0;
        int index = 0;
        String currentYear = formatTime(System.currentTimeMillis(), "yyyy");
        List<String> list = new ArrayList<>();
        for (long timestamp = start; timestamp < end; ) {
            String year = formatTime(timestamp, "yyyy");
            if (year.equals(currentYear)) {
                currentValue = index + 1;
            }
            list.add(year);
            timestamp += MILLISECONDS_PER_YEAR;
            index++;
        }
        yearArr = list.toArray(new String[]{});
        mWvYear.setDisplayedValues(yearArr);
        mWvYear.setValue(currentValue);
        mWvYear.setOnValueChangedListener(new WheelView.OnValueChangeListener() {
            @Override
            public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                initDay(Integer.parseInt(yearArr[newVal-1]), mWvMonth.getValue());
            }
        });
    }

    public void initDefaultMoth() {
        initMonth();
        mWvMonth.setValue(Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    public void initMonth() {
        mWvMonth.setMinValue(1);
        mWvMonth.setMaxValue(12);
        mWvMonth.setOnValueChangedListener(new WheelView.OnValueChangeListener() {
            @Override
            public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                initDay(Integer.parseInt(yearArr[mWvYear.getValue()-1]), newVal);
            }
        });
    }

    public void initDefaultDay() {
        initDay(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1);
        mWvDay.setValue(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    public void initDay(int year, int month) {
        mWvDay.setMinValue(1);
        int newMaxDay = getMonthOfDay(year, month);
        int lastMaxDay = mWvDay.getMaxValue();
        if (lastMaxDay != newMaxDay) {
            mWvDay.setMaxValue(newMaxDay);
            int value = mWvDay.getValue();
            if (value > newMaxDay) {
                mWvDay.setValue(newMaxDay);
            }
        }
    }

    public void initDefaultHour() {
        mWvHour.setValue(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    }

    public void initDefaultMinute() {
        mWvMinute.setValue(Calendar.getInstance().get(Calendar.MINUTE));
    }

    public void initDefaultSecond() {
        mWvSecond.setValue(Calendar.getInstance().get(Calendar.SECOND));
    }

    public static String formatTime(Long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    public static int getMonthOfDay(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                int day;
                if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                    day = 29;
                } else {
                    day = 28;
                }
                return day;
            default:
                return 0;
        }
    }

    @NonNull
    public int[] getDate() {
        switch (mStyle) {
            case STYLE_YYYY_MM_DD_HH_MM_SS:
                return new int[] {Integer.parseInt(yearArr[mWvYear.getValue()-1]), mWvMonth.getValue(), mWvDay.getValue(),
                        mWvHour.getValue(), mWvMinute.getValue(), mWvSecond.getValue()};
            case STYLE_YYYY_MM_DD_HH_MM:
                return new int[] {Integer.parseInt(yearArr[mWvYear.getValue()-1]), mWvMonth.getValue(), mWvDay.getValue(),
                        mWvHour.getValue(), mWvMinute.getValue()};
            case STYLE_YYYY_MM_DD:
                return new int[] {Integer.parseInt(yearArr[mWvYear.getValue()-1]), mWvMonth.getValue(), mWvDay.getValue()};
            case STYLE_HH_MM_SS:
                return new int[] {mWvHour.getValue(), mWvMinute.getValue(), mWvSecond.getValue()};
            case STYLE_HH_MM:
                return new int[] {mWvHour.getValue(), mWvMinute.getValue()};
            default:
                break;
        }
        return new int[] {Integer.parseInt(yearArr[mWvYear.getValue()-1]), mWvMonth.getValue(), mWvDay.getValue(),
                mWvHour.getValue(), mWvMinute.getValue(), mWvSecond.getValue()};
    }

}
