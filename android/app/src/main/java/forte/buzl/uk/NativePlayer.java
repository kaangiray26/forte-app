package forte.buzl.uk;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import java.io.IOException;

// CapacitorJS
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;


@CapacitorPlugin(name = "NativePlayer")
public class NativePlayer extends Plugin {

    private Context context;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable progressRunnable;

    @Override
    public void load(){
        super.load();

        // Get the context
        context = getContext();
        
        // initialize the media player
        mediaPlayer = new MediaPlayer();

        // Set callbacks
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                notifyListeners("end", null);
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                JSObject ret = new JSObject();
                ret.put("duration", mp.getDuration());

                mp.start();
                notifyListeners("load", ret);
            }
        });

        // Create a Handler to handle periodic updates
        handler = new Handler();

        // Define a Runnable for updating the progress
        progressRunnable = new Runnable() {
            @Override
            public void run(){
                JSObject ret = new JSObject();
                ret.put("position", mediaPlayer.getCurrentPosition());
                
                notifyListeners("timeupdate", ret);
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(progressRunnable, 0);
    }

    @PluginMethod
    public void playDataSource(PluginCall call){
        JSObject data = call.getData();
        String url = data.getString("url");

        mediaPlayer.setAudioAttributes(
            new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        );

        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }

        try{
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        call.resolve();
    }

    @PluginMethod
    public void play(PluginCall call){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }

        call.resolve();
    }

    @PluginMethod
    public void pause(PluginCall call){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }

        call.resolve();
    }

    @PluginMethod
    public void duration(PluginCall call){
        JSObject ret = new JSObject();
        ret.put("duration", mediaPlayer.getDuration());

        call.resolve(ret);
    }

    @PluginMethod
    public void position(PluginCall call){
        JSObject ret = new JSObject();
        ret.put("position", mediaPlayer.getCurrentPosition());

        call.resolve(ret);
    }

    @PluginMethod
    public void seek(PluginCall call){
        JSObject data = call.getData();
        String position = data.getString("position");

        mediaPlayer.seekTo(Integer.parseInt(position));

        call.resolve();
    }

    @PluginMethod
    public void reset(PluginCall call){
        mediaPlayer.reset();
        call.resolve();
    }

    @PluginMethod
    public void mute(PluginCall call){
        JSObject data = call.getData();
        String volume = data.getString("muted");

        if(volume.equals("true")){
            mediaPlayer.setVolume(0.0f, 0.0f);
        } else {
            mediaPlayer.setVolume(1.0f, 1.0f);
        }

        call.resolve();
    }
}