package me.mvdw.swiperecyclerviewexample.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Martijn van der Woude on 07-09-15.
 */
public class SwipeRecyclerViewItem implements Parcelable {

    private String text;

    public SwipeRecyclerViewItem(){}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    protected SwipeRecyclerViewItem(Parcel in) {
        text = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SwipeRecyclerViewItem> CREATOR = new Parcelable.Creator<SwipeRecyclerViewItem>() {
        @Override
        public SwipeRecyclerViewItem createFromParcel(Parcel in) {
            return new SwipeRecyclerViewItem(in);
        }

        @Override
        public SwipeRecyclerViewItem[] newArray(int size) {
            return new SwipeRecyclerViewItem[size];
        }
    };
}