package com.example.cmc_application.util;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
    static final public String FORMAT_FULL_DATETIME = "yyyy-MM-dd HH:mm:ss";
    static final public String FORMAT_DATETIME = "yyyy-MM-dd HH:mm";
    static final public String FORMAT_SHORT_DATETIME = "yyyyMMddHHmm";
    static final public String FORMAT_SHORT_DATE = "yyyyMMdd";
    static final public String FORMAT_DATE = "yyyy-MM-dd";
    static final public String FORMAT_DATE_SLASH = "yyyy/MM/dd";
    static final public String FORMAT_TIME = "HH:mm";

    static public String getDateString(long timestamp, String format) {
        return getDateString(new Date(timestamp), format);
    }

    @SuppressLint("SimpleDateFormat")
    static public String getDateString(Date date, String format) {
        if (date == null)
            return null;

        if (format == null)
            return date.toString();

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getNowString(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    static public String getUpperCaseNum(String src) {
        String outStr = "";
        char[] chars = src.toCharArray();
        int tranTemp = 0;

        for (int i = 0; i < chars.length; i++) {
            tranTemp = (int) chars[i];
            if (tranTemp <= 57 && tranTemp >= 48)
                tranTemp += 65248;
            outStr += (char) tranTemp;
        }
        return outStr;
    }

    static private float getTextWidth(TextPaint textPaint, String text) {
        float[] widths = new float[text.length()];
        float textWidth = 0;
        textPaint.getTextWidths(text, widths);

        // caculate amount of text width
        for (int i = 0; i < widths.length; i++) {
            textWidth += widths[i];
        }
        return textWidth;
    }

    static public void autofitTextview(TextView textview, int width) {
        String text = textview.getText().toString();

        TextPaint textPaint = textview.getPaint();
        float textSize = 48;
        textPaint.setTextSize(textSize);

        ViewGroup.LayoutParams params = textview.getLayoutParams();
        float textWidth = 0;

        do {
            textWidth = getTextWidth(textPaint, text);
            if (textWidth > width) {
                textSize -= 2;
                textPaint.setTextSize(textSize);
            } else
                break;
        } while (textSize >= 30); // min text size is 36 px
    }

    static public int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    static public void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    static public String getFilenameFromUrl(String url) {
        if (url == null)
            return null;

        int i = url.lastIndexOf("/");
        return url.substring(i + 1);
    }


    public static void saveToFile(String filename, String data) {

        Writer writer;
        File root = Environment.getExternalStorageDirectory();
        File outDir = new File(root.getAbsolutePath() + File.separator + "EVA");
        if (!outDir.isDirectory()) {
            outDir.mkdir();
        }
        try {
            if (!outDir.isDirectory()) {
                throw new IOException(
                        "Unable to create directory EZ_time_tracker. Maybe the SD card is mounted?");
            }
            File outputFile = new File(outDir, filename);
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
        }

    }

    public static int findIndex(String[] s, String data){
        for(int i = 0; i < s.length; i++){
            if(s[i].equals(data))
                return i;
        }
        return -1;
    }

    public static String decimalPointFirst(int divisor1, int divisor2){
        float num= (float) divisor1 / divisor2;
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(num);
    }
}
