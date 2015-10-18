[![Platform](http://img.shields.io/badge/platform-android-brightgreen.svg?style=flat)](http://developer.android.com/index.html)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

SwipeRecyclerView
===

A swipeable RecyclerView. This library includes a merge adapter to allow for multiple subadapters. Also includes a subadapter with header and footer support.

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

###SwipeRecyclerViewAdapter

Extend this adapter in your project to return views for the swipable rows in your RecyclerView. Simply override `onBindViewHolder` and call `getFrontView()`, `getBackLeftView()`, `getBackRightView()` on the `viewHolder` to get references to the inflated layouts that you set as attributes in your xml on the `SwipeRecyclerView`.

```
@Override
public void onBindViewHolder(final MainViewHolder viewHolder, int i) {
    super.onBindViewHolder(viewHolder, i);

    // Handle the swipeable content rows, header rows or footer rows
    if(viewHolder instanceof SwipeableViewHolder){
        
        // Get references to the views in the swipeable row
        LinearLayout frontView = (LinearLayout) ((SwipeableViewHolder) viewHolder).getFrontView();
        LinearLayout backLeftView = (LinearLayout) ((SwipeableViewHolder) viewHolder).getBackLeftView();
        LinearLayout backRightView = (LinearLayout) ((SwipeableViewHolder) viewHolder).getBackRightView();

        // Do whatever you want in your swipeable row
        frontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Front clicked", Toast.LENGTH_SHORT).show();
            }
        });

        if (backLeftView != null) {
            backLeftView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "Back left clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (backRightView != null) {
            backRightView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "Back right clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    } else if(viewHolder instanceof HeaderViewHolder){
        ((HeaderViewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Header clicked", Toast.LENGTH_SHORT).show();
            }
        });
    } else if(viewHolder instanceof FooterViewHolder){
        ((FooterViewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Footer clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

#####Methods
This adapter extends from `RecyclerViewHeaderFooterSubAdapter` and adds header and footer support.
- `addHeaderView(View headerView)`
- `addHeaderView(int index, View headerView)`
- `addFooterView(View footerView)`
- `addFooterView(int index, View footerView)`

- `notifyHeaderItemInserted(int index)`
- `notifyContentItemInserted(int index)`
- `notifyFooterItemInserted(int index)`

Get data object for an adapter position (for example to delete it from the dataset):
- `getDataForPosition(int position)`

You can also override the in XML specified general layouts on the SwipeRecyclerView for each adapter by calling:
- `setBackLeftViewResourceId(final int backLeftViewResourceId)`
- `setBackRightViewResourceId(final int backRightViewResourceId)`
- `setFrontViewResourceId(final int frontViewResourceId)`

###RecyclerViewMergeAdapter
This project has a dependency on my `RecyclerViewMergeAdapter`, which can be found here: https://github.com/martijnvdwoude/recycler-view-merge-adapter

The merge adapter allows for multiple adapters to be used on one RecyclerView instead of only one. 

## License
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
