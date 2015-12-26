package gelo.com.musicplayer.ui.presenter;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import java.util.List;

import gelo.com.musicplayer.data.entity.Song;
import gelo.com.musicplayer.data.service.MediaPlaybackService;
import gelo.com.musicplayer.ui.view.music.MusicActivity;
import gelo.com.musicplayer.ui.view.music.MusicView;

/**
 * Created by samsung on 10/14/2015.
 */
public interface MusicPresenter extends MvpPresenter<MusicView>{
    void loadSongList();
    void playSong(Song song);
    void performSearch(String query);
}
