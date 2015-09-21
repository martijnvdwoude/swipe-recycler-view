package me.mvdw.swiperecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2008-2009 CommonsWare, LLC
 * Portions (c) 2009 Google, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Adapter that merges multiple child adapters and views
 * into a single contiguous whole.
 *
 * Adapters used as pieces within MergeAdapter must have
 * view type IDs monotonically increasing from 0. Ideally,
 * adapters also have distinct ranges for their row ids, as
 * returned by getItemId().
 *
 */
public class SwipeRecyclerViewMergeAdapter<T extends RecyclerView.Adapter> extends RecyclerView.Adapter {

    private Context mContext;
    protected ArrayList<LocalAdapter> mHeaderAdapters = new ArrayList<>();
    protected ArrayList<LocalAdapter> mContentAdapters = new ArrayList<>();
    protected ArrayList<LocalAdapter> mFooterAdapters = new ArrayList<>();
    protected ArrayList<LocalAdapter> mHiddenHeaderAdapters = new ArrayList<>();
    protected ArrayList<LocalAdapter> mHiddenFooterAdapters = new ArrayList<>();

    protected ForwardingDataSetObserver observer = new ForwardingDataSetObserver();
    private int mViewTypeIndex=0;

    private boolean mHideHeadersForEmptyContent = false;
    private boolean mHideFootersForEmptyContent = false;

    public SwipeRecyclerViewMergeAdapter() {
    }

    public SwipeRecyclerViewMergeAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * A Mergeable adapter must implement both ListAdapter and SpinnerAdapter to be useful in lists and spinners.
     */

    public class LocalAdapter {
        public final T mAdapter;
        public int mLocalPosition = 0;
        public Map<Integer, Integer> mViewTypesMap = new HashMap<>();

        public LocalAdapter(T adapter) {
            mAdapter = adapter;
        }
    }

    /** Append the given adapter to the list of merged adapters. */
    public void addAdapter(T adapter) {
        addAdapter(mContentAdapters.size(), adapter);
    }

    /** Add the given adapter to the list of merged adapters at the given index. */
    public void addHeaderAdapter(int index, T adapter) {
        if(!mHideHeadersForEmptyContent) {
            mHeaderAdapters.add(index, new LocalAdapter(adapter));

            adapter.registerAdapterDataObserver(observer);
            notifyDataSetChanged();
        } else {
            mHiddenHeaderAdapters.add(index, new LocalAdapter(adapter));
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void addAdapter(int index, T adapter) {
        mContentAdapters.add(index, new LocalAdapter(adapter));

        adapter.registerAdapterDataObserver(observer);
        notifyDataSetChanged();
    }

    public void addFooterAdapter(int index, T adapter) {
        if(!mHideFootersForEmptyContent) {
            mFooterAdapters.add(index, new LocalAdapter(adapter));

            adapter.registerAdapterDataObserver(observer);
            notifyDataSetChanged();
        } else {
            mHiddenFooterAdapters.add(index, new LocalAdapter(adapter));
            adapter.registerAdapterDataObserver(observer);
        }
    }

    /** Remove the given adapter from the list of merged adapters. */
    public void removeAdapter(T adapter) {
        removeAdapter(mContentAdapters.indexOf(adapter));
    }

    /** Remove the adapter at the given index from the list of merged adapters. */
    public void removeHeaderAdapter(int index) {
        LocalAdapter adapter = mHeaderAdapters.remove(index);
        adapter.mAdapter.unregisterAdapterDataObserver(observer);
        notifyDataSetChanged();
    }

    public void removeAdapter(int index) {
        LocalAdapter adapter = mContentAdapters.remove(index);
        adapter.mAdapter.unregisterAdapterDataObserver(observer);
        notifyDataSetChanged();
    }

    public void removeFooterAdapter(int index) {
        LocalAdapter adapter = mFooterAdapters.remove(index);
        adapter.mAdapter.unregisterAdapterDataObserver(observer);
        notifyDataSetChanged();
    }

    public int getSubAdapterCount() {
        return mContentAdapters.size();
    }

    public T getSubAdapter(int index) {
        return mContentAdapters.get(index).mAdapter;
    }

    /**
     * Adds a new View to the roster of things to appear in
     * the aggregate list.
     *
     * @param view
     *          Single view to add
     */
    public void addHeaderView(View view) {
        ArrayList<View> list=new ArrayList<View>(1);
        list.add(view);
        addHeaderViews(list);
    }

    public void addView(View view) {
        ArrayList<View> list=new ArrayList<View>(1);
        list.add(view);
        addContentViews(list);
    }

    public void addFooterView(View view) {
        ArrayList<View> list=new ArrayList<View>(1);
        list.add(view);
        addFooterViews(list);
    }

    /**
     * Adds a list of views to the roster of things to appear
     * in the aggregate list.
     *
     * @param views
     *          List of views to add
     */
    public void addHeaderViews(List<View> views) {
        addHeaderAdapter(mHeaderAdapters.size(), (T) new ViewsAdapter(mContext, views));
    }

    public void addContentViews(List<View> views) {
        addAdapter((T) new ViewsAdapter(mContext, views));
    }

    public void addFooterViews(List<View> views) {
        addFooterAdapter(mFooterAdapters.size(), (T) new ViewsAdapter(mContext, views));
    }

    @Override
    public int getItemCount() {
        int count = 0;

        for (LocalAdapter adapter : mHeaderAdapters) {
            count += adapter.mAdapter.getItemCount();
        }

        for (LocalAdapter adapter : mContentAdapters) {
            count += adapter.mAdapter.getItemCount();
        }

        for (LocalAdapter adapter : mFooterAdapters) {
            count += adapter.mAdapter.getItemCount();
        }

        return count;
        // TODO: cache counts until next onChanged
    }

    /**
     * For a given merged position, find the corresponding Adapter and local position within that Adapter by iterating through Adapters and
     * summing their counts until the merged position is found.
     *
     * @param position a merged (global) position
     * @return the matching Adapter and local position, or null if not found
     */
    public LocalAdapter getAdapterOffsetForItem(final int position) {
        final int headerAdapterCount = mHeaderAdapters.size();
        final int contentAdapterCount = mContentAdapters.size();
        final int footerAdapterCount = mFooterAdapters.size();
        final int adapterCount = headerAdapterCount + contentAdapterCount + footerAdapterCount;

        int i = 0;
        int count = 0;

        while (i < adapterCount) {
            LocalAdapter a = null;

            if(headerAdapterCount != 0 && i < headerAdapterCount){
                a = mHeaderAdapters.get(i);
            } else if(contentAdapterCount != 0 && i < contentAdapterCount + headerAdapterCount){
                a = mContentAdapters.get(i - headerAdapterCount);
            } else if(footerAdapterCount != 0 && i < footerAdapterCount + contentAdapterCount + headerAdapterCount){
                a = mFooterAdapters.get(i - (contentAdapterCount + headerAdapterCount));
            }

            int newCount = count + a.mAdapter.getItemCount();
            if (position < newCount) {
                a.mLocalPosition = position - count;
                return a;
            }

            count = newCount;
            i++;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        LocalAdapter result = getAdapterOffsetForItem(position);
        int localViewType = result.mAdapter.getItemViewType(result.mLocalPosition);
        if (result.mViewTypesMap.containsValue(localViewType)) {
            for (Map.Entry<Integer, Integer> entry : result.mViewTypesMap.entrySet()) {
                if (entry.getValue() == localViewType) {
                    return entry.getKey();
                }
            }
        }

        mViewTypeIndex += 1;
        result.mViewTypesMap.put(mViewTypeIndex, localViewType);
        return mViewTypeIndex;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        for (LocalAdapter adapter: mHeaderAdapters){
            if (adapter.mViewTypesMap.containsKey(viewType))
                return adapter.mAdapter.onCreateViewHolder(viewGroup,adapter.mViewTypesMap.get(viewType));
        }

        for (LocalAdapter adapter: mContentAdapters){
            if (adapter.mViewTypesMap.containsKey(viewType))
                return adapter.mAdapter.onCreateViewHolder(viewGroup,adapter.mViewTypesMap.get(viewType));
        }

        for (LocalAdapter adapter: mFooterAdapters){
            if (adapter.mViewTypesMap.containsKey(viewType))
                return adapter.mAdapter.onCreateViewHolder(viewGroup,adapter.mViewTypesMap.get(viewType));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        LocalAdapter result = getAdapterOffsetForItem(position);
        result.mAdapter.onBindViewHolder(viewHolder, result.mLocalPosition);
    }

    public int getLocalPosition(int position){
        return getAdapterOffsetForItem(position).mLocalPosition;
    }

    /**
     * Notify items inserted
     *
     * @param index
     */
    public void notifyItemInserted(T adapter, int index){
        adapter.notifyItemInserted(index + getTotalHeaderViewCount());
        handleShowingHeadersAndFooters();
    }

    private void handleShowingHeadersAndFooters(){
        if(mHideHeadersForEmptyContent && getTotalContentViewCount() > 0 && mHiddenHeaderAdapters.size() > 0){
            for(LocalAdapter headerAdapter : mHiddenHeaderAdapters) {
                mHeaderAdapters.add(0, headerAdapter);
            }

            mHiddenHeaderAdapters.clear();

            notifyItemRangeInserted(0, getTotalHeaderViewCount());
        }

        if(mHideFootersForEmptyContent && getTotalContentViewCount() > 0 && mHiddenFooterAdapters.size() > 0){
            for(LocalAdapter footerAdapter : mHiddenFooterAdapters) {
                mFooterAdapters.add(0, footerAdapter);
            }

            mHiddenFooterAdapters.clear();

            notifyItemRangeInserted(getTotalHeaderViewCount() + getTotalContentViewCount(), getTotalFooterViewCount());
        }
    }

    /**
     * Notify item removed
     *
     * @param index
     */
    public void notifyItemRemoved(T adapter, int index){
        adapter.notifyItemRemoved(index);
        handleHidingHeadersAndFooters();
    }

    /**
     * Set hide headers and footers
     *
     * @param hideHeadersForEmptyContent
     */
    public void setHideHeadersForEmptyContent(boolean hideHeadersForEmptyContent){
        this.mHideHeadersForEmptyContent = hideHeadersForEmptyContent;
    }

    public void setHideFootersForEmptyContent(boolean hideFootersForEmptyContent){
        this.mHideFootersForEmptyContent = hideFootersForEmptyContent;
    }

    private void handleHidingHeadersAndFooters(){
        if(mHideHeadersForEmptyContent && getTotalContentViewCount() == 0){
            for(LocalAdapter headerAdapter : mHeaderAdapters) {
                mHiddenHeaderAdapters.add(0, headerAdapter);
            }

            mHeaderAdapters.clear();

            notifyItemRangeRemoved(0, mHiddenHeaderAdapters.size());
        }

        if(mHideFootersForEmptyContent && getTotalContentViewCount() == 0){
            for(LocalAdapter footerAdapter : mFooterAdapters) {
                mHiddenFooterAdapters.add(0, footerAdapter);
            }

            mFooterAdapters.clear();

            notifyItemRangeRemoved(getTotalHeaderViewCount() + getTotalContentViewCount(), mHiddenFooterAdapters.size());
        }
    }

    /**
     * Get total amount of views in certain adapter type
     */
    private int getTotalHeaderViewCount(){
        int count = 0;

        for(LocalAdapter adapter : mHeaderAdapters){
            count += adapter.mAdapter.getItemCount();
        }

        return count;
    }

    private int getTotalContentViewCount(){
        int count = 0;

        for(LocalAdapter adapter : mContentAdapters){
            count += adapter.mAdapter.getItemCount();
        }

        return count;
    }

    private int getTotalFooterViewCount(){
        int count = 0;

        for(LocalAdapter adapter : mFooterAdapters){
            count += adapter.mAdapter.getItemCount();
        }

        return count;
    }

    /**
     * Forwarding data set observer
     *
     */
    private class ForwardingDataSetObserver extends RecyclerView.AdapterDataObserver {
        @Override public void onChanged() {
            notifyDataSetChanged();
        }

        @Override public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    /**
     * ViewsAdapter, ported from CommonsWare SackOfViews adapter.
     */
    public static class ViewsAdapter extends RecyclerView.Adapter {
        private List<View> views=null;
        private Context context;

        /**
         * Constructor creating an empty list of views, but with
         * a specified count. Subclasses must override newView().
         */
        public ViewsAdapter(Context context, int count) {
            super();
            this.context = context;

            views=new ArrayList<>(count);

            for (int i=0;i<count;i++) {
                views.add(null);
            }
        }

        /**
         * Constructor wrapping a supplied list of views.
         * Subclasses must override newView() if any of the elements
         * in the list are null.
         */
        public ViewsAdapter(Context context, List<View> views) {
            super();
            this.context = context;

            this.views=views;
        }

        /**
         * How many items are in the data set represented by this
         * Adapter.
         */
        @Override
        public int getItemCount() {
            return(views.size());
        }

        /**
         * Get the type of View that will be created by getView()
         * for the specified item.
         * @param position Position of the item whose data we want
         */
        @Override
        public int getItemViewType(int position) {
            return(position);
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //view type is equal to the position in this adapter.
            ViewsViewHolder holder = new ViewsViewHolder(views.get(viewType));
            return holder;
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        }

        /**
         * Get the row id associated with the specified position
         * in the list.
         * @param position Position of the item whose data we want
         */
        @Override
        public long getItemId(int position) {
            return(position);
        }

        public boolean hasView(View v) {
            return(views.contains(v));
        }

        /**
         * Create a new View to go into the list at the specified
         * position.
         * @param position Position of the item whose data we want
         * @param parent ViewGroup containing the returned View
         */
        protected View newView(int position, ViewGroup parent) {
            throw new RuntimeException("You must override newView()!");
        }
    }

    public static class ViewsViewHolder extends RecyclerView.ViewHolder{
        public ViewsViewHolder(View itemView) {
            super(itemView);
        }
    }
}