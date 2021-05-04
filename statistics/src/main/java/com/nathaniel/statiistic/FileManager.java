package com.nathaniel.statiistic;

import android.content.Context;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by small on 2016/11/7.
 */

class FileManager {

    //写数据
    void writeFileAppend(Context context, String fileName, String writeString) throws IOException {
        try {

            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_APPEND);
            byte[] bytes = writeString.getBytes();
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String readLogFile(Context context, String filename) {
        String result = "";
        try {
            FileInputStream fileInputStream = context.openFileInput(filename);
            int length = fileInputStream.available();
            byte[] buffer = new byte[length];
            int line = fileInputStream.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
