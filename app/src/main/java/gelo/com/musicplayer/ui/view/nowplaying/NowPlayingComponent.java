package gelo.com.musicplayer.ui.view.nowplaying;

import dagger.Component;
import gelo.com.musicplayer.ActivityScoped;
import gelo.com.musicplayer.ApplicationComponent;
import gelo.com.musicplayer.ui.presenter.NowPlayingPresenterImpl;

@ActivityScoped
@Component(dependencies = ApplicationComponent.class)
public interface NowPlayingComponent {
    NowPlayingPresenterImpl presenter();
    //TODO add this viewadapter
}
