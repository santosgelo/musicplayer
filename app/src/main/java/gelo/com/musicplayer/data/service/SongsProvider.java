package gelo.com.musicplayer.data.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.LongSparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import gelo.com.musicplayer.ui.adapter.SongsAdapter;
import rx.Observable;

import gelo.com.musicplayer.data.entity.Song;
import rx.Subscriber;

/**
 * Queries the device and returns the list of local songs
 */
public class SongsProvider implements ISongProvider{

    private static final String STR_WHERE_MUSIC = "=1";
    private final Context mContext;

    @Inject
    public SongsProvider(Context context) {
        mContext = context;
    }

    private LongSparseArray<String> getCoverArtPaths(Context context) {

        LongSparseArray<String> map = new LongSparseArray<>();
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                null,
                null,
                null);
        if (c != null) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                String path = c.getString(1);
                Long id = c.getLong(0);
                map.put(id, path);
            }
            c.close();
        }

        // returns a mapping of Album ID => art file path
        return map;
    }

    public List<Song> searchSongs(String keyword) {
        LongSparseArray<String> albumArtUriDictionary = getCoverArtPaths(mContext);
        List<Song> songs = new ArrayList<>();
        //retrieve song info
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};
        final String where = MediaStore.Audio.Media.IS_MUSIC + STR_WHERE_MUSIC + " AND ("+MediaStore.Audio.Media.TITLE + " LIKE '%"+keyword+"%' OR "
                +MediaStore.Audio.Media._ID + " LIKE '%"+keyword+"%' "
                +MediaStore.Audio.Media.ARTIST + " LIKE '%"+keyword+"%')";
        final Cursor musicCursor = musicResolver.query(musicUri,
                cursor_cols, where, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int albumIdColumn = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            int durationColumn = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                Long thisAlbumId = musicCursor.getLong(albumIdColumn);
                int thisDuration = musicCursor.getInt(durationColumn);
                String thisAlbumArtUri = albumArtUriDictionary.get(thisAlbumId);
                Song song = new Song(thisTitle, thisArtist, thisId);
                song.setAlbumId(thisAlbumId);
                song.setAlbum(thisAlbum);
                song.setDuration(thisDuration);
                song.setAlbumArtUri(thisAlbumArtUri);

                songs.add(song);
            }
            while (musicCursor.moveToNext());
        }
        if(musicCursor != null){
            musicCursor.close();
        }
        songs = sortSongsByTitle(songs);
        return songs;
    }

    public List<Song> getSongList() {
        LongSparseArray<String> albumArtUriDictionary = getCoverArtPaths(mContext);
        List<Song> songs = new ArrayList<>();
        //retrieve song info
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};
        final String where = MediaStore.Audio.Media.IS_MUSIC + STR_WHERE_MUSIC;
        final Cursor musicCursor = musicResolver.query(musicUri,
                cursor_cols, where, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int albumIdColumn = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            int durationColumn = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                Long thisAlbumId = musicCursor.getLong(albumIdColumn);
                int thisDuration = musicCursor.getInt(durationColumn);
                String thisAlbumArtUri = albumArtUriDictionary.get(thisAlbumId);
                Song song = new Song(thisTitle, thisArtist, thisId);
                song.setAlbumId(thisAlbumId);
                song.setAlbum(thisAlbum);
                song.setDuration(thisDuration);
                song.setAlbumArtUri(thisAlbumArtUri);

                songs.add(song);
            }
            while (musicCursor.moveToNext());
        }
        if(musicCursor != null){
            musicCursor.close();
        }
        songs = sortSongsByTitle(songs);
        return songs;
    }

    public Observable<List<Song>> getSongListRx(){
        return Observable.just(getSongList());
    }

    public static List<Song> sortSongsByTitle(List<Song> songs) {
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                return song.getTitle().compareTo(t1.getTitle());
            }
        });
        return songs;
    }

    public static List<Song> sortSongsByArtist(List<Song> songs) {
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                return song.getArtist().compareTo(t1.getArtist());
            }
        });
        return songs;
    }

    public static List<Song> sortSongsByAlbum(List<Song> songs){
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                return song.getAlbum().compareTo(t1.getAlbum());
            }
        });
        return songs;
    }

    public static List<Song> shuffleSongs(List<Song> songs) {
        Collections.shuffle(songs);
        return songs;
    }

    public static Observable<List<Song>> sortSongListRx(List<Song> songList, int selectedSortingType) {
        switch (selectedSortingType){
            case SongsAdapter.ENUM_SORT_BY_ALBUM:
                return Observable.just(sortSongsByAlbum(songList));
            case SongsAdapter.ENUM_SORT_BY_ARTIST:
                return Observable.just(sortSongsByArtist(songList));
            default: //by TITLE
                return Observable.just(sortSongsByTitle(songList));
        }
    }
    public static List<Song> sortSongs(List<Song> songs,int selectedSortingType) {
        switch (selectedSortingType){
            case SongsAdapter.ENUM_SORT_BY_ALBUM:
                songs = sortSongsByAlbum(songs);
            case SongsAdapter.ENUM_SORT_BY_ARTIST:
                songs = sortSongsByArtist(songs);
            default: //by TITLE
                songs = sortSongsByTitle(songs);
        }
        return songs;
    }
}