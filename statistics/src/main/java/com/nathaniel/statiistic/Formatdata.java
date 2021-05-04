package com.nathaniel.statiistic;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nathaniel.statiistic.StatisticsActivity.TAG;

/**
 * Created by small on 2016/11/2.
 */

class Formatdata {

    float long2long(long data) {

        double bytes = data / 1024.0;
        return (float) (bytes / 1024.0);
    }

    float long2float(long data) {
        double bytes = data / 1024.0;
        return (float) (bytes / 1024.0);
    }

    String long2string(long data) {
        DecimalFormat df = new DecimalFormat("#.##");
        double bytes = data / 1024.0;
        String result = "";
        if (bytes > 1048576.0 && bytes / 1048576.0 > 0) {
            result = df.format(bytes / 1048576.0) + "G";
        } else if (bytes > 1024.0 && bytes / 1024.0 > 0) {
            result = df.format(bytes / 1024.0) + "M";
        } else {
            result = df.format(bytes) + "k";
        }
        Log.d("qiang", "longtostring:" + result);
        return result;
    }

    long getNumFromString(String data) {
        String regex = "\\d+\\.?\\d*";
        String result_match = "";
        Pattern regData = Pattern.compile(regex);
        Matcher matcher = regData.matcher(data);
        if (matcher.find()) {
            result_match = matcher.group(0);
        }
        Log.d(TAG, "match:" + result_match);
        Log.d(TAG, "match_data:" + data);

        long result;
        double num = Double.parseDouble(result_match);
        String last = getLastFromString(data);
        switch (last) {
            case "k":
                result = (long) (num * 1024);
                break;
            case "M":
                result = (long) (num * 1024 * 1024);
                break;
            default://G
                result = (long) (num * 1024 * 1024 * 1024);
        }
        Log.d("qiang", "GetNumFromString(bytes):" + result);
        return result;
    }

    private String getLastFromString(String data) {
        String regex = "[MGk]";
        String result = "";
        Pattern regData = Pattern.compile(regex);
        Matcher matcher = regData.matcher(data);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        Log.d("qiang", "GetLastFromString:" + result);
        return result;
    }

}
