package gelo.com.musicplayer.ui.view;


import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

import javax.inject.Inject;

import gelo.com.musicplayer.data.service.MediaPlaybackService;
import gelo.com.musicplayer.ui.view.nowplaying.NowPlayingActivity;

public class Navigator {
    private final Context context;

    @Inject
    public Navigator(Context activityContext){
        this.context = activityContext;
    }

    public void openNowPlayingActivity(String trackId){
        Intent intent = NowPlayingActivity.getLaunchIntent(context, trackId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startActivity(Intent intent) {
        context.startActivity(intent);
    }

}
