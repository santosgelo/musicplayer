package gelo.com.musicplayer.ui.presenter;

import javax.inject.Inject;

import dagger.Lazy;
import gelo.com.musicplayer.data.service.MediaPlaybackService;
import gelo.com.musicplayer.data.service.SongsProvider;
import gelo.com.musicplayer.mvp.mosby.BaseRxPresenter;
import gelo.com.musicplayer.ui.view.Navigator;
import gelo.com.musicplayer.ui.view.nowplaying.NowPlayingView;
import timber.log.Timber;

public class NowPlayingPresenterImpl  extends BaseRxPresenter<NowPlayingView> implements NowPlayingPresenter  {

    SongsProvider songsProvider;
    Navigator navigator;
    @Inject
    Lazy<MediaPlaybackService> lazyMediaPlaybackService;

    MediaPlaybackService mediaPlaybackService;
    @Inject
    public NowPlayingPresenterImpl(SongsProvider songsProvider,Navigator navigator){
        super();
        this.songsProvider = songsProvider;
        this.navigator = navigator;
        //this.mediaPlaybackService = mediaPlaybackService;
        Timber.tag("NowPlayingPresenterImpl");
        mediaPlaybackService = lazyMediaPlaybackService.get();
    }
    
}
