package com.michaelchaplin.spendometer;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;


/* An abstract class that gives a RecyclerView Adapter the ability to populate cursor data into a custom RecyclerView.ViewHolder object
 * TODO: Convert this class to use an mDataObserver
 */
public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private Cursor mCursor;
    protected Context mContext;
    private boolean mDataValid;
    private int mRowIDColumn;

    // Public constructor
    public RecyclerViewCursorAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;

        // Sets the mDataValid flag based on if the cursor is null or not
        mDataValid = cursor != null;

        // Sets the mRowIDColumn value to the cursor position if the data is valid
        // Sets the mRowIDColumn value to -1 if the data is invalid
        mRowIDColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
    }

    // Abstract method used to pass a cursor into the OnBindViewHolder method
    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        // Returns null since there aren't any modifications to make to this override method
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        // If the data is invalid (ie. cursor is null), throw an error
        if(!mDataValid) {
            throw new IllegalStateException("onBindViewHolder is being called when the cursor data is not valid");
        }

        // If the cursor is invalid, throw an error
        if(!mCursor.moveToPosition(position)){
            throw new IllegalStateException("Cursor can't be moved to the given position: " + position);
        }

        // Calls the abstract onBindViewHolder constructor to pass in a cursor
        onBindViewHolder(holder, mCursor);
    }

    // Returns the cursor that was passed into the adapter
    public Cursor getCursor() {
        return mCursor;
    }

    // Replaces the current cursor with a new cursor
    public void replaceCursor(Cursor cursor){

        Cursor oldCursor = swapCursor(cursor);
        Log.d(LOG_TAG, "replaceCursor: Cursor has been replaced");

        // If there is a valid current cursor, then close it
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    // Swap in a new cursor
    public Cursor swapCursor(Cursor newCursor) {

        // Checks if the new and older cursor are identical and if so, do nothing
        if(mCursor == newCursor){
            return null;
        }

        // Sets the oldCursor to be the cursor being swapped out
        Cursor oldCursor = mCursor;
        mCursor = newCursor;

        // If the new cursor is valid, notify the adapter that the cursor has changed and swap cursors
        if(mCursor != null){
            mDataValid = true;
            mRowIDColumn = newCursor.getColumnIndexOrThrow(SpendometerContract.ExpenseEntry._ID);
            notifyDataSetChanged();
            Log.d(LOG_TAG, "swapCursor: newCursor is valid");
        } else {
            // If the cursor is not valid, notify the adapter that all the cursor's rows have been removed
            notifyItemRangeRemoved(0, getItemCount());
            // Clear the data in the newCursor
            mDataValid = false;
            mRowIDColumn = -1;
            mCursor = null;
            Log.d(LOG_TAG, "swapCursor: newCursor is not valid");
        }

        return oldCursor;
    }

    @Override
    public int getItemCount() {

        // Returns the number of rows in the cursor if the data is valid
        if(mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {

        // Returns the row ID of the cursor at the selected position (ViewHolder) in the adapter
        if(mDataValid && mCursor != null && mCursor.moveToPosition(position)){
            return mCursor.getLong(mRowIDColumn);
        }
        return 0;
    }

    // Automatically sets the Adapter's setHasStableIDs value to true
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }


}
