package gelo.com.musicplayer.ui.view.music;

import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

import gelo.com.musicplayer.data.entity.Song;
import gelo.com.musicplayer.mvp.mosby.BaseMvpView;

/**
 * Created by samsung on 10/14/2015.
 */
public interface MusicView extends BaseMvpView {
    void showSongs(List<Song> songList);
    void hideLoadingIndicator();
}
