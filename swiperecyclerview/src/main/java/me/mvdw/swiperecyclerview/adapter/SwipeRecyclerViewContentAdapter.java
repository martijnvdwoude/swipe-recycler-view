package me.mvdw.swiperecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.mvdw.swiperecyclerview.R;
import me.mvdw.swiperecyclerview.view.SwipeRecyclerViewRowView;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public abstract class SwipeRecyclerViewContentAdapter extends RecyclerView.Adapter<SwipeRecyclerViewContentAdapter.SwipeRecyclerViewHolder>  {

    private int mBackLeftViewResourceId;
    private int mBackRightViewResourceId;
    private int mFrontViewResourceId;

    @Override
    public SwipeRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swipe_recycler_view_item, parent, false);
        return new SwipeRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SwipeRecyclerViewHolder viewHolder, int i) {
        viewHolder.bindItem();
    }

    @Override
    public int getItemCount() {
        return 0;
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
     * Viewholder
     */
    public class SwipeRecyclerViewHolder extends RecyclerView.ViewHolder {

        private SwipeRecyclerViewRowView mSwipeRecyclerViewRowView;

        public SwipeRecyclerViewHolder(View itemView) {
            super(itemView);

            mSwipeRecyclerViewRowView = (SwipeRecyclerViewRowView) itemView.findViewById(R.id.swipe_recycler_view_row_view);

            mSwipeRecyclerViewRowView.setFrontViewResourceId(mFrontViewResourceId);
            mSwipeRecyclerViewRowView.setBackLeftViewResourceId(mBackLeftViewResourceId);
            mSwipeRecyclerViewRowView.setBackRightViewResourceId(mBackRightViewResourceId);

            mSwipeRecyclerViewRowView.initViews();
        }

        public void bindItem() {

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
    }
}