package com.nathaniel.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author nathaniel
 * @version V1.0.0
 * @contact <a href="mailto:nathanwriting@126.com">contact me</a>
 * @package com.nathaniel.sample
 * @datetime 4/16/21 - 4:42 PM
 */
public class IncludeActivity extends AppCompatActivity {
    private static final String TAG = IncludeActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_include);
        TextView textView = findViewById(R.id.test_include1);
        Log.d(TAG, "=>" + textView.getText().toString());
    }
}
