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

    private Context mContext;

    private SwipeRecyclerViewRowView mTouchedRowView;

    float startX = 0;
//    float velocityX = 0;

    private boolean mOpened;
    private boolean mIsSwiping;

//    private VelocityTracker velocityTracker;
    private int mTouchSlop;

    int mFrontViewResourceId;
    int mBackLeftViewResourceId;
    int mBackRightViewResourceId;

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
            try {
                SwipeRecyclerViewContentAdapter subAdapter = (SwipeRecyclerViewContentAdapter) adapter.getSubAdapter(i);
                subAdapter.setBackLeftViewResource(mBackLeftViewResourceId);
                subAdapter.setBackRightViewResource(mBackRightViewResourceId);
                subAdapter.setFrontViewResource(mFrontViewResourceId);
            } catch(ClassCastException e) {

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

                if(mOpened){
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

                            if(backLeftRect.contains(x, 0)){
                                animateClose();
                                mOpened = false;

                                return false;
                            } else if(backRightRect.contains(x, 0)){
                                animateClose();
                                mOpened = false;

                                return false;
                            }
                        }
                    }

                    return true;
                } else {
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

                            mTouchedRowView = ((SwipeRecyclerViewRowView) child);

                            break;
                        }
                    }
                }

                return false;

            case MotionEvent.ACTION_MOVE:
                return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()){
            case MotionEvent.ACTION_DOWN:

                // Close opened view
                animateClose();
                mOpened = false;
                mIsSwiping = false;

                break;

            case MotionEvent.ACTION_MOVE:

                final float deltaX = calculateDeltaX(motionEvent);

                if(deltaX > mTouchSlop && mTouchedRowView != null){
                    View frontView = mTouchedRowView.getFrontView();
                    frontView.setTranslationX(motionEvent.getRawX() - startX);
                    mIsSwiping = true;
                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:

                if(mIsSwiping){
                    animateOpen();
                    mOpened = true;
                }

                break;
        }

        return super.onTouchEvent(motionEvent);
    }

    private float calculateDeltaX(MotionEvent motionEvent){
        return Math.abs(motionEvent.getX() - startX);
    }

    /**
     * Preliminary animation methods
     *
     */
    private void animateOpen(){
        View frontView = mTouchedRowView.getFrontView();
        View backRightView = mTouchedRowView.getBackRightView();
        ObjectAnimator anim = ObjectAnimator.ofFloat(frontView, "translationX", frontView.getTranslationX(), 0f - backRightView.getWidth());
        anim.setDuration(300);
        anim.start();
    }

    private void animateClose(){
        View frontView = mTouchedRowView.getFrontView();
        ObjectAnimator anim = ObjectAnimator.ofFloat(frontView, "translationX", frontView.getTranslationX(), 0f);
        anim.setDuration(300);
        anim.start();
    }
}