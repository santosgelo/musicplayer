package gelo.com.musicplayer.ui.view.nowplaying;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import gelo.com.musicplayer.AndroidApplication;
import gelo.com.musicplayer.R;
import gelo.com.musicplayer.data.entity.Song;
import gelo.com.musicplayer.mvp.mosby.BaseActivity;
import gelo.com.musicplayer.ui.adapter.RecyclerViewItemClickListener;
import gelo.com.musicplayer.ui.presenter.NowPlayingPresenter;
import gelo.com.musicplayer.util.StringUtils;
import timber.log.Timber;


public class NowPlayingActivity extends BaseActivity<NowPlayingView,NowPlayingPresenter> implements NowPlayingView {

    NowPlayingComponent component;

    @Override
    protected void createComponent() {
        component = DaggerNowPlayingComponent.builder()
                    .applicationComponent(((AndroidApplication)getApplication()).getComponent())
                    .build();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        Timber.tag("NowPlayingActivity");
        Timber.d("onCreate");
        String trackId = getIntent().getStringExtra(EXTRA_TRACK_ID);
        Timber.d("trackId "+trackId);
        //we need to build the UI here
        //set the album song
    }
    @NonNull
    @Override
    public NowPlayingPresenter createPresenter() {
        return component.presenter();
    }

    @Override
    public void startPlayingSong(Song song) {

    }

    @Override
    public void updateSeekBar(int position) {

    }

    @Override
    public void setAlbumArt(Uri uri) {

    }

    @Override
    public void setSongTitle(String title) {

    }

    @Override
    public void setSongArtist(String artist) {

    }

    private static final String EXTRA_TRACK_ID = "extra_track_id";
    /**
     * Generates the intent neede by the client code to launch this activity. This method is a sample
     * of who to avoid duplicate this code by all the application.
     */
    public static Intent getLaunchIntent(final Context context, final String trackId) {
        if (StringUtils.isNullOrEmpty(trackId)) {
            throwIllegalArgumentException();
        }
        Intent intent = new Intent(context, NowPlayingActivity.class);
        return intent.putExtra(EXTRA_TRACK_ID, trackId);
    }

    private static void throwIllegalArgumentException() {
        throw new IllegalArgumentException(
                "NowPlayingActivity has to be launched using a track identifier as extra");
    }
}
