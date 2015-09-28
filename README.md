# SwipeRecyclerView
SwipeRecyclerView including a MergeAdapter with header and footer support

Rough first draft of a swipable RecyclerView. Lots of stuff left to implement/clean-up/improve/optimize.

####SwipeRecyclerViewAdapter
Extend this adapter in your project to return views for the swipable rows in your RecyclerView. Simply override `onBindViewHolder` and call `getFrontView()`, `getBackLeftView()`, `getBackRightView()` on the `viewHolder` to get references to the inflated layouts that you set as attributes in your xml on the `SwipeRecyclerView`.

Finally set this adapter on the `SwipeRecyclerView` directly or add it to a `SwipeRecyclerViewMergeAdapter` first to support multiple adapters and header and footer support.
