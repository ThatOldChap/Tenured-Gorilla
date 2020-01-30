package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import java.util.List;

// Class that represents a Day that contains a list of Expenses
public interface Parent<C> {

    // Getter method to return the list of the Parent's child items
    List<C> getChildList();

    // Getter method used to determine if a Parent's view shows up as initially expanded
    boolean isInitiallyExpanded();
}
