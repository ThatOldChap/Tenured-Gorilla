package com.michaelchaplin.spendometer;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExpenseListAdapter extends RecyclerViewCursorAdapter<ExpenseViewHolder> {

    private RecyclerViewItemTouchListener mExpenseTouchListener;

    // The Constructor to add a cursor and TouchListener capability into the adapter
    public ExpenseListAdapter(Context context, Cursor cursor, RecyclerViewItemTouchListener expenseTouchListener) {

        // Invokes the superclass methods for ExpenseListAdapter
        super(context, cursor);

        // Passes an ExpenseTouchListener into the adapter
        this.mExpenseTouchListener = expenseTouchListener;
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the expense list item layout into a new view
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_expense, parent,false);

        // Return a new ExpenseViewHolder with a TouchListener attached
        return new ExpenseViewHolder(view, mExpenseTouchListener);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder viewHolder, Cursor cursor) {

        // Verifies whether the cursor was moved to the requested position of ViewHolder being bound
        cursor.moveToPosition(cursor.getPosition());

        // Sets the cursor data into the ExpenseViewHolder
        viewHolder.setCursorData(cursor);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

}
