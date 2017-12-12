package com.tungnui.abccomputer.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tungnui.abccomputer.activity.MainActivity;

import java.util.Map;

import com.tungnui.abccomputer.R;
import com.tungnui.abccomputer.activity.ProductDetailsActivity;
import com.tungnui.abccomputer.activity.WebPageActivity;
import com.tungnui.abccomputer.data.constant.AppConstants;
import com.tungnui.abccomputer.data.preference.AppPreference;
import com.tungnui.abccomputer.data.sqlite.NotificationDBController;

/**
 * Created by Ashiq on 4/19/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> params = remoteMessage.getData();

            if (AppPreference.getInstance(MyFirebaseMessagingService.this).isNotificationOn()) {
                sendNotification(params.get("type"), params.get("title"), params.get("message"), params.get("id"), params.get("url"));
            }
        }
    }


    private void sendNotification(String type, String title, String message, String productId, String url) {

        if (productId.isEmpty()) {
            productId = "-1";
        }

        NotificationDBController notifyController = new NotificationDBController(this);
        notifyController.open();
        notifyController.insertNotificationItem(type, title, message, Integer.parseInt(productId), url);
        notifyController.close();

        Intent intent;
        if (type != null && !type.isEmpty() && type.equals(AppConstants.NOTIFY_TYPE_MESSAGE)) {
            intent = new Intent(this, MainActivity.class);
        } else if (type != null && !type.isEmpty() && type.equals(AppConstants.NOTIFY_TYPE_PRODUCT)) {
            intent = new Intent(this, ProductDetailsActivity.class);
            intent.putExtra(AppConstants.PRODUCT_ID, Integer.parseInt(productId));
        } else if (type != null && !type.isEmpty() && type.equals(AppConstants.NOTIFY_TYPE_URL)) {
            intent = new Intent(this, WebPageActivity.class);
            intent.putExtra(AppConstants.BUNDLE_KEY_TITLE, title);
            intent.putExtra(AppConstants.BUNDLE_KEY_URL, url);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000})
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

}
