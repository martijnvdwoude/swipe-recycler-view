package me.mvdw.swiperecyclerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import me.mvdw.swiperecyclerview.R;

/**
 * Created by Martijn van der Woude on 08-09-15.
 */
public class SwipeRecyclerViewRowView extends LinearLayout {

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
        if(mBackLeftViewResourceId != 0) {
            ViewStub stub = (ViewStub) findViewById(R.id.back_left_stub);
            stub.setLayoutResource(mBackLeftViewResourceId);
            mBackLeftView = (ViewGroup) stub.inflate();
        }

        if(mBackRightViewResourceId != 0) {
            ViewStub stub = (ViewStub) findViewById(R.id.back_right_stub);
            stub.setLayoutResource(mBackRightViewResourceId);
            mBackRightView = (ViewGroup) stub.inflate();
        }

        if(mFrontViewResourceId != 0) {
            ViewStub stub = (ViewStub) findViewById(R.id.front_stub);
            stub.setLayoutResource(mFrontViewResourceId);
            mFrontView = (ViewGroup) stub.inflate();
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