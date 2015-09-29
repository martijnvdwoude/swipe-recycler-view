package me.mvdw.swiperecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public abstract class RecyclerViewHeaderFooterAdapter extends RecyclerView.Adapter<RecyclerViewHeaderFooterAdapter.MainViewHolder> {

    protected ArrayList<?> mData;

    protected ArrayList<View> mHeaderViews = new ArrayList<>();
    protected ArrayList<View> mFooterViews = new ArrayList<>();

    private int mContentItemLayout;

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case MainViewHolder.TYPE_HEADER:
                return new HeaderViewHolder(new LinearLayout(parent.getContext()), this);

            case MainViewHolder.TYPE_CONTENT:
                View view = LayoutInflater.from(parent.getContext()).inflate(mContentItemLayout, parent, false);
                return new MainViewHolder(view, this);

            case MainViewHolder.TYPE_FOOTER:
                return new FooterViewHolder(new LinearLayout(parent.getContext()), this);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(MainViewHolder viewHolder, int position) {
        if(viewHolder instanceof HeaderViewHolder){
            if(mHeaderViews.get(position).getParent() != null)
                ((LinearLayout) mHeaderViews.get(position).getParent()).removeView(mHeaderViews.get(position));

            ((HeaderViewHolder) viewHolder).rootView.addView(mHeaderViews.get(position));

        } else if(viewHolder instanceof FooterViewHolder){
            position -= mData.size() + mHeaderViews.size();

            if(mFooterViews.get(position).getParent() != null)
                ((LinearLayout) mFooterViews.get(position).getParent()).removeView(mFooterViews.get(position));

            ((FooterViewHolder) viewHolder).rootView.addView(mFooterViews.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + getHeaderItemCount() + getFooterItemCount();
    }

    public int getHeaderItemCount(){
        return mHeaderViews.size();
    }

    public int getFooterItemCount(){
        return mFooterViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (!mHeaderViews.isEmpty() && position < mHeaderViews.size()) {
            return MainViewHolder.TYPE_HEADER;
        } else if (position < mData.size() + mHeaderViews.size()) {
            return MainViewHolder.TYPE_CONTENT;
        } else if (!mFooterViews.isEmpty() && position < mFooterViews.size() + mData.size() + mHeaderViews.size()) {
            return MainViewHolder.TYPE_FOOTER;
        }

        return super.getItemViewType(position);
    }

    public void setData(ArrayList<?> data) {
        this.mData = data;
    }

    public Object getDataForPosition(int position){
        return mData.get(position - mHeaderViews.size());
    }

    /**
     * Add header view
     *
     * @param headerView
     */
    public void addHeaderView(View headerView) {
        addHeaderView(0, headerView);
    }

    public void addHeaderView(int index, View headerView) {
        this.mHeaderViews.add(index, headerView);
    }

    /**
     * Add footer view
     *
     * @param footerView
     */
    public void addFooterView(View footerView){
        addFooterView(0, footerView);
    }

    public void addFooterView(int index, View footerView) {
        this.mFooterViews.add(index, footerView);
    }

    /**
     * Notify items inserted by view type
     *
     * @param index
     */
    public void notifyHeaderItemInserted(int index) {
        super.notifyItemInserted(index);
    }

    public void notifyContentItemInserted(int index) {
        super.notifyItemInserted(index + mHeaderViews.size());
    }

    public void notifyFooterItemInserted(int index) {
        super.notifyItemInserted(index + mData.size() + mHeaderViews.size());
    }

    /**
     * Viewholders
     *
     */
    public class MainViewHolder extends RecyclerView.ViewHolder {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_CONTENT = 1;
        public static final int TYPE_FOOTER = 2;

        private RecyclerView.Adapter adapter;

        public MainViewHolder(View itemView, RecyclerView.Adapter adapter) {
            super(itemView);

            this.adapter = adapter;
        }

        public int getLocalPosition(){
            RecyclerView recyclerView = (RecyclerView) itemView.getParent();
            RecyclerView.Adapter mainAdapter = recyclerView.getAdapter();

            int position = super.getAdapterPosition();

            if(mainAdapter instanceof SwipeRecyclerViewMergeAdapter){
                for(Object localAdapter : ((SwipeRecyclerViewMergeAdapter) mainAdapter).mAdapters){
                    RecyclerView.Adapter adapter = ((SwipeRecyclerViewMergeAdapter.LocalAdapter) localAdapter).mAdapter;

                    if(adapter.equals(this.adapter)){
                        break;
                    } else {
                        position -= adapter.getItemCount();
                    }
                }
            }

            return position;
        }
    }

    public class HeaderViewHolder extends MainViewHolder {

        private ViewGroup rootView;

        public HeaderViewHolder(ViewGroup itemView, RecyclerView.Adapter adapter) {
            super(itemView, adapter);

            this.rootView = itemView;
        }
    }

    public class FooterViewHolder extends MainViewHolder {

        private ViewGroup rootView;

        public FooterViewHolder(ViewGroup itemView, RecyclerView.Adapter adapter) {
            super(itemView, adapter);

            rootView = itemView;
        }
    }
}