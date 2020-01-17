package com.michaelchaplin.spendometer;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaelchaplin.spendometer.data.SpendometerContract;

public class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mCategory;
    public TextView mDate;
    public TextView mCost;
    public TextView mNotes;
    public TextView mAccount;
    public ImageView mIcon;
    RecyclerViewItemTouchListener mExpenseTouchListener;

    // Constructor to add all the views into the ViewHolder for an expense
    public ExpenseViewHolder(View itemView, RecyclerViewItemTouchListener expenseTouchListener) {

        // Invokes the superclass methods for RecyclerView.ViewHolder
        super(itemView);

        // Finds the views that will populated with the expense data
        mCategory = itemView.findViewById(R.id.expense_category);
        mDate = itemView.findViewById(R.id.expense_date);
        mCost = itemView.findViewById(R.id.expense_cost);
        mNotes = itemView.findViewById(R.id.expense_notes);
        mAccount = itemView.findViewById(R.id.expense_account);
        mIcon = itemView.findViewById(R.id.expense_category_icon);

        // Creates and sets an ExpenseTouchListener on the created ExpenseViewHolder
        mExpenseTouchListener = expenseTouchListener;
    }

    public void setCursorData(Cursor cursor){

        // Assigns local variables the values from the cursor at the given position
        String category = cursor.getString(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_CATEGORY));
        Double cost = cursor.getDouble(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_COST));
        int date = cursor.getInt(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_DATE));
        String notes = cursor.getString(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_NOTES));
        int icon = cursor.getInt(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_ICON_ID));
        String account = cursor.getString(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_ACCOUNT));

        // Binds the data extracted from the cursor at the given position into the views within the ExpenseViewHolder
        mCategory.setText(category);
        mCost.setText("$" + String.valueOf(cost));
        mDate.setText(Integer.toString(date));
        mNotes.setText(notes);
        mIcon.setImageResource(icon);
        mAccount.setText(account);
    }

    @Override
    public void onClick(View view) {
        mExpenseTouchListener.onItemClick(getAdapterPosition());
    }
}
