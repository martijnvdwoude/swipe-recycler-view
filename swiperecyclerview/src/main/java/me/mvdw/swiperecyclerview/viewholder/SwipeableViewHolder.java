package me.mvdw.swiperecyclerview.viewholder;

import android.view.View;
import android.view.ViewGroup;

import java.util.Observable;
import java.util.Observer;

import me.mvdw.swiperecyclerview.R;
import me.mvdw.swiperecyclerview.adapter.FrontViewTranslationObservable;
import me.mvdw.swiperecyclerview.adapter.RecyclerViewHeaderFooterSubAdapter;
import me.mvdw.swiperecyclerview.adapter.SwipeRecyclerViewAdapter;
import me.mvdw.swiperecyclerview.view.SwipeRecyclerViewRowView;

/**
 * Created by Martijn van der Woude on 29-09-15.
 */
public class SwipeableViewHolder extends RecyclerViewHeaderFooterSubAdapter.MainViewHolder implements Observer {

    private SwipeRecyclerViewRowView mSwipeRecyclerViewRowView;

    public SwipeableViewHolder(View itemView,
                               int frontViewResourceId,
                               int backLeftResourceId,
                               int backRightResourceId,
                               boolean enableFrontViewTranslationObservable,
                               FrontViewTranslationObservable frontViewTranslationObservable) {

        super(itemView);

        mSwipeRecyclerViewRowView = (SwipeRecyclerViewRowView) itemView.findViewById(R.id.swipe_recycler_view_row_view);

        mSwipeRecyclerViewRowView.setFrontViewResourceId(frontViewResourceId);
        mSwipeRecyclerViewRowView.setBackLeftViewResourceId(backLeftResourceId);
        mSwipeRecyclerViewRowView.setBackRightViewResourceId(backRightResourceId);

        mSwipeRecyclerViewRowView.initViews();

        if(enableFrontViewTranslationObservable)
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
            ((SwipeRecyclerViewAdapter) getAdapter()).onFrontViewTranslationChanged(this, this.getFrontView().getTranslationX());
        }
    }
}
