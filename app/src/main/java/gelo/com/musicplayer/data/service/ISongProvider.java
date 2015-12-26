package gelo.com.musicplayer.data.service;

import java.util.List;

import gelo.com.musicplayer.data.entity.Song;
import rx.Observable;

public interface ISongProvider {
    List<Song> searchSongs(String keyword);
    List<Song> getSongList();
    Observable<List<Song>> getSongListRx();
}
