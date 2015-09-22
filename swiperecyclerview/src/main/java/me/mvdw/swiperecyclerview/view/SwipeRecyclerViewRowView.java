package me.mvdw.swiperecyclerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.mvdw.swiperecyclerview.R;

/**
 * Created by Martijn van der Woude on 08-09-15.
 */
public class SwipeRecyclerViewRowView extends FrameLayout {

    int mFrontViewResourceId;
    int mBackLeftViewResourceId;
    int mBackRightViewResourceId;

    private Context mContext;

    private ViewGroup mFrontView;
    private ViewGroup mBackLeftView;
    private ViewGroup mBackRightView;

    public SwipeRecyclerViewRowView(Context context) {
        super(context);

        this.mContext = context;
        init();
    }

    public SwipeRecyclerViewRowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        init();
    }

    public SwipeRecyclerViewRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        init();
    }

    private void init(){
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.swipe_recycler_view_row_view, this, true);
    }

    public void initViews(){
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(mBackLeftViewResourceId != 0) {
            mBackLeftView = (ViewGroup) mInflater.inflate(mBackLeftViewResourceId, (ViewGroup) findViewById(R.id.back_left_view_wrapper), true);
        }

        if(mBackRightViewResourceId != 0) {
            mBackRightView = (ViewGroup) mInflater.inflate(mBackRightViewResourceId, (ViewGroup) findViewById(R.id.back_right_view_wrapper), true);
        }

        if(mFrontViewResourceId != 0) {
            mFrontView = (ViewGroup) mInflater.inflate(mFrontViewResourceId, (ViewGroup) findViewById(R.id.front_view_wrapper), true);
        }
    }

    public void setBackLeftViewResourceId(int mBackLeftViewResourceId) {
        this.mBackLeftViewResourceId = mBackLeftViewResourceId;
    }

    public void setBackRightViewResourceId(int mBackRightViewResourceId) {
        this.mBackRightViewResourceId = mBackRightViewResourceId;
    }

    public void setFrontViewResourceId(int mFrontViewResourceId) {
        this.mFrontViewResourceId = mFrontViewResourceId;
    }

    public ViewGroup getBackLeftView() {
        return mBackLeftView;
    }

    public ViewGroup getBackRightView() {
        return mBackRightView;
    }

    public ViewGroup getFrontView() {
        return mFrontView;
    }
}