package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;

// Custom viewholder class for a Parent that keeps track of the exapanded states and allows for
// triggering on expansion-based events
public class ParentViewHolder<P extends Parent<C>, C> extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ParentViewHolderExpandableListener mParentViewHolderExpandableListener;
    private boolean mIsExpanded;
    P mParent;
    ExpandableRecyclerAdapter mExpandableRecyclerAdapter;

    // Default constructor for the class
    public ParentViewHolder(View itemView) {
        super(itemView);
        mIsExpanded = false;
    }

    // Returns the Parent associated with this ViewHolder
    public P getParent() {
        return mParent;
    }

    // Returns the adapter position of the Parent associated with this ViewHolder (if it still exists)
    public int getParentAdapterPosition() {

        int flatPosition = getAdapterPosition();

        // Returns the flatPosition even if it is invalid
        if (flatPosition == RecyclerView.NO_POSITION) {
            return flatPosition;
        }

        return mExpandableRecyclerAdapter.getNearestParentPosition(flatPosition);
    }

    // Sets an OnClickListener on the itemView passed into the ParentViewHolder through the constructor
    public void setMainItemClickToExpand() {
        itemView.setOnClickListener(this);
    }

    // Returns the expanded state for the Parent in this ParentViewHolder
    public boolean isExpanded() {
        return mIsExpanded;
    }

    // Setter method to set the expanded state of the Parent item
    public void setExpandedState(boolean expandedState) {
        mIsExpanded = expandedState;
    }

    // Callback method when the expansion state is changed
    public void onExpansionToggled(boolean expansionToggled) {
        // TODO: Customize animations here
    }

    // Setter for the ExpandableListener implemented in the ExpandableRecyclerAdapter
    void setParentViewHolderExpandableListener(ParentViewHolderExpandableListener parentViewHolderExpandableListener){
        mParentViewHolderExpandableListener = parentViewHolderExpandableListener;
    }

    @Override
    public void onClick(View view) {

        // Sets the onClick method to be controlled by a setter method
        if(mIsExpanded) {
            collapseView();
        } else {
            expandView();
        }
    }

    // Setter method for allowing the onClick Override method
    public boolean shouldItemViewClickToggleExpansion() {
        return true;
    }

    interface ParentViewHolderExpandableListener {

        // Called when a parent is expanded
        // The flatParentPosition is the index of the parent in the list being expanded
        void onParentExpanded(int flatParentPosition);

        // Called when a parent is collapsed
        void onParentCollapsed(int flatParentPosition);
    }

    // Method for triggering the expansion of the parent
    private void expandView() {
        setExpandedState(true);
        onExpansionToggled(false);

        // Returns the flat adapter position to the interface method the listener isn't null
        if (mParentViewHolderExpandableListener != null) {
            mParentViewHolderExpandableListener.onParentExpanded(getAdapterPosition());
        }
    }

    // Method for triggering the collapse of the parent
    private void collapseView() {
        setExpandedState(false);
        onExpansionToggled(true);

        // Returns the flat adapter position to the interface method the listener isn't null
        if (mParentViewHolderExpandableListener != null) {
            mParentViewHolderExpandableListener.onParentCollapsed(getAdapterPosition());
        }
    }
}
