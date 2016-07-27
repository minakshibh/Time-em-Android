
package com.time_em.utils;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.time_em.android.R;
import com.time_em.android.SplashActivity;
import com.time_em.dashboard.HomeActivity;

import java.util.List;
import java.util.Random;


/**
 *
 */
public class GcmIntentService extends IntentService {

	private static final String TAG = "GcmIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GcmIntentService(String name) {
        super(name);
    }
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

	    Bundle extras = intent.getExtras();
	    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

	    String messageType = gcm.getMessageType(intent);
        String message=extras.getString("message");
	    if(!extras.isEmpty()) {

				Log.e(TAG, "extras not empty");

			if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

					Log.e(TAG, "msg type: " + messageType);
                    Log.e(TAG, "msg : " + message);
				//go to next
                appStatus(message);
				}
	    }
    }


    private void appStatus(String message) {

      //  generateNotification(getApplicationContext(),message);
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;

        /*if (componentInfo.getPackageName().equalsIgnoreCase("com.time_em.android")) {
            Log.e(TAG, "app open");
        } else {*/
            generateNotification(getApplicationContext(),message);
     //   }
    }

    private void generateNotification(Context context, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true)
                .setSmallIcon(R.drawable.icon)
                .setDefaults(Notification.DEFAULT_ALL);
              //  .setTicker(getString(R.string.new_notifications));

        builder.setContentTitle(getString(R.string.app_name))
                .setContentText(message);
        Intent intent = new Intent(this.getApplicationContext(), SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1234, builder.build());
        Intent broadcastIntent = new Intent();
       // broadcastIntent.setAction(getString(R.string.ACTION_BOOKING_UPDATED));
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
    }
}
