package com.WTK.ChangPao.commmon.utils;

import com.WTK.ChangPao.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtils {


    public static String formatStringDateShort(int year, int month, int day) {
        return UIHelper.getString(R.string.date_year_month_day, String.valueOf(year),
                month < 10 ? "0" + month : String.valueOf(month),
                day < 10 ? "0" + day : String.valueOf(day));
    }


    public static String getStringDateShort(long time) {
        Date currentTime = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

}
