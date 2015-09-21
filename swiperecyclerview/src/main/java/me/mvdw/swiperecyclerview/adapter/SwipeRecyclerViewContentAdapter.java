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

    private int mBackLeftViewResource;
    private int mBackRightViewResource;
    private int mFrontViewResource;

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

    public void setBackLeftViewResource(final int backLeftViewResource){
        this.mBackLeftViewResource = backLeftViewResource;
    }

    public void setBackRightViewResource(final int backRightViewResource){
        this.mBackRightViewResource = backRightViewResource;
    }

    public void setFrontViewResource(final int frontViewResource){
        this.mFrontViewResource = frontViewResource;
    }

    /**
     * Viewholder
     */
    public class SwipeRecyclerViewHolder extends RecyclerView.ViewHolder {

        private SwipeRecyclerViewRowView mSwipeRecyclerViewRowView;

        public SwipeRecyclerViewHolder(View itemView) {
            super(itemView);

            mSwipeRecyclerViewRowView = (SwipeRecyclerViewRowView) itemView.findViewById(R.id.swipe_recycler_view_row_view);

            mSwipeRecyclerViewRowView.setFrontViewResourceId(mFrontViewResource);
            mSwipeRecyclerViewRowView.setBackLeftViewResourceId(mBackLeftViewResource);
            mSwipeRecyclerViewRowView.setBackRightViewResourceId(mBackRightViewResource);

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