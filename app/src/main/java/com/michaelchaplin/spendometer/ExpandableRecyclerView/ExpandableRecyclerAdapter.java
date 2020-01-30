package com.michaelchaplin.spendometer.ExpandableRecyclerView;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.michaelchaplin.spendometer.ExpandableRecyclerView.ParentViewHolder.ParentViewHolderExpandableListener;

import java.util.ArrayList;
import java.util.List;

/**
 * "P extends Parent<C>" is the Parent Object type that is instantiated with the adapter
 * "C" is the Child Object type that is defined by the Parent
 * "PVH extends ParentViewHolder" is the ParentViewHolder type that is instantiated with the adapter
 * "CVH extends ChildViewHolder" is the ChildViewHolder type that is instantiated with the adapter
 *
 * Example implementation:
 *
 * public class ExpandableRecyclerAdapter<Recipe<Ingredients>, Ingredients, RecipeViewHolder, IngredientViewHolder>
 */

// Implementation of a RecyclerView.Adapter that adds the ability to expand/collapse list items
public abstract class ExpandableRecyclerAdapter<P extends Parent<C>, C, PVH extends ParentViewHolder, CVH extends ChildViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // TODO: Implement Expanded State Map for savedState

    // Integers for defining types of views/positions
    public static final int TYPE_PARENT = 0;
    public static final int TYPE_CHILD = 1;
    public static final int TYPE_FIRST_USER = 2;
    public static final int INVALID_FLAT_POSITION = -1;

    /**
     * OMG I think this is the following:
     * <p>
     * Parent 1, Child 1
     * Parent 1, Child 2
     * Parent 1, Child 3
     * Parent 2, Child 1
     * Parent 2, Child 2
     */
    // A list of all currently expanded parents and their children, in order
    private List<ExpandableWrapper<P, C>> mFlatItemList;

    private List<P> mParentList;

    // Interface that allows objects to register themselves as expand/collapse listeners to be notified of change events
    public ExpandCollapseListener mExpandCollapseListener;

    // To be implemented in the Activity/Fragment where the RecyclerView is used
    public interface ExpandCollapseListener {

        // Called when a parent is expanded and uses the position of the parent in the list being expanded
        void onParentExpanded(int parentPosition);

        // Called when a parent is collapsed and uses the position of the parent in the list being collapsed
        void onParentCollapsed(int parentPosition);
    }

    // List of all the RecyclerViews active?
    private List<RecyclerView> mAttachedRecyclerViewPool;

    // Primary Constructor for the adapter
    public ExpandableRecyclerAdapter(List<P> parentList) {

        // Invokes the superclass' methods
        super();

        mParentList = parentList;
        mFlatItemList = generateFlattenedParentChildList(parentList);
        mAttachedRecyclerViewPool = new ArrayList<>();
    }


    // Implementation of the onCreateViewHolder method that is split to creates ViewHolders for both the Parent and Child
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (isParentViewType(viewType)) {

            PVH pvh = onCreateParentViewHolder(viewGroup, viewType);
            pvh.setParentViewHolderExpandableListener(mParentViewHolderExpandCollapseListneer);
            pvh.mExpandableRecyclerAdapter = this;
            return pvh;

        } else {

            CVH cvh = onCreateChildViewHolder(viewGroup, viewType);
            cvh.mExpandableRecyclerAdapter = this;
            return cvh;
        }
    }

    // Callback from the standard onCreateViewHolder to create the ParentViewHolder
    public abstract PVH onCreateParentViewHolder(ViewGroup parentViewGroup, int viewType);

    // Callback from the standard onCreateViewHolder to create the ChildViewHolder
    public abstract CVH onCreateChildViewHolder(ViewGroup childViewGroup, int viewType);

    // Implementation of the onBindViewHolder method that is split to bind date tp ViewHolders for both the Parent and Child
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int flatPosition) {

        if (flatPosition > mFlatItemList.size()) {
            throw new IllegalStateException("Trying to bind item out of the bounds, size: " + mFlatItemList.size() +
                    " flatPosition: " + flatPosition + ". Data was changed without a call to notify");
        }

        // Extracts a list item from the ExpandableWrapper at the given flatPosition
        ExpandableWrapper<P, C> listItem = mFlatItemList.get(flatPosition);

        // If the list item at the flatPosition is a Parent item, proceed
        if (listItem.isParent()) {

            PVH parentViewHolder = (PVH) holder;
            if (parentViewHolder.shouldItemViewClickToggleExpansion()) {
                parentViewHolder.setMainItemClickToExpand();
            }

            // Sets the ParentViewHolder to be expandable
            parentViewHolder.setExpandedState(listItem.isExpanded());

            // Sets the Parent to be the item in the ExpandableWrapper at the given flatPosition
            parentViewHolder.mParent = listItem.getParent();

            // Calls the method to bind data to the ParentViewHolder
            onBindParentViewHolder(parentViewHolder, getNearestParentPosition(flatPosition), listItem.getParent());

        } else {

            CVH childViewHolder = (CVH) holder;

            // Sets the Child to be the item in the expandableWrapper at the given flatPosition
            childViewHolder.mChild = listItem.getChild();

            // Calls the method to bind data to the ChildViewHolder
            onBindChildViewHolder(childViewHolder, getNearestParentPosition(flatPosition), getChildPosition(flatPosition), listItem.getChild());
        }
    }

    // Callback from the standard onBindViewHolder to bind data to a ParentViewHolder
    public abstract void onBindParentViewHolder(PVH parentViewHolder, int parentPosition, P parent);

    // Callback from the standard onBindViewHolder to bind data to a ChildViewHolder
    public abstract void onBindChildViewHolder(CVH childViewHolder, int parentPosition, int childPosition, C child);

    // Getter method that returns the number of parents and children currently expanded (ie. flatItemList size)
    public int getFlatItemCount() {
        return mFlatItemList.size();
    }

    // Getter method to return an integer that represents the View Type at a given flatPosition
    public int getItemViewType(int flatPosition) {

        // Get a list item with a Parent and Child at the given flatPosition
        ExpandableWrapper<P, C> listItem = mFlatItemList.get(flatPosition);

        // Returns the View Type of either the Parent and/or Child view at a given flatPosition
        if (listItem.isParent()) {
            return getParentViewType(getNearestParentPosition(flatPosition));
        } else {
            return getChildViewType(getNearestParentPosition(flatPosition), getChildPosition(flatPosition));
        }
    }

    // Returns the view type of the parent at a given parentPosition
    // May only work if using a single ParentViewHolder type in the Recycler
    public int getParentViewType(int parentPosition) {
        return TYPE_PARENT;
    }

    // Returns the view type of the child at a given parentPosition and childPosition
    // May only work if using a single ParentViewHolder type and ChildViewHolder type in the Recycler
    public int getChildViewType(int parentPosition, int childPosition) {
        return TYPE_CHILD;
    }

    // Used to determine whether a ViewType is a Parent or not
    public boolean isParentViewType(int viewType) {
        return viewType == TYPE_PARENT;
    }

    // Get a list of the parents that this adapter is working with
    public List<P> getParentList() {
        return mParentList;
    }

    // Sets a new list of parents for the adapter and notify any registered observers
    public void setParentList(List<P> parentList, boolean preserveExpansionState) {
        mParentList = parentList;
        notifyParentDataSetChanged(preserveExpansionState);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // Adds the RecyclerView used into the RecyclerView pool
        mAttachedRecyclerViewPool.add(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        // Removes the RecyclerView used out of the RecyclerView pool
        mAttachedRecyclerViewPool.remove(recyclerView);
    }

    // Setter method for the ExpandCollapseListener
    public void setExpandCollapseListener(ExpandCollapseListener expandCollapseListener) {
        mExpandCollapseListener = expandCollapseListener;
    }

    // Method called when the ParentViewHolder has triggered an expansion for its parent
    protected void parentExpandedFromViewHolder(int flatParentPosition) {

        // Gets an ExpandableWrapper list item at the flatParentPosition
        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);

        // Expands the parent and adds its children to the flatList of items
        updateExpandedParent(parentWrapper, flatParentPosition, true);
    }

    // Method to expand a parent and adds its children to the flatList of items
    private void updateExpandedParent(ExpandableWrapper<P, C> parentWrapper, int flatParentPosition, boolean expansionTriggeredByListItemClick) {

        // Bypasses the method if the Wrapper is already expanded
        if(parentWrapper.isExpanded()) {
            return;
        }

        parentWrapper.setExpandedState(true);
        int adjFlatParentPosition = flatParentPosition + 1; // Adjusted to account for the position starting at 0 instead of 1

        // Gets the wrapped Child list from the passed in parentWrapper
        List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();

        if(wrappedChildList != null) {
            int childCount = wrappedChildList.size();

            // Inserts the wrapped Child list into new positions in the parentWrapper
            // ie. if childCount = 4, it  will cycle through mFlatItemList[0] to [3]
            for (int i = 0; i < childCount; i++) {
                mFlatItemList.add(adjFlatParentPosition + i, wrappedChildList.get(i));
            }

            // Notifies any observers that qty: childCount Child items have been inserted
            notifyItemRangeInserted(adjFlatParentPosition, childCount);
        }

        // When the parent is expanded, get the nearest parent item position
        if(expansionTriggeredByListItemClick && mExpandCollapseListener != null) {
            mExpandCollapseListener.onParentExpanded(getNearestParentPosition(flatParentPosition));
        }
    }

    // Method called when the ParentViewHolder has triggered a collapse for its parent
    protected void parentCollapsedFromViewHolder(int flatParentPosition) {

        // Gets an ExpandableWrapper list item at the flatParentPosition
        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);

        //
        updateCollapsedParent(parentWrapper, flatParentPosition, true);
    }

    // Method to collapse a parent and removes its children from the flatList of items
    public void updateCollapsedParent(ExpandableWrapper<P, C> parentWrapper, int flatParentPosition, boolean collapseTriggeredByListItemClick) {

        // Bypasses the method if the Wrapper is already collapsed
        if(!parentWrapper.isExpanded()) {
            return;
        }

        parentWrapper.setExpandedState(false);
        int adjFlatParentPosition = flatParentPosition + 1; // Adjusted to account for the childList starting at 0 instead of 1

        // Gets the wrapped Child list from the passed in parentWrapper
        List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();

        if(wrappedChildList != null) {
            int childCount = wrappedChildList.size();

            // Removes the wrapped Child list from the positions in the parentWrapper
            // ie. if childCount = 4, it  will cycle from mFlatItemList[3] to [0]
            for (int i = childCount - 1; i >= 0; i--){
                mFlatItemList.remove(adjFlatParentPosition + i);
            }

            // Notifies any observers that qty: childCount Child items have been removed
            notifyItemRangeRemoved(adjFlatParentPosition, childCount);
        }

        // When the parent is collapsed, get the nearest parent item position
        if(collapseTriggeredByListItemClick && mExpandCollapseListener != null) {
            mExpandCollapseListener.onParentCollapsed(getNearestParentPosition(flatParentPosition));
        }
    }

    // Implementation of the ParentViewHolderExpandableListener from the ParentViewHolder class
    private ParentViewHolderExpandableListener mParentViewHolderExpandableListener = new ParentViewHolderExpandableListener() {

        @Override
        public void onParentExpanded(int flatParentPosition) {
            parentExpandedFromViewHolder(flatParentPosition);
        }

        @Override
        public void onParentCollapsed(int flatParentPosition) {
            parentCollapsedFromViewHolder(flatParentPosition);
        }
    };

    // Expands a specific Parent in a list of Parents
    public void expandParent(P parent) {

        ExpandableWrapper<P, C> parentWrapper = new ExpandableWrapper<>(parent);

        // Gets the flatPosition of the parent in the mFlatItemsList
        int flatParentPosition = mFlatItemList.indexOf(parentWrapper);
        if(flatParentPosition == INVALID_FLAT_POSITION) {
            return;
        }

        // Expands the views in each RecyclerView at the ViewHolder level
        expandViews(mFlatItemList.get(flatParentPosition), flatParentPosition);
    }

    // Calls through to the ParentViewHolder to expand views for each RecyclerView a specified parent is a child of
    private void expandViews(ExpandableWrapper<P, C> parentWrapper, int flatParentPosition) {
        PVH viewHolder;

        for(RecyclerView recyclerView : mAttachedRecyclerViewPool) {
            viewHolder = (PVH) recyclerView.findViewHolderForAdapterPosition(flatParentPosition);

            if(viewHolder != null && !viewHolder.isExpanded()) {
                viewHolder.setExpandedState(true);
                viewHolder.onExpansionToggled(false);
            }
        }
        updateCollapsedParent(parentWrapper, flatParentPosition, false);
    }

    // Expands a parent at the specified index in a list of parents
    public void expandParent(int parentPosition) {
        expandParent(mParentList.get(parentPosition));
    }

    // Expands all parents in a range of indices in a list of parents
    public void expandParentRange(int startParentPosition, int parentCount) {
        int endParentPosition = startParentPosition + parentCount;

        for(int i = startParentPosition; i < endParentPosition; i++) {
            expandParent(i);
        }
    }

    // Expands all parents in a list of parents
    public void expandAllParents() {

        for (P parent : mParentList) {
            expandParent(parent);
        }
    }

    // Collapses a specific Parent in a list of Parents
    public void collapseParent(P parent) {

        ExpandableWrapper<P, C> parentWrapper = new ExpandableWrapper<>(parent);

        // Gets the flatPosition of the parent in the mFlatItemsList
        int flatParentPosition = mFlatItemList.indexOf(parentWrapper);
        if(flatParentPosition == INVALID_FLAT_POSITION) {
            return;
        }

        // Collapses the views in each RecyclerView at the ViewHolder level
        collapseViews(mFlatItemList.get(flatParentPosition), flatParentPosition);
    }

    // Calls through to the ParentViewHolder to collapse views for each RecyclerView a specified parent is a child of
    private void collapseViews(ExpandableWrapper<P, C> parentWrapper, int flatParentPosition) {
        PVH viewHolder;

        for (RecyclerView recyclerView : mAttachedRecyclerViewPool) {

            viewHolder = (PVH) recyclerView.findViewHolderForAdapterPosition(flatParentPosition);
            if(viewHolder != null && !viewHolder.isExpanded()) {
                viewHolder.setExpandedState(false);
                viewHolder.onExpansionToggled(true);
            }
        }
        updateCollapsedParent(parentWrapper, flatParentPosition, false);
    }


    // Collapses a parent at the specified index in a list of parents
    public void collapseParent(int parentPosition) {
        collapseParent(mParentList.get(parentPosition));
    }

    // Collapses all parents in a range of indices in a list of parents
    public void collapseParentRange(int startParentPosition, int parentCount) {
        int endParentPosition = startParentPosition + parentCount;

        for(int i = startParentPosition; i < endParentPosition; i++) {
            collapseParent(i);
        }
    }

    // Collapses all parents in a list of parents
    public void collapseAllParents() {

        for (P parent : mParentList) {
            collapseParent(parent);
        }
    }

    // Returns the nearest Parent position given a relative index in an entire RecyclerView
    // If it is the index of a parent, it will return the corresponding parent position
    // If it is the index of a child within the RecyclerView, it will return the position of that child's parent
    int getNearestParentPosition(int flatPosition) {

        if(flatPosition == 0) {
            return 0;
        }

        // Sets the parentCount at -1 to account for the position starting at [0]
        int parentCount = -1;

        // Increment through the flatPosition indexes until a Parent is found
        for(int i = 0; i <= flatPosition; i++) {

            ExpandableWrapper<P, C> listItem = mFlatItemList.get(i);
            if(listItem.isParent()) {
                parentCount++;
            }
        }
        return parentCount;
    }

    // Given an index relative to the entire RecyclerView for a child item, returns the child position within the child list of a parent
    int getChildPosition(int flatPosition) {

        if(flatPosition == 0) {
            return 0;
        }

        int childCount = 0;
        for(int i = 0; i < flatPosition; i++) {

            ExpandableWrapper<P, C> listItem = mFlatItemList.get(i);
            if(listItem.isParent()) {
                childCount = 0;
            } else {
                childCount++;
            }
        }
        return childCount;
    }



















}
