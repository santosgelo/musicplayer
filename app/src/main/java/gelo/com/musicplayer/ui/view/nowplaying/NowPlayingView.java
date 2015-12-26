package gelo.com.musicplayer.ui.view.nowplaying;

import android.net.Uri;

import gelo.com.musicplayer.data.entity.Song;
import gelo.com.musicplayer.mvp.mosby.BaseMvpView;

public interface NowPlayingView extends BaseMvpView {
    void startPlayingSong(Song song);
    void updateSeekBar(int position);
    void setAlbumArt(Uri uri);
    void setSongTitle(String title);
    void setSongArtist(String artist);
}
