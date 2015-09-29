package me.mvdw.swiperecyclerviewexample.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.mvdw.swiperecyclerview.adapter.SwipeRecyclerViewMergeAdapter;
import me.mvdw.swiperecyclerview.view.SwipeRecyclerView;
import me.mvdw.swiperecyclerviewexample.R;
import me.mvdw.swiperecyclerviewexample.adapter.ExampleSwipeRecyclerViewHeaderFooterAdapter;
import me.mvdw.swiperecyclerviewexample.object.SwipeRecyclerViewItem;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public class ExampleActivityFragment extends Fragment implements ExampleSwipeRecyclerViewHeaderFooterAdapter.ExampleSwipeRecyclerViewContentAdapterListener {

    private SwipeRecyclerView swipeRecyclerView;
    private SwipeRecyclerViewMergeAdapter mergeAdapter;
    private ExampleSwipeRecyclerViewHeaderFooterAdapter swipeRecyclerViewAdapter1;
    private ExampleSwipeRecyclerViewHeaderFooterAdapter swipeRecyclerViewAdapter2;

    private int count = 0;

    private ArrayList<SwipeRecyclerViewItem> recyclerViewItems;
    private ArrayList<SwipeRecyclerViewItem> swipeRecyclerViewItems;

    public ExampleActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_example, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // New datasets
        recyclerViewItems = new ArrayList<>();
        swipeRecyclerViewItems = new ArrayList<>();

        recyclerViewItems.add(new SwipeRecyclerViewItem());
        recyclerViewItems.add(new SwipeRecyclerViewItem());
        recyclerViewItems.add(new SwipeRecyclerViewItem());

        // Get recycler view
        swipeRecyclerView = (SwipeRecyclerView) view.findViewById(R.id.swipe_recycler_view);
        swipeRecyclerView.setCloseRowInterpolator(new FastOutSlowInInterpolator());

        // Set layoutmanager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        swipeRecyclerView.setLayoutManager(layoutManager);
        swipeRecyclerView.setHasFixedSize(true);

        // New merge adapter
        mergeAdapter = new SwipeRecyclerViewMergeAdapter();

        // New content adapter no headers and footers
        swipeRecyclerViewAdapter1 = new ExampleSwipeRecyclerViewHeaderFooterAdapter(getContext());
        swipeRecyclerViewAdapter1.setData(recyclerViewItems);
        swipeRecyclerViewAdapter1.setFrontViewTranslationObservableEnabled(true);

        // New content adapter with headers and footers
        swipeRecyclerViewAdapter2 = new ExampleSwipeRecyclerViewHeaderFooterAdapter(getContext());
        swipeRecyclerViewAdapter2.setData(swipeRecyclerViewItems);
        swipeRecyclerViewAdapter2.setListener(this);
        swipeRecyclerViewAdapter2.setFrontViewTranslationObservableEnabled(true);

        // New header view
        TextView header = new TextView(getActivity());
        header.setText("Header");
        swipeRecyclerViewAdapter2.addHeaderView(header);

        // New footer view
        TextView footer = new TextView(getActivity());
        footer.setText("Footer");
        swipeRecyclerViewAdapter2.addFooterView(footer);

        // Add adapters to merge adapter
        mergeAdapter.addAdapter(swipeRecyclerViewAdapter1);
        mergeAdapter.addAdapter(swipeRecyclerViewAdapter2);

        // Set merge adapter to recycler view
        swipeRecyclerView.setAdapter(mergeAdapter);
    }

    public void addItem(){
        if(swipeRecyclerView.hasOpenedItem()) {
            swipeRecyclerView.closeOpenedItem();
        } else {
            count++;

            SwipeRecyclerViewItem newItem = new SwipeRecyclerViewItem();
            newItem.setText("Item number " + count);

            swipeRecyclerViewItems.add(0, newItem);

            swipeRecyclerViewAdapter2.notifyContentItemInserted(0);
        }
    }

    @Override
    public void onDeleteClicked(int position) {
        SwipeRecyclerViewItem itemToDelete = (SwipeRecyclerViewItem) swipeRecyclerViewAdapter2.getDataForPosition(position);
        swipeRecyclerViewItems.remove(itemToDelete);

        swipeRecyclerViewAdapter2.notifyItemRemoved(position);

        swipeRecyclerView.closeOpenedItem();
    }
}
