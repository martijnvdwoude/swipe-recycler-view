package me.mvdw.swiperecyclerviewexample.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.mvdw.swiperecyclerview.adapter.SwipeRecyclerViewContentAdapter;
import me.mvdw.swiperecyclerviewexample.object.SwipeRecyclerViewItem;
import me.mvdw.swiperecyclerviewexample.R;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public class ExampleSwipeRecyclerViewContentAdapter extends SwipeRecyclerViewContentAdapter {

    public interface ExampleSwipeRecyclerViewContentAdapterListener {
        void onDeleteClicked(int position);
    }

    private ExampleSwipeRecyclerViewContentAdapterListener mListener;

    private ArrayList<SwipeRecyclerViewItem> mData;

    private Context mContext;

    public ExampleSwipeRecyclerViewContentAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public SwipeRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return super.onCreateViewHolder(parent, i);
    }

    @Override
    public void onBindViewHolder(final SwipeRecyclerViewContentAdapter.SwipeRecyclerViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);

        // Set front view data
        LinearLayout frontView = (LinearLayout) viewHolder.getFrontView();
        LinearLayout backLeftView = (LinearLayout) viewHolder.getBackLeftView();
        LinearLayout backRightView = (LinearLayout) viewHolder.getBackRightView();

        final TextView title =  (TextView) frontView.findViewById(R.id.front_text_view);
        title.setText(mData.get(i).getText());

        frontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Front clicked: " + title.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        if(backLeftView != null) {
            backLeftView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "Back left clicked: " + title.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(backRightView != null) {
            backRightView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onDeleteClicked(viewHolder.getPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(ArrayList<SwipeRecyclerViewItem> data){
        this.mData = data;
    }

    public void setListener(ExampleSwipeRecyclerViewContentAdapterListener listener){
        this.mListener = listener;
    }

    @Override
    protected void onFrontViewTranslationChanged(final SwipeRecyclerViewContentAdapter.SwipeRecyclerViewHolder viewHolder, float frontViewTranslationX){
        // Little parallax effect example
        viewHolder.getBackRightView().findViewById(R.id.back_right_text_view).setTranslationX(frontViewTranslationX / 5);
    }
}