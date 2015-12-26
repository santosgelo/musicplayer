package gelo.com.musicplayer.ui.view.nowplaying;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.ScrollView;

import gelo.com.musicplayer.R;
import timber.log.Timber;

/**
 * A custom view for song player controls
 */
public class SongPlayerView extends ScrollView {

    private static final float FLOAT_THRESHOLD_RIGHT = 0.7f;
    private static final float FLOAT_THRESHOLD_LEFT = 0.3f;
    private static final float FLOAT_THRESHOLD_UP = 0.7f;
    private static final float FLOAT_THRESHOLD_DOWN = 0.7f;
    private boolean enableScrolling = false;
    private OnSwipeListener onSwipeListener;
    private float lastActionDownPointX = 0;
    private float lastActionDownPointY = 0;


    private boolean isEnableScrolling() {
        return enableScrolling;
    }

    public void setEnableScrolling(boolean enableScrolling) {
        this.enableScrolling = enableScrolling;
    }

    public SongPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SongPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SongPlayerView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        if (isEnableScrolling()) {
            return super.onInterceptTouchEvent(event);
        } else {
            int action = event.getAction();
            Timber.d("onInterceptTouchEvent " + action);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Timber.d("ACTION_DOWN " + event.getX());
                    lastActionDownPointX = event.getX();
                    lastActionDownPointY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //when a significant swipe from the center or within the Play button is detected,
                    //this means user wants to play the next or prev song
                    ImageButton btnPlay = (ImageButton) this.findViewById(R.id.btn_play_music);
                    float x = event.getX() / ((float) getWidth());
                    float y = event.getY() / ((float) getHeight());
                    float touchEndedX = event.getX();
                    float touchEndedY = event.getY();
                    float startX = btnPlay.getX();
                    float endX = startX + btnPlay.getWidth();
                    float startY = btnPlay.getY();
                    float endY = startY + btnPlay.getHeight();
                    Timber.d("Swiped "+touchEndedY + " "+lastActionDownPointY +" "+ btnPlay.getWidth());
                    if ((Math.abs(touchEndedX - lastActionDownPointX) > btnPlay.getWidth()) //swipe is long enough horizontally
                            && lastActionDownPointX > startX && lastActionDownPointX < endX //swipe is from play button horizontal bounds
                            && lastActionDownPointY > startY && lastActionDownPointY < endY) { //swipe is from play button vertical bounds
                        if (onSwipeListener != null) {
                            if (x >= FLOAT_THRESHOLD_RIGHT) {
                                onSwipeListener.onSwipedRight();
                            } else if (x <= FLOAT_THRESHOLD_LEFT) {
                                onSwipeListener.onSwipedLeft();
                            }
                            lastActionDownPointX = 0;
                            lastActionDownPointY = 0;
                        }

                        Timber.d("onInterceptTouchEvent return false");
                        return false;
                    }/* else if((Math.abs(touchEndedY - lastActionDownPointY) > 50) //swipe is long enough horizontally
                            && lastActionDownPointX > startX && lastActionDownPointX < endX //swipe is from play button horizontal bounds
                            && lastActionDownPointY > startY && lastActionDownPointY < endY) { //swipe is from play button vertical bounds
                        if (onSwipeListener != null) {
                            if (y >= FLOAT_THRESHOLD_UP) {
                                //onSwipeListener.onSwipedRight();
                                Log.d(TAG,"Swiped UP");
                            } else if (y <= FLOAT_THRESHOLD_DOWN) {
                                //onSwipeListener.onSwipedLeft();
                                Log.d(TAG,"Swiped DOWN");
                            }
                            lastActionDownPointX = 0;
                            lastActionDownPointY = 0;
                        }
                        return false;
                    }*/
                    break;
            }
            return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        requestDisallowInterceptTouchEvent(true);
        if (isEnableScrolling()) {
            return super.onTouchEvent(ev);
        } else {
            int action = ev.getAction();
            Timber.d("onTouchEvent "+ action);
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    //when the view detects move on the disc,
                    //we rotate the disc
                    //Log.d(TAG, "onTouchEvent ACTION_MOVE ");
                    //TODO move this to fragment
                    float x = ev.getX() / ((float) getWidth());
                    float y = ev.getY() / ((float) getHeight());
                    //Log.d(TAG, "onTouchEvent ACTION_MOVE "+x + " "+y);
                    float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
                    if (onSwipeListener != null) {
                        onSwipeListener.onSwiped(rotDegrees);
                    }
                    break;
            }
            return false;
        }
    }

    private float cartesianToPolar(float x, float y) {
        return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    public interface OnSwipeListener {
        void onSwiped(float movement);

        void onSwipedRight();

        void onSwipedLeft();
    }

}