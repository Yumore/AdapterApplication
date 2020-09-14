package com.nathaniel.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * @author nathaniel
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PreferencesUtils.initSharedPreferences(this)
        findViewById<View>(R.id.btn_recycler_view).setOnClickListener { startActivity(Intent(this@MainActivity, RecyclerViewActivity::class.java)) }
        findViewById<View>(R.id.btn_list_view).setOnClickListener { startActivity(Intent(this@MainActivity, ListViewActivity::class.java)) }
        findViewById<View>(R.id.btn_nested_scroll_view).setOnClickListener { startActivity(Intent(this@MainActivity, NestedScrollViewActivity::class.java)) }
        findViewById<View>(R.id.btn_scroll_view).setOnClickListener { startActivity(Intent(this@MainActivity, ScrollViewActivity::class.java)) }
        findViewById<View>(R.id.btn_web_view).setOnClickListener { startActivity(Intent(this@MainActivity, WebViewActivity::class.java)) }
        findViewById<View>(R.id.btn_item_click).setOnClickListener { startActivity(Intent(this@MainActivity, SampleActivity::class.java)) }
    }
}