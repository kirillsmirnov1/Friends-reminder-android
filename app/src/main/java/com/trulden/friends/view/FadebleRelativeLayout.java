package com.trulden.friends.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.trulden.friends.R;

/**
 * CardView with «faded» state
 */
public class FadebleRelativeLayout extends RelativeLayout {

    private boolean mFaded = false;
    private static final int[] FADED_STATE = new int[] { R.attr.state_faded };

    public FadebleRelativeLayout(@NonNull Context context) {
        super(context);
    }

    public FadebleRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {

        int[] state = super.onCreateDrawableState(extraSpace + 1);
        if(mFaded){
            mergeDrawableStates(state, FADED_STATE);
        }
        return state;
    }

    public void setFaded(boolean faded){
        if(mFaded!=faded) {
            mFaded = faded;
            refreshDrawableState();
        }
    }
}
