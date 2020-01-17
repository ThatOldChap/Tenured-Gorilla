package com.michaelchaplin.spendometer;


// Interface used for setting up a TouchListener on a ViewHolder for a RecyclerViewAdapter
public interface RecyclerViewItemTouchListener {

    void onItemClick(int position);
}
