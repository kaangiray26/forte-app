package forte.buzl.uk;

import static androidx.core.content.ContextCompat.getSystemService;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

// Context
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

// Media Session
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

// NotificationCompat
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import java.io.IOException;

@CapacitorPlugin(name = "MediaControl")
public class MediaControl extends Plugin {

    private Context context;
    private MediaStyle mediaStyle;
    private MediaSessionCompat mediaSession;
    private NotificationCompat notificationCompat;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MediaControl";

    @Override
    public void load() {
        super.load();

        // Get the context
        context = getContext();

        // Create Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Forte Media Controls";
            String description = "Forte Media Controls Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a MediaSessionCompat
        mediaSession = new MediaSessionCompat(context, "MediaControl");

        // MediaStyle object
        mediaStyle = new MediaStyle().setMediaSession(mediaSession.getSessionToken());

        // PlaybackStateCompat object
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP);
        stateBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());

        mediaSession.setActive(true);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setStyle(mediaStyle)
                .setSmallIcon(R.drawable.ic_forte)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @PluginMethod
    public void setMetadata(PluginCall call){
        JSObject data = call.getData();
        String title = data.getString("title");
        String artist = data.getString("artist");
        String album = data.getString("album");
        String albumArt = data.getString("albumArt");

        // MediaMetadataCompat object
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, albumArt);
        mediaSession.setMetadata(metadataBuilder.build());

        // Update the notification
        createNotification();

        call.resolve();
    }

    public void createNotification() {
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setStyle(mediaStyle)
                .setSmallIcon(R.drawable.ic_forte)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}