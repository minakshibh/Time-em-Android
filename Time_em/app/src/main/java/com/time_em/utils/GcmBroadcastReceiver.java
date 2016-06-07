
package com.time_em.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	private static final String TAG = "GcmBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "received notification!");

	    //Explicitly specify that GcmIntentService will handle this intent
	    ComponentName comp = new ComponentName(context.getPackageName(),
			    GcmIntentService.class.getName());

	    //start the service, keeping the device awake while it is starting
	    startWakefulService(context, intent.setComponent(comp));

	    setResultCode(Activity.RESULT_OK);
    }
}
