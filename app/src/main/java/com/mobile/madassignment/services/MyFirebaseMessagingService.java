package com.mobile.madassignment.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.mobile.madassignment.MainActivity;
import com.mobile.madassignment.R;


/**
 * Created by lq on 05/05/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    FirebaseUser user = myAuth.getCurrentUser();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (false) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                // a expense was added

                if(remoteMessage.getData().get("actionType").matches("newExpense")){
                    String groupKey = remoteMessage.getData().get("groupId");
                    String groupName = remoteMessage.getData().get("groupName");
                    String createdBy = remoteMessage.getData().get("creatorName");
                    String messageBody = "a new expense created by " + createdBy +"(group: " + groupName+")";
                    String messageTitle = "new expense";
                    sendNotification(messageTitle, messageBody, groupKey,(int)remoteMessage.getSentTime() );
                }else if(remoteMessage.getData().get("actionType").matches("deleteExpense")){
                    String groupKey = remoteMessage.getData().get("groupId");
                    String groupName = remoteMessage.getData().get("groupName");
                    String createdBy = remoteMessage.getData().get("creatorName");
                    String messageBody = "a expense deleted by " + createdBy +"(group: " + groupName+")";
                    String messageTitle = "a expense";
                    sendNotification(messageTitle, messageBody, groupKey, (int)remoteMessage.getSentTime());

                }
            }


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }



    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageTitle, String messageBody, String groupKey,int msgid) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("group_key",groupKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)

                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        try {
           Bitmap bitmap = Glide.with(this)
                    .load(R.drawable.cocoin_logo)
                    .asBitmap()
                    .centerCrop()
                    .into(128, 128)
                    .get();
            notificationBuilder.setLargeIcon(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(msgid /* ID of notification */, notificationBuilder.build());
    }


}

