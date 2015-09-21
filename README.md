# SwipeRecyclerView
SwipeRecyclerView including a MergeAdapter with header and footer support

Very, very rough first draft of a swipable RecyclerView. Lots of stuff left to implement/clean-up/improve/optimize.

###SwipeRecyclerViewContentAdapter
Extend this adapter in your project to return views for the swipable rows in your RecyclerView. Simply override `onBindViewHolder` and call `getFrontView()`, `getBackLeftView()`, `getBackRightView()` on the `viewHolder` to get references to the inflated views that you set as attributes in your xml on the `SwipeRecyclerView`.

##TODO:
- VelocityTracker for swiping open items
- Implement both swipe directions in the TouchListener (only swiping to the right is currently partially implemented)
- Implement functionality to disable swiping in either direction to prevent revealing either back views for individual items
- Prevent swiping open items while scrolling the RecyclerView (now only scrolling will get disabled while swiping open an item)
- Public methods for the SwipeRecyclerView
- Implement functionality to rearrange items in their adapters
- Implement more and useful attributes in addition to the attributes to provide the layouts for the front and back views
- Find out how different ItemAnimators behave, especially in the MergeAdapter with the header and footer subadapters
- Find out how ItemDecorators behave in combination with the MergeAdapter for dividers or spacing between items
- Split the SwipeRecyclerViewMergeAdapter into a base RecyclerViewMergeAdapter and extend that one for the header and footer support
- Much more...
