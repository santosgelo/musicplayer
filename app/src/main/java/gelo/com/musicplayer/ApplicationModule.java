package gelo.com.musicplayer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gelo.com.musicplayer.data.service.MediaPlaybackService;
import gelo.com.musicplayer.data.service.SongsProvider;
import retrofit.RestAdapter;
import timber.log.Timber;

@Module
public class ApplicationModule {
    final Application application;


    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }


    @Provides
    public Context provideContext() {
        return application;
    }

    /*@Provides
    public GitHubAPI provideGitHubAPI() {
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(GitHubAPI.URI).build();
        return adapter.create(GitHubAPI.class);
    }*/
    @Provides
    public SongsProvider provideSongsProvider(){
        SongsProvider songsProvider = new SongsProvider(provideContext());
        return songsProvider;
    }

    @Provides
    public MediaPlaybackService provideMediaPlaybackService(){
        return ((AndroidApplication)application).getMediaPlaybackService();
    }


}

