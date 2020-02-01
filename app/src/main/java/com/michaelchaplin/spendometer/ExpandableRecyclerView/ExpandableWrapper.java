package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import java.util.ArrayList;
import java.util.List;

// A wrapper that is used to link metadata with a list item

/**
 * A wrapper that is used to create a 2-column by X-row matrix
 * Ex. <Parent 1, Child 1>
 *     <Parent 1, Child 2>
 *     <Parent 1, Child 3>
 *     <Parent 2, Child 1>
 *     <Parent 2, Child 2>
 * @param <P> Parent List item
 * @param <C> Child List item
 */
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

    @Override
    public boolean equals(Object object) {

        // If the an object being compared is an ExpandableWrapper<P, C>, return true
        if(this == object) {return true;}

        // If the object isn't an ExpandableWrapper<P, C>, return false
        if(object == null || getClass() != object.getClass()) { return false;}

        // Sets up an item named "that" which has unknown Parent and Child items
        final ExpandableWrapper<?, ?> that = (ExpandableWrapper<?, ?>) object;

        boolean parentNonNullChecker = mParent != null;
        boolean thatParentNonNullChecker = that.mParent != null;
        boolean childNonNullChecker = mChild != null;
        boolean thatChildNonNullChecker = that.mChild != null;

        // If mParent isn't null and mParent doesn't equal the mParent of "that", return false
        // If mParent isn't null and mParent equals the mParent of "that", then return false if "that" has a non-null mParent
        if (parentNonNullChecker ? !mParent.equals(that.mParent) : thatParentNonNullChecker) {
            return false;
        }

        // If mChild isn't null and mChild equals the mChild of "that", return true
        // If mChild isn't null and mChild doesn't equal the mChild of "that", return true if the mChild of "that" is non-Null
        return childNonNullChecker ? mChild.equals(that.mChild) : thatChildNonNullChecker;
    }

    // TODO: Implement in the future for the savedState
    @Override
    public int hashCode() {

        boolean parentNonNullChecker = mParent != null;
        boolean childNonNullChecker = mChild != null;

        // Sets the hashcode if mParent/mChild is non-Null
        int result = parentNonNullChecker ? mParent.hashCode() : 0;

        // Uses 31 as a prime number to multiply hashcode by
        result = 31 * result + (childNonNullChecker ? mChild.hashCode() : 0);
        return result;
    }
}
