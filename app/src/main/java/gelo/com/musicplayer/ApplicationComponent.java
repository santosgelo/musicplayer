package gelo.com.musicplayer;

import android.content.Context;
import android.support.annotation.Nullable;

import dagger.Component;
import gelo.com.musicplayer.data.service.MediaPlaybackService;
import gelo.com.musicplayer.data.service.SongsProvider;

@ApplicationScoped @Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    Context context();
    //GitHubAPI gitHubAPI();
    //TODO add dependencies here (e.g. services)
    SongsProvider songsProvider();
    MediaPlaybackService mediaPlaybackService();
}
