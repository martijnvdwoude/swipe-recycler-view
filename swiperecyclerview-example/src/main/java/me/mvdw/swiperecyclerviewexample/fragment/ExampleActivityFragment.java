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

import me.mvdw.swiperecyclerview.view.SwipeRecyclerView;
import me.mvdw.swiperecyclerviewexample.R;
import me.mvdw.swiperecyclerviewexample.adapter.ExampleSwipeRecyclerViewHeaderFooterAdapter;
import me.mvdw.swiperecyclerviewexample.object.SwipeRecyclerViewItem;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public class ExampleActivityFragment extends Fragment implements ExampleSwipeRecyclerViewHeaderFooterAdapter.ExampleSwipeRecyclerViewContentAdapterListener {

    private SwipeRecyclerView swipeRecyclerView;
    private ExampleSwipeRecyclerViewHeaderFooterAdapter swipeRecyclerViewAdapter;

    private int count = 0;

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

        // New dataset
        swipeRecyclerViewItems = new ArrayList<>();

        // Get recycler view
        swipeRecyclerView = (SwipeRecyclerView) view.findViewById(R.id.swipe_recycler_view);
        swipeRecyclerView.setCloseRowInterpolator(new FastOutSlowInInterpolator());

        // Set layoutmanager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        swipeRecyclerView.setLayoutManager(layoutManager);
        swipeRecyclerView.setHasFixedSize(true);

        // New content adapter
        swipeRecyclerViewAdapter = new ExampleSwipeRecyclerViewHeaderFooterAdapter(getContext());
        swipeRecyclerViewAdapter.setData(swipeRecyclerViewItems);
        swipeRecyclerViewAdapter.setListener(this);
        swipeRecyclerViewAdapter.setFrontViewTranslationObservableEnabled(true);

        // New header view
        TextView header = new TextView(getActivity());
        header.setText("Header");
        swipeRecyclerViewAdapter.addHeaderView(header);

        // New footer view
        TextView footer = new TextView(getActivity());
        footer.setText("Footer");
        swipeRecyclerViewAdapter.addFooterView(footer);

        // Set header footer adapter
        swipeRecyclerView.setAdapter(swipeRecyclerViewAdapter);
    }

    public void addItem(){
        if(swipeRecyclerView.hasOpenedItem()) {
            swipeRecyclerView.closeOpenedItem();
        } else {
            count++;

            SwipeRecyclerViewItem newItem = new SwipeRecyclerViewItem();
            newItem.setText("Item number " + count);

            swipeRecyclerViewItems.add(0, newItem);

            swipeRecyclerViewAdapter.notifyContentItemInserted(0);
        }
    }

    @Override
    public void onDeleteClicked(int position) {
        SwipeRecyclerViewItem itemToDelete = (SwipeRecyclerViewItem) swipeRecyclerViewAdapter.getDataForPosition(position);
        swipeRecyclerViewItems.remove(itemToDelete);

        swipeRecyclerViewAdapter.notifyItemRemoved(position);

        swipeRecyclerView.closeOpenedItem();
    }
}
