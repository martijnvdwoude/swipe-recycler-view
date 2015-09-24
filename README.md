# SwipeRecyclerView
SwipeRecyclerView including a MergeAdapter with header and footer support

Rough first draft of a swipable RecyclerView. Lots of stuff left to implement/clean-up/improve/optimize.

###SwipeRecyclerViewContentAdapter
Extend this adapter in your project to return views for the swipable rows in your RecyclerView. Simply override `onBindViewHolder` and call `getFrontView()`, `getBackLeftView()`, `getBackRightView()` on the `viewHolder` to get references to the inflated layouts that you set as attributes in your xml on the `SwipeRecyclerView`.

Finally set this adapter on the `SwipeRecyclerView` directly or add it to a `SwipeRecyclerViewMergeAdapter` first to support multiple adapters and header and footer support.

##TODO:
- Improve VelocityTracker for swiping open items
- Further improve how the front and back views are added/inflated to the SwipeRecyclerViewRowView
- Implement functionality to disable swiping in either direction to prevent revealing either back views for individual items
- More public methods for the SwipeRecyclerView
- Implement functionality to rearrange items in their adapters
- Implement more and useful attributes in addition to the attributes to provide the layouts for the front and back views
- Find out how different ItemAnimators behave, especially in the MergeAdapter with the header and footer subadapters
- Find out how ItemDecorators behave in combination with the MergeAdapter for dividers or spacing between items
- Split the SwipeRecyclerViewMergeAdapter into a base RecyclerViewMergeAdapter and extend that one for the header and footer support
- Much more...
