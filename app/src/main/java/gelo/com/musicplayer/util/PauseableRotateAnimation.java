package gelo.com.musicplayer.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

/**
 * Custom RotateAnimation class which can pause/resume by control
 */
public class PauseableRotateAnimation extends RotateAnimation {
    private long mElapsedAtPause=0;
    private boolean mPaused=false;
    public PauseableRotateAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PauseableRotateAnimation(float fromDegrees, float toDegrees) {
        super(fromDegrees, toDegrees);
    }

    public PauseableRotateAnimation(float fromDegrees, float toDegrees, float pivotX, float pivotY) {
        super(fromDegrees, toDegrees, pivotX, pivotY);
    }

    public PauseableRotateAnimation(float fromDegrees, float toDegrees, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        super(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation) {
        if(mPaused && mElapsedAtPause==0) {
            mElapsedAtPause=currentTime-getStartTime();
        }
        if(mPaused)
            setStartTime(currentTime-mElapsedAtPause);
        return super.getTransformation(currentTime, outTransformation);
    }

    public void pause() {
        mElapsedAtPause=0;
        mPaused=true;
    }

    public void resume() {
        mPaused=false;
    }
}
