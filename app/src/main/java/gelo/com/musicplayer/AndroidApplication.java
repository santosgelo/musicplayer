package gelo.com.musicplayer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.facebook.drawee.backends.pipeline.Fresco;

import gelo.com.musicplayer.data.service.MediaPlaybackService;
import timber.log.Timber;


public class AndroidApplication extends Application {

    ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        startMediaPlaybackService();
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public ApplicationComponent getComponent() {
        return component;
    }


    private Intent playIntent;
    public void unbindMediaPlaybackService(){
        Timber.d("unbindMediaPlaybackService");
       /* if(serviceConnection!=null){
            unbindService(serviceConnection);
        }*/
     /*   if(playIntent!=null){
            context.stopService(playIntent);
        }*/
    }

    private void startMediaPlaybackService() {
        if (playIntent == null) {
            playIntent = new Intent(this, MediaPlaybackService.class);
        }
        Timber.d("Start MediaPlaybackService");
        bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(playIntent);
    }
    public MediaPlaybackService getMediaPlaybackService(){return  mediaPlaybackService;}

    private MediaPlaybackService mediaPlaybackService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Timber.d("onServiceConnected");
            MediaPlaybackService.SongBinder songBinder = (MediaPlaybackService.SongBinder) binder;
            mediaPlaybackService = songBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Timber.d("onServiceDisconnected");
        }
    };
}

