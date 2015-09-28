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
public abstract class RecyclerViewHeaderFooterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected ArrayList<?> mData;

    protected ArrayList<View> mHeaderViews = new ArrayList<>();
    protected ArrayList<View> mFooterViews = new ArrayList<>();

    private int mContentItemLayout;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case MainViewHolder.TYPE_HEADER:
                return new HeaderViewHolder(new LinearLayout(parent.getContext()));

            case MainViewHolder.TYPE_CONTENT:
                View view = LayoutInflater.from(parent.getContext()).inflate(mContentItemLayout, parent, false);
                return new MainViewHolder(view);

            case MainViewHolder.TYPE_FOOTER:
                return new FooterViewHolder(new LinearLayout(parent.getContext()));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof HeaderViewHolder){
            if(mHeaderViews.get(position).getParent() != null)
                ((HeaderViewHolder) viewHolder).rootView.removeView(mHeaderViews.get(position));

            ((HeaderViewHolder) viewHolder).rootView.addView(mHeaderViews.get(position));

        } else if(viewHolder instanceof FooterViewHolder){
            position -= mData.size() + mHeaderViews.size();

            if(mFooterViews.get(position).getParent() != null)
                ((FooterViewHolder) viewHolder).rootView.removeView(mFooterViews.get(position));

            ((FooterViewHolder) viewHolder).rootView.addView(mFooterViews.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + mHeaderViews.size() + mFooterViews.size();
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

        public MainViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class HeaderViewHolder extends MainViewHolder {

        private ViewGroup rootView;

        public HeaderViewHolder(ViewGroup itemView) {
            super(itemView);

            rootView = itemView;
        }
    }

    public class FooterViewHolder extends MainViewHolder {

        private ViewGroup rootView;

        public FooterViewHolder(ViewGroup itemView) {
            super(itemView);

            rootView = itemView;
        }
    }
}