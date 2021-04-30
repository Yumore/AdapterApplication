package eu.faircode.netguard.receiver;

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import eu.faircode.netguard.DatabaseHelper;
import eu.faircode.netguard.GuardUtils;

/**
 * @author admin
 */
public class ReceiverPackageRemoved extends BroadcastReceiver {
    private static final String TAG = "NetGuard.Receiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(TAG, "Received " + intent);
        GuardUtils.logExtras(intent);
        String action = (intent == null ? null : intent.getAction());
        if (Intent.ACTION_PACKAGE_FULLY_REMOVED.equals(action)) {
            int uid = intent.getIntExtra(Intent.EXTRA_UID, 0);
            if (uid > 0) {
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
                databaseHelper.clearLog(uid);
                databaseHelper.clearAccess(uid, false);
                // installed notification
                NotificationManagerCompat.from(context).cancel(uid);
                // access notification
                NotificationManagerCompat.from(context).cancel(uid + 10000);
            }
        }
    }
}
