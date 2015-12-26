package gelo.com.musicplayer.ui.presenter;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import gelo.com.musicplayer.AndroidApplication;
import gelo.com.musicplayer.ApplicationComponent;
import gelo.com.musicplayer.ApplicationModule;
import gelo.com.musicplayer.data.entity.Song;
import gelo.com.musicplayer.data.service.MediaPlaybackService;
import gelo.com.musicplayer.data.service.SongsProvider;
import gelo.com.musicplayer.mvp.mosby.BaseRxPresenter;
import gelo.com.musicplayer.ui.adapter.SongsAdapter;
import gelo.com.musicplayer.ui.view.Navigator;
import gelo.com.musicplayer.ui.view.music.MusicView;
import gelo.com.musicplayer.ui.view.music.SortDialog;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

/**
 *
 */
public class MusicPresenterImpl extends BaseRxPresenter<MusicView> implements MusicPresenter {

    SongsProvider songsProvider;
    Navigator navigator;
    @Inject
    Lazy<MediaPlaybackService> lazyMediaPlaybackService;
    private List<Song> songList;
    private int sortingType = SongsAdapter.ENUM_SORT_BY_TITLE;
    private MediaPlaybackService mediaPlaybackService;

    @Inject
    public MusicPresenterImpl(SongsProvider songsProvider,Navigator navigator){
        this.songsProvider = songsProvider;
        this.navigator = navigator;
        Timber.tag("MusicPresenterImp");
    }

    @Override
    public void loadSongList() {
        new RxIOSubscription<List<Song>>().add(
                songsProvider.getSongListRx(),
                new Subscriber<List<Song>>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("loadSongList completed");
                        getView().hideLoadingIndicator();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showMessage(e.getMessage());
                        getView().hideLoadingIndicator();
                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        getView().showSongs(songs);
                        songList = songs;
                    }
                }
        );
    }

    @Override
    public void playSong(Song song) {
        if(mediaPlaybackService==null){
            mediaPlaybackService = lazyMediaPlaybackService.get();
        }
       if(mediaPlaybackService!=null){
            Timber.d("play song - " + song.getTitle());
            if(mediaPlaybackService.getSongList()==null){
                mediaPlaybackService.setSongList(songList);
            }
            new RxIOSubscription<Boolean>().add(
                    Observable.just(mediaPlaybackService.playSong(song)),
                    new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {
                            Timber.d("playback started");
                            navigator.openNowPlayingActivity(""+mediaPlaybackService.getCurrentSong().getId());
                        }

                        @Override
                        public void onError(Throwable e) {
                            getView().showMessage(e.getMessage());
                            Timber.d("error on playback "+e.getMessage());
                        }

                        @Override
                        public void onNext(Boolean success) {
                        }
                    }
            );
        } else {
            Timber.d("MediaPlaybackService is null.");
        }
    }


    @Override
    public void performSearch(String query) {

        new RxIOSubscription<List<Song>>().add(
                Observable.just(songsProvider.searchSongs(query)),
                new Subscriber<List<Song>>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("SearchSongList completed");
                        getView().hideLoadingIndicator();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showMessage(e.getMessage());
                        getView().hideLoadingIndicator();
                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        getView().showSongs(songs);
                        songList = songs;
                    }
                }
        );
    }
}
