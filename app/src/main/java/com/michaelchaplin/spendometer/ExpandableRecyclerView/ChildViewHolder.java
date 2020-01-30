package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;

// Custom ViewHolder class for a child that keeps track of the expanded states and allows for
// triggering on expansion-based events
public class ChildViewHolder<C> extends RecyclerView.ViewHolder {

    C mChild;
    ExpandableRecyclerAdapter mExpandableRecyclerAdapter;

    // Default constructor for the class
    public ChildViewHolder(View itemView) {
        super(itemView);
    }

    // Getter method to return the childListItem associated with this ChildViewHolder
    public C getChild() {
        return mChild;
    }

    // Returns the adapter position of the Parent associated with this ChildViewHolder (if it still exists)
    public int getParentAdapterPosition() {

        int flatPosition = getAdapterPosition();

        // Returns an invalid position if no adapter is present or if the flatPosition is invalid
        if(mExpandableRecyclerAdapter == null || flatPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        return mExpandableRecyclerAdapter.getNearestParentPosition(flatPosition);
    }

    // Returns the adapter position of the Child associated with this ChildViewHolder
    public int getChildAdapterPosition() {

        int flatPosition = getAdapterPosition();

        if(mExpandableRecyclerAdapter == null || flatPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        return mExpandableRecyclerAdapter.getChildPosition(flatPosition);
    }
}
