package gelo.com.musicplayer.ui.view.music;

import gelo.com.musicplayer.ActivityScoped;
import gelo.com.musicplayer.ApplicationComponent;
import dagger.Component;
import gelo.com.musicplayer.data.service.MediaPlaybackService;
import gelo.com.musicplayer.ui.adapter.SongsHeaderAdapter;
import gelo.com.musicplayer.ui.presenter.MusicPresenterImpl;
import gelo.com.musicplayer.ui.view.Navigator;

@ActivityScoped
@Component(dependencies = ApplicationComponent.class)
public interface MusicComponent {
    MusicPresenterImpl presenter();
    SongsHeaderAdapter adapter();
}
