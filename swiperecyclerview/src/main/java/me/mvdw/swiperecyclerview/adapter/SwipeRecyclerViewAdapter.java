package me.mvdw.swiperecyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.mvdw.swiperecyclerview.R;
import me.mvdw.swiperecyclerview.viewholder.SwipeableViewHolder;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public class SwipeRecyclerViewAdapter extends RecyclerViewHeaderFooterSubAdapter<RecyclerViewHeaderFooterSubAdapter.MainViewHolder> {

    private int mBackLeftViewResourceId;
    private int mBackRightViewResourceId;
    private int mFrontViewResourceId;

    private boolean mEnableFrontViewTranslationObservable;
    private FrontViewTranslationObservable frontViewTranslationObservable = new FrontViewTranslationObservable();

    private Context mContext;

    public SwipeRecyclerViewAdapter(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case RecyclerViewHeaderFooterSubAdapter.MainViewHolder.TYPE_CONTENT:

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.swipe_recycler_view_item, parent, false);
                return new SwipeableViewHolder(view, mFrontViewResourceId, mBackLeftViewResourceId, mBackRightViewResourceId, mEnableFrontViewTranslationObservable, frontViewTranslationObservable);
        }

        return super.onCreateViewHolder(parent, viewType);
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
}