package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import java.util.ArrayList;
import java.util.List;

// A wrapper that is used to link metadata with a list item
public class ExpandableWrapper<P extends Parent<C>, C> {

    private P mParent;
    private C mChild;
    private boolean mWrappedParent;
    private boolean mIsExpanded;

    private List<ExpandableWrapper<P, C>> mWrappedChildList;

    // Constructor that wraps a parent object of type P
    public ExpandableWrapper(P parent) {
        mParent = parent;
        mWrappedParent = true;
        mIsExpanded = false;

        mWrappedChildList = createChildItemList(parent);
    }

    // Constructor that wraps a child object
    public ExpandableWrapper(C child) {
        mChild = child;
        mWrappedParent = false;
        mIsExpanded = false;
    }

    // Get methods
    public P getParent() {
        return mParent;
    }
    public C getChild() {
        return mChild;
    }

    // Set methods
    public void setParent(P parent) {
        mParent = parent;
        mWrappedChildList = createChildItemList(parent);
    }

    public void setExpandedState(boolean expandedState){
        mIsExpanded = expandedState;
    }
    public boolean isExpanded() {
        return mIsExpanded;
    }

    // Determines whether a wrapped item is a parent
    public boolean isParent() {
        return mWrappedParent;
    }

    // Returns the initial expanded state of the ExpenseDay
    public boolean isParentInitiallyExpanded() {
        if(!mWrappedParent) {
            throw new IllegalStateException("Parent not wrapped");
        }
        return mParent.isInitiallyExpanded();
    }

    // Returns a wrapped list of children from a wrapped parent
    public List<ExpandableWrapper<P, C>> getWrappedChildList() {
        if (mWrappedParent) {
            throw new IllegalStateException("Parent not wrapped");
        }
        return mWrappedChildList;
    }

    // Method to create a list of child items that belong to a parent
    private List<ExpandableWrapper<P, C>> createChildItemList(P parentListItem) {

        // Creates an ArrayList to store a list of ExpandableWrappers
        List<ExpandableWrapper<P, C>> childItemList = new ArrayList<>();

        // Uses the Parent interface method to obtain the list of child items
        for (C child : parentListItem.getChildList()) {
            childItemList.add(new ExpandableWrapper<P, C>(child));
        }
        return childItemList;
    }

    // TODO: Implement in the future for the savedState
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    // TODO: Implement in the future for the savedState
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
