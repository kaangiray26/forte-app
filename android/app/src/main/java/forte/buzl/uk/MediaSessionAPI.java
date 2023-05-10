package forte.buzl.uk;

import android.os.Bundle;
import android.content.Context;
import android.app.PendingIntent;
import android.content.Intent;

import android.app.Notification;
import android.app.Notification.MediaStyle;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;

import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.MediaController;
import android.media.session.PlaybackState;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "MediaSessionAPI")
public class MediaSessionAPI extends Plugin {

    private Context context;
    private MediaSession session;
    private NotificationManager notificationManager;

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "MediaSessionAPI";
    static PendingIntent prevPendingIntent, pausePendingIntent, nextPendingIntent; 

    @Override
    public void load() {
        super.load();

        context = getContext();
        // Create a media session.
        session = new MediaSession(context, "MediaSessionAPI");

        // Get the notification manager service.
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel.
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "MediaSessionAPI", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        // Create a pending intent that is invoked when the user clicks on a button
        prevPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent("prev"), 0);
        pausePendingIntent = PendingIntent.getBroadcast(context, 1, new Intent("pause"), 0);
        nextPendingIntent = PendingIntent.getBroadcast(context, 2, new Intent("next"), 0);
    }

    @PluginMethod
    public void setMetadata(PluginCall call){
        if(call == null){
            call.reject("Missing metadata!");
            return;
        }

        MediaMetadata mediaMetadata = new MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, call.getString("title"))
            .putString(MediaMetadata.METADATA_KEY_ARTIST, call.getString("artist"))
            .putString(MediaMetadata.METADATA_KEY_ALBUM, call.getString("album"))
            .putString(MediaMetadata.METADATA_KEY_ART_URI, call.getString("cover"))
            .build();

        session.setMetadata(mediaMetadata);
        
        display();

        call.resolve();
    }

    public void updateNotification(){
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_forte)
            .setContentTitle(session.getController().getMetadata().getString(MediaMetadata.METADATA_KEY_TITLE))
            .setContentText(session.getController().getMetadata().getString(MediaMetadata.METADATA_KEY_ARTIST));

        Notification notification = builder.build();
        
        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void notifyEvent(String event){
        JSObject data = new JSObject();
        data.put("event", event);
        notifyListeners("mediaSessionEvent", data);
    }

    public void display(){
        // Get cover
        Bitmap image = null;
        try{
            URL url = new URL(session.getController().getMetadata().getString(MediaMetadata.METADATA_KEY_ART_URI));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            image = BitmapFactory.decodeStream(input);
        } catch (Exception e){
            return;
        }

        Notification notification = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
        // Show controls on lock screen even when user hides sensitive content.
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setSmallIcon(R.drawable.ic_forte)
        // Add media control buttons that invoke intents in your media service
        .addAction(R.drawable.ic_prev, "Previous", prevPendingIntent) // #0
        .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)  // #1
        .addAction(R.drawable.ic_next, "Next", nextPendingIntent)     // #2
        // Apply the media style template
        .setStyle(new MediaStyle()
            .setShowActionsInCompactView(1 /* #1: pause button */)
            .setMediaSession(session.getSessionToken()))
        .setContentTitle(session.getController().getMetadata().getString(MediaMetadata.METADATA_KEY_TITLE))
        .setContentText(session.getController().getMetadata().getString(MediaMetadata.METADATA_KEY_ARTIST))
        .setLargeIcon(image)
        .build();

    }
}