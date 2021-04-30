package eu.faircode.netguard.surface;

/*
    This file is part of NetGuard.

    NetGuard is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    NetGuard is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NetGuard.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2015-2019 by Marcel Bokhorst (M66B)
*/

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.faircode.netguard.DatabaseHelper;
import eu.faircode.netguard.GuardUtils;
import eu.faircode.netguard.R;
import eu.faircode.netguard.adapter.AdapterDns;
import eu.faircode.netguard.service.ServiceSinkhole;

public class ActivityDns extends AppCompatActivity {
    private static final String TAG = "NetGuard.DNS";

    private static final int REQUEST_EXPORT = 1;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private boolean running;
    private AdapterDns adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GuardUtils.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resolving);

        getSupportActionBar().setTitle(R.string.setting_show_resolved);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView lvDns = findViewById(R.id.lvDns);
        adapter = new AdapterDns(this, DatabaseHelper.getInstance(this).getDns());
        lvDns.setAdapter(adapter);

        running = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dns, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        PackageManager pm = getPackageManager();
        menu.findItem(R.id.menu_export).setEnabled(getIntentExport().resolveActivity(pm) != null);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_refresh) {
            refresh();
            return true;
        } else if (itemId == R.id.menu_cleanup) {
            cleanup();
            return true;
        } else if (itemId == R.id.menu_clear) {
            GuardUtils.areYouSure(this, R.string.menu_clear, this::clear);
            return true;
        } else if (itemId == R.id.menu_export) {
            export();
            return true;
        }
        return false;
    }

    private void refresh() {
        updateAdapter();
    }

    private void doOnUiCode(int type, Throwable throwable) {
        Handler uiThread = new Handler(Looper.getMainLooper());
        uiThread.post(() -> {
            // 更新你的UI
            if (type == 1) {
                ServiceSinkhole.reload("DNS clear", ActivityDns.this, false);
            } else if (type == 2) {
                if (running) {
                    Toast.makeText(ActivityDns.this, R.string.msg_completed, Toast.LENGTH_LONG).show();
                }
            } else if (type == 3) {
                Toast.makeText(ActivityDns.this, throwable.toString(), Toast.LENGTH_LONG).show();
            } else {
                ServiceSinkhole.reload("DNS cleanup", ActivityDns.this, false);
            }
            updateAdapter();
        });
    }

    private void cleanup() {
        executorService.submit(() -> {
            Log.i(TAG, "Cleanup DNS");
            DatabaseHelper.getInstance(ActivityDns.this).cleanupDns();
            doOnUiCode(0, null);
        });
    }

    private void clear() {
        executorService.submit(() -> {
            Log.i(TAG, "Clear DNS");
            DatabaseHelper.getInstance(ActivityDns.this).clearDns();
            doOnUiCode(1, null);
        });
    }

    private void export() {
        startActivityForResult(getIntentExport(), REQUEST_EXPORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult request=" + requestCode + " result=" + requestCode + " ok=" + (resultCode == RESULT_OK));
        if (requestCode == REQUEST_EXPORT) {
            if (resultCode == RESULT_OK && data != null) {
                handleExport(data);
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private Intent getIntentExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // text/xml
        intent.setType("*/*");
        String regex = "yyyyMMdd";
        intent.putExtra(Intent.EXTRA_TITLE, "netguard_dns_" + new SimpleDateFormat(regex).format(System.currentTimeMillis()) + ".xml");
        return intent;
    }

    private void handleExport(final Intent data) {
        executorService.submit(() -> {
            OutputStream out;
            try {
                Uri target = data.getData();
                Log.i(TAG, "Writing URI=" + target);
                out = getContentResolver().openOutputStream(target);
                xmlExport(out);
                doOnUiCode(2, null);
                if (out != null) {
                    out.close();
                }
            } catch (Throwable ex) {
                Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                doOnUiCode(3, ex);
            }
        });
    }

    private void xmlExport(OutputStream out) throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(out, "UTF-8");
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.startTag(null, "netguard");

        DateFormat df = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US); // RFC 822

        try (Cursor cursor = DatabaseHelper.getInstance(this).getDns()) {
            int colTime = cursor.getColumnIndex("time");
            int colQName = cursor.getColumnIndex("qname");
            int colAName = cursor.getColumnIndex("aname");
            int colResource = cursor.getColumnIndex("resource");
            int colTTL = cursor.getColumnIndex("ttl");
            while (cursor.moveToNext()) {
                long time = cursor.getLong(colTime);
                String qname = cursor.getString(colQName);
                String aname = cursor.getString(colAName);
                String resource = cursor.getString(colResource);
                int ttl = cursor.getInt(colTTL);

                serializer.startTag(null, "dns");
                serializer.attribute(null, "time", df.format(time));
                serializer.attribute(null, "qname", qname);
                serializer.attribute(null, "aname", aname);
                serializer.attribute(null, "resource", resource);
                serializer.attribute(null, "ttl", Integer.toString(ttl));
                serializer.endTag(null, "dns");
            }
        }

        serializer.endTag(null, "netguard");
        serializer.endDocument();
        serializer.flush();
    }

    private void updateAdapter() {
        if (adapter != null) {
            adapter.changeCursor(DatabaseHelper.getInstance(this).getDns());
        }
    }

    @Override
    protected void onDestroy() {
        running = false;
        adapter = null;
        super.onDestroy();
    }
}
