# SwipeRecyclerView

Rough first draft of a swipable RecyclerView.

####SwipeRecyclerView

#####XML usage:
```
<me.mvdw.swiperecyclerview.view.SwipeRecyclerView
        android:id="@+id/my_swipe_recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        custom:backLeftView="@layout/my_back_left_view_layout"
        custom:backRightView="@layout/my_back_right_view_layout"
        custom:frontView="@layout/my_front_view_layout"/>
```


####SwipeRecyclerViewAdapter
Basic swipeable adapter without header and footer support

Extend this adapter in your project to return views for the swipable rows in your RecyclerView. Simply override `onBindViewHolder` and call `getFrontView()`, `getBackLeftView()`, `getBackRightView()` on the `viewHolder` to get references to the inflated layouts that you set as attributes in your xml on the `SwipeRecyclerView`.

#####Methods
You can override the in XML specified general layouts on the SwipeRecyclerView for this adapter by calling:
- `setBackLeftViewResourceId(final int backLeftViewResourceId)`
- `setBackRightViewResourceId(final int backRightViewResourceId)`
- `setFrontViewResourceId(final int frontViewResourceId)`


####RecyclerViewHeaderFooterAdapter
Basic adapter including header and footer support.

#####Methods
- `addHeaderView(View headerView)`
- `addHeaderView(int index, View headerView)`
- `addFooterView(View footerView)`
- `addFooterView(int index, View footerView)`

- `notifyHeaderItemInserted(int index)`
- `notifyContentItemInserted(int index)`
- `notifyFooterItemInserted(int index)`


####SwipeRecyclerViewHeaderFooterAdapter
Swipeable version of `RecyclerViewHeaderFooterAdapter`

#####Additional methods

You can override the in XML specified general layouts on the SwipeRecyclerView for this adapter by calling:
- `setBackLeftViewResourceId(final int backLeftViewResourceId)`
- `setBackRightViewResourceId(final int backRightViewResourceId)`
- `setFrontViewResourceId(final int frontViewResourceId)`

Get data object for an adapter position (for example to delete it from the dataset):
- `getDataForPosition(int position)`


####SwipeRecyclerViewMergeAdapter
Finally set either of the adapters on the `SwipeRecyclerView` directly or add it to a `SwipeRecyclerViewMergeAdapter` first to support multiple adapters. The merge adapter also allows for mixing all types of adapters.
