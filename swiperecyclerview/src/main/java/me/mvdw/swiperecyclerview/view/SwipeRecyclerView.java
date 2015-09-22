package me.mvdw.swiperecyclerview.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import me.mvdw.swiperecyclerview.adapter.SwipeRecyclerViewContentAdapter;
import me.mvdw.swiperecyclerview.adapter.SwipeRecyclerViewMergeAdapter;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public class SwipeRecyclerView extends RecyclerView {

    private SwipeRecyclerViewRowView mTouchedRowView;

    float startX = 0;
//    float velocityX = 0;
//    private VelocityTracker velocityTracker;
    private int mTouchSlop;

    int mFrontViewResourceId;
    int mBackLeftViewResourceId;
    int mBackRightViewResourceId;

    private SwipeStatus mSwipeStatus;
    private OpenStatus mOpenStatus = OpenStatus.CLOSED;

    private enum SwipeStatus {
        NOT_SWIPING,
        LEFT,
        RIGHT
    }

    private enum OpenStatus {
        CLOSED,
        LEFT,
        RIGHT
    }

    public SwipeRecyclerView(Context context) {
        super(context);
    }

    public SwipeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                me.mvdw.swiperecyclerview.R.styleable.SwipeRecyclerView,
                0, 0);

        try {
            mFrontViewResourceId = a.getResourceId(me.mvdw.swiperecyclerview.R.styleable.SwipeRecyclerView_frontView, 0);
            mBackLeftViewResourceId = a.getResourceId(me.mvdw.swiperecyclerview.R.styleable.SwipeRecyclerView_backLeftView, 0);
            mBackRightViewResourceId = a.getResourceId(me.mvdw.swiperecyclerview.R.styleable.SwipeRecyclerView_backRightView, 0);
        } finally {
            a.recycle();
        }

        ViewConfiguration viewConfiguration = ViewConfiguration.get(this.getContext());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    public SwipeRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set merge adapter for header and footer support
     *
     * @param adapter
     */
    public void setAdapter(SwipeRecyclerViewMergeAdapter adapter) {
        for(int i = 0; i < adapter.getSubAdapterCount(); i++){
            if(adapter.getSubAdapter(i) instanceof SwipeRecyclerViewContentAdapter){
                SwipeRecyclerViewContentAdapter subAdapter = (SwipeRecyclerViewContentAdapter) adapter.getSubAdapter(i);
                subAdapter.setBackLeftViewResource(mBackLeftViewResourceId);
                subAdapter.setBackRightViewResource(mBackRightViewResourceId);
                subAdapter.setFrontViewResource(mFrontViewResourceId);
            }
        }

        super.setAdapter(adapter);
    }

    /**
     * Set regular content adapter without header and footer support
     *
     * @param adapter
     */
    public void setAdapter(SwipeRecyclerViewContentAdapter adapter) {
        adapter.setBackLeftViewResource(mFrontViewResourceId);
        adapter.setBackRightViewResource(mBackRightViewResourceId);
        adapter.setFrontViewResource(mFrontViewResourceId);

        super.setAdapter(adapter);
    }

    /**
     * Handle and intercept touch events for the recyclerview
     *
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()){
            case MotionEvent.ACTION_DOWN:

                startX = motionEvent.getRawX();

                Rect rect = new Rect();
                int childCount = this.getChildCount();
                int[] listViewCoords = new int[2];
                this.getLocationOnScreen(listViewCoords);
                int x = (int) motionEvent.getRawX() - listViewCoords[0];
                int y = (int) motionEvent.getRawY() - listViewCoords[1];
                View child;
                for (int i = 0; i < childCount; i++) {
                    child = this.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        if(mOpenStatus != OpenStatus.CLOSED){
                            View backLeftView = ((SwipeRecyclerViewRowView) child).getBackLeftView();
                            Rect backLeftRect = new Rect();

                            if(backLeftView != null) {
                                backLeftView.getHitRect(backLeftRect);
                            }

                            View backRightView = ((SwipeRecyclerViewRowView) child).getBackRightView();
                            Rect backRightRect = new Rect();

                            if(backRightView != null) {
                                backRightView.getHitRect(backRightRect);
                            }

                            // If a click happens in the area of the back views, pass the event to the row
                            if(mOpenStatus == OpenStatus.LEFT && backLeftRect.contains(x, 0)){
                                return false;
                            } else if(mOpenStatus == OpenStatus.RIGHT && backRightRect.contains(x, 0)){
                                return false;
                            }

                            // Otherwise intercept it and just close the row
                            closeOpenedItem();

                            return true;

                        } else {
                            mTouchedRowView = ((SwipeRecyclerViewRowView) child);

                            return false;
                        }
                    }
                }

            case MotionEvent.ACTION_MOVE:
                return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()){
            case MotionEvent.ACTION_MOVE:

                final float deltaX = calculateDeltaX(motionEvent);

                if(mTouchedRowView != null){
                    if(deltaX < 0 && Math.abs(deltaX) > mTouchSlop){
                        // Swipe left
                        View frontView = mTouchedRowView.getFrontView();
                        frontView.setTranslationX(motionEvent.getRawX() - startX);
                        mSwipeStatus = SwipeStatus.LEFT;
                        return true;
                    } else if(deltaX > 0 && Math.abs(deltaX) > mTouchSlop){
                        // Swipe right
                        View frontView = mTouchedRowView.getFrontView();
                        frontView.setTranslationX(motionEvent.getRawX() - startX);
                        mSwipeStatus = SwipeStatus.RIGHT;
                        return true;
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                if(mSwipeStatus == SwipeStatus.LEFT){
                    openRightAnimated();
                } else if(mSwipeStatus == SwipeStatus.RIGHT){
                    openLeftAnimated();
                }

                mSwipeStatus = SwipeStatus.NOT_SWIPING;

                break;
        }

        return super.onTouchEvent(motionEvent);
    }

    private float calculateDeltaX(MotionEvent motionEvent){
        return motionEvent.getX() - startX;
    }

    /**
     * Preliminary animation methods
     *
     */
    private void openLeftAnimated(){
        mOpenStatus = OpenStatus.LEFT;

        View frontView = mTouchedRowView.getFrontView();
        View backLeftView = mTouchedRowView.getBackLeftView();
        ObjectAnimator anim = ObjectAnimator.ofFloat(frontView, "translationX", frontView.getTranslationX(), 0f + backLeftView.getWidth());
        anim.setDuration(300);
        anim.start();
    }

    private void openRightAnimated(){
        mOpenStatus = OpenStatus.RIGHT;

        View frontView = mTouchedRowView.getFrontView();
        View backRightView = mTouchedRowView.getBackRightView();
        ObjectAnimator anim = ObjectAnimator.ofFloat(frontView, "translationX", frontView.getTranslationX(), 0f - backRightView.getWidth());
        anim.setDuration(300);
        anim.start();
    }

    /**
     * Public methods
     *
     */
    public boolean hasOpenedItem(){
        return mOpenStatus != OpenStatus.CLOSED;

    }

    public void closeOpenedItem(){
        mOpenStatus = OpenStatus.CLOSED;

        View frontView = mTouchedRowView.getFrontView();
        ObjectAnimator anim = ObjectAnimator.ofFloat(frontView, "translationX", frontView.getTranslationX(), 0f);
        anim.setDuration(300);
        anim.start();
    }
}