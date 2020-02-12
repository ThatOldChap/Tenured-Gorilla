package com.michaelchaplin.spendometer.ExpandableRecyclerView;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.michaelchaplin.spendometer.ExpandableRecyclerView.ParentViewHolder.ParentViewHolderExpandableListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * "P extends Parent<C>" is the Parent Object type that is instantiated with the adapter
 * "C" is the Child Object type that is defined by the Parent
 * "PVH extends ParentViewHolder" is the ParentViewHolder type that is instantiated with the adapter
 * "CVH extends ChildViewHolder" is the ChildViewHolder type that is instantiated with the adapter
 * <p>
 * Example implementation:
 * <p>
 * public class ExpandableRecyclerAdapter<Recipe<Ingredients>, Ingredients, RecipeViewHolder, IngredientViewHolder>
 */

// Implementation of a RecyclerView.Adapter that adds the ability to expand/collapse list items
public abstract class ExpandableRecyclerAdapter<P extends Parent<C>, C, PVH extends ParentViewHolder, CVH extends ChildViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String EXPANDED_STATE_MAP = "ExpandableRecyclerAdapter.ExpandedStateMap";

    // Integers for defining types of views/positions
    public static final int TYPE_PARENT = 0;
    public static final int TYPE_CHILD = 1;
    public static final int TYPE_FIRST_USER = 2;
    public static final int INVALID_FLAT_POSITION = -1;

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

    private Map<P, Boolean> mExpansionStateMap;

    // Primary Constructor for the adapter
    public ExpandableRecyclerAdapter(List<P> parentList) {

        // Invokes the superclass' methods
        super();

        mParentList = parentList;
        mFlatItemList = generateFlattenedParentChildList(parentList);
        mAttachedRecyclerViewPool = new ArrayList<>();
        mExpansionStateMap = new HashMap<>(mParentList.size());
    }

    // Implementation of the onCreateViewHolder method that is split to creates ViewHolders for both the Parent and Child
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (isParentViewType(viewType)) {

            PVH pvh = onCreateParentViewHolder(viewGroup, viewType);
            pvh.setParentViewHolderExpandableListener(mParentViewHolderExpandableListener);
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
    public int getItemCount() {
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
        if (parentWrapper.isExpanded()) {
            return;
        }

        parentWrapper.setExpandedState(true);
        mExpansionStateMap.put(parentWrapper.getParent(), true);
        int adjFlatParentPosition = flatParentPosition + 1; // Adjusted to account for the position starting at 0 instead of 1

        // Gets the wrapped Child list from the passed in parentWrapper
        List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();

        if (wrappedChildList != null) {
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
        if (expansionTriggeredByListItemClick && mExpandCollapseListener != null) {
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
    private void updateCollapsedParent(ExpandableWrapper<P, C> parentWrapper, int flatParentPosition, boolean collapseTriggeredByListItemClick) {

        // Bypasses the method if the Wrapper is already collapsed
        if (!parentWrapper.isExpanded()) {
            return;
        }

        parentWrapper.setExpandedState(false);
        mExpansionStateMap.put(parentWrapper.getParent(), false);
        int adjFlatParentPosition = flatParentPosition + 1; // Adjusted to account for the childList starting at 0 instead of 1

        // Gets the wrapped Child list from the passed in parentWrapper
        List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();

        if (wrappedChildList != null) {
            int childCount = wrappedChildList.size();

            // Removes the wrapped Child list from the positions in the parentWrapper
            // ie. if childCount = 4, it  will cycle from mFlatItemList[3] to [0]
            for (int i = childCount - 1; i >= 0; i--) {
                mFlatItemList.remove(adjFlatParentPosition + i);
            }

            // Notifies any observers that qty: childCount Child items have been removed
            notifyItemRangeRemoved(adjFlatParentPosition, childCount);
        }

        // When the parent is collapsed, get the nearest parent item position
        if (collapseTriggeredByListItemClick && mExpandCollapseListener != null) {
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
        if (flatParentPosition == INVALID_FLAT_POSITION) {
            return;
        }

        // Expands the views in each RecyclerView at the ViewHolder level
        expandViews(mFlatItemList.get(flatParentPosition), flatParentPosition);
    }

    // Calls through to the ParentViewHolder to expand views for each RecyclerView a specified parent is a child of
    private void expandViews(ExpandableWrapper<P, C> parentWrapper, int flatParentPosition) {
        PVH viewHolder;

        for (RecyclerView recyclerView : mAttachedRecyclerViewPool) {
            viewHolder = (PVH) recyclerView.findViewHolderForAdapterPosition(flatParentPosition);

            if (viewHolder != null && !viewHolder.isExpanded()) {
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

        for (int i = startParentPosition; i < endParentPosition; i++) {
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
        if (flatParentPosition == INVALID_FLAT_POSITION) {
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
            if (viewHolder != null && !viewHolder.isExpanded()) {
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

        for (int i = startParentPosition; i < endParentPosition; i++) {
            collapseParent(i);
        }
    }

    // Collapses all parents in a list of parents
    public void collapseAllParents() {

        for (P parent : mParentList) {
            collapseParent(parent);
        }
    }

    // Stores the expanded state across the activity
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putSerializable(EXPANDED_STATE_MAP, generateExpandedStateMap());
    }

    @SuppressWarnings("unchecked")
    // Gets the expandable state map of the saved instance state
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {

        // Return if there is not savedState or the bundle doesn't have the state map saved
        if (savedInstanceState == null || !savedInstanceState.containsKey(EXPANDED_STATE_MAP)) {
            return;
        }

        HashMap<Integer, Boolean> expandedStateMap = (HashMap<Integer, Boolean>) savedInstanceState.getSerializable(EXPANDED_STATE_MAP);
        if (expandedStateMap == null) {
            return;
        }

        List<ExpandableWrapper<P, C>> itemList = new ArrayList<>();
        int parentsCount = mParentList.size();

        for (int i = 0; i < parentsCount; i++) {

            ExpandableWrapper<P, C> parentWrapper = new ExpandableWrapper<>(mParentList.get(i));
            itemList.add(parentWrapper);

            if (expandedStateMap.containsKey(i)) {
                boolean expanded = expandedStateMap.get(i);
                parentWrapper.setExpandedState(expanded);

                if (expanded) {
                    List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();
                    int childrenCount = wrappedChildList.size();

                    for (int j = 0; j < childrenCount; j++) {
                        ExpandableWrapper<P, C> childWrapper = wrappedChildList.get(j);
                        itemList.add(childWrapper);
                    }
                }
            }
        }
        mFlatItemList = itemList;
        notifyDataSetChanged();
    }


    // Returns the nearest Parent position given a relative index in an entire RecyclerView
    // If it is the index of a parent, it will return the corresponding parent position
    // If it is the index of a child within the RecyclerView, it will return the position of that child's parent
    int getNearestParentPosition(int flatPosition) {

        if (flatPosition == 0) {
            return 0;
        }

        // Sets the parentCount at -1 to account for the position starting at [0]
        int parentCount = -1;

        // Increment through the flatPosition indexes until a Parent is found
        for (int i = 0; i <= flatPosition; i++) {

            ExpandableWrapper<P, C> listItem = mFlatItemList.get(i);
            if (listItem.isParent()) {
                parentCount++;
            }
        }
        return parentCount;
    }

    // Given an index relative to the entire RecyclerView for a child item, returns the child position within the child list of a parent
    int getChildPosition(int flatPosition) {

        if (flatPosition == 0) {
            return 0;
        }

        int childCount = 0;
        for (int i = 0; i < flatPosition; i++) {

            ExpandableWrapper<P, C> listItem = mFlatItemList.get(i);
            if (listItem.isParent()) {
                childCount = 0;
            } else {
                childCount++;
            }
        }
        return childCount;
    }


    // General notification to DataObservers that the dataset has changed
    public void notifyParentDataSetChanged(boolean preserveExpansionState) {

        if (preserveExpansionState) {
            mFlatItemList = generateFlattenedParentChildList(mParentList, mExpansionStateMap);
        } else {
            mFlatItemList = generateFlattenedParentChildList(mParentList);
        }
        notifyDataSetChanged();
    }

    // Notify any registered Observers that the parent at parentPosition has been inserted
    public void notifyParentInserted(int parentPosition) {

        P parent = mParentList.get(parentPosition);
        int flatParentPosition;

        // Uses the getFlatParentPosition method to obtain the flatParentPosition unless it is the last parent in mFlatItemsList
        if (parentPosition < mParentList.size() - 1) {
            flatParentPosition = getFlatParentPosition(parentPosition);
        } else {
            flatParentPosition = mFlatItemList.size();
        }

        // Determines the size of ExpandableItemWrapper to add to define the item range
        int sizeChanged = addParentWrapper(flatParentPosition, parent);
        notifyItemRangeInserted(flatParentPosition, sizeChanged);
    }

    // Notify any registered Observers that the parents in a certain range have been inserted
    public void notifyParentRangeInserted(int parentPositionStart, int itemCount) {

        int initialFlatParentPosition;

        // Use the standard method to get the initialFlatParentPosition unless the parents are the last in the list
        if (parentPositionStart < mParentList.size() - itemCount) {
            initialFlatParentPosition = getFlatParentPosition(parentPositionStart);
        } else {
            initialFlatParentPosition = mFlatItemList.size();
        }

        int sizeChanged = 0;
        int flatParentPosition = initialFlatParentPosition;
        int changedParent;
        int parentPositionEnd = parentPositionStart + itemCount;

        // Loop through the range of parents being updated to get the total sizeChanged
        for (int i = parentPositionStart; i < parentPositionEnd; i++) {

            // Get the parent at its position in the list
            P parent = mParentList.get(i);

            // Returns the flat size of Parents and Children added to the list
            changedParent = addParentWrapper(flatParentPosition, parent);

            // Increment flatParent position and size counter
            flatParentPosition += changedParent;
            sizeChanged += changedParent;
        }
        notifyItemRangeInserted(initialFlatParentPosition, sizeChanged);
    }

    // Function to add a Parent with wrapped Children into the mFlatItemsList
    private int addParentWrapper(int flatParentPosition, P parent) {

        int sizeChanged = 1;
        ExpandableWrapper<P, C> parentWrapper = new ExpandableWrapper<>(parent);

        // Adds the parent to the flatItemList
        mFlatItemList.add(flatParentPosition, parentWrapper);

        // Determines the sizeChanged based on if the parent was expanded or not
        if (parentWrapper.isParentInitiallyExpanded()) {

            parentWrapper.setExpandedState(true);

            // Gets the flat list of children of the parent
            List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();

            // Adds the Parent and list of wrapped Children to the mFlatItemList
            mFlatItemList.addAll(flatParentPosition + sizeChanged, wrappedChildList);

            // Adds the amount of Children in the wrappedChildList to the sizeChanged count
            sizeChanged += wrappedChildList.size();
        }
        return sizeChanged;
    }

    // Notify any registered Observers that a Parent at parentPosition was removed
    public void notifyParentRemoved(int parentPosition) {

        int flatParentPosition = getFlatParentPosition(parentPosition);
        int sizeChanged = removeParentWrapper(flatParentPosition);

        notifyItemRangeRemoved(flatParentPosition, sizeChanged);
    }

    // Notify any registered Observers that the parents in a certain range have been removed
    public void notifyParentRangeRemoved(int parentPositionStart, int itemCount) {

        int sizeChanged = 0;
        int flatParentPositionStart = getFlatParentPosition(parentPositionStart);

        // Loops through the range of parents to remove the Parent and children counts from the mFlatItemsList
        for (int i = 0; i < itemCount; i++) {
            sizeChanged += removeParentWrapper(flatParentPositionStart);
        }
        notifyItemRangeRemoved(flatParentPositionStart, sizeChanged);
    }

    // Function to remove a Parent with wrapped Children from the mFlatItemsList
    private int removeParentWrapper(int flatParentPosition) {

        int sizeChanged = 1;
        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.remove(flatParentPosition);

        // Determines the sizeChanged based on if the parent was expanded or not
        if (parentWrapper.isExpanded()) {
            // Get the size of the wrappedChildList
            int childListSize = parentWrapper.getWrappedChildList().size();

            // Cycle through the list and remove the children from the list
            for (int i = 0; i < childListSize; i++) {
                mFlatItemList.remove(flatParentPosition);
                sizeChanged++;
            }
        }
        return sizeChanged;
    }

    // Notify any registered Observers that a parent has changed
    public void notifyParentChanged(int parentPosition) {

        P parent = mParentList.get(parentPosition);
        int flatParentPositionStart = getFlatParentPosition(parentPosition);

        // Returns the number of Parents and Children that have changed
        int sizeChanged = changeParentWrapper(flatParentPositionStart, parent);

        notifyItemRangeChanged(flatParentPositionStart, sizeChanged);
    }

    // Notify any registered Observers that a range of parents has changed
    public void notifyParentRangeChanged(int parentPositionStart, int itemCount) {

        int flatParentPosition = getFlatParentPosition(parentPositionStart);
        int sizeChanged = 0;
        int changed;
        P parent;

        for (int i = 0; i < itemCount; i++) {

            // Gets the parent at each position in the range
            parent = mParentList.get(parentPositionStart);

            // Returns the number of Children that have changed in the parent
            changed = changeParentWrapper(flatParentPosition, parent);
            sizeChanged += changed;
            flatParentPosition += changed;
            parentPositionStart++;
        }
        notifyItemRangeChanged(flatParentPosition, sizeChanged);
    }

    // Function to show a Parent has Children that may have changed from the mFlatItemsList
    private int changeParentWrapper(int flatParentPosition, P parent) {

        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
        parentWrapper.setParent(parent);

        int sizeChanged = 1;

        if (parentWrapper.isExpanded()) {

            List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();
            int childSize = wrappedChildList.size();
            int adjFlatParentPosition = flatParentPosition + 1; // Accounts for the position starts at [0]

            for (int i = 0; i < childSize; i++) {

                // Changes the children in the wrappedChildList
                mFlatItemList.set(adjFlatParentPosition + i, wrappedChildList.get(i));
                sizeChanged++;
            }
        }
        return sizeChanged;
    }

    // Notify any registered Observers that the parent and its children have moved positions
    public void notifyParentMoved(int fromParentPosition, int toParentPosition) {

        int fromFlatParentPosition = getFlatParentPosition(fromParentPosition);
        ExpandableWrapper<P, C> fromParentWrapper = mFlatItemList.get(fromFlatParentPosition);

        boolean isCollapsed = !fromParentWrapper.isExpanded();
        boolean isExpandedNoChildren = !isCollapsed && (fromParentWrapper.getWrappedChildList().size() == 0);

        if (isCollapsed || isExpandedNoChildren) {

            int toFlatParentPosition = getFlatParentPosition(toParentPosition);
            ExpandableWrapper<P, C> toParentWrapper = mFlatItemList.get(toFlatParentPosition);

            mFlatItemList.remove(fromFlatParentPosition);

            int childOffset = 0;

            // Gets the number of children in the toParentWrapper if there any
            if (toParentWrapper.isExpanded()) {
                childOffset = toParentWrapper.getWrappedChildList().size();
            }

            mFlatItemList.add(toFlatParentPosition + childOffset, fromParentWrapper);
            notifyItemMoved(fromFlatParentPosition, toFlatParentPosition + childOffset);
        } else {

            int sizeChanged = 0;
            int childListSize = fromParentWrapper.getWrappedChildList().size();

            // Removes the Parent and Children from the fromFlatParentPosition
            for (int i = 0; i < childListSize + 1; i++) {
                mFlatItemList.remove(fromFlatParentPosition);
                sizeChanged++;
            }
            notifyItemRangeRemoved(fromFlatParentPosition, sizeChanged);

            int toFlatParentPosition = getFlatParentPosition(toParentPosition);
            int childOffset = 0;

            // Gets the n
            if (toFlatParentPosition != INVALID_FLAT_POSITION) {

                ExpandableWrapper<P, C> toParentWrapper = mFlatItemList.get(toFlatParentPosition);

                // Gets the number of children in the toParentWrapper if there any
                if (toParentWrapper.isExpanded()) {
                    childOffset = toParentWrapper.getWrappedChildList().size();
                }
            } else {
                toFlatParentPosition = mFlatItemList.size();
            }

            mFlatItemList.add(toFlatParentPosition + childOffset, fromParentWrapper);
            List<ExpandableWrapper<P, C>> wrappedChildList = fromParentWrapper.getWrappedChildList();

            // Modifies the changes size to account for the added Parent
            sizeChanged = wrappedChildList.size() + 1;
            int adjChildOffset = childOffset + 1;

            // Adds all the wrapped children to the mFlatItemsList
            mFlatItemList.addAll(toFlatParentPosition + adjChildOffset, wrappedChildList);
            notifyItemRangeInserted(toFlatParentPosition + childOffset, sizeChanged);
        }
    }

    // Notifies any registered Observers that a Child has been inserted at a given Parent position
    public void notifyChildInserted(int parentPosition, int childPosition) {

        // Gets the parent at the given position
        int flatParentPosition = getFlatParentPosition(parentPosition);
        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);

        parentWrapper.setParent(mParentList.get(parentPosition));

        if (parentWrapper.isExpanded()) {

            // Gets the wrapped child at the passed in position
            ExpandableWrapper<P, C> wrappedChild = parentWrapper.getWrappedChildList().get(childPosition);
            int adjChildPosition = childPosition + 1;

            // Adds the wrapped child to the mFlatItemsList
            mFlatItemList.add(flatParentPosition + adjChildPosition, wrappedChild);
            notifyItemInserted(flatParentPosition + adjChildPosition);
        }
    }

    // Notifies any registered Observers that a range of Children have been inserted at a given Parent and Child Position
    public void notifyChildRangeInserted(int parentPosition, int childPositionStart, int itemCount) {

        int flatParentPosition = getFlatParentPosition(parentPosition);
        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
        parentWrapper.setParent(mParentList.get(parentPosition));

        if (parentWrapper.isExpanded()) {

            List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();

            for (int i = 0; i < itemCount; i++) {

                ExpandableWrapper<P, C> child = wrappedChildList.get(childPositionStart + i);
                mFlatItemList.add(flatParentPosition + childPositionStart + 1 + i, child);
            }
            notifyItemRangeInserted(flatParentPosition + childPositionStart + 1, itemCount);
        }
    }

    // Notifies any registered Observers that a child at a given Parent and Child has been removed
    public void notifyChildRemoved(int parentPosition, int childPosition) {

        int flatParentPosition = getFlatParentPosition(parentPosition);
        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
        parentWrapper.setParent(mParentList.get(parentPosition));
        int adjChildPosition = childPosition + 1;

        if (parentWrapper.isExpanded()) {
            mFlatItemList.remove(flatParentPosition + adjChildPosition);
            notifyItemRemoved(flatParentPosition + adjChildPosition);
        }
    }

    // Notify any registered Observers that a range of children at a given Parent and child position have been removed
    public void notifyChildRangeRemoved(int parentPosition, int childPositionStart, int itemCount) {

        int flatParentPosition = getFlatParentPosition(parentPosition);
        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
        parentWrapper.setParent(mParentList.get(parentPosition));
        int adjChildPositionStart = childPositionStart + 1;

        if (parentWrapper.isExpanded()) {

            for (int i = 0; i < itemCount; i++) {
                mFlatItemList.remove(flatParentPosition + adjChildPositionStart);
            }
            notifyItemRangeRemoved(flatParentPosition + adjChildPositionStart, itemCount);
        }
    }

    // Notify any registered Observers that a child at a given Parent and Child has been updated
    public void notifyChildChanged(int parentPosition, int childPosition) {

        P parent = mParentList.get(parentPosition);
        int flatParentPosition = getFlatParentPosition(parentPosition);
        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
        parentWrapper.setParent(parent);
        int adjChildPosition = childPosition + 1;

        if (parentWrapper.isExpanded()) {

            int flatChildPosition = flatParentPosition + adjChildPosition;
            ExpandableWrapper<P, C> child = parentWrapper.getWrappedChildList().get(childPosition);
            mFlatItemList.set(flatChildPosition, child);
            notifyItemChanged(flatChildPosition);
        }
    }

    // Notify any registered Observers that a child at a given Parent and Child range has been updated
    public void notifyChildRangeChanged(int parentPosition, int childPositionStart, int itemCount) {

        P parent = mParentList.get(parentPosition);
        int flatParentPosition = getFlatParentPosition(parentPosition);
        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
        parentWrapper.setParent(parent);
        int adjChildPosition = childPositionStart + 1;

        if (parentWrapper.isExpanded()) {

            int flatChildPosition = flatParentPosition + adjChildPosition;

            for (int i = 0; i < itemCount; i++) {

                ExpandableWrapper<P, C> child = parentWrapper.getWrappedChildList().get(adjChildPosition);
                mFlatItemList.set(flatChildPosition + i, child);
            }
            notifyItemRangeChanged(flatChildPosition, itemCount);
        }
    }

    // Notify any registered Observers that a child has been moved to another position within a parent
    public void notifyChildMoved(int parentPosition, int fromChildPosition, int toChildPosition) {

        P parent = mParentList.get(parentPosition);
        int flatParentPosition = getFlatParentPosition(parentPosition);
        ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
        parentWrapper.setParent(parent);
        int adjFromChildPosition = fromChildPosition + 1;
        int adjToChildPosition = toChildPosition + 1;

        if (parentWrapper.isExpanded()) {

            ExpandableWrapper<P, C> fromChild = mFlatItemList.remove(flatParentPosition + adjFromChildPosition);
            mFlatItemList.add(flatParentPosition + adjFromChildPosition, fromChild);
            notifyItemMoved(flatParentPosition + adjFromChildPosition, flatParentPosition + adjToChildPosition);
        }
    }

    // Generates a full flattened list of all parents and their children, in order
    private List<ExpandableWrapper<P, C>> generateFlattenedParentChildList(List<P> parentList) {

        List<ExpandableWrapper<P, C>> flatItemList = new ArrayList<>();
        int parentCount = parentList.size();

        for (int i = 0; i < parentCount; i++) {

            P parent = parentList.get(i);
            generateParentWrapper(flatItemList, parent, parent.isInitiallyExpanded());
        }
        return flatItemList;
    }

    // Generates a full list of all parents and children in order using a savedInstanceSate HashMap
    private List<ExpandableWrapper<P, C>> generateFlattenedParentChildList(List<P> parentList, Map<P, Boolean> savedLastExpansionState) {

        List<ExpandableWrapper<P, C>> flatItemList = new ArrayList<>();
        int parentCount = parentList.size();

        for (int i = 0; i < parentCount; i++) {

            P parent = parentList.get(i);

            Boolean lastExpandedState = savedLastExpansionState.get(parent);

            // If the lastExpandedState is null, then return the initiallyExpanded state of the parent
            // If the lastExpandedState non-null, then return the lastExpandedState of the parent
            boolean shouldExpand = lastExpandedState == null ? parent.isInitiallyExpanded() : lastExpandedState;

            generateParentWrapper(flatItemList, parent, shouldExpand);
        }
        return flatItemList;
    }

    // Creates a parentWrapper
    private void generateParentWrapper(List<ExpandableWrapper<P, C>> flatItemList, P parent, boolean shouldExpand) {

        ExpandableWrapper<P, C> parentWrapper = new ExpandableWrapper<>(parent);
        flatItemList.add(parentWrapper);

        // Expands a Parent's children if applicable
        if (shouldExpand) {
            generateExpandedChildren(flatItemList, parentWrapper);
        }
    }

    // Expands the children of a wrapped parent
    private void generateExpandedChildren(List<ExpandableWrapper<P, C>> flatItemList, ExpandableWrapper<P, C> parentWrapper) {

        // Expands the parentWrapper
        parentWrapper.setExpandedState(true);

        List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();
        int childCount = wrappedChildList.size();

        // Adds all the wrappedChildren to the flatItemList
        for (int i = 0; i < childCount; i++) {
            ExpandableWrapper<P, C> childWrapper = wrappedChildList.get(i);
            flatItemList.add(childWrapper);
        }
    }

    // Generates the HashMap used to store the expanded state of all the items in a list
    private HashMap<Integer, Boolean> generateExpandedStateMap() {

        HashMap<Integer, Boolean> parentHashMap = new HashMap<>();
        int childCount = 0;
        int listItemCount = mFlatItemList.size();

        // Cycles through the whole mFlatItemList to
        for (int i = 0; i < listItemCount; i++) {

            // Checks if the listItem is valid
            if (mFlatItemList.get(i) != null) {

                // Gets a wrapped list item
                ExpandableWrapper<P, C> listItem = mFlatItemList.get(i);
                if (listItem.isParent()) {
                    // Adds the HashMap fields for the Parents and accounts for bypassing the children
                    parentHashMap.put(i - childCount, listItem.isExpanded());
                } else {
                    childCount++;
                }
            }
        }
        return parentHashMap;
    }

    // Converts the parent's position in the flatParent (expanded) list
    private int getFlatParentPosition(int parentPosition) {

        int parentCount = 0;
        int listItemCount = mFlatItemList.size();

        for (int i = 0; i < listItemCount; i++) {

            // Checks if the list item is a Parent and increment if so
            if (mFlatItemList.get(i).isParent()) {
                parentCount++;

                // When the parentPosition surpasses the parentCount, the index in the flatItemList is the flatParentPosition
                if (parentCount > parentPosition) {
                    return i;
                }
            }
        }
        return INVALID_FLAT_POSITION;
    }
}
