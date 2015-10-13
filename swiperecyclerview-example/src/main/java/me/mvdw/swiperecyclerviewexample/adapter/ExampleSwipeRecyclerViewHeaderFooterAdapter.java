package me.mvdw.swiperecyclerviewexample.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.mvdw.swiperecyclerview.adapter.SwipeRecyclerViewHeaderFooterAdapter;
import me.mvdw.swiperecyclerview.viewholder.SwipeableViewHolder;
import me.mvdw.swiperecyclerviewexample.R;
import me.mvdw.swiperecyclerviewexample.object.SwipeRecyclerViewItem;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public class ExampleSwipeRecyclerViewHeaderFooterAdapter extends SwipeRecyclerViewHeaderFooterAdapter {

    public interface ExampleSwipeRecyclerViewContentAdapterListener {
        void onDeleteClicked(int position);
    }

    private ExampleSwipeRecyclerViewContentAdapterListener mListener;

    private Context mContext;

    public ExampleSwipeRecyclerViewHeaderFooterAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public SwipeableViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return super.onCreateViewHolder(parent, i);
    }

    @Override
    public void onBindViewHolder(final SwipeableViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);

        int type = getItemViewType(i);

        switch (type) {
            case SwipeRecyclerViewHeaderFooterAdapter.MainViewHolder.TYPE_CONTENT:
                // Set front view data
                LinearLayout frontView = (LinearLayout) viewHolder.getFrontView();
                LinearLayout backLeftView = (LinearLayout) viewHolder.getBackLeftView();
                LinearLayout backRightView = (LinearLayout) viewHolder.getBackRightView();

                final TextView title = (TextView) frontView.findViewById(R.id.front_text_view);
                title.setText(((SwipeRecyclerViewItem) getDataForPosition(i)).getText());

                frontView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "Front clicked: " + title.getText(), Toast.LENGTH_SHORT).show();
                    }
                });

                if (backLeftView != null) {
                    backLeftView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(mContext, "Back left clicked: " + title.getText(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if (backRightView != null) {
                    backRightView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mListener != null) {
                                mListener.onDeleteClicked(viewHolder.getSubAdapterPosition());
                            }
                        }
                    });
                }

                break;
        }
    }

    public void setListener(ExampleSwipeRecyclerViewContentAdapterListener listener){
        this.mListener = listener;
    }

    @Override
    protected void onFrontViewTranslationChanged(final SwipeableViewHolder viewHolder, float frontViewTranslationX){
        // Little parallax effect example
        LinearLayout backRightView = (LinearLayout) viewHolder.getBackRightView();
        TextView backRightTextView = (TextView) backRightView.findViewById(R.id.back_right_text_view);

        // TODO: implement proper parallax code
        float backRightViewTranslationX = frontViewTranslationX / 5 + backRightTextView.getWidth() / 2;

        if(backRightViewTranslationX < 0){
            backRightViewTranslationX = 0;
        }

        backRightTextView.setTranslationX(backRightViewTranslationX);
    }
}