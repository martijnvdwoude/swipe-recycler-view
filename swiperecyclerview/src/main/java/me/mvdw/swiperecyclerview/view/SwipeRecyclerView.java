package me.mvdw.swiperecyclerview.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import me.mvdw.swiperecyclerview.adapter.SwipeRecyclerViewContentAdapter;
import me.mvdw.swiperecyclerview.adapter.SwipeRecyclerViewMergeAdapter;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public class SwipeRecyclerView extends RecyclerView {

    private SwipeRecyclerViewRowView mTouchedRowView;
    private SwipeRecyclerViewRowView mOpenedRowView;

    float startX = 0;
//    float velocityX = 0;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    int mFrontViewResourceId;
    int mBackLeftViewResourceId;
    int mBackRightViewResourceId;

    private OpeningStatus mOpeningStatus;
    private OpenStatus mOpenStatus = OpenStatus.CLOSED;

    private enum OpeningStatus {
        NOT_OPENING,
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
        mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
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
                        // Save the touched row and pass the event to the row
                        mTouchedRowView = ((SwipeRecyclerViewRowView) child);

                        if(mOpenStatus != OpenStatus.CLOSED){
                            // Get hit rects of back views
                            Rect backLeftRect = new Rect();

                            if(((SwipeRecyclerViewRowView) child).getBackLeftView() != null) {
                                ((SwipeRecyclerViewRowView) child).getBackLeftView().getHitRect(backLeftRect);
                            }

                            Rect backRightRect = new Rect();

                            if(((SwipeRecyclerViewRowView) child).getBackRightView() != null) {
                                ((SwipeRecyclerViewRowView) child).getBackRightView().getHitRect(backRightRect);
                            }

                            // If a click happens in the hit rects of the back views, pass the event to the row
                            if(mOpenStatus == OpenStatus.LEFT
                                    && backLeftRect.contains(x, 0)
                                    && mTouchedRowView == mOpenedRowView){
                                return false;
                            } else if(mOpenStatus == OpenStatus.RIGHT
                                    && backRightRect.contains(x, 0)
                                    && mTouchedRowView == mOpenedRowView){
                                return false;
                            }

                            // Otherwise intercept it and just close the row
                            closeOpenedItem();

                            return true;

                        } else {
                            mVelocityTracker = VelocityTracker.obtain();
                            mVelocityTracker.addMovement(motionEvent);

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

                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();
                float absVelocityX = Math.abs(velocityX);

                if(mTouchedRowView != null){
                    if(deltaX < 0 && Math.abs(deltaX) > mTouchSlop){
                        // Swipe left, revealing right view
                        mTouchedRowView.getFrontView().setTranslationX(motionEvent.getRawX() - startX + mTouchSlop);

                        if(absVelocityX >= mMinFlingVelocity
                                || Math.abs(deltaX) > mTouchedRowView.getBackRightView().getWidth() / 2)
                            mOpeningStatus = OpeningStatus.LEFT;
                        else
                            mOpeningStatus = OpeningStatus.NOT_OPENING;

                        return true;
                    } else if(deltaX > 0 && Math.abs(deltaX) > mTouchSlop){
                        // Swipe right, revealing left view
                        mTouchedRowView.getFrontView().setTranslationX(motionEvent.getRawX() - startX + mTouchSlop);

                        if(absVelocityX >= mMinFlingVelocity
                                || Math.abs(deltaX) > mTouchedRowView.getBackLeftView().getWidth() / 2)
                            mOpeningStatus = OpeningStatus.RIGHT;
                        else
                            mOpeningStatus = OpeningStatus.NOT_OPENING;

                        return true;
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                if(mOpeningStatus == OpeningStatus.LEFT){
                    mOpenedRowView = mTouchedRowView;
                    openRightAnimated();
                    mOpeningStatus = OpeningStatus.NOT_OPENING;
                } else if(mOpeningStatus == OpeningStatus.RIGHT){
                    mOpenedRowView = mTouchedRowView;
                    openLeftAnimated();
                    mOpeningStatus = OpeningStatus.NOT_OPENING;
                } else if(mOpeningStatus == OpeningStatus.NOT_OPENING) {
                    closeOpenedItem();
                }

                mTouchedRowView = null;

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

        ObjectAnimator anim = ObjectAnimator.ofFloat(
                mOpenedRowView.getFrontView(),
                "translationX",
                mOpenedRowView.getFrontView().getTranslationX(),
                0f + mOpenedRowView.getBackLeftView().getWidth());

        anim.setDuration(300);
        anim.start();
    }

    private void openRightAnimated(){
        mOpenStatus = OpenStatus.RIGHT;

        ObjectAnimator anim = ObjectAnimator.ofFloat(
                mOpenedRowView.getFrontView(),
                "translationX",
                mOpenedRowView.getFrontView().getTranslationX(),
                0f - mOpenedRowView.getBackRightView().getWidth());

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

        if(mOpenedRowView != null) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(
                    mOpenedRowView.getFrontView(),
                    "translationX",
                    mOpenedRowView.getFrontView().getTranslationX(),
                    0f);

            anim.setDuration(300);
            anim.start();
        } else if(mTouchedRowView != null) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(
                    mTouchedRowView.getFrontView(),
                    "translationX",
                    mTouchedRowView.getFrontView().getTranslationX(),
                    0f);

            anim.setDuration(300);
            anim.start();
        }

        mOpenedRowView = null;
    }
}