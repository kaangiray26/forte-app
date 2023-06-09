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
import android.graphics.BitmapFactory;
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
import androidx.core.content.ContextCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import java.io.IOException;
import java.net.URL;

@CapacitorPlugin(name = "MediaControl")
public class MediaControl extends Plugin {

    private Context context;
    private MediaStyle mediaStyle;
    private MediaSessionCompat mediaSession;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "Forte";

    @Override
    public void load() {
        super.load();

        // Get the context
        context = getContext();

        // Create Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Forte Media Controls";
            String description = "Forte Media Controls Notification Channel";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a MediaSessionCompat
        mediaSession = new MediaSessionCompat(context, "MediaControl");

        // Set Callbacks
        mediaSession.setCallback(new MediaSessionCompat.Callback(){
            @Override
            public void onPlay() {
                notifyListeners("fortePlayAction", null);
            }

            @Override
            public void onPause() {
                JSObject ret = new JSObject();
                notifyListeners("fortePauseAction", null);
            }

            @Override
            public void onSkipToNext(){
                JSObject ret = new JSObject();
                notifyListeners("forteNextAction", null);
            }

            @Override
            public void onSkipToPrevious(){
                JSObject ret = new JSObject();
                notifyListeners("fortePreviousAction", null);
            }

            // Seek to a specific position
            @Override
            public void onSeekTo(long pos) {
                JSObject ret = new JSObject();
                ret.put("position", pos);
                notifyListeners("forteSeekAction", ret);
            }
            
        });

        // MediaStyle object
        mediaStyle = new MediaStyle()
            .setMediaSession(mediaSession.getSessionToken())
            .setShowActionsInCompactView(0,1,2,3);

        // PlaybackStateCompat object
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP);
        stateBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());

        // Activate the MediaSession
        mediaSession.setActive(true);
    }

    @PluginMethod
    public void setMetadata(PluginCall call){
        JSObject data = call.getData();
        String title = data.getString("title");
        String album = data.getString("album");
        String artist = data.getString("artist");
        String duration = data.getString("duration");
        String albumArt = data.getString("albumArt");

        // Get image
        Bitmap bitmap = null;
        if (albumArt != null) {
            System.out.println("Getting album cover...");
            try {
                URL url = new URL(albumArt);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // MediaMetadataCompat object
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(duration));
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, albumArt);
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
        mediaSession.setMetadata(metadataBuilder.build());

        // Update the notification
        createNotification();

        call.resolve();
    }

    @PluginMethod
    public void setPlaybackState(PluginCall call){
        JSObject data = call.getData();
        String state = data.getString("state");

        int playbackState = PlaybackStateCompat.STATE_NONE;

        switch (state) {
            case "none":
                break;
            case "playing":
                playbackState = PlaybackStateCompat.STATE_PLAYING;
                break;
            case "paused":
                playbackState = PlaybackStateCompat.STATE_PAUSED;
                break;
            case "stopped":
                playbackState = PlaybackStateCompat.STATE_STOPPED;
                break;
        }

        // PlaybackStateCompat object
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        stateBuilder.setState(playbackState, 0, 1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());

        // Update the notification
        createNotification();

        call.resolve();
    }

    @PluginMethod
    public void setProgress(PluginCall call){
        JSObject data = call.getData();
        String progress = data.getString("progress");

        // PlaybackStateCompat object
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO);
        stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, Long.parseLong(progress), 1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());

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