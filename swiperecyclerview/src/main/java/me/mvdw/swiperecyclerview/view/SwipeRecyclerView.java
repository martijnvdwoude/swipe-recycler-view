package me.mvdw.swiperecyclerview.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import me.mvdw.recyclerviewmergeadapter.adapter.RecyclerViewMergeAdapter;
import me.mvdw.swiperecyclerview.adapter.SwipeRecyclerViewAdapter;
import me.mvdw.swiperecyclerview.adapter.SwipeRecyclerViewHeaderFooterAdapter;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public class SwipeRecyclerView extends RecyclerView {

    private int mFrontViewResourceId;
    private int mBackLeftViewResourceId;
    private int mBackRightViewResourceId;

    private VelocityTracker mVelocityTracker;
    private float mVelocityX = 0;
    private int mTouchSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    private SwipeRecyclerViewRowView mTouchedRowView;
    private SwipeRecyclerViewRowView mOpenedRowView;

    private float startX = 0;
    private float mTouchedRowViewTranslationX;

    private TimeInterpolator mCloseRowInterpolator;

    private boolean mIsAnimating;
    private boolean mIsSwiping;
    private boolean mFrontViewClicked;

    private OpenStatus mOpenStatus = OpenStatus.CLOSED;

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
     * Set merge adapter and loop through the adapters and set the view resource Id's if an adapter is swipeable
     *
     * @param adapter
     */
    public void setAdapter(RecyclerViewMergeAdapter adapter) {
        for(int i = 0; i < adapter.getSubAdapterCount(); i++){
            if(adapter.getSubAdapter(i) instanceof SwipeRecyclerViewAdapter){
                SwipeRecyclerViewAdapter subAdapter = (SwipeRecyclerViewAdapter) adapter.getSubAdapter(i);

                subAdapter.setBackLeftViewResourceId(subAdapter.getBackLeftViewResourceId() != 0 ? subAdapter.getBackLeftViewResourceId() : mBackLeftViewResourceId);
                subAdapter.setBackRightViewResourceId(subAdapter.getBackRightViewResourceId() != 0 ? subAdapter.getBackRightViewResourceId() : mBackRightViewResourceId);
                subAdapter.setFrontViewResourceId(subAdapter.getFrontViewResourceId() != 0 ? subAdapter.getFrontViewResourceId() : mFrontViewResourceId);

            } else if(adapter.getSubAdapter(i) instanceof SwipeRecyclerViewHeaderFooterAdapter){
                SwipeRecyclerViewHeaderFooterAdapter subAdapter = (SwipeRecyclerViewHeaderFooterAdapter) adapter.getSubAdapter(i);

                subAdapter.setBackLeftViewResourceId(subAdapter.getBackLeftViewResourceId() != 0 ? subAdapter.getBackLeftViewResourceId() : mBackLeftViewResourceId);
                subAdapter.setBackRightViewResourceId(subAdapter.getBackRightViewResourceId() != 0 ? subAdapter.getBackRightViewResourceId() : mBackRightViewResourceId);
                subAdapter.setFrontViewResourceId(subAdapter.getFrontViewResourceId() != 0 ? subAdapter.getFrontViewResourceId() : mFrontViewResourceId);
            }
        }

        super.setAdapter(adapter);
    }

    /**
     * Set swipeable adapter without header and footer support
     *
     * @param adapter
     */
    public void setAdapter(SwipeRecyclerViewAdapter adapter) {
        adapter.setBackLeftViewResourceId(adapter.getBackLeftViewResourceId() != 0 ? adapter.getBackLeftViewResourceId() : mBackLeftViewResourceId);
        adapter.setBackRightViewResourceId(adapter.getBackRightViewResourceId() != 0 ? adapter.getBackRightViewResourceId() : mBackRightViewResourceId);
        adapter.setFrontViewResourceId(adapter.getFrontViewResourceId() != 0 ? adapter.getFrontViewResourceId() : mFrontViewResourceId);

        super.setAdapter(adapter);
    }

    /**
     * Set swipeable adapter with header and footer support
     *
     * @param adapter
     */
    public void setAdapter(SwipeRecyclerViewHeaderFooterAdapter adapter) {
        adapter.setBackLeftViewResourceId(adapter.getBackLeftViewResourceId() != 0 ? adapter.getBackLeftViewResourceId() : mBackLeftViewResourceId);
        adapter.setBackRightViewResourceId(adapter.getBackRightViewResourceId() != 0 ? adapter.getBackRightViewResourceId() : mBackRightViewResourceId);
        adapter.setFrontViewResourceId(adapter.getFrontViewResourceId() != 0 ? adapter.getFrontViewResourceId() : mFrontViewResourceId);

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
        float deltaX = calculateDeltaX(motionEvent);

        switch (motionEvent.getActionMasked()){

            case MotionEvent.ACTION_DOWN:

                mIsSwiping = false;
                mFrontViewClicked = false;

                // Set position of touch event
                startX = motionEvent.getX();

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

                    if (rect.contains(x, y) && child instanceof SwipeRecyclerViewRowView) {
                        // Save the touched row and pass the event to the row
                        mTouchedRowView = ((SwipeRecyclerViewRowView) child);

                        // Save translation of touched row
                        mTouchedRowViewTranslationX = mTouchedRowView.getFrontView().getTranslationX();

                        if(!hasOpenedItem()) {
                            mVelocityTracker = VelocityTracker.obtain();
                        } else if(hasOpenedItem() && mTouchedRowView != mOpenedRowView) {
                            closeOpenedItem();

                            return true;
                        } else if(hasOpenedItem()) {
                            // Get hit rects of back views
                            Rect backLeftRect = new Rect();

                            if (((SwipeRecyclerViewRowView) child).getBackLeftView() != null) {
                                ((SwipeRecyclerViewRowView) child).getBackLeftView().getHitRect(backLeftRect);
                            }

                            Rect backRightRect = new Rect();

                            if (((SwipeRecyclerViewRowView) child).getBackRightView() != null) {
                                ((SwipeRecyclerViewRowView) child).getBackRightView().getHitRect(backRightRect);
                            }

                            // If a click happens in the hit rects of the back views while the row is open, pass the event to the row
                            if (mOpenStatus == OpenStatus.LEFT
                                    && backLeftRect.contains(x, 0)
                                    && mTouchedRowView == mOpenedRowView) {

                                return false;
                            } else if (mOpenStatus == OpenStatus.RIGHT
                                    && backRightRect.contains(x, 0)
                                    && mTouchedRowView == mOpenedRowView) {

                                return false;
                            } else {
                                mFrontViewClicked = true;
                            }
                        }
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE:

                if(!mIsAnimating && mTouchedRowView != null) {
                    // Measure velocity
                    mVelocityTracker.addMovement(motionEvent);
                    mVelocityTracker.computeCurrentVelocity(1000);
                    float velocityX = mVelocityTracker.getXVelocity();
                    mVelocityX = Math.abs(velocityX);

                    // Make front view follow position of the finger only if view is not scrolling
                    if (deltaX > 0 && Math.abs(deltaX) > mTouchSlop
                            && getScrollState() == SCROLL_STATE_IDLE) {
                        mTouchedRowView.getFrontView().setTranslationX(motionEvent.getRawX() + mTouchedRowViewTranslationX - startX - mTouchSlop);
                        updateFrontViewTranslationObservables(mTouchedRowView.getFrontView());

                        mIsSwiping = true;
                    } else if (deltaX < 0 && Math.abs(deltaX) > mTouchSlop
                            && getScrollState() == SCROLL_STATE_IDLE) {
                        mTouchedRowView.getFrontView().setTranslationX(motionEvent.getRawX() + mTouchedRowViewTranslationX - startX + mTouchSlop);
                        updateFrontViewTranslationObservables(mTouchedRowView.getFrontView());

                        mIsSwiping = true;
                    }

                    // Prevent scrolling if row is swiping open or a row is already open
                    if(mIsSwiping || hasOpenedItem()){
                        return false;
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                mIsSwiping = false;

                mTouchedRowViewTranslationX = 0;

                if(mTouchedRowView != null && getScrollState() == SCROLL_STATE_IDLE){
                    switch(mOpenStatus){

                        case CLOSED:
                            if(deltaX < 0 && Math.abs(deltaX) > mTouchSlop){
                                // Swiped to the left
                                // If swiped with enough velocity
                                // Or swiped past half of the back view
                                if (mVelocityX >= mMinFlingVelocity
                                        || mTouchedRowView.getFrontView().getTranslationX() < mTouchedRowView.getBackRightView().getWidth() / 2 * -1) {
                                    mOpenedRowView = mTouchedRowView;
                                    float distanceToTravel = mTouchedRowView.getBackRightView().getWidth() - Math.abs(mTouchedRowView.getFrontView().getTranslationX());

                                    if(Math.abs(deltaX) < mTouchedRowView.getBackRightView().getWidth()
                                            && mVelocityX >= mMinFlingVelocity) {
                                        openBackRightView(getAnimationDurationForVelocity(mVelocityX, distanceToTravel));
                                        mTouchedRowView = null;
                                    } else {
                                        openBackRightView(300);
                                        mTouchedRowView = null;
                                    }
                                } else {
                                    closeOpenedItem();
                                }

                                return true;
                            } else if(deltaX > 0 && Math.abs(deltaX) > mTouchSlop){
                                // Swiped to the right
                                // If swiped with enough velocity
                                // Or swiped past half of the back view
                                if (mVelocityX >= mMinFlingVelocity
                                        || mTouchedRowView.getFrontView().getTranslationX() > mTouchedRowView.getBackLeftView().getWidth() / 2) {
                                    mOpenedRowView = mTouchedRowView;
                                    float distanceToTravel = mTouchedRowView.getBackLeftView().getWidth() - mTouchedRowView.getFrontView().getTranslationX();

                                    if(deltaX < mTouchedRowView.getBackLeftView().getWidth()
                                            && mVelocityX >= mMinFlingVelocity) {
                                        openBackLeftView(getAnimationDurationForVelocity(mVelocityX, distanceToTravel));
                                        mTouchedRowView = null;
                                    } else {
                                        openBackLeftView(300);
                                        mTouchedRowView = null;
                                    }
                                } else {
                                    closeOpenedItem();
                                }

                                return true;
                            }

                            break;

                        case LEFT:
                            if(deltaX > 0 && Math.abs(deltaX) > mTouchSlop){
                                // Swiped to the right
                                // Animate view back to where it was
                                openBackLeftView(300);
                                mTouchedRowView = null;

                                if(mFrontViewClicked){
                                    return true;
                                }
                            } else if(deltaX < 0 && Math.abs(deltaX) > mTouchSlop){
                                // Swiped to the left
                                // If swiped with enough velocity
                                if (mVelocityX >= mMinFlingVelocity
                                        || mTouchedRowView.getFrontView().getTranslationX() < mTouchedRowView.getBackLeftView().getWidth() / 2) {
                                    closeOpenedItem();
                                } else {
                                    // Animate view back to where it was
                                    openBackLeftView(300);
                                    mTouchedRowView = null;
                                }

                                if(mFrontViewClicked){
                                    return true;
                                }
                            } else {
                                // If clicking and no movement, just close the opened row
                                closeOpenedItem();

                                if(mFrontViewClicked){
                                    return true;
                                }
                            }

                            break;

                        case RIGHT:
                            if(deltaX < 0 && Math.abs(deltaX) > mTouchSlop){
                                // Swiped to the left
                                // Animate view back to where it was
                                openBackRightView(300);
                                mTouchedRowView = null;

                                if(mFrontViewClicked){
                                    return true;
                                }
                            } else if(deltaX > 0 && Math.abs(deltaX) > mTouchSlop){
                                // Swiped to the right
                                // If swiped with enough velocity
                                // Or swiped past half of the back view
                                if (mVelocityX >= mMinFlingVelocity
                                        || mTouchedRowView.getFrontView().getTranslationX() > mTouchedRowView.getBackRightView().getWidth() / 2 * -1) {
                                    closeOpenedItem();
                                } else {
                                    // Animate view back to where it was
                                    openBackRightView(300);
                                    mTouchedRowView = null;
                                }

                                if(mFrontViewClicked){
                                    return true;
                                }
                            } else {
                                // If clicking and no movement, just close the opened row
                                closeOpenedItem();

                                if(mFrontViewClicked){
                                    return true;
                                }
                            }

                            break;
                    }
                }

                break;
        }

        return super.onInterceptTouchEvent(motionEvent);
    }

    private float calculateDeltaX(MotionEvent motionEvent){
        return motionEvent.getX() - startX;
    }

    /**
     * Private animation methods
     *
     */
    private void openBackLeftView(int animationDuration){
        mOpenStatus = OpenStatus.LEFT;

        ObjectAnimator anim = ObjectAnimator.ofFloat(
                mOpenedRowView.getFrontView(),
                "translationX",
                mOpenedRowView.getFrontView().getTranslationX(),
                0f + mOpenedRowView.getBackLeftView().getWidth());

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                updateFrontViewTranslationObservables(mOpenedRowView.getFrontView());
            }
        });

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        anim.setDuration(animationDuration);
        anim.start();
    }

    private void openBackRightView(int animationDuration){
        mOpenStatus = OpenStatus.RIGHT;

        ObjectAnimator anim = ObjectAnimator.ofFloat(
                mOpenedRowView.getFrontView(),
                "translationX",
                mOpenedRowView.getFrontView().getTranslationX(),
                0f - mOpenedRowView.getBackRightView().getWidth());

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                updateFrontViewTranslationObservables(mOpenedRowView.getFrontView());
            }
        });

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        anim.setDuration(animationDuration);
        anim.start();
    }

    private int getAnimationDurationForVelocity(float velocity, float distanceToTravel){
        return (int) (distanceToTravel / (velocity / 1000));
    }

    /**
     * Public methods
     *
     */
    public void setCloseRowInterpolator(TimeInterpolator closeInterpolator){
        this.mCloseRowInterpolator = closeInterpolator;
    }

    public boolean hasOpenedItem(){
        return mOpenStatus != OpenStatus.CLOSED;
    }

    public void closeOpenedItem(){
        mOpenStatus = OpenStatus.CLOSED;

        ObjectAnimator anim = new ObjectAnimator();

        if(mOpenedRowView != null) {
            anim = ObjectAnimator.ofFloat(
                    mOpenedRowView.getFrontView(),
                    "translationX",
                    mOpenedRowView.getFrontView().getTranslationX(),
                    0f);

            // Update the front view translation observable during updates of the animation
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    updateFrontViewTranslationObservables(mOpenedRowView.getFrontView());
                }
            });
        } else if(mTouchedRowView != null) {
            anim = ObjectAnimator.ofFloat(
                    mTouchedRowView.getFrontView(),
                    "translationX",
                    mTouchedRowView.getFrontView().getTranslationX(),
                    0f);

            // Update the front view translation observable during updates of the animation
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    updateFrontViewTranslationObservables(mTouchedRowView.getFrontView());
                }
            });
        }

        // Add animator listener to set the opened row to null only when the animation ends
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mOpenedRowView = null;
                mTouchedRowView = null;
                mIsAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mOpenedRowView = null;
                mTouchedRowView = null;
                mIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        if(mCloseRowInterpolator != null) {
            anim.setInterpolator(mCloseRowInterpolator);
        }

        anim.setDuration(300);
        anim.start();
    }

    /**
     * Update front view translation observables for all adapters that have it enabled
     * and pass the swiping front view to the observables
     */
    private void updateFrontViewTranslationObservables(ViewGroup frontView){
        Adapter adapter = getAdapter();

        if(adapter instanceof SwipeRecyclerViewAdapter){
            if(((SwipeRecyclerViewAdapter) adapter).isFrontViewTranslationObservableEnabled())
                ((SwipeRecyclerViewAdapter) adapter).getFrontViewTranslationObservable().frontViewTranslationChanged(frontView);

        } else if(adapter instanceof SwipeRecyclerViewHeaderFooterAdapter){
            if(((SwipeRecyclerViewHeaderFooterAdapter) adapter).isFrontViewTranslationObservableEnabled())
                ((SwipeRecyclerViewHeaderFooterAdapter) adapter).getFrontViewTranslationObservable().frontViewTranslationChanged(frontView);

        } else if(adapter instanceof RecyclerViewMergeAdapter){
            for(int i = 0; i < ((RecyclerViewMergeAdapter) adapter).getSubAdapterCount(); i++){
                if(((RecyclerViewMergeAdapter) adapter).getSubAdapter(i) instanceof SwipeRecyclerViewAdapter){
                    SwipeRecyclerViewAdapter subAdapter = (SwipeRecyclerViewAdapter) ((RecyclerViewMergeAdapter) adapter).getSubAdapter(i);

                    if(subAdapter.isFrontViewTranslationObservableEnabled())
                        subAdapter.getFrontViewTranslationObservable().frontViewTranslationChanged(frontView);
                } else if(((RecyclerViewMergeAdapter) adapter).getSubAdapter(i) instanceof SwipeRecyclerViewHeaderFooterAdapter){
                    SwipeRecyclerViewHeaderFooterAdapter subAdapter = (SwipeRecyclerViewHeaderFooterAdapter) ((RecyclerViewMergeAdapter) adapter).getSubAdapter(i);

                    if(subAdapter.isFrontViewTranslationObservableEnabled())
                        subAdapter.getFrontViewTranslationObservable().frontViewTranslationChanged(frontView);
                }
            }
        }
    }
}