package gelo.com.musicplayer.data.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import gelo.com.musicplayer.R;
import gelo.com.musicplayer.data.entity.Song;
import timber.log.Timber;

/**
 * Created by samsung on 10/19/2015.
 */
public class MediaPlaybackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;

    private static final int INT_STEP_SEEK = 800; // in ms, step for every forward or backward seek is called
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private List<Song> songList;
    private final IBinder songBinder = new SongBinder();
    private static final int NOTIFY_ID = 1;
    private onPlayedSongListener onplayedSongListener;
    private Context mContext;

    @Inject
    public MediaPlaybackService(){}

    public List<Song> getSongList() {
        return songList;
    }

    public class SongBinder extends Binder {
        public MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.tag("MediaPlaybackService");
        Timber.d("onCreate");
        mediaPlayer = new MediaPlayer();
        initializeMediaPlayer();
    }

    //when activity attaches
    @Override
    public IBinder onBind(Intent intent) {
        Timber.d("onBind");
        return songBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Timber.d("onUnbind");
        mediaPlayer.stop();
        mediaPlayer.release();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSession.release();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Timber.d("onCompletion");
        if (mediaPlayer.getCurrentPosition() > 0) {
            mp.reset();
            playNextSong();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Timber.d("onError");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Timber.d("onPrepared");
        startPlayback();
        //setNotification();
        buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        //stopForeground(true);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Timber.d("onTaskRemoved");
        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.cancelAll();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setNotification() {
        Timber.d("setNotification");
        Intent notIntent = new Intent(getApplicationContext(), MediaPlaybackService.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt;
        pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(currentSong.getTitle())
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(currentSong.getTitle());
        Notification not = builder.build();

        //startForeground(NOTIFY_ID, not);
    }

    public interface onPlayedSongListener {
        void onPlayedSong(Song song);
    }

    private void initializeMediaPlayer() {
        mContext = getApplicationContext();
        mediaPlayer.setWakeMode(mContext,
                PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void setSongList(List<Song> songs) {
        Timber.d("setSongList count - "+songs.size());
        this.songList = new ArrayList<>();
        //let's remove null songs
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getTitle() != null) {
                this.songList.add(songs.get(i));
            }
        }
        Collections.sort(this.songList, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                return song.getTitle().compareTo(t1.getTitle());
            }
        });
        //TODO we don't need this in the future, every song list should vbe in different order
    }

    public boolean setSong(Song song) {
        if(song!=null){
            this.currentSong = song;
            return true;
        }
        return false;
    }

    public void setOnPlayedSongListener(onPlayedSongListener listener) {
        onplayedSongListener = listener;
    }

    public boolean playSong() {
        if (currentSong != null) {
            mediaPlayer.reset();
            long currentSongId = currentSong.getId();
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currentSongId);
            Timber.d("playSong " + currentSong.getTitle());
            try {
                mediaPlayer.setDataSource(mContext, trackUri);
            } catch (Exception e) {
                Timber.e("Error setting data source", e);
                return false;
            }
            mediaPlayer.prepareAsync();
            return true;
        }
        return  false;
    }

    public boolean playSong(Song song){
        return setSong(song) && playSong();
    }

    public int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer() {
        mediaPlayer.pause();
    }

    public void seek(int position) {
        mediaPlayer.seekTo(position);
    }

    private void startPlayback() {
        mediaPlayer.start();
        if (onplayedSongListener != null) {
            onplayedSongListener.onPlayedSong(currentSong);
        }
    }

    public void resumePlayback() {
        mediaPlayer.start();
    }

    public void playPreviousSong() {
        int index = songList.indexOf(currentSong);
        do {
            index--;
            if (index < 0) {
                index = songList.size() - 1;
            }
            currentSong = songList.get(index);
        } while (currentSong.getTitle() == null && currentSong.getArtist() == null);
        playSong();
    }

    public void playNextSong() {
        Timber.d("playNextSong");
        int index = songList.indexOf(currentSong);
        do {
            index++;
            if (index >= songList.size()) {
                index = 0;
            }
            currentSong = songList.get(index);
        } while (currentSong.getTitle() == null && currentSong.getArtist() == null);
        playSong();
    }

    public void seekForward() {
        int position = mediaPlayer.getCurrentPosition();
        if (position <= mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(position + INT_STEP_SEEK);
        }

    }

    public void seekBackward() {
        int position = mediaPlayer.getCurrentPosition();
        if (position >= 0) {
            mediaPlayer.seekTo(position - INT_STEP_SEEK);
        }
    }

    public Song getCurrentSong(){
        return currentSong;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            mController.getTransportControls().play();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            mController.getTransportControls().pause();
        } else if( action.equalsIgnoreCase( ACTION_FAST_FORWARD ) ) {
            mController.getTransportControls().fastForward();
        } else if( action.equalsIgnoreCase( ACTION_REWIND ) ) {
            mController.getTransportControls().rewind();
        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
            mController.getTransportControls().skipToPrevious();
        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            mController.getTransportControls().skipToNext();
        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            mController.getTransportControls().stop();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private Notification.Action generateAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), MediaPlaybackService.class );
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private void buildNotification( Notification.Action action ) {
        Notification.MediaStyle style = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            style = new Notification.MediaStyle();
        }

        Intent intent = new Intent( getApplicationContext(), MediaPlaybackService.class );
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        Notification.Builder builder = new Notification.Builder( this )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(currentSong.getTitle())
                .setContentText(currentSong.getArtist())
                .setDeleteIntent(pendingIntent )
                .setStyle(style);
        if(currentSong.getAlbumArtUri()!=null){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(currentSong.getAlbumArtUri())));
                builder.setLargeIcon(bitmap);
            } catch (IOException e) {
                Timber.e("Error loading bitmap");
                e.printStackTrace();
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            builder.addAction( generateAction( android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS ) );
            builder.addAction( generateAction( android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND ) );
            builder.addAction( action );
            builder.addAction( generateAction( android.R.drawable.ic_media_ff, "Fast Foward", ACTION_FAST_FORWARD ) );
            builder.addAction( generateAction( android.R.drawable.ic_media_next, "Next", ACTION_NEXT ) );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                style.setShowActionsInCompactView(0,1,2,3,4);
            }
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( 1, builder.build() );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( mManager == null ) {
            initMediaSessions();
        }

        handleIntent( intent );
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initMediaSessions() {
        mSession = new MediaSession(getApplicationContext(), "simple player session");
        mController =new MediaController(getApplicationContext(), mSession.getSessionToken());

        mSession.setCallback(new MediaSession.Callback(){
                                 @Override
                                 public void onPlay() {
                                     super.onPlay();
                                     Timber.d("onPlay");
                                     resumePlayback();
                                     buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
                                 }

                                 @Override
                                 public void onPause() {
                                     super.onPause();
                                     Timber.d("onPause");
                                     pausePlayer();
                                     buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
                                 }

                                 @Override
                                 public void onSkipToNext() {
                                     super.onSkipToNext();
                                     Timber.d("onSkipToNext");
                                     //Change media here
                                     playNextSong();
                                     buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
                                 }

                                 @Override
                                 public void onSkipToPrevious() {
                                     super.onSkipToPrevious();
                                     Timber.d("onSkipToPrevious");
                                     //Change media here
                                     playPreviousSong();
                                     buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
                                 }

                                 @Override
                                 public void onFastForward() {
                                     super.onFastForward();
                                     Timber.d("onFastForward");
                                     //Manipulate current media here
                                     seekForward();
                                 }

                                 @Override
                                 public void onRewind() {
                                     super.onRewind();
                                     Timber.d("onRewind");
                                     //Manipulate current media here
                                     seekBackward();
                                 }

                                 @Override
                                 public void onStop() {
                                     super.onStop();
                                     Timber.d("onStop");
                                     //Stop media player here
                                     mediaPlayer.stop();
                                     NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                     notificationManager.cancel( 1 );
                                     stopSelf();
                                     /*Intent intent = new Intent( getApplicationContext(), MediaPlaybackService.class );
                                     stopService( intent );*/
                                 }

                                 @Override
                                 public void onSeekTo(long pos) {
                                     super.onSeekTo(pos);
                                 }

                                 @Override
                                 public void onSetRating(Rating rating) {
                                     super.onSetRating(rating);
                                 }
                             }
        );
    }

}
