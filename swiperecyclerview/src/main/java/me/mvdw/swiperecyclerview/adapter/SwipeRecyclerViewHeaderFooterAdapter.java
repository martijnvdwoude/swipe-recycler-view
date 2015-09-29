package me.mvdw.swiperecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Observable;
import java.util.Observer;

import me.mvdw.swiperecyclerview.R;
import me.mvdw.swiperecyclerview.view.SwipeRecyclerViewRowView;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public class SwipeRecyclerViewHeaderFooterAdapter extends RecyclerViewHeaderFooterAdapter {

    private int mBackLeftViewResourceId;
    private int mBackRightViewResourceId;
    private int mFrontViewResourceId;

    private boolean mEnableFrontViewTranslationObservable;
    private FrontViewTranslationObservable frontViewTranslationObservable = new FrontViewTranslationObservable();

    private Context mContext;

    public SwipeRecyclerViewHeaderFooterAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public static class FrontViewTranslationObservable extends Observable {
        public void frontViewTranslationChanged(ViewGroup frontView){
            setChanged();
            notifyObservers(frontView);
        }
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case MainViewHolder.TYPE_CONTENT:

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.swipe_recycler_view_item, parent, false);
                return new SwipeableViewHolder(view, this);
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(MainViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
    }

    public void setBackLeftViewResourceId(final int backLeftViewResourceId){
        this.mBackLeftViewResourceId = backLeftViewResourceId;
    }

    public void setBackRightViewResourceId(final int backRightViewResourceId){
        this.mBackRightViewResourceId = backRightViewResourceId;
    }

    public void setFrontViewResourceId(final int frontViewResourceId){
        this.mFrontViewResourceId = frontViewResourceId;
    }

    public int getBackLeftViewResourceId() {
        return mBackLeftViewResourceId;
    }

    public int getBackRightViewResourceId() {
        return mBackRightViewResourceId;
    }

    public int getFrontViewResourceId() {
        return mFrontViewResourceId;
    }

    /**
     * Getters/setters for the front view observable
     */
    public void setFrontViewTranslationObservableEnabled(boolean enableFrontViewTranslationObservable) {
        this.mEnableFrontViewTranslationObservable = enableFrontViewTranslationObservable;
    }

    public boolean isFrontViewTranslationObservableEnabled() {
        return mEnableFrontViewTranslationObservable;
    }

    public FrontViewTranslationObservable getFrontViewTranslationObservable(){
        return frontViewTranslationObservable;
    }

    /**
     * Override this method to update the content of the row according to the X translation
     * of the front view. This can be used to do parallax effects or motion based animations.
     *
     * @param viewHolder reference to the viewHolder of the swiping row
     * @param frontViewTranslationX the X translation value of the front view
     */
    protected void onFrontViewTranslationChanged(SwipeableViewHolder viewHolder, float frontViewTranslationX){}

    /**
     * Viewholder
     */
    public class SwipeableViewHolder extends MainViewHolder implements Observer {

        private SwipeRecyclerViewRowView mSwipeRecyclerViewRowView;

        public SwipeableViewHolder(View itemView, RecyclerView.Adapter adapter) {
            super(itemView, adapter);

            mSwipeRecyclerViewRowView = (SwipeRecyclerViewRowView) itemView.findViewById(R.id.swipe_recycler_view_row_view);

            mSwipeRecyclerViewRowView.setFrontViewResourceId(mFrontViewResourceId);
            mSwipeRecyclerViewRowView.setBackLeftViewResourceId(mBackLeftViewResourceId);
            mSwipeRecyclerViewRowView.setBackRightViewResourceId(mBackRightViewResourceId);

            mSwipeRecyclerViewRowView.initViews();

            if(mEnableFrontViewTranslationObservable)
                frontViewTranslationObservable.addObserver(this);
        }

        public ViewGroup getBackLeftView(){
            return mSwipeRecyclerViewRowView.getBackLeftView();
        }

        public ViewGroup getBackRightView(){
            return mSwipeRecyclerViewRowView.getBackRightView();
        }

        public ViewGroup getFrontView(){
            return mSwipeRecyclerViewRowView.getFrontView();
        }

        @Override
        public void update(Observable observable, Object frontView) {
            if(frontView == getFrontView()) {
                onFrontViewTranslationChanged(this, this.getFrontView().getTranslationX());
            }
        }
    }
}